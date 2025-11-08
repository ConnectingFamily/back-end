package familyConnection.domain.user.controller;

import familyConnection.domain.user.dto.UpdateProfileRequestDto;
import familyConnection.domain.user.dto.UserProfileResponseDto;
import familyConnection.domain.user.service.UserService;
import familyConnection.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PatchMapping("/profile")
  public ResponseEntity<ApiResponse<UserProfileResponseDto>> updateProfile(
      @Valid @RequestBody UpdateProfileRequestDto request) {

    // JWT에서 사용자 ID 추출
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());

    // 프로필 업데이트
    UserProfileResponseDto profileResponse = userService.updateProfile(userId, request);

    return ResponseEntity.ok(ApiResponse.onSuccess(profileResponse));
  }
}
