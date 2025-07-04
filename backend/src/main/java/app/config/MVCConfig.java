package app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class MVCConfig implements WebMvcConfigurer {
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Value("${upload.url.prefix}")
    private String urlPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(urlPrefix + "**") // /media/**
                .addResourceLocations("file:" + uploadDirectory); // file path on server
    }
}