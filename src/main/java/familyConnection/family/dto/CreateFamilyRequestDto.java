package familyConnection.family.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateFamilyRequestDto {
  @NotBlank(message = "가족 이름은 필수입니다.")
  @Size(max = 10, message = "가족 이름은 최대 10자까지 입력 가능합니다.")
  private String familyName;
}
