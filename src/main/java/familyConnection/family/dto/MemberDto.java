package familyConnection.family.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
  private Long userId;
  private String nickname;
  private String profileImageUrl;
}
