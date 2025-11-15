//package com.swulion.crossnote.client;
//
//import com.swulion.crossnote.config.ApiKeys;
//import com.swulion.crossnote.dto.Curation.CurationSourceDto;
//// 1. [수정] 올바른 DTO 클래스들을 import (Item, NationalLibRootDto)
//import com.swulion.crossnote.dto.Curation.Item;
//import com.swulion.crossnote.dto.Curation.NationalLibRootDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException; // 2. [추가] 파싱 예외 import
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Slf4j
//@Service("nationalLibClient")
//@RequiredArgsConstructor
//public class NationalLibClient implements CurationSourceClient {
//
//    private final RestTemplate restTemplate;
//    private final ApiKeys apiKeys;
//
//    private static final String NATIONAL_LIB_API_URL = "https://www.nl.go.kr/NL/search/openApi/search.do";
//
//    @Override
//    public CurationSourceDto fetchSource(String query) {
//
//        URI uri = UriComponentsBuilder.fromUriString(NATIONAL_LIB_API_URL)
//                .queryParam("key", apiKeys.getNationalLib())
//                .queryParam("kwd", query)
//                .queryParam("pageSize", 1)
//                .encode(StandardCharsets.UTF_8)
//                .build()
//                .toUri();
//
//        log.info("[NationalLibClient] API 호출 시도 (XML). URI: {}", uri);
//
//        try {
//            // 3. [수정] NationalLibRootDto.class로 XML을 바로 파싱 시도
//            ResponseEntity<NationalLibRootDto> response = restTemplate.getForEntity(
//                    uri, NationalLibRootDto.class
//            );
//
//            HttpStatusCode statusCode = response.getStatusCode();
//            NationalLibRootDto responseBody = response.getBody(); // 4. [수정] DTO 타입 변경
//
//            log.info("[NationalLibClient] 응답 수신. Status: {}, Body is null: {}",
//                    statusCode.value(), (responseBody == null));
//
//            // 5. [수정] XML 실제 경로(root -> result -> items)로 null 체크
//            if (statusCode.equals(HttpStatus.OK) &&
//                    responseBody != null &&
//                    responseBody.getResult() != null &&
//                    responseBody.getResult().getItems() != null) {
//
//                List<Item> items = responseBody.getResult().getItems(); // 6. [수정] DTO 타입 변경
//                if (items.isEmpty()) {
//                    log.warn("국립중앙도서관 API 응답에 <item>이 없습니다. (결과 0건) 쿼리: {}", query);
//                    return null;
//                }
//
//                Item item = items.get(0); // 7. [수정] DTO 타입 변경
//
//                // 8. [수정] 실제 XML에 있는 <title_info>를 사용
//                // (기존 <CONTENT_INFO>는 XML에 존재하지 않았습니다)
//                String originalText = item.getTitleInfo();
//
//                if (originalText == null || originalText.isBlank()) {
//                    log.warn("국립중앙도서관 API 응답에 title_info가 없습니다.");
//                    return null;
//                }
//
//                // 9. [수정] Item DTO의 detailLink 사용
//                String url = item.getDetailLink();
//                if (url != null && !url.startsWith("http")) {
//                    url = "https://www.nl.go.kr" + url;
//                }
//
//                log.info("[NationalLibClient] 소스 확보 성공: {}", originalText);
//
//                return CurationSourceDto.builder()
//                        .originalText(originalText)
//                        .sourceUrl(url)
//                        .imageUrl(null)
//                        .build();
//            }
//
//            // 10. [수정] 상세한 실패 로그 (경로 기준)
//            log.warn("국립중앙도서관 API 응답이 OK가 아니거나 result/items가 null입니다. (Status: {}, BodyNull: {}, ResultNull: {}, ItemsNull: {}) 쿼리: {}",
//                    statusCode.value(),
//                    (responseBody == null),
//                    (responseBody == null ? "N/A" : responseBody.getResult() == null),
//                    (responseBody == null || responseBody.getResult() == null ? "N/A" : responseBody.getResult().getItems() == null),
//                    query);
//            return null;
//
//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            log.error("국립중앙도서관 API 호출 실패 [{}]. 응답 본문: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
//            return null;
//
//            // 11. [추가] XML 파싱 실패 예외처리 (가장 중요!)
//        } catch (HttpMessageNotReadableException e) {
//            log.error("국립중앙도서관 API XML 파싱 실패. DTO와 XML 구조가 불일치합니다. (DTO: NationalLibRootDto.class)", e);
//            return null;
//
//        } catch (Exception e) {
//            log.error("국립중앙도서관 API 호출/파싱 실패 (기타 오류): {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//    @Override
//    public String getSourceType() {
//        return "DOCUMENT";
//    }
//}