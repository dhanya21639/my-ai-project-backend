package com.example.rag.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class AppConfig {
	@Bean
	WebMvcConfigurer cors(@Value("${cors.allowed-origin:http://localhost:5173}") String origin) {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry r) {
				r.addMapping("/api/**").allowedOrigins(origin).allowedMethods("GET", "POST", "OPTIONS")
						.allowedHeaders("*");
			}
		};
	}
}
