package familyConnection.domain.comment.service;

import familyConnection.domain.comment.dto.CommentCreateRequestDto;
import familyConnection.domain.comment.dto.CommentResponseDto;
import familyConnection.domain.comment.dto.CommentUpdateRequestDto;
import familyConnection.domain.comment.entity.Comment;
import familyConnection.domain.comment.repository.CommentRepository;
import familyConnection.domain.question.entity.DailyQuestion;
import familyConnection.domain.question.repository.DailyQuestionRepository;
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
public class CommentService {

        private final CommentRepository commentRepository;
        private final DailyQuestionRepository dailyQuestionRepository;
        private final UserRepository userRepository;

        @Transactional
        public CommentResponseDto createComment(Long userId, CommentCreateRequestDto request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

                DailyQuestion dailyQuestion = dailyQuestionRepository.findById(request.getDailyQuestionId())
                                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

                Comment comment = Comment.builder()
                                .dailyQuestion(dailyQuestion)
                                .user(user)
                                .content(request.getContent())
                                .type(request.getType())
                                .isDeleted(false)
                                .build();

                Comment saved = commentRepository.save(comment);

                return CommentResponseDto.builder()
                                .commentId(saved.getCommentId())
                                .dailyQuestionId(saved.getDailyQuestion().getDailyQuestionId())
                                .userId(saved.getUser().getId())
                                .userNickname(saved.getUser().getNickname())
                                .content(saved.getContent())
                                .type(saved.getType())
                                .createdAt(saved.getCreatedAt())
                                .updatedAt(saved.getUpdatedAt())
                                .build();
        }

        @Transactional
        public CommentResponseDto updateComment(Long userId, Long commentId, CommentUpdateRequestDto request) {
                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

                // 작성자 체크
                if (!comment.getUser().getId().equals(userId)) {
                        throw new CustomException(ErrorStatus._FORBIDDEN);
                }

                comment.setContent(request.getContent());
                if (request.getType() != null) {
                        comment.setType(request.getType());
                }

                return CommentResponseDto.builder()
                                .commentId(comment.getCommentId())
                                .dailyQuestionId(comment.getDailyQuestion().getDailyQuestionId())
                                .userId(comment.getUser().getId())
                                .userNickname(comment.getUser().getNickname())
                                .content(comment.getContent())
                                .type(comment.getType())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build();
        }

        @Transactional
        public void deleteComment(Long userId, Long commentId) {
                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

                if (!comment.getUser().getId().equals(userId)) {
                        throw new CustomException(ErrorStatus._FORBIDDEN);
                }

                comment.setIsDeleted(true);
        }

        @Transactional(readOnly = true)
        public List<CommentResponseDto> getCommentsByDailyQuestionId(Long dailyQuestionId) {
                DailyQuestion dailyQuestion = dailyQuestionRepository.findById(dailyQuestionId)
                                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

                List<Comment> comments = commentRepository
                                .findByDailyQuestionAndIsDeletedFalseOrderByCreatedAtDesc(dailyQuestion);

                return comments.stream()
                                .map(comment -> CommentResponseDto.builder()
                                                .commentId(comment.getCommentId())
                                                .dailyQuestionId(comment.getDailyQuestion().getDailyQuestionId())
                                                .userId(comment.getUser().getId())
                                                .userNickname(comment.getUser().getNickname())
                                                .content(comment.getContent())
                                                .type(comment.getType())
                                                .createdAt(comment.getCreatedAt())
                                                .updatedAt(comment.getUpdatedAt())
                                                .build())
                                .collect(Collectors.toList());
        }
}
