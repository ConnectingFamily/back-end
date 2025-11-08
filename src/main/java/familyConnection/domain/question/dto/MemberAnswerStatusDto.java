package familyConnection.domain.question.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberAnswerStatusDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
    private Boolean answered;
    // 공개 가능한 경우에만 내려줄 답변
    private String answerContent;
}