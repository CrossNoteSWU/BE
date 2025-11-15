package com.swulion.crossnote.service;

import com.swulion.crossnote.entity.ColumnCategory;
import com.swulion.crossnote.entity.ColumnEntity;
import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.repository.ColumnCategoryRepository;
import com.swulion.crossnote.repository.ColumnRepository;
import com.swulion.crossnote.repository.Curation.CurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestColumnCurationService {

    private final ColumnRepository columnRepository;
    private final CurationRepository curationRepository;
    private final ColumnCategoryRepository columnCategoryRepository;

    private static final int MIN_LIKE_COUNT = 10;
    private static final int MIN_SCRAP_COUNT = 10;
    private static final CurationType BEST_COLUMN_TYPE = CurationType.BEST_COLUMN;

    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void curateBestColumns() {
        log.info("--- 베스트 칼럼 큐레이션 작업 시작: {} ---", LocalDateTime.now());

        // 1. 베스트 칼럼 기준 충족 칼럼 조회
        List<ColumnEntity> potentialBestColumns =
                columnRepository.findByLikeCountGreaterThanEqualAndScrapCountGreaterThanEqual(
                        MIN_LIKE_COUNT, MIN_SCRAP_COUNT);

        int newCurationsCount = 0;

        for (ColumnEntity column : potentialBestColumns) {
            Long columnId = column.getColumnId();

            // 2. 이미 해당 칼럼이 'BEST_COLUMN' 타입으로 큐레이션에 등록되었는지 확인 (원본 ID 기준)
            if (curationRepository.existsByOriginalColumnIdAndCurationType(columnId, BEST_COLUMN_TYPE)) {
                continue;
            }

            // 3. 원본 칼럼의 카테고리 정보 조회
            List<ColumnCategory> columnCategories = columnCategoryRepository.findByColumnId(column);

            if (columnCategories.isEmpty()) {
                log.warn("칼럼 ID {}에 카테고리 정보가 없습니다. 큐레이션 등록 스킵.", columnId);
                continue;
            }

            // 카테고리 정렬 (순서 보장 - columnCategoryId 기준)
            columnCategories.sort(Comparator.comparing(ColumnCategory::getColumnCategoryId));

            // 메인 카테고리 (첫 번째) 추출
            Category mainCategory = columnCategories.get(0).getCategoryId();
            // 교차 카테고리 (두 번째) 추출 (있다면)
            Category crossCategory = columnCategories.size() > 1 ? columnCategories.get(1).getCategoryId() : null;

            // 4. Curation Entity 생성 (칼럼 정보 복사)
            Curation newCuration = Curation.fromColumn(column, mainCategory, crossCategory);

            // 원본 칼럼의 좋아요/스크랩 카운트 복사
            newCuration.setLikeCount(Long.valueOf(column.getLikeCount()));
            newCuration.setScrapCount(Long.valueOf(column.getScrapCount()));

            // sourceUrl이 null인 경우, 원본 칼럼 URL로 대체
            if (newCuration.getSourceUrl() == null) {
                String columnDetailUrl = "/column/detail/" + columnId;
                newCuration.setSourceUrl(columnDetailUrl);
            }

            newCuration.setCurationType(BEST_COLUMN_TYPE);
            newCuration.setOriginalColumnId(columnId);

            // 5. 큐레이션 등록
            Curation savedCuration = curationRepository.save(newCuration);
            newCurationsCount++;

            // 6. 원본 칼럼의 isBestColumn 플래그 업데이트
            if (!column.isBestColumn()) {
                column.setBestColumn(true);
                columnRepository.save(column);
            }
        }

        log.info("베스트 칼럼 큐레이션 작업 완료. 총 {}개 신규 등록", newCurationsCount);
    }
}