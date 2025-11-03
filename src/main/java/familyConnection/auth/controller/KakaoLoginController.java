package familyConnection.auth.controller;

import familyConnection.auth.dto.KakaoLoginRequestDto;
import familyConnection.auth.dto.TokenDto;
import familyConnection.auth.service.KakaoLoginService;
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
    public ResponseEntity<TokenDto> kakaoLogin(@RequestBody KakaoLoginRequestDto loginRequest) {
        TokenDto tokenDto = kakaoLoginService.login(loginRequest.getAuthorizationCode());
        return ResponseEntity.ok(tokenDto);
    }
}
