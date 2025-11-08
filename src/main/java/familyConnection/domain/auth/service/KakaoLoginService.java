package familyConnection.domain.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import familyConnection.domain.auth.dto.TokenDto;
import familyConnection.domain.family.repository.FamilyMemberRepository;
import familyConnection.global.security.jwt.JwtTokenProvider;
import familyConnection.domain.user.controller.UserRepository;
import familyConnection.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final FamilyMemberRepository familyMemberRepository;

  // RestClient는 WebClient와 달리 webflux 필요 X
  private final RestClient restClient = RestClient.create();

  @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
  private String clientSecret;

  @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String redirectUri;

  @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
  private String tokenUri;

  @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
  private String userInfoUri;

  @Transactional
  public TokenDto login(String authorizationCode) {
    // 1) 토큰 발급
    KakaoTokenResponse kakaoToken = getKakaoToken(authorizationCode);

    // 2) 사용자 정보 조회
    KakaoUserInfoResponse userInfo = getKakaoUserInfo(kakaoToken.getAccessToken());

    // 3) 신규 유저 여부 판단 (upsert 전에 조회)
    String kakaoId = String.valueOf(userInfo.getId());
    boolean isNewUser = userRepository.findByKakaoId(kakaoId).isEmpty();

    // 4) upsert
    User user = upsertUser(userInfo);

    // 5) 가족방 소속 여부 확인 (FamilyMember와 Family 모두 활성화된 경우만)
    boolean hasFamily = familyMemberRepository.existsByUserAndMemberAndFamilyActive(user);

    // 6) JWT 발급
    String accessToken = jwtTokenProvider.createAccessToken(
        String.valueOf(user.getId()),
        Map.of("provider", "kakao"));
    String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(user.getId()));

    return TokenDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .isNewUser(isNewUser)
        .hasFamily(hasFamily)
        .build();
  }

  private KakaoTokenResponse getKakaoToken(String authorizationCode) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "authorization_code");
    formData.add("client_id", clientId);
    formData.add("redirect_uri", redirectUri);
    formData.add("code", authorizationCode);
    formData.add("client_secret", clientSecret);

    return restClient.post()
        .uri(tokenUri)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(formData) // FormHttpMessageConverter가 자동 인코딩
        .retrieve()
        .body(KakaoTokenResponse.class);
  }

  private KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
    return restClient.get()
        .uri(userInfoUri)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .retrieve()
        .body(KakaoUserInfoResponse.class);
  }

  private User upsertUser(KakaoUserInfoResponse userInfo) {
    String kakaoId = String.valueOf(userInfo.getId());
    String email = userInfo.getKakaoAccount() != null ? userInfo.getKakaoAccount().getEmail() : null;
    String nickname = (userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null)
        ? userInfo.getKakaoAccount().getProfile().getNickname()
        : null;
    String profileImageUrl = (userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getProfile() != null)
        ? userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
        : null;

    return userRepository.findByKakaoId(kakaoId)
        .map(user -> {
          user.setLastLoginAt(LocalDateTime.now());
          user.setIsActive(true);
          // 필요시 닉네임/프로필 갱신
          if (nickname != null)
            user.setNickname(nickname);
          if (profileImageUrl != null)
            user.setProfileImageUrl(profileImageUrl);
          if (email != null)
            user.setEmail(email);
          return user;
        })
        .orElseGet(() -> {
          User newUser = User.builder()
              .kakaoId(kakaoId)
              .email(email) // 동의 안 하면 null일 수 있음
              .nickname(nickname)
              .profileImageUrl(profileImageUrl)
              .isActive(true)
              .lastLoginAt(LocalDateTime.now())
              .build();
          return userRepository.save(newUser);
        });
  }

  // ===== Kakao DTOs =====
  @Getter
  private static class KakaoTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("refresh_token_expires_in")
    private int refreshTokenExpiresIn;
  }

  @Getter
  private static class KakaoUserInfoResponse {
    private Long id;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    public static class KakaoAccount {
      private String email;
      private Profile profile;

      @Getter
      public static class Profile {
        private String nickname;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
      }
    }
  }
}
