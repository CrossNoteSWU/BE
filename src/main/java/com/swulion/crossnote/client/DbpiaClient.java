package com.swulion.crossnote.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.dto.Curation.CurationSourceDto;
import com.swulion.crossnote.dto.Curation.DbpiaResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service("dbpiaClient")
@RequiredArgsConstructor
public class DbpiaClient implements CurationSourceClient {

    private final RestTemplate restTemplate;
    private final ApiKeys apiKeys;
    private final ObjectMapper objectMapper;

    private static final String DBPIA_API_URL = "https://api.dbpia.co.kr/v2/search/search.json";

    @Override
    public CurationSourceDto fetchSource(String query) {

        log.info("[DbpiaClient] API 호출 시도. 쿼리: {}", query);
        URI uri = UriComponentsBuilder.fromUriString(DBPIA_API_URL)
                .queryParam("key", apiKeys.getDbpia())
                .queryParam("target", "se")
                .queryParam("searchall", query)
                .queryParam("output", "json")
                .queryParam("pagecount", 1)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            String responseBody = response.getBody();
            MediaType contentType = response.getHeaders().getContentType();
            HttpStatusCode statusCode = response.getStatusCode();

            log.info("[DbpiaClient] 응답 수신. Status: {}, Content-Type: {}, Body is null: {}",
                    statusCode.value(), contentType, (responseBody == null));

            log.info("[DbpiaClient] RAW JSON Response Body: {}", responseBody);

            if (statusCode.equals(HttpStatus.OK) && responseBody != null) {
                if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {

                    // JSON (String)을 List<DbpiaResponseDto>로 파싱
                    List<DbpiaResponseDto> responseList = objectMapper.readValue(
                            responseBody,
                            new TypeReference<List<DbpiaResponseDto>>() {}
                    );

                    // 파싱 성공 및 데이터 유효성 검사
                    if (responseList != null && !responseList.isEmpty() &&
                            responseList.get(0).getResult() != null &&
                            responseList.get(0).getResult().getItems() != null &&
                            !responseList.get(0).getResult().getItems().isEmpty())
                    {
                        // DTO 구조를 따라 올바른 Item 객체를 가져옴
                        DbpiaResponseDto.Item item = responseList.get(0).getResult().getItems().get(0);
                        String originalText = item.getTitle();

                        // title이 null이 아니고, HTML 태그를 제거
                        if (originalText != null && !originalText.isBlank()) {

                            originalText = originalText.replaceAll("<!HS>", "").replaceAll("<!HE>", "");

                            log.info("[DbpiaClient] 소스 확보 성공: {}", originalText);

                            return CurationSourceDto.builder()
                                    .originalText(originalText)
                                    .sourceUrl(item.getLinkUrl())
                                    .imageUrl(null)
                                    .build();
                        }
                    }

                    // title이 null이거나 리스트가 비어있을 경우
                    log.warn("DBpia API 응답에 title이 없거나 item 리스트가 비어있습니다. (쿼리: {})", query);
                    return null;

                } else {
                    log.error("[DbpiaClient] API가 200 OK를 반환했지만 JSON이 아닌 응답을 보냈습니다. Content-Type: {}. 응답 본문: {}", contentType, responseBody);
                    return null;
                }
            }

            log.warn("DBpia API 응답이 OK가 아니거나 본문이 비어있습니다. (Status: {}, Body is null: {}) 쿼리: {}",
                    statusCode.value(), (responseBody == null), query);
            return null;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("[DbpiaClient] API 호출 실패 [{}]. 응답 본문: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return null;
        } catch (Exception e) {
            log.error("[DbpiaClient] API 호출/파싱 실패 (기타 오류): {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getSourceType() {
        return "PAPER"; // (논문)
    }
}