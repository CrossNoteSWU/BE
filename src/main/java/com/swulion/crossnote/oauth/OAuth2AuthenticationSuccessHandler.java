package com.swulion.crossnote.oauth;

import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.jwt.JwtTokenProvider;
import com.swulion.crossnote.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
- 동작 방식: 로그인 성공 후 프론트엔드 URL로 리다이렉트
- JWT를 URL 파라미터로 전달
- FE가 존재하고, 브라우저 리다이렉트 방식으로 OAuth2 토큰 전달할 때
*/
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 배포 도메인
    private static final String URL = "https://cross-note.com/social";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1. CustomOAuth2User에서 넘겨준 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String provider = (String) attributes.get("provider");

        Boolean isNewUserObj = (Boolean) attributes.get("isNewUser");
        boolean isNewuser = (isNewUserObj != null) && isNewUserObj;

        // 2. 이메일로 DB에서 유저를 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 DB에 없습니다. email=" + email));

        // 3. 서비스의 JWT Access Token을 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
        log.info("JWT Token 생성 완료: {}", accessToken, refreshToken);

        // 4. Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set(
                "RT:" + email, // Key
                refreshToken, // Value
                jwtTokenProvider.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );

        // 5. FE 요청사항: 리다이렉트 파라미터
        // 신규 회원: kakao-signup, google-signup
        // 기존 회원: login
        String type = isNewuser ? (provider + "-signup") : "login";

        // 6. URL 생성
        // 최종 URL 예시: https://cross-note.com/social?provider=kakao&type=login&accessToken...
        String targetUrl = UriComponentsBuilder.fromUriString(URL)
                .queryParam("provider", provider)
                .queryParam("type", type)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        log.info("Redirecting to {}", targetUrl);

        // 7. Redirect
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}