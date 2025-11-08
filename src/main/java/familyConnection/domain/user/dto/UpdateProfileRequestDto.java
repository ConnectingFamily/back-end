package familyConnection.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequestDto {
  @NotBlank(message = "닉네임은 필수입니다.")
  @Size(max = 5, message = "닉네임은 최대 5자까지 입력 가능합니다.")
  private String nickname;

  private String profileImageUrl; // 선택사항
}
