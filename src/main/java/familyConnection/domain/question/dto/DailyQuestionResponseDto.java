package familyConnection.domain.question.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DailyQuestionResponseDto {
    private Long dailyQuestionId;
    private Long questionId;
    private Integer questionNumber;
    private String questionText;
    private String counselingTechnique;
    private String description;
    private String exampleAnswer;
    private Boolean isAllAnswered;
    private LocalDate assignedDate;
    private List<MemberAnswerStatusDto> members;
}