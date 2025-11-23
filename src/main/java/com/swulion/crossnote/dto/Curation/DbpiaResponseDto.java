package com.swulion.crossnote.dto.Curation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;


@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbpiaResponseDto {

    @JsonProperty("result")
    private ResultData result;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultData {

        @JsonProperty("item")
        private List<Item> items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("title")
        private String title;

        @JsonProperty("link_url")
        private String linkUrl;
    }
}