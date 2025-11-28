package com.swulion.crossnote.repository.Curation;

import com.swulion.crossnote.entity.Curation.Curation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

// 동적 쿼리
public interface CurationRepositoryCustom {

    Page<Curation> findDynamicFeed(
            List<Long> categoryIds,
            String curationType,
            String query,
            LocalDateTime thirtyDaysAgo,
            Pageable pageable
    );
}