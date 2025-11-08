package familyConnection.domain.family.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FamilySearchResponseDto {
  private Long familyId;
  private String familyName;
  private String inviteCode;
  private Integer memberCount;
  private List<MemberDto> members;
}
