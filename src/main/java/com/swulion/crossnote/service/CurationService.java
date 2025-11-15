package com.swulion.crossnote.service;

import com.swulion.crossnote.client.CurationSourceClient;
//import com.swulion.crossnote.client.KciClient;
//import com.swulion.crossnote.client.NationalLibClient;
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

    //private final KciClient kciClient;
    //private final NationalLibClient nationalLibClient;

    private final Random random = new Random(); // (랜덤 선택용)

    // 큐레이션 생성 프로세스에서 바라던 - 29*4=116개의 큐레이션 생성 (스케쥴 적용 필요)
//    @Transactional
//    public void createDailyCurations() {
//        log.info("데일리 큐레이션 생성 작업 시작 (29개 분야 × 4개)");
//
//        List<Category> categories = categoryRepository.findByParentCategoryIdIsNotNull();
//        if (categories.isEmpty()) {
//            log.warn("DB에 생성 가능한 카테고리가 없습니다.");
//            return;
//        }
//
//        for (Category category : categories) {
//            // Level A (LEVEL_1)
//            createInsightCuration(category, CurationLevel.LEVEL_1);
//            createCrossNoteCuration(category, CurationLevel.LEVEL_1);
//
//            // Level B (LEVEL_2)
//            createInsightCuration(category, CurationLevel.LEVEL_2);
//            createCrossNoteCuration(category, CurationLevel.LEVEL_2);
//        }
//
//        log.info("데일리 큐레이션 생성 작업 완료.");
//    }

    // 현실적인 대안 - 4번에 걸쳐서 큐레이션 생성하기 (API 호출량 제한 이유)
    //
    @Transactional
    public void createDailyCurations() {
        log.info("데일리 큐레이션 생성 작업 시작 (분산 배치 실행)");

        // 1. 모든 하위 카테고리 (29개)를 조회합니다.
        List<Category> allCategories = categoryRepository.findByParentCategoryIdIsNotNull();

        if (allCategories.isEmpty()) {
            log.warn("DB에 생성 가능한 카테고리가 없습니다.");
            return;
        }

        // 2. 분할 계산: 29개 카테고리를 4개의 배치로 나눕니다.
        final int BATCH_COUNT = 4;
        // 29 / 4 = 7.25 이므로 한 배치당 최대 8개 카테고리를 처리합니다.
        final int CATEGORIES_PER_BATCH = (int) Math.ceil((double) allCategories.size() / BATCH_COUNT); // 8

        // 3. 현재 실행할 배치 번호 설정 (수동 조정 필요: 1, 2, 3, 4)
        //    실제 스케줄러에서는 이 값을 DB나 캐시에서 관리하며, 다음 실행 시 +1 되어야 합니다.
        int currentBatchIndex = 1; // 현재 첫 번째 배치 (1)만 실행. (11/15 오후 3시 30분)

        int startIndex = (currentBatchIndex - 1) * CATEGORIES_PER_BATCH; // 0
        // endIndex는 현재 배치 사이즈(8)를 넘지 않도록, 전체 리스트 사이즈(29)를 넘지 않도록 설정
        int endIndex = Math.min(currentBatchIndex * CATEGORIES_PER_BATCH, allCategories.size());

        // 4. 현재 배치에 해당하는 카테고리만 추출
        //    예: allCategories.subList(0, 8) -> 8개 카테고리만 포함
        List<Category> categoriesToProcess = allCategories.subList(startIndex, endIndex);

        log.warn("=== [TEST] 현재 배치({}/{}): {}개 카테고리 (총 {}개 큐레이션) 생성 시작 ===",
                currentBatchIndex, BATCH_COUNT, categoriesToProcess.size(), categoriesToProcess.size() * 4);

        // 5. 선택된 카테고리에 대해서만 큐레이션 생성
        for (Category category : categoriesToProcess) {
            // Level A (LEVEL_1)
            createInsightCuration(category, CurationLevel.LEVEL_1);
            createCrossNoteCuration(category, CurationLevel.LEVEL_1);

            // Level B (LEVEL_2)
            createInsightCuration(category, CurationLevel.LEVEL_2);
            createCrossNoteCuration(category, CurationLevel.LEVEL_2);
        }

        log.info("데일리 큐레이션 생성 작업 완료.");
    }

