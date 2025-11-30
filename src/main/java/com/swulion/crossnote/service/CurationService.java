package com.swulion.crossnote.service;

import com.swulion.crossnote.client.CurationSourceClient;
//import com.swulion.crossnote.client.KciClient;
//import com.swulion.crossnote.client.NationalLibClient;
import com.swulion.crossnote.client.KciClient;
import com.swulion.crossnote.client.NlBookClient;
import com.swulion.crossnote.dto.Curation.*;
import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.entity.Curation.Like;
import com.swulion.crossnote.entity.Curation.Scrap;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.CategoryRepository;
import com.swulion.crossnote.repository.Curation.CurationRepository;
import com.swulion.crossnote.repository.Curation.LikeRepository;
import com.swulion.crossnote.repository.Curation.ScrapRepository;
import com.swulion.crossnote.repository.UserCategoryPreferenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurationService {

    private final CategoryRepository categoryRepository;
    private final CurationRepository curationRepository;
    private final UserCategoryPreferenceRepository userPreferenceRepository;
    private final ScrapRepository scrapRepository;
    private final LikeRepository likeRepository;

    private final List<CurationSourceClient> allClients; // (Spring이 5개 Client Bean을 모두 주입)
    private final GeminiService geminiService;
    private final TerminologyService terminologyService;

    // 테스트용
    private final KciClient kciClient;
    private final NlBookClient nlBookClient;

    private final Random random = new Random(); // (랜덤 선택용)

    /*
     * 매일 새벽 1시, 29개 카테고리에 대해 총 116개의 큐레이션을 자동 생성.
     * 명세: 카테고리당 4개 (Insight L1/L2, CrossNote L1/L2)
     */
    // 큐레이션 자동 생성
    @Scheduled(cron = "0 0 1 * * *") // 매일 01:00:00 실행
    @Transactional
    public void scheduleDailyCurationCreation() {
        log.info("=== [Daily Batch] 큐레이션 자동 생성 시작 (Total Target: 116) ===");

        List<Category> allCategories = categoryRepository.findByParentCategoryIdIsNotNull();
        if (allCategories.isEmpty()) {
            log.warn("생성 가능한 카테고리가 없습니다.");
            return;
        }

        int successCount = 0;

        // 29개 카테고리 순회
        for (Category category : allCategories) {
            try {
                log.info(">> Processing Category: {}", category.getCategoryName());

                // 1. Insight (Level 1 & 2)
                createInsightCuration(category, CurationLevel.LEVEL_1);
                createInsightCuration(category, CurationLevel.LEVEL_2);

                // 2. CrossNote (Level 1 & 2)
                createCrossNoteCuration(category, CurationLevel.LEVEL_1);
                createCrossNoteCuration(category, CurationLevel.LEVEL_2);

                successCount += 4;

                // GeminiService 내부에 2초 딜레이가 있지만,
                // KCI/국립중앙도서관 등 소스 API의 부하 분산을 위해 카테고리 간 추가 텀을 둠
                Thread.sleep(3000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("스케줄러 인터럽트 발생", ie);
                break;
            } catch (Exception e) {
                // 한 카테고리가 실패해도 나머지는 계속 진행
                log.error("카테고리[{}] 생성 중 오류 발생: {}", category.getCategoryName(), e.getMessage());
            }
        }

        log.info("=== [Daily Batch] 큐레이션 생성 완료. 생성된 수량(예상): {} ===", successCount);
    }

    // '인사이트' 큐레이션 생성
    private void createInsightCuration(Category category, CurationLevel level) {
        if (allClients.isEmpty()) return;

        // fetchSourceWithRetry를 사용하여 소스 확보 로직 대체
        CurationSourceDto source = fetchSourceWithRetry(category.getCategoryName(), allClients);
        if (source == null) return; // 모든 시도 실패 시 생성 중단

        //TerminologyService를 통해 원문 분석 (AI 생성 전에 실행)
        List<Long> categoryIds = List.of(category.getCategoryId());
        TerminologyService.TermCountResult analysisResult =
                terminologyService.analyzeSourceText(source.getOriginalText(), categoryIds); // List<Long> 전달

        // 난이도 분석 결과에 따라 최종 Level 동적 결정
        CurationLevel finalLevel = determineFinalLevel(analysisResult);

        // 최종 Level에 맞춰 AI에게 제목/요약 요청
        AiGeneratedContentDto content = geminiService.generateContent(source.getOriginalText(), finalLevel);
        Curation curation = Curation.builder()
                .category(category)
                .curationType(CurationType.INSIGHT)
                .sourceUrl(source.getSourceUrl())
                .imageUrl(source.getImageUrl())
                .title(content.getTitle())
                .description(content.getDescription())
                .curationLevel(finalLevel) // 요청 레벨(L1/L2) 확정
                .terminologyDensity(analysisResult.getDensity()) // 원문 분석 결과 저장
                .build();
        curationRepository.save(curation);
    }

    // '크로스노트' 큐레이션 생성
    private void createCrossNoteCuration(Category category, CurationLevel level) {
        List<CurationSourceClient> crossNoteClients = allClients.stream()
                .filter(client -> !"DOCUMENT".equals(client.getSourceType()))
                .collect(Collectors.toList());
        if (crossNoteClients.isEmpty()) return;

        // fetchSourceWithRetry를 사용하여 소스 확보 로직 대체
        CurationSourceDto source = fetchSourceWithRetry(category.getCategoryName(), crossNoteClients);
        if (source == null) return; // 모든 시도 실패 시 생성 중단

        Category crossCategory = findRandomCrossCategory(category);
        if (crossCategory == null) return;

        // CrossNote 개선 로직 적용 (메인 + 교차 카테고리 모두 사용하여 분석)
        List<Long> categoryIds = List.of(category.getCategoryId(), crossCategory.getCategoryId());
        TerminologyService.TermCountResult analysisResult =
                terminologyService.analyzeSourceText(source.getOriginalText(), categoryIds);

        // 난이도 분석 결과에 따라 최종 Level 동적 결정
        CurationLevel finalLevel = determineFinalLevel(analysisResult);

        // 최종 Level에 맞춰 AI에게 제목/요약 요청
        AiGeneratedContentDto content = geminiService.generateContent(source.getOriginalText(), finalLevel);
        Curation curation = Curation.builder()
                .category(category)
                .crossCategory(crossCategory)
                .curationType(CurationType.CROSSNOTE)
                .sourceUrl(source.getSourceUrl())
                .imageUrl(source.getImageUrl())
                .title(content.getTitle())
                .description(content.getDescription())
                .curationLevel(finalLevel) // 요청 레벨(L1/L2) 확정
                .terminologyDensity(analysisResult.getDensity()) // 원문 분석 결과 저장
                .build();
        curationRepository.save(curation);
    }

    // 2.1. 개인화 피드 로직
    @Transactional(readOnly = true)
    public List<CurationFeedDto> getPersonalizedFeed(User user) {
        // 1. 사용자 관심 분야 ID
        List<Long> preferenceCategoryIds = userPreferenceRepository.findByUser(user)
                .stream()
                .map(pref -> pref.getCategory().getCategoryId())
                .collect(Collectors.toList());

        // 2. '오늘(00:00 이후)' 생성된 큐레이션 중 관심 분야 필터링
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime startOfToday = LocalDate.now(zoneId).atStartOfDay();

        List<Curation> candidates = curationRepository
                .findByCategory_CategoryIdInAndCreatedAtAfter(preferenceCategoryIds, startOfToday);

        // 3. 유형별 그룹핑
        Map<CurationType, List<Curation>> grouped = candidates.stream()
                .collect(Collectors.groupingBy(Curation::getCurationType));

        List<Curation> cnList = grouped.getOrDefault(CurationType.CROSSNOTE, new ArrayList<>());
        List<Curation> inList = grouped.getOrDefault(CurationType.INSIGHT, new ArrayList<>());
        List<Curation> bcList = grouped.getOrDefault(CurationType.BEST_COLUMN, new ArrayList<>());

        // 그룹 내 최신순 정렬
        sortListByLatest(cnList);
        sortListByLatest(inList);
        sortListByLatest(bcList);

        // 4. 할당 및 혼합 로직 (목표: CN 2, IN 2, BC 2)
        List<Curation> finalMix = new ArrayList<>();
        int targetPerType = 2;
        int totalTarget = 6;

        // (1) 기본 할당량 확보
        List<Curation> selectedCN = pickTop(cnList, targetPerType);
        List<Curation> selectedIN = pickTop(inList, targetPerType);
        List<Curation> selectedBC = pickTop(bcList, targetPerType);

        finalMix.addAll(selectedCN);
        finalMix.addAll(selectedIN);
        finalMix.addAll(selectedBC);

        // (2) 부족분 재할당 (Priority: CN > IN)
        int needed = totalTarget - finalMix.size();

        if (needed > 0) {
            // 이미 선택된 것을 제외한 잔여 리스트
            List<Curation> remainCN = getRemaining(cnList, selectedCN);
            List<Curation> remainIN = getRemaining(inList, selectedIN);

            // 1순위: 크로스노트에서 충원
            for (Curation c : remainCN) {
                if (needed == 0) break;
                finalMix.add(c);
                needed--;
            }
            // 2순위: 인사이트에서 충원
            for (Curation c : remainIN) {
                if (needed == 0) break;
                finalMix.add(c);
                needed--;
            }
        }

        // 5. 최종 정렬 (Priority 1: Type, Priority 2: Latest)
        // Type Priority: CrossNote(1) > Insight(2) > BestColumn(3)
        return finalMix.stream()
                .sorted(Comparator
                        .comparingInt(this::getTypePriority) // 1차 정렬: 타입
                        .thenComparing(Curation::getCreatedAt, Comparator.reverseOrder()) // 2차 정렬: 최신순
                )
                .map(CurationFeedDto::new)
                .collect(Collectors.toList());
    }

    private void sortListByLatest(List<Curation> list) {
        list.sort(Comparator.comparing(Curation::getCreatedAt).reversed());
    }

    private List<Curation> pickTop(List<Curation> source, int limit) {
        return source.stream().limit(limit).collect(Collectors.toList());
    }

    private List<Curation> getRemaining(List<Curation> source, List<Curation> picked) {
        List<Curation> remaining = new ArrayList<>(source);
        remaining.removeAll(picked);
        return remaining;
    }

    private int getTypePriority(Curation c) {
        if (c.getCurationType() == CurationType.CROSSNOTE) return 1;
        if (c.getCurationType() == CurationType.INSIGHT) return 2;
        if (c.getCurationType() == CurationType.BEST_COLUMN) return 3;
        return 99;
    }

    // 크로스할 카테고리를 찾는 헬퍼 메서드
    private Category findRandomCrossCategory(Category originalCategory) {
        // (DB 호출 최소화를 위해 모든 카테고리를 메모리에 캐싱하는 것도 고려해볼 수 있습니다)
        List<Category> allCategories = categoryRepository.findByParentCategoryIdIsNotNull();

        // 원본 카테고리 제외
        allCategories.removeIf(c -> c.getCategoryId().equals(originalCategory.getCategoryId()));

        if (allCategories.isEmpty()) {
            return null; // (카테고리가 1개뿐일 경우)
        }

        Collections.shuffle(allCategories);
        return allCategories.get(0);
    }

    // 클라이언트 순회하며 소스를 확보하는 헬퍼 메서드 - 원문 확보 로직 강화
    private CurationSourceDto fetchSourceWithRetry(String categoryName, List<CurationSourceClient> clients) {
        if (clients.isEmpty()) return null;

        // 클라이언트 순서를 섞어 매번 다른 클라이언트를 우선적으로 시도
        List<CurationSourceClient> shuffledClients = new ArrayList<>(clients);
        Collections.shuffle(shuffledClients);

        // 최대 3번의 재시도(클라이언트 교체)를 가정하고 시도
        for (CurationSourceClient client : shuffledClients) {
            try {
                CurationSourceDto source = client.fetchSource(categoryName);
                if (source != null && source.getOriginalText() != null) {
                    log.info("소스 확보 성공: {} (Client: {})", source.getTitle(), client.getClass().getSimpleName());
                    return source;
                }
            } catch (Exception e) {
                log.warn("Client {} 호출 실패: {}", client.getClass().getSimpleName(), e.getMessage());
            }
        }
        log.warn("모든 클라이언트 시도 실패: 카테고리 '{}'에 대한 소스 확보 불가", categoryName);
        return null; // 모든 시도 실패
    }

    // TerminologyService의 임계값을 사용하거나 여기에 정의
    private static final double DENSITY_THRESHOLD = 0.02;
    private static final int ABS_TERM_COUNT_THRESHOLD = 2;

    // Level 결정 헬퍼 메서드 추가
    private CurationLevel determineFinalLevel(TerminologyService.TermCountResult result) {
        if (result.getDensity() >= DENSITY_THRESHOLD || result.getCount() >= ABS_TERM_COUNT_THRESHOLD) {
            // 임계값 초과: 전문적인 Level B로 결정
            return CurationLevel.LEVEL_2;
        }
        // 임계값 미만: 일반적인 Level A로 결정
        return CurationLevel.LEVEL_1;
    }

    // 2.1-1. 전체 피드 로직
    @Transactional(readOnly = true)
    public Page<CurationFeedDto> getAllCurationFeed(List<Long> categoryIds, String curationType, String query, Pageable pageable) {
        // 30일 이내
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // QueryDSL 동적 쿼리 메서드 호출
        Page<Curation> resultPage = curationRepository.findDynamicFeed(
                categoryIds,
                curationType,
                query,
                thirtyDaysAgo,
                pageable
        );
        // Page<Curation>을 Page<CurationFeedDto>로 변환
        return resultPage.map(CurationFeedDto::new);
    }

    // 2.2. 큐레이션 상세 보기 로직
    @Transactional(readOnly = true)
    public CurationDetailDto getCurationDetail(Long curationId, User user) {

        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(() -> new EntityNotFoundException("큐레이션을 찾을 수 없습니다. ID: " + curationId));

        boolean isLiked = false;
        boolean isScrapped = false;

        if (user != null) {
            isLiked = likeRepository.existsByUserAndTargetTypeAndTargetId(
                    user, ScrapTargetType.CURATION, curationId
            );

            isScrapped = scrapRepository.existsByUserAndTargetTypeAndTargetId(
                    user, ScrapTargetType.CURATION, curationId
            );
        }
        return new CurationDetailDto(curation, isLiked, isScrapped);
    }

    // 2.2. 큐레이션 좋아요 토글 로직
    @Transactional
    public CurationToggleResponseDto toggleCurationLike(Long curationId, User user) {
        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(() -> new EntityNotFoundException("큐레이션을 찾을 수 없습니다. ID: " + curationId));

        Optional<Like> likeOptional = likeRepository.findByUserAndTargetTypeAndTargetId(
                user, ScrapTargetType.CURATION, curationId
        );

        if (likeOptional.isPresent()) {
            likeRepository.delete(likeOptional.get());
            curation.decrementLikeCount();

            return new CurationToggleResponseDto(false, curation.getLikeCount());
        } else {
            likeRepository.save(new Like(user, ScrapTargetType.CURATION, curationId));
            curation.incrementLikeCount();

            return new CurationToggleResponseDto(true, curation.getLikeCount());
        }
    }

    // 2.2. 큐레이션 스크랩 토글 로직
    @Transactional
    public CurationToggleResponseDto toggleCurationScrap(Long curationId, User user) {
        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(() -> new EntityNotFoundException("큐레이션을 찾을 수 없습니다. ID: " + curationId));

        Optional<Scrap> scrapOptional = scrapRepository.findByUserAndTargetTypeAndTargetId(
                user, ScrapTargetType.CURATION, curationId
        );

        if (scrapOptional.isPresent()) {
            scrapRepository.delete(scrapOptional.get());
            curation.decrementScrapCount();

            return new CurationToggleResponseDto(false, curation.getScrapCount());
        } else {
            scrapRepository.save(new Scrap(user, ScrapTargetType.CURATION, curationId));
            curation.incrementScrapCount();

            return new CurationToggleResponseDto(true, curation.getScrapCount());
        }
    }

    // 초기화용
    @Transactional
    public void deleteAllCurations() {
        log.warn("큐레이션 전체 삭제 작업 시작");
        likeRepository.deleteAllInBatch();
        scrapRepository.deleteAllInBatch();

        curationRepository.deleteAllInBatch();
    }
}