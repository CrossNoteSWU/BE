package com.swulion.crossnote.dto.Curation;

import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.entity.Curation.CurationType;
import lombok.Getter;

@Getter
public class CurationDetailDto {
    // CurationFeedDto의 모든 필드
    private Long curationId;
    private String title;
    private String description;
    private String sourceUrl; // 원문 링크
    private String imageUrl;
    private CurationType curationType;
    private CurationLevel curationLevel;
    private String categoryName;
    private String crossCategoryName; // 크로스노트일 경우

    // 좋아요/스크랩 정보 추가
    private long likeCount;
    private long scrapCount;
    private boolean isLiked;
    private boolean isScrapped;

    // 베스트 칼럼 추가
    private Long originalColumnId;
    private boolean isBestColumn;

    public CurationDetailDto(Curation curation, boolean isLiked, boolean isScrapped) {
        this.curationId = curation.getId();
        this.title = curation.getTitle();
        this.description = curation.getDescription();
        this.sourceUrl = curation.getSourceUrl();
        this.imageUrl = curation.getImageUrl();
        this.curationType = curation.getCurationType();
        this.curationLevel = curation.getCurationLevel();

        if(curation.getCategory() != null) {
            this.categoryName = curation.getCategory().getCategoryName();
        }
        if(curation.getCrossCategory() != null) {
            this.crossCategoryName = curation.getCrossCategory().getCategoryName();
        }

        this.likeCount = curation.getLikeCount();
        this.scrapCount = curation.getScrapCount();
        this.isLiked = isLiked;
        this.isScrapped = isScrapped;

        this.isBestColumn = curation.getCurationType() == CurationType.BEST_COLUMN;
        this.originalColumnId = curation.getOriginalColumnId();
    }
}