//    // 테스트용 - 제미나이x. KCI와 NationalLib만
//    // --- 2. [수정] 메서드 내용을 KCI와 NationalLib 호출 테스트로 덮어씁니다. ---
//    @Transactional
//    public void createDailyCurations() {
//        log.warn("[API CLIENT TEST MODE] KCI와 NationalLib 클라이언트 호출만 테스트합니다. (Gemini 호출 없음)");
//
//        String testQuery = "철학"; // 테스트할 검색어
//
//        // === KCI Client 테스트 ===
//        try {
//            log.info("[API TEST] KciClient.fetchSource('{}') 호출 시도...", testQuery);
//            CurationSourceDto kciResult = kciClient.fetchSource(testQuery);
//
//            if (kciResult != null && kciResult.getOriginalText() != null) {
//                log.info("[API TEST] KCI Client 성공! Title: {}", kciResult.getOriginalText().substring(0, Math.min(kciResult.getOriginalText().length(), 70)));
//            } else {
//                log.warn("[API TEST] KCI Client가 null을 반환했습니다. (로그 확인 필요)");
//            }
//        } catch (Exception e) {
//            log.error("[API TEST] KCI Client 호출 중 예외 발생", e);
//        }
//
//        // === NationalLib Client 테스트 ===
//        try {
//            log.info("[API TEST] NationalLibClient.fetchSource('{}') 호출 시도...", testQuery);
//            CurationSourceDto nlResult = nationalLibClient.fetchSource(testQuery);
//
//            if (nlResult != null && nlResult.getOriginalText() != null) {
//                log.info("[API TEST] NationalLib Client 성공! Title: {}", nlResult.getOriginalText().substring(0, Math.min(nlResult.getOriginalText().length(), 70)));
//            } else {
//                log.warn("[API TEST] NationalLib Client가 null을 반환했습니다. (로그 확인 필요)");
//            }
//        } catch (Exception e) {
//            log.error("[API TEST] NationalLib Client 호출 중 예외 발생", e);
//        }
//
//        log.warn("[API CLIENT TEST MODE] 테스트 완료.");
//    }

    /*
     * '인사이트' 큐레이션 생성 (기능 명세서 2.2-1: 모든 소스 사용)
     */
    private void createInsightCuration(Category category, CurationLevel level) {
        if (allClients.isEmpty()) return;

        // ⭐ 2번 항목 해결: fetchSourceWithRetry를 사용하여 소스 확보 로직 대체
        CurationSourceDto source = fetchSourceWithRetry(category.getCategoryName(), allClients);
        if (source == null) return; // 모든 시도 실패 시 생성 중단

        //TerminologyService를 통해 원문 분석 (AI 생성 전에 실행)
        List<Long> categoryIds = List.of(category.getCategoryId());
        TerminologyService.TermCountResult analysisResult =
                terminologyService.analyzeSourceText(source.getOriginalText(), categoryIds); // List<Long> 전달

        // ⭐ Step 2: 난이도 분석 결과에 따라 최종 Level 동적 결정
        CurationLevel finalLevel = determineFinalLevel(analysisResult);

        // ⭐ Step 3: 최종 Level에 맞춰 AI에게 제목/요약 요청
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

    /*
     * '크로스노트' 큐레이션 생성 (기능 명세서 2.2-2: '공적문서' 제외)
     */
    private void createCrossNoteCuration(Category category, CurationLevel level) {
        List<CurationSourceClient> crossNoteClients = allClients.stream()
                .filter(client -> !"DOCUMENT".equals(client.getSourceType()))
                .collect(Collectors.toList());
        if (crossNoteClients.isEmpty()) return;

        // ⭐ 2번 항목 해결: fetchSourceWithRetry를 사용하여 소스 확보 로직 대체
        CurationSourceDto source = fetchSourceWithRetry(category.getCategoryName(), crossNoteClients);
        if (source == null) return; // 모든 시도 실패 시 생성 중단

        Category crossCategory = findRandomCrossCategory(category);
        if (crossCategory == null) return;

        // Step 2: CrossNote 개선 로직 적용 (메인 + 교차 카테고리 모두 사용하여 분석)
        List<Long> categoryIds = List.of(category.getCategoryId(), crossCategory.getCategoryId());
        TerminologyService.TermCountResult analysisResult =
                terminologyService.analyzeSourceText(source.getOriginalText(), categoryIds);

        // ⭐ Step 2: 난이도 분석 결과에 따라 최종 Level 동적 결정
        CurationLevel finalLevel = determineFinalLevel(analysisResult);

        // ⭐ Step 3: 최종 Level에 맞춰 AI에게 제목/요약 요청
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
        // 1. 사용자의 관심/전문 분야 ID 목록 조회
        List<Long> preferenceCategoryIds = userPreferenceRepository.findByUser(user)
                .stream()
                .map(pref -> pref.getCategory().getCategoryId())
                .collect(Collectors.toList());

        // 2. 사용자의 레벨 조회 (User 엔티티에 getCurationLevel()이 있어야 함)
        CurationLevel userLevel = user.getCurationLevel(); //

        // 3. 오늘 0시 0분
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        // 4. 사용자의 선호 분야/레벨에 맞는 '오늘 생성된' 큐레이션 조회
        List<Curation> candidates = curationRepository
                .findByCategory_CategoryIdInAndCurationLevelAndCreatedAtAfter(
                        preferenceCategoryIds, userLevel, startOfToday
                );

        // 5. 유형별로 분리하고, 우선순위 2 (최신성) 기준으로 정렬 (재할당 로직을 위한 준비)
        Map<CurationType, List<Curation>> groupedCandidates = candidates.stream()
                .sorted(Comparator.comparing(Curation::getCreatedAt).reversed())
                .collect(Collectors.groupingBy(
                        Curation::getCurationType,
                        LinkedHashMap::new, // 순서 유지를 위해 LinkedHashMap 사용
                        Collectors.toList()
                ));

        // 최종 믹스 리스트
        List<Curation> finalMix = new ArrayList<>();

        // 6. 확보 및 재할당 로직 구현 (할당량: CN 2, IN 2, BC 2)
        int targetCN = 2;
        int targetIN = 2;
        int targetBC = 2;
        int totalTarget = targetCN + targetIN + targetBC;

        // 초기 확보
        List<Curation> cnList = groupedCandidates.getOrDefault(CurationType.CROSSNOTE, Collections.emptyList());
        List<Curation> inList = groupedCandidates.getOrDefault(CurationType.INSIGHT, Collections.emptyList());
        List<Curation> bcList = groupedCandidates.getOrDefault(CurationType.BEST_COLUMN, Collections.emptyList());

        // 확보된 초기 수량 (잔여분 계산을 위해 확보 후 리스트에서 제거)
        int securedCN = Math.min(cnList.size(), targetCN);
        int securedIN = Math.min(inList.size(), targetIN);
        int securedBC = Math.min(bcList.size(), targetBC);

        finalMix.addAll(cnList.subList(0, securedCN));
        finalMix.addAll(inList.subList(0, securedIN));
        finalMix.addAll(bcList.subList(0, securedBC));

        int needed = totalTarget - finalMix.size();

        // 재할당 시작 (최대 6개가 될 때까지)
        // 재할당 우선순위: CN > IN
        if (needed > 0) {
            // 잔여분 리스트 생성 (초기 확보된 콘텐츠는 제외)
            List<Curation> remainingCN = cnList.subList(securedCN, cnList.size());
            List<Curation> remainingIN = inList.subList(securedIN, inList.size());
            // BC는 재할당 우선순위가 낮으므로, 잔여분을 사용하지 않고 CN/IN만 사용

            // 1. CN 잔여분을 우선 사용
            for (int i = 0; i < remainingCN.size() && needed > 0; i++) {
                finalMix.add(remainingCN.get(i));
                needed--;
            }

            // 2. IN 잔여분 사용
            for (int i = 0; i < remainingIN.size() && needed > 0; i++) {
                finalMix.add(remainingIN.get(i));
                needed--;
            }

            // 3. (옵션) BC 잔여분 사용 - 문서에는 명확히 언급 없으나, 콘텐츠 부족 시 채우는 역할
            // if (needed > 0) { ... }
        }

        // 7. 최종 믹스에 대해 우선순위 1 & 2 정렬 (혹시 재할당된 콘텐츠 순서가 꼬였을 경우 대비)
        finalMix.sort(Comparator
                .comparing((Curation c) -> getPriority(c.getCurationType()))
                .thenComparing(Curation::getCreatedAt).reversed()
        );

        // 8. 최대 6개로 제한하여 반환 (needed 로직으로 6개 내외가 되지만, 안전장치)
        return finalMix.stream()
                .limit(totalTarget)
                .map(CurationFeedDto::new)
                .collect(Collectors.toList());
    }

    /*
     * 크로스할 카테고리를 찾는 헬퍼 메서드
     */
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

    // 우선순위 정렬을 위한 헬퍼 메서드
    private int getPriority(CurationType type) {
        switch (type) {
            case CROSSNOTE: return 1;
            case INSIGHT: return 2;
            case BEST_COLUMN: return 3;
            default: return 99;
        }
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
    private static final double DENSITY_THRESHOLD = 0.03;
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
    public Page<CurationFeedDto> getAllCurationFeed(Long categoryId, String curationType, String query, Pageable pageable) {
        // 30일 이내
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // QueryDSL 동적 쿼리 메서드 호출
        Page<Curation> resultPage = curationRepository.findDynamicFeed(
                categoryId,
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
}