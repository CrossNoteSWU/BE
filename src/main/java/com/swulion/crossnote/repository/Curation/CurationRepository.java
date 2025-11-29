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
    // 베스트 칼럼 중복 등록 방지 (원본 칼럼 ID 기준)
    boolean existsByOriginalColumnIdAndCurationType(Long originalColumnId, CurationType curationType);

    // 사용자 선호 카테고리 중, 특정 시점(오늘 0시) 이후 생성된 것 조회
    List<Curation> findByCategory_CategoryIdInAndCreatedAtAfter(List<Long> categoryIds, LocalDateTime createdAt);
}