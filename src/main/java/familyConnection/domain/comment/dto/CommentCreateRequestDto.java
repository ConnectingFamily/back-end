package familyConnection.domain.comment.dto;

import familyConnection.domain.comment.entity.CommentType;
import lombok.Getter;

@Getter
public class CommentCreateRequestDto {
    private Long dailyQuestionId; // 어느 일일 질문에 다는지
    private String content;
    private CommentType type; // 댓글 타입 (TEXT, EMOJI)
}