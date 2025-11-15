package com.swulion.crossnote.dto.Curation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DBpia API의 실제 JSON 응답 구조에 맞게 수정된 DTO
 * [ { "result": { "item": [ ... ] } } ]
 */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
// 이 DTO는 루트 배열 [ ] 안의 첫 번째 { } 객체에 매핑됩니다.
public class DbpiaResponseDto {

    @JsonProperty("result") // 1. "result" 객체를 가져옵니다.
    private ResultData result;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultData {

        @JsonProperty("item") // 2. "result" 객체 안의 "item" 배열을 가져옵니다.
        private List<Item> items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("title") // 3. "item" 배열 안의 "title"을 가져옵니다.
        private String title;

        @JsonProperty("link_url") // 4. "item" 배열 안의 "link_url"을 가져옵니다.
        private String linkUrl;
    }
}