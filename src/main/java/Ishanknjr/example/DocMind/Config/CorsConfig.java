package Ishanknjr.example.DocMind.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {
    private final AppPropertiesConfig appProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = appProperties.getCors().getAllowedOrigins().split(",");
       String[] allowedMethods =  appProperties.getCors().getAllowedMethods().split(" ,");
       String[] allowdheaders = appProperties.getCors().getAllowedHeaders().split(" ,");

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowdheaders)
                .allowCredentials(true);
    }
}
