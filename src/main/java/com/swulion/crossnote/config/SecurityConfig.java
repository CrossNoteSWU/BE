package com.swulion.crossnote.config;

import com.swulion.crossnote.jwt.JwtAuthenticationFilter;
import com.swulion.crossnote.oauth.CustomOAuth2User;
import com.swulion.crossnote.oauth.OAuth2AuthenticationSuccessHandler;
import com.swulion.crossnote.oauth.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2User customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final Optional<CorsConfigurationSource> corsConfigurationSource;
    private final Environment environment;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         CustomOAuth2User customOAuth2UserService,
                         OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                         OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                         @Autowired(required = false) @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfigurationSource,
                         Environment environment) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.corsConfigurationSource = Optional.ofNullable(corsConfigurationSource);
        this.environment = environment;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 프로덕션 환경에서는 CORS 비활성화 (Nginx에서 처리)
        boolean isProd = environment != null && 
                         java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod");
        
        http
                .csrf(AbstractHttpConfigurer::disable);
        
        // 프로덕션 환경이 아니면 CORS 활성화 (개발 환경용)
        if (!isProd && corsConfigurationSource.isPresent()) {
            http.cors(cors -> cors.configurationSource(corsConfigurationSource.get()));
        } else {
            // 프로덕션 환경에서는 CORS 비활성화 (Nginx에서 처리)
            http.cors(AbstractHttpConfigurer::disable);
        }
        
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/").permitAll() // 루트 경로 허용
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/curation").permitAll() // 큐레이션 목록 조회는 허용
                        .requestMatchers("/curation/**").authenticated() // 그 외 큐레이션은 인증 필요
                        .requestMatchers(HttpMethod.GET,"/notification/subscribe").permitAll() // SSE(EventSource) 연결 인증 없이 허용 (Authorization 헤더 전달 불가)
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .authorizationEndpoint(auth -> auth.baseUri("/auth/login"))
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
                        })
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}