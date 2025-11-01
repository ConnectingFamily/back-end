package familyConnection.config;

import familyConnection.security.jwt.JwtAuthenticationFilter;
import familyConnection.security.jwt.JwtTokenProvider;

import familyConnection.security.oauth.KakaoOAuth2UserService;
import familyConnection.security.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.io.PrintWriter;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoOAuth2UserService kakaoOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    // 필요 시 주입해서 사용
    // private final KakaoOAuth2UserService kakaoOAuth2UserService;
    // private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private static final String[] SWAGGER = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };
    private static final String[] PUBLIC = {
            "/", "/actuator/health", "/auth/**", "/oauth2/**", "/login/**"
    };
    private static final String[] STATIC = {
            "/favicon.ico", "/assets/**", "/css/**", "/js/**", "/images/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // REST API 기본 세팅
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(lo -> lo.disable())
                .requestCache(cache -> cache.disable())

                // 권한 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER).permitAll()
                        .requestMatchers(PUBLIC).permitAll()
                        .requestMatchers(STATIC).permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .anyRequest().authenticated()
                )

                // 인증/인가 실패 시 JSON으로 응답
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, ex1) -> {
                            res.setStatus(HttpStatus.UNAUTHORIZED.value());
                            res.setContentType("application/json;charset=UTF-8");
                            try (PrintWriter w = res.getWriter()) {
                                w.write("{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\"}");
                            }
                        })
                        .accessDeniedHandler((req, res, ex2) -> {
                            res.setStatus(HttpStatus.FORBIDDEN.value());
                            res.setContentType("application/json;charset=UTF-8");
                            try (PrintWriter w = res.getWriter()) {
                                w.write("{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\"}");
                            }
                        })
                )

                // OAuth2 (카카오) – 서비스/핸들러 구현했으면 주석 해제
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(kakaoOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .defaultSuccessUrl("/login/success", true)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 명시 (프론트 도메인에 맞게 수정)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);
        c.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }

    // 비번 사용 시 대비
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
