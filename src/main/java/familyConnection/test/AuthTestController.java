/*package familyConnection.test;

import familyConnection.global.apiPayload.ApiResponse;
import familyConnection.global.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Tag(name = "Auth Test", description = "🧪 로컬/테스트 환경용 JWT 발급 API")
@RestController
@RequestMapping("/api/test/auth")
@RequiredArgsConstructor
public class AuthTestController {

    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "테스트용 JWT 발급",
            description = "원하는 userId로 JWT Access Token을 즉시 발급합니다. " +
                    "Kakao 로그인 등 실제 인증 과정을 거치지 않고 SecurityContext 테스트용으로만 사용해야 합니다."
    )
    @GetMapping("/token")
    public ResponseEntity<ApiResponse<String>> generateTestToken(
            @Parameter(description = "JWT로 가장할 사용자 ID", example = "1")
            @RequestParam("userId") Long userId
    ) {
        // claims는 비워둬도 괜찮음
        String token = jwtTokenProvider.createAccessToken(String.valueOf(userId), new HashMap<>());
        return ResponseEntity.ok(ApiResponse.onSuccess(token));
    }
}
*/