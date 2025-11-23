package com.swulion.crossnote.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 모든 origin 허용
        config.setAllowedOriginPatterns(List.of("*"));
        // 모든 HTTP 메서드 허용
        config.setAllowedMethods(List.of("*"));
        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));
        // 모든 origin을 허용할 때는 credentials를 false로 설정해야 함
        config.setAllowCredentials(false);
        // preflight 요청 캐시 시간
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
