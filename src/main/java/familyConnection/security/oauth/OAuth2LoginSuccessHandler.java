// familyConnection/security/oauth/OAuth2LoginSuccessHandler.java
package familyConnection.security.oauth;

import familyConnection.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import familyConnection.user.User;
import familyConnection.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String kakaoId = String.valueOf(principal.getAttribute("id"));

        User user = userRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    u.setIsActive(true);
                    u.setLastLoginAt(LocalDateTime.now());
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new IllegalStateException("카카오 사용자 동기화 실패"));

        // JWT subject = 내부 user_id
        String access = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()), Map.of("provider", "kakao"));
        String refresh = jwtTokenProvider.createRefreshToken(String.valueOf(user.getId()));

        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of(
                "accessToken", access,
                "refreshToken", refresh,
                "user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "nickname", user.getNickname(),
                        "profileImageUrl", user.getProfileImageUrl()
                )
        ));
    }
}
