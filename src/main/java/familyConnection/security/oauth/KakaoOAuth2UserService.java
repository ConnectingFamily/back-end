// familyConnection/security/oauth/KakaoOAuth2UserService.java
package familyConnection.security.oauth;

import familyConnection.user.User;
import familyConnection.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static java.util.Collections.singleton;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String kakaoId = String.valueOf(attributes.get("id"));

        Map<String, Object> account = safeMap(attributes.get("kakao_account"));
        String email = account != null ? (String) account.get("email") : null;

        Map<String, Object> profile = account != null ? safeMap(account.get("profile")) : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;
        String profileImageUrl = profile != null ? (String) profile.get("profile_image_url") : null;

        userRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    // 존재하면 변경분만 반영
                    if (email != null) u.setEmail(email);
                    if (nickname != null) u.setNickname(nickname);
                    if (profileImageUrl != null) u.setProfileImageUrl(profileImageUrl);
                    u.setIsActive(true);
                    u.setLastLoginAt(LocalDateTime.now());
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .kakaoId(kakaoId)
                        .email(email)
                        .nickname(nickname)
                        .profileImageUrl(profileImageUrl)
                        .isActive(true)
                        .lastLoginAt(LocalDateTime.now())
                        .build()));

        // SecurityContext에 들어갈 OAuth2User
        return new DefaultOAuth2User(
                singleton(() -> "ROLE_USER"),
                attributes,
                "id"
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeMap(Object obj) {
        return (obj instanceof Map<?,?> m) ? (Map<String, Object>) m : null;
    }
}
