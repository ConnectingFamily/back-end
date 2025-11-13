package familyConnection.domain.family.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinFamilyResponseDto {
  private Long familyId;
  private String familyName;
  private String inviteCode;
  private Long memberId;
  private String role;
  private LocalDateTime joinedAt;
}
