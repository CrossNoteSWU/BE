package com.swulion.crossnote.dto.Curation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class NaverNewsResponseDto {
    private List<Item> items;

    @Getter
    @NoArgsConstructor
    public static class Item {
        private String title;
        private String originallink; // 원본 링크
        private String link; // 네이버 뉴스 링크
        private String description;
        private String pubDate;
    }
}