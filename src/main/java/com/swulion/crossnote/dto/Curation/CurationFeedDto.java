package com.swulion.crossnote.dto.Curation;

import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.entity.Curation.CurationType;
import lombok.Getter;

@Getter
public class CurationFeedDto {
    private Long curationId;
    private String title;
    private String description;
    private String sourceUrl;
    private String imageUrl;
    private CurationType curationType;
    private CurationLevel curationLevel;
    private String categoryName;
    private String crossCategoryName; // 크로스노트일 경우

    // 베스트 칼럼 여부
    private boolean isBestColumn;

    public CurationFeedDto(Curation curation) {
        this.curationId = curation.getId();
        this.title = curation.getTitle();
        this.description = curation.getDescription();
        this.sourceUrl = curation.getSourceUrl();
        this.imageUrl = curation.getImageUrl();
        this.curationType = curation.getCurationType();
        this.curationLevel = curation.getCurationLevel();

        if(curation.getCategory() != null){
            this.categoryName = curation.getCategory().getCategoryName();
        }
        if(curation.getCrossCategory() != null){
            this.crossCategoryName = curation.getCrossCategory().getCategoryName();
        }

        // CurationType이 베스트칼럼인지 확인
        this.isBestColumn = curation.getCurationType() == CurationType.BEST_COLUMN;
    }
}
