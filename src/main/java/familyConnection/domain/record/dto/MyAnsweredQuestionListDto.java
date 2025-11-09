package familyConnection.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyAnsweredQuestionListDto {
    private List<MyAnsweredQuestionDto> records;
    private long totalCount;
    private int page;
    private int limit;
}
