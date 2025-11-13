package com.swulion.crossnote.client;

import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.dto.Curation.NaverNewsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverNewsClient {

    private final RestTemplate restTemplate;
    private final ApiKeys apiKeys;
    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

    // 특정 키워드(예: "IT")로 뉴스 기사 1개를 가져오는 메서드
    public NaverNewsResponseDto.Item fetchNews(String query) {
        // URI 생성
        URI uri = UriComponentsBuilder.fromUriString(NAVER_NEWS_API_URL)
                .queryParam("query", query) // 검색어
                .queryParam("display", 1)   // 1개만 가져오기
                .queryParam("sort", "sim")  // 정확도순
                .build()
                .toUri();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", apiKeys.getNaver().getClientId());
        headers.set("X-Naver-Client-Secret", apiKeys.getNaver().getClientSecret());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // JSON 응답 선호

        // HttpEntity에 헤더 담기
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // API 호출 (GET)
            ResponseEntity<NaverNewsResponseDto> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    NaverNewsResponseDto.class
            );

            // 결과 파싱
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<NaverNewsResponseDto.Item> items = response.getBody().getItems();
                return items.isEmpty() ? null : items.get(0);
            }
        } catch (Exception e) {
            //log.error("Naver News API 호출 실패: {}", e.getMessage());
        }
        return null;
    }
}