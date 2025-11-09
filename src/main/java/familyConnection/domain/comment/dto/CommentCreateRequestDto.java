package familyConnection.domain.comment.dto;


import lombok.Getter;

@Getter
public class CommentCreateRequestDto {
    private Long answerId;   // 어느 답변에 다는지
    private String content;
}