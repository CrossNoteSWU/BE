package com.swulion.crossnote.dto.Curation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // 정의하지 않은 필드 무시
public class KciResponseDto {
    @JsonProperty("article-name")
    private String title; // 논문 제목

    @JsonProperty("author")
    private String author; // 논문 저자

    @JsonProperty("pub-year")
    private String pubYear; // 발행연도

    @JsonProperty("url")
    private String url; // 논문 url

    @JsonProperty("imageUrl")
    private String imageUrl;
}
