package familyConnection.domain.comment.dto;

import familyConnection.domain.comment.entity.CommentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private Long dailyQuestionId;
    private Long userId;
    private String userNickname;
    private String content;
    private CommentType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
