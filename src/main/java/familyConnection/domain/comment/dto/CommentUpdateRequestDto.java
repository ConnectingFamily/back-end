package familyConnection.domain.comment.dto;

import familyConnection.domain.comment.entity.CommentType;
import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {
    private String content;
    private CommentType type;
}