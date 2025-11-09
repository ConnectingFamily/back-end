package familyConnection.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MonthlyRecordResponseDto {
    private Long familyId;
    private int year;
    private int month;
    private List<LocalDate> answeredDates;  // 달력에 찍을 날짜들
    private int totalAnswered;              // 그 달에 내가 답한 개수
    private int continuousDays;             // 그 달에서의 최대 연속 답변 일수
}
