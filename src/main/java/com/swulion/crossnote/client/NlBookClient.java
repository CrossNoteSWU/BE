package com.swulion.crossnote.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swulion.crossnote.dto.Curation.CurationSourceDto;
import com.swulion.crossnote.dto.Curation.NlBookResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NlBookClient implements CurationSourceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.url.national-lib}")
    private String apiUrl;
    @Value("${api.key.national-lib}")
    private String apiKey;

    private static final String IMAGE_BASE_DOMAIN = "https://cover.nl.go.kr/";
    private static final String NO_IMAGE_URL = "https://www.nl.go.kr/contents/images/search/noimage/noimage_NL1.gif";

    // CurationSourceClient 구현: 특정 키워드로 책 1건 가져오기
    @Override
    public CurationSourceDto fetchSource(String query) {
        List<NlBookResponseDto> books = searchBooks(query);

        if (!books.isEmpty()) {
            NlBookResponseDto dto = books.get(0);

            // 이미지 URL 처리
            String rawImage = dto.getImageUrl();
            String fullImageUrl = (rawImage == null || rawImage.isBlank()) ? NO_IMAGE_URL : IMAGE_BASE_DOMAIN + rawImage;

            // detailLink 전체 URL 처리
            String sourceUrl = "https://www.nl.go.kr" + dto.getDetailLink();

            return CurationSourceDto.builder()
                    .title(dto.getTitleInfo())
                    .originalText(dto.getTitleInfo() + ". " + dto.getAuthor() + ". " + dto.getPublisher())
                    .sourceUrl(sourceUrl)
                    .imageUrl(fullImageUrl)
                    .build();
        }
        return null;
    }

    @Override
    public String getSourceType() {
        return "NL_BOOK";
    }

    public List<NlBookResponseDto> searchBooks(String keyword) {
        log.info("국립중앙도서관 도서 검색 요청: {}", keyword);

        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("key", apiKey)
                    .queryParam("kwd", keyword)
                    .queryParam("srchTarget", "total")
                    .queryParam("apiType", "json")
                    .queryParam("category", "도서")
                    .queryParam("pageNum", 1)
                    .queryParam("pageSize", 10)
                    .build()
                    .toUri();

            log.info("생성된 요청 URI: {}", uri);

            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            Map<String, Object> body = response.getBody();

            if (body == null || !body.containsKey("result")) {
                return new ArrayList<>();
            }

            Object resultObj = body.get("result");
            if (resultObj == null) return new ArrayList<>();

            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultObj;
            List<NlBookResponseDto> books = new ArrayList<>();

            for (Map<String, Object> item : resultList) {
                NlBookResponseDto dto = objectMapper.convertValue(item, NlBookResponseDto.class);
                books.add(dto);
            }
            return books;

        } catch (Exception e) {
            log.error("국립중앙도서관 API 호출 실패", e);
            return new ArrayList<>();
        }
    }
}
