//package com.swulion.crossnote.client;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.swulion.crossnote.config.ApiKeys;
//import com.swulion.crossnote.dto.Curation.CurationSourceDto;
//import com.swulion.crossnote.dto.Curation.KciResponseDto;
//import com.swulion.crossnote.client.CurationSourceClient;
//import com.swulion.crossnote.config.JsonConfig;
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class KciClient implements CurationSourceClient {
//
//    private final RestTemplate restTemplate;
//    private final ApiKeys apiKeys;
//    private final ObjectMapper objectMapper;
//
//    private static final String KCI_API_URL = "https://open.kci.go.kr/po/openapi/openApiSearch.kci";
//    // URL 예시
//    // https://open.kci.go.kr/po/openapi/openApiSearch.kci?apiCode=articleSearch&key=인증키&title=검색키워드,
//
//    @Override
//    public CurationSourceDto fetchSource(String query) {
//        try {
//            // URI 안전하게 생성 (한글 포함)
//            URI uri = UriComponentsBuilder.fromUriString(KCI_API_URL)
//                    .queryParam("key", apiKeys.getKci())
//                    .queryParam("apiCode", "articleSearch")
//                    .queryParam("title", query) // 한글 그대로
//                    .queryParam("displayCount", 1)
//                    .encode() // UTF-8로 안전하게 인코딩
//                    .build()
//                    .toUri();
//
//            log.info("[KciClient] API 호출 시도. URI: {}", uri);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setAccept(List.of(MediaType.APPLICATION_XML));
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(
//                    uri, HttpMethod.GET, entity, String.class
//            );
//
//            log.info("RAW Response Body:\n{}", response.getBody());
//
//            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || response.getBody().isBlank()) {
//                log.warn("[KciClient] API 응답이 없거나 오류 발생. Status: {}", response.getStatusCode().value());
//                return null;
//            }
//
//            log.info("[KciClient] 소스 확보 성공: {}", title);
//
//            return CurationSourceDto.builder()
//                    .originalText(abstractText)
//                    .sourceUrl(article.getUrl())
//                    .imageUrl(null)
//                    .build();
//
//        } catch (Exception e) {
//            log.error("[KciClient] API 호출/파싱 실패: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//    private String findOriginalText(List<KciResponseDto.LangText> list) {
//        if (list == null || list.isEmpty()) return null;
//        return list.stream()
//                .filter(lt -> "original".equals(lt.getLang()))
//                .map(KciResponseDto.LangText::getValue)
//                .findFirst()
//                .orElse(list.get(0).getValue());
//    }
//
//    @Override
//    public String getSourceType() {
//        return "DOCUMENT";
//    }
//}
