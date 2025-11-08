package familyConnection.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
    private final Boolean isNewUser; // 신규 유저 여부 (온보딩 필요)
    private final Boolean hasFamily; // 가족방 소속 여부

    @Builder
    public TokenDto(String accessToken, String refreshToken, Boolean isNewUser, Boolean hasFamily) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewUser = isNewUser;
        this.hasFamily = hasFamily;
    }
}
