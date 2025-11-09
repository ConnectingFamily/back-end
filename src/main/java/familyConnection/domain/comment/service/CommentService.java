package familyConnection.domain.comment.service;

import familyConnection.domain.comment.dto.CommentCreateRequestDto;
import familyConnection.domain.comment.dto.CommentResponseDto;
import familyConnection.domain.comment.dto.CommentUpdateRequestDto;
import familyConnection.domain.comment.entity.Comment;
import familyConnection.domain.comment.repository.CommentRepository;
import familyConnection.domain.question.entity.Answer;
import familyConnection.domain.question.repository.AnswerRepository;
import familyConnection.domain.user.entity.User;
import familyConnection.domain.user.repository.UserRepository;
import familyConnection.global.apiPayload.code.status.ErrorStatus;
import familyConnection.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;


    @Transactional
    public CommentResponseDto createComment(Long userId, CommentCreateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus._USER_NOT_FOUND));

        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new CustomException(ErrorStatus._NOT_FOUND));

        Comment comment = Comment.builder()
                .answer(answer)
                .user(user)
                .content(request.getContent())
                .isDeleted(false)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .commentId(saved.getCommentId())
                .answerId(saved.getAnswer().getDailyQuestion().getDailyQuestionId()) // or request.getAnswerId()
                .userId(saved.getUser().getId())
                .content(saved.getContent())
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

        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .answerId(comment.getAnswer().getDailyQuestion().getDailyQuestionId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
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
}
