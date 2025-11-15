package com.swulion.crossnote.entity.Curation;

import com.swulion.crossnote.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
