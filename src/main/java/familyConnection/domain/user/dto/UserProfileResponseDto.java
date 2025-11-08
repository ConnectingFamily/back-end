package familyConnection.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserProfileResponseDto {
  private Long userId;
  private String nickname;
  private String profileImageUrl;
  private LocalDateTime updatedAt;
}
