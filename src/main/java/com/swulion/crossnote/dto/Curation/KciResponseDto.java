package com.swulion.crossnote.dto.Curation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // 정의하지 않은 필드 무시
public class KciResponseDto {

    @JsonProperty("record")
    private List<Record> record;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Record {

        @JsonProperty("journalInfo")
        private JournalInfo journalInfo;

        @JsonProperty("articleInfo")
        private ArticleInfo articleInfo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JournalInfo {
        @JsonProperty("journal-name")
        private String journalName;

        @JsonProperty("publisher-name")
        private String publisherName;

        @JsonProperty("pub-year")
        private String pubYear;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArticleInfo {

        @JsonProperty("title-group")
        private TitleGroup titleGroup;

        @JsonProperty("author-group")
        private AuthorGroup authorGroup;

        @JsonProperty("url")
        private String url;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TitleGroup {
        @JsonProperty("article-title")
        private List<LangTitle> articleTitle;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LangTitle {
        @JsonProperty("lang")
        private String lang;

        @JsonProperty("")
        private String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorGroup {
        @JsonProperty("author")
        private List<String> author;
    }
}
