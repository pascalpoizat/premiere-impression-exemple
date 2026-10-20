package fr.miage.premiereimpression.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Le front est servi depuis une autre origine : il faut l'autoriser explicitement à appeler l'API. */
@Configuration
class CorsConfiguration implements WebMvcConfigurer {

    private final String[] originesAutorisees;

    CorsConfiguration(@Value("${app.cors.origines-autorisees}") String[] originesAutorisees) {
        this.originesAutorisees = originesAutorisees;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(originesAutorisees)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
