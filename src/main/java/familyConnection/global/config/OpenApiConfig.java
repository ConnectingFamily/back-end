package familyConnection.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                // 1) 전역으로 "이 API는 이 인증 방식을 쓴다" 선언
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                // 2) JWT Bearer 스키마 정의
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("ConnectionFamily API")
                        .description("ConnectionFamily 백엔드 API 문서")
                        .version("v1.0.0")
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation().description("README"));
    }
}
