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
public class NlBookResponseDto {

    @JsonProperty("titleInfo")
    private String titleInfo;

    @JsonProperty("authorInfo")
    private String author;

    @JsonProperty("pubInfo")
    private String publisher;

    @JsonProperty("pubYearInfo")
    private String pubYear;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("detailLink")
    private String detailLink;

    @JsonProperty("imageUrl")
    private String imageUrl;
}
