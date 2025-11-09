package familyConnection.domain.aianswerhelper.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnswerDraftResponseDto {
    private String originalAnswer;  // 내가 보낸 원본
    private String improvedAnswer;  // AI가 다듬어준 답변
    private String emotion;         // "기쁜", "감사한" 등
    private List<String> feedback;  // 3줄 피드백
}
