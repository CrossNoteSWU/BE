package com.swulion.crossnote.config;

import com.swulion.crossnote.oauth.CustomOAuth2User;
//import com.swulion.crossnote.oauth.OAuth2AuthenticationSuccessHandler;
import com.swulion.crossnote.oauth.OAuth2LoginSuccessHandler;
import com.swulion.crossnote.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/* Spring Security 설정 */
@Configuration
@EnableWebSecurity // Spring Security 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 인증 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomOAuth2User customOAuth2UserService;
    //private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler; // FE 연결용
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler; // BE 테스트용

    public static final String[] allowUrls = {
            "/health",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/login"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CorsConfig의 Bean을 주입받아 사용
    private final CorsConfigurationSource corsConfigurationSource;

    // 'SecurityFilterChain' 빈을 추가하여 HTTP 보안 설정을 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화 (csrf.disable() 전에 위치) - CorsConfig의 Bean 사용
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // CSRF 보호 기능 비활성화
                .csrf(csrf -> csrf.disable())

                // 세션 관리: STATELESS (JWT 사용 시 세션 미사용)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 기본 폼 로그인과 HTTP Basic 인증을 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // API 엔드포인트별 접근 권한을 설정
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS preflight 요청은 모두 허용 (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // /auth/** 경로는 모두 허용
                        .requestMatchers("/auth/**").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                // 소셜 로그인(OAuth2) 기능 활성화하고, 관련 서비스(CustomOAuth2User, OAuth2LoginSuccessHandler)를(을) 연결
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        //.successHandler(oAuth2AuthenticationSuccessHandler) // FE 테스트용
                        .successHandler(oAuth2LoginSuccessHandler) // BE 테스트용
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/auth/login") // 카카오/구글 로그인 시작 주소
                        )
                )
                // HTML 리다이렉트 대신 JSON 형태로 인증 실패 응답
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
                        })
                );

        // JWT 필터: Spring Security 필터 체인의 가장 앞단에 배치하여, 모든 요청을 토큰 검사부터 하도록 설정
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // [CORS 설정 Bean 추가] - SecurityConfig에서도 CORS 설정 유지 (CorsConfig와 공존)
    @Bean("securityCorsConfigurationSource")
    public CorsConfigurationSource securityCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 프론트 개발용 origin 허용 (dev: localhost:3000, prod: cross-note.com)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://cross-note.com"
        ));
        
        // 허용할 HTTP 메서드: GET, POST, PUT, PATCH, DELETE, OPTIONS
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // 허용할 헤더: 모든 헤더 허용 (Content-Type, Authorization 등 포함)
        config.setAllowedHeaders(List.of("*"));
        
        // 자격 증명 허용 (쿠키/Authorization 헤더 사용)
        config.setAllowCredentials(true);
        
        // preflight 요청의 캐시 시간 (초 단위)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
        return source;
    }
}
