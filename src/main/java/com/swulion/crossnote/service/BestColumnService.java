//package com.swulion.crossnote.service;
//
//import com.swulion.crossnote.entity.ColumnEntity;
//import com.swulion.crossnote.entity.Curation.Curation;
//import com.swulion.crossnote.entity.Curation.CurationLevel;
//import com.swulion.crossnote.entity.Curation.CurationType;
//import com.swulion.crossnote.repository.ColumnRepository;
//import com.swulion.crossnote.repository.Curation.CurationRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BestColumnSchedulerService {
//
//    private final ColumnRepository columnRepository;
//    private final CurationRepository curationRepository;
//
//    /*
//     * 매 1시간마다 베스트 칼럼이 있는지 확인하고 큐레이션으로 등록
//     */
//    @Scheduled(cron = "0 0 * * * *") // 매시 정각 (테스트용: 10분마다 "0 */10 * * * *")
//    @Transactional
//    public void convertBestColumnsToCuration() {
//        log.info("베스트 칼럼 스케줄러 시작...");
//
//        // 1. 베스트 칼럼 기준을 충족하는 칼럼 조회
//        List<ColumnEntity> candidates = columnRepository.findBestColumnCandidates();
//
//        for (ColumnEntity column : candidates) {
//            // (칼럼 엔티티에 getColumnUrl() 같은 메서드가 있다고 가정)
//            String originalUrl = column.getColumnUrl();
//
//            // 2. 이미 'BEST_COLUMN' 타입으로 등록되었는지 중복 확인
//            boolean alreadyExists = curationRepository.existsBySourceUrlAndCurationType(
//                    originalUrl, CurationType.BEST_COLUMN
//            );
//
//            if (alreadyExists) {
//                continue; // 이미 등록됨 -> 건너뛰기
//            }
//
//            log.info("신규 베스트 칼럼 발견: (ID: {}) 큐레이션으로 등록합니다.", column.getId());
//
//            // 3. `Curation`은 Level 1/2가 필수이므로, 2단계와 동일하게
//            //    Level 1, Level 2 버전 큐레이션을 각각 생성
//            Curation curationA = Curation.builder()
//                    .category(column.getCategory()) // (칼럼의 카테고리)
//                    // .crossCategory(null) // 베스트 칼럼은 cross가 아님
//                    .curationType(CurationType.BEST_COLUMN)
//                    .sourceUrl(originalUrl)
//                    .imageUrl(column.getImageUrl()) // (칼럼의 이미지)
//                    .title(column.getTitle()) // (칼럼의 제목)
//                    .description(column.getSummary()) // (칼럼의 요약/내용)
//                    .curationLevel(CurationLevel.LEVEL_1) // Level 1 버전
//                    .build();
//
//            Curation curationB = Curation.builder()
//                    .category(column.getCategory())
//                    .curationType(CurationType.BEST_COLUMN)
//                    .sourceUrl(originalUrl)
//                    .imageUrl(column.getImageUrl())
//                    .title(column.getTitle())
//                    .description(column.getSummary())
//                    .curationLevel(CurationLevel.LEVEL_2) // Level 2 버전
//                    .build();
//
//            curationRepository.save(curationA);
//            curationRepository.save(curationB);
//        }
//    }
//}