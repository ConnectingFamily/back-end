package familyConnection.domain.family.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FamilyResponseDto {
  private Long familyId;
  private String familyName;
  private String inviteCode;
  private Long createdBy;
  private LocalDateTime createdAt;
}
