package gr.aueb.cf.eduapp.core;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Explain how to authenticate (JWT? API Key? OAuth2?). Provides a way to
// input the token in Swagger UI by adding an "Authorize" button to Swagger UI.
// Auto-injects the Authorization: Bearer <token> header in requests.
@SecurityScheme(
        name = "Bearer Authentication",     // Must match @SecurityRequirement's name
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    // Provides information about your API that appears in Swagger UI/OpenAPI docs.
    // Follows OpenAPI specification requirements for API documentation
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EduApp API")
                        .version("1.0.0")
                        .description("Provides API for managing Coding Factory EDU")
                        .contact(new Contact()
                                .name("Admin")
                                .email("codingfactory@aueb.gr")
                                .url("https://codingfactory.aueb.gr"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}