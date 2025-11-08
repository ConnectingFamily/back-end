package familyConnection.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
    private final Boolean isNewUser; // 신규 유저 여부 (온보딩 필요)

    @Builder
    public TokenDto(String accessToken, String refreshToken, Boolean isNewUser) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewUser = isNewUser;
    }
}
