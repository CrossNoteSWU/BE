package com.swulion.crossnote.entity.Curation;

import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.ColumnEntity;
import com.swulion.crossnote.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "curation")
public class Curation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cross_category_id")
    private Category crossCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurationType curationType;

    @Column(nullable = false, length = 2000)
    private String sourceUrl;

    @Column(length = 2000)
    private String imageUrl;

    @Column(nullable = false, length = 100)
    private String title; // AI가 생성한 제목

    @Column(nullable = false, length = 300)
    private String description; // AI가 생성한 소개글

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurationLevel curationLevel;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long scrapCount = 0L;

    @Column(nullable = false)
    private double terminologyDensity;

    // 원본 칼럼 작성자 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    // 원본 칼럼 ID (베스트 칼럼 중복 방지 및 원본 이동용)
    private Long originalColumnId;

    public void incrementLikeCount() {
        this.likeCount++;
    }
    public void decrementLikeCount() {
        if(this.likeCount > 0){
            this.likeCount--;
        }
    }

    public void incrementScrapCount() {
        this.scrapCount++;
    }
    public void decrementScrapCount() {
        if(this.scrapCount > 0){
            this.scrapCount--;
        }
    }

    public static Curation fromColumn(ColumnEntity column, Category mainCategory, Category crossCategory) {
        return Curation.builder()
                .category(mainCategory)
                .crossCategory(crossCategory)
                .author(column.getColumnAutherId()) // 원본 작성자
                // 베스트 칼럼은 원문 URL이 없으므로, title과 content만 복사
                .sourceUrl(null)
                .imageUrl(column.getImageUrl())
                .title(column.getTitle())
                .description(column.getContent() != null ? column.getContent().substring(0, Math.min(column.getContent().length(), 300)) : "")
                .curationLevel(CurationLevel.LEVEL_1)
                .terminologyDensity(0.0) // 칼럼은 분석 안 함
                .likeCount(0L)
                .scrapCount(0L)
                .build();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
