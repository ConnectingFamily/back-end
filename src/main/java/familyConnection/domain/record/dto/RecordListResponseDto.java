package familyConnection.domain.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecordListResponseDto {
    private List<RecordListItemDto> records;
    private long totalCount;
    private int page;
    private int limit;
}
