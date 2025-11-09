package familyConnection.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecordDetailResponseDto {
    private Long dailyQuestionId;
    private Integer questionNumber;
    private String questionText;
    private String counselingTechnique;
    private String description;
    private String exampleAnswer;
    private LocalDate assignedDate;

    private List<FamilyAnswerDto> answers;
}
