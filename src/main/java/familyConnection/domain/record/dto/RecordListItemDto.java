package familyConnection.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecordListItemDto {
    private Long dailyQuestionId;
    private Integer questionNumber;
    private String questionText;
    private LocalDate assignedDate;
    private String myAnswer;
    private LocalDateTime submittedAt;
}