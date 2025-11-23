package com.swulion.crossnote.dto.MyPage;

import com.swulion.crossnote.entity.Curation.CurationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScrappedCurationDto {
    private Long scrapId;
    private Long curationId;
    private CurationType curationType; // INSIGHT, CROSSNOTE, BEST_COLUMN
    private String field; // 29개 세부 분야 중 1개
    private String title;
    private String imageUrl;
    private String description;
}

