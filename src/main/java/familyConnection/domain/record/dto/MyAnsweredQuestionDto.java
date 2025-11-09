package familyConnection.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MyAnsweredQuestionDto {
    private Long dailyQuestionId;     // 상세 보러 갈 때 필요
    private Integer questionNumber;   // Q9 이런 거
    private String questionText;      // 질문 제목
    private LocalDate assignedDate;   // 2025-09-28 이런 거
    private String answerPreview;     // 내 답변 앞부분
}
