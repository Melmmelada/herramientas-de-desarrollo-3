package cl.usm.sansaweigh.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//configura el título y descripción que aparece en la interfaz de Swagger.
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SansaWeigh API")
                        .version("1.0.0")
                        .description("Microservicio de gestión de pesaje de paquetes"));
    }
}