package com.swulion.crossnote.repository.Curation;

import com.swulion.crossnote.entity.Curation.Curation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

// 동적 쿼리
public interface CurationRepositoryCustom {

    Page<Curation> findDynamicFeed(
            Long categoryId,
            String curationType,
            String query,
            LocalDateTime thirtyDaysAgo,
            Pageable pageable
    );
}