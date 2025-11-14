package com.swulion.crossnote.client;

import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.dto.Curation.NaverNewsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverNewsClient {

    private final RestTemplate restTemplate;
    private final ApiKeys apiKeys;
    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

    // (Mock 로직을 실제 API 호출로 수정)
    public NaverNewsResponseDto.Item fetchNews(String query) {

        log.info("[NAVER API KEY CHECK] ClientId={}, ClientSecret={}",
                apiKeys.getNaver().getClientId(),
                apiKeys.getNaver().getClientSecret());

        // 1. URI 생성 (UTF-8 인코딩)
        URI uri = UriComponentsBuilder.fromUriString(NAVER_NEWS_API_URL)
                .queryParam("query", query)
                .queryParam("display", 1)   // 1개만 가져오기
                .queryParam("sort", "sim")  // 정확도순
                .encode(StandardCharsets.UTF_8) // (한글 쿼리 인코딩)
                .build()
                .toUri();

        // 2. HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", apiKeys.getNaver().getClientId());
        headers.set("X-Naver-Client-Secret", apiKeys.getNaver().getClientSecret());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 3. HttpEntity에 헤더 담기
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 4. API 호출
            ResponseEntity<NaverNewsResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    NaverNewsResponseDto.class
            );

            // 5. 결과 파싱
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<NaverNewsResponseDto.Item> items = response.getBody().getItems();
                if (!items.isEmpty()) {
                    return items.get(0);
                }
            }
            log.warn("Naver News API 응답에 아이템이 없습니다. 쿼리: {}", query);
            return null;

        } catch (Exception e) {
            log.error("Naver News API 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }
}