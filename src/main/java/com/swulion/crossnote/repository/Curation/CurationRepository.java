package com.swulion.crossnote.repository.Curation;

import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.entity.Curation.CurationType;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface CurationRepository extends JpaRepository<Curation, Long>, CurationRepositoryCustom {

    // 선호 카테고리 목록, 레벨, 생성 날짜로 조회 (개인화 피드용)
    List<Curation> findByCategory_CategoryIdInAndCurationLevelAndCreatedAtAfter(
            List<Long> categoryIds,
            CurationLevel curationLevel,
            LocalDateTime createdAt
    );

    // 생성 날짜 이후로 페이징 조회 (전체 피드용(임시))
    Page<Curation> findByCreatedAtAfter(LocalDateTime createdAt, Pageable pageable);

    // 원본 URL로 이미 베스트 칼럼 큐레이션이 등록되었는지 조회
    boolean existsBySourceUrlAndCurationType(String sourceUrl, CurationType curationType);
}