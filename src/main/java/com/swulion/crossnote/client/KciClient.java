package com.swulion.crossnote.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.dto.Curation.CurationSourceDto;
import com.swulion.crossnote.client.CurationSourceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KciClient implements CurationSourceClient {

    private final RestTemplate restTemplate;
    private final ApiKeys apiKeys;

    private static final String KCI_API_URL = "https://open.kci.go.kr/po/openapi/openApiSearch.kci";

    @Override
    public CurationSourceDto fetchSource(String query) {
        try {
            URI uri = UriComponentsBuilder.fromUriString(KCI_API_URL)
                    .queryParam("key", apiKeys.getKci())
                    .queryParam("apiCode", "articleSearch")
                    .queryParam("title", query)
                    .queryParam("displayCount", 1)
                    .queryParam("sort", "match") // 정확도순 정렬
                    .encode()
                    .build()
                    .toUri();

            // 디버깅 용도입니다. 주석 해제 시 로그 多
            //log.info("[KciClient] 호출 URI: {}", uri);

            String xml = restTemplate.getForObject(uri, String.class);

            if (xml == null || xml.isBlank()) {
                log.warn("[KciClient] XML 응답 없음");
                return null;
            }

            //log.info("[KciClient] RAW XML:\n{}", xml);

            XmlMapper xmlMapper = new XmlMapper();
            KciXmlResponse response = xmlMapper.readValue(xml, KciXmlResponse.class);

            if (response.getOutputData() == null ||
                    response.getOutputData().getRecord() == null ||
                    response.getOutputData().getRecord().isEmpty()) {
                log.warn("[KciClient] record 없음");
                return null;
            }

            KciXmlResponse.Record record = response.getOutputData().getRecord().get(0);
            if (record.getArticleInfo() == null) {
                log.warn("[KciClient] article-info 없음");
                return null;
            }

            KciXmlResponse.ArticleInfo info = record.getArticleInfo();
            String title = extractTitle(info.getTitleGroup());
            String author = extractAuthor(info.getAuthorGroup());
            String url = info.getUrl();

            return CurationSourceDto.builder()
                    .title(title)
                    .originalText(title + (author != null ? ", " + author : ""))
                    .sourceUrl(url)
                    .imageUrl(null) // KCI 이미지 URL은 API에 없으므로 null 처리
                    .build();

        } catch (Exception e) {
            log.error("[KciClient] XML 파싱 실패", e);
            return null;
        }
    }

    private String extractTitle(KciXmlResponse.TitleGroup group) {
        if (group == null || group.getArticleTitle() == null) return null;
        return group.getArticleTitle().stream()
                .filter(t -> "original".equalsIgnoreCase(t.getLang()))
                .map(KciXmlResponse.LangTitle::getValue)
                .findFirst()
                .orElseGet(() ->
                        group.getArticleTitle().isEmpty() ? null :
                                group.getArticleTitle().get(0).getValue()
                );
    }

    private String extractAuthor(KciXmlResponse.AuthorGroup group) {
        if (group == null || group.getAuthor() == null || group.getAuthor().isEmpty()) return null;
        return group.getAuthor().get(0);
    }

    @Override
    public String getSourceType() {
        return "DOCUMENT";
    }

    // XML dto
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.ToString
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class KciXmlResponse {

        @JsonProperty("outputData")
        private OutputData outputData;

        @lombok.Getter
        @lombok.Setter
        @lombok.NoArgsConstructor
        @lombok.ToString
        @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
        public static class OutputData {

            // XML에서 <record> 태그가 리스트로 들어옴
            @JacksonXmlElementWrapper(useWrapping = false)
            @JsonProperty("record")
            private List<Record> record;
        }

        @lombok.Getter
        @lombok.Setter
        @lombok.NoArgsConstructor
        @lombok.ToString
        @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
        public static class Record {

            @JsonProperty("articleInfo")
            private ArticleInfo articleInfo;
        }

        @lombok.Getter
        @lombok.Setter
        @lombok.NoArgsConstructor
        @lombok.ToString
        @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
        public static class ArticleInfo {

            @JsonProperty("title-group") // 핵심: kebab-case 매핑
            private TitleGroup titleGroup;

            @JsonProperty("author-group") // 핵심: kebab-case 매핑
            private AuthorGroup authorGroup;

            @JsonProperty("url")
            private String url;
        }

        @lombok.Getter
        @lombok.Setter
        @lombok.NoArgsConstructor
        @lombok.ToString
        @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
        public static class TitleGroup {

            @JacksonXmlElementWrapper(useWrapping = false)
            @JsonProperty("article-title") // 핵심: kebab-case 매핑
            private List<LangTitle> articleTitle;
        }

        @lombok.Getter
        @lombok.Setter
        @lombok.NoArgsConstructor
        @lombok.ToString
        @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
        public static class LangTitle {

            @JacksonXmlProperty(isAttribute = true) // lang은 속성(Attribute)임
            private String lang;

            @JacksonXmlText // 태그 사이의 값(CDATA 등)을 가져옴
            private String value;
        }

        @lombok.Getter
        @lombok.Setter
        @lombok.NoArgsConstructor
        @lombok.ToString
        @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
        public static class AuthorGroup {

            @JacksonXmlElementWrapper(useWrapping = false)
            @JsonProperty("author")
            private List<String> author;
        }
    }
}
