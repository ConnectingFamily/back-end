package familyConnection.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ConnectionFamily API")
                        .description("ConnectionFamily 백엔드 API 문서")
                        .version("v1.0.0")
                        .license(new License().name("MIT")))
                .externalDocs(new ExternalDocumentation().description("README"));
    }
}
