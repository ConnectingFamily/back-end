package familyConnection.domain.auth.controller;

import familyConnection.domain.auth.dto.KakaoLoginRequestDto;
import familyConnection.domain.auth.dto.TokenDto;
import familyConnection.domain.auth.service.KakaoLoginService;
import familyConnection.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class KakaoLoginController {

  private final KakaoLoginService kakaoLoginService;

  @PostMapping("/kakao/login")
  public ResponseEntity<ApiResponse<TokenDto>> kakaoLogin(@RequestBody KakaoLoginRequestDto loginRequest) {
    TokenDto tokenDto = kakaoLoginService.login(loginRequest.getAuthorizationCode());
    return ResponseEntity.ok(ApiResponse.onSuccess(tokenDto));
  }
}
