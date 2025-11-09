package familyConnection.domain.reaction.service;

import familyConnection.domain.reaction.dto.ReactionRequestDto;
import familyConnection.domain.reaction.dto.ReactionResponseDto;
import familyConnection.domain.reaction.entity.Reaction;
import familyConnection.domain.reaction.repository.ReactionRepository;
import familyConnection.domain.user.entity.User;
import familyConnection.domain.user.repository.UserRepository;
import familyConnection.global.apiPayload.code.status.ErrorStatus;
import familyConnection.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;

    /**
     * 반응 추가
     */
    @Transactional
    public ReactionResponseDto addReaction(Long userId, ReactionRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        String targetType = request.getTargetType().toUpperCase(); // "ANSWER", "COMMENT" 통일
        Long targetId = request.getTargetId();
        String emojiType = request.getEmojiType();

        // 이미 내가 같은 대상에 같은 이모지를 눌렀으면 -> 에러 or 무시
        reactionRepository.findByTargetTypeAndTargetIdAndUserAndEmojiType(
                        targetType, targetId, user, emojiType
                )
                .ifPresent(r -> {
                    throw new CustomException(ErrorStatus._BAD_REQUEST); // "이미 반응을 남겼습니다." 용 코드 나중에 추가해도 됨
                });

        Reaction reaction = Reaction.builder()
                .targetType(targetType)
                .targetId(targetId)
                .user(user)
                .emojiType(emojiType)
                .build();

        Reaction saved = reactionRepository.save(reaction);

        return ReactionResponseDto.builder()
                .reactionId(saved.getReactionId())
                .targetType(saved.getTargetType())
                .targetId(saved.getTargetId())
                .userId(saved.getUser().getId())
                .emojiType(saved.getEmojiType())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /**
     * 반응 취소
     * - 동일한 targetType, targetId, emojiType, user 조합을 찾아서 삭제
     */
    @Transactional
    public void removeReaction(Long userId, Long reactionId) {
        Reaction reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        if (!reaction.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorStatus._FORBIDDEN);
        }

        reactionRepository.delete(reaction);
    }
    /**
     * 대상에 달린 반응 조회
     */
    @Transactional(readOnly = true)
    public List<ReactionResponseDto> getReactions(String targetType, Long targetId) {
        List<Reaction> reactions = reactionRepository
                .findByTargetTypeAndTargetId(targetType.toUpperCase(), targetId);

        return reactions.stream()
                .map(r -> ReactionResponseDto.builder()
                        .reactionId(r.getReactionId())
                        .targetType(r.getTargetType())
                        .targetId(r.getTargetId())
                        .userId(r.getUser().getId())
                        .emojiType(r.getEmojiType())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
