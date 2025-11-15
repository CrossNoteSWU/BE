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
    
//    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시 0분 0초
//    @Transactional
//    public void createDailyCurations() {
//        log.info("데일리 큐레이션 생성 작업을 시작합니다...");
//
//        // 카테고리 목록 가져오기
//        List<Category> categories = categoryRepository.findByParentCategoryIdIsNotNull();
//
//        for (Category category : categories) {
//            // 인사이트 타입 생성
//            createInsightCurationForCategory(category);
//            // 크로스노트 타입 생성
//            createCrossNoteCurationForCategory(category);
//        }
//        log.info("데일리 큐레이션 생성 작업 완료.");
//    }

    // 테스트용
    @Transactional
    public void createDailyCurations() {
        log.info("데일리 큐레이션 생성 작업을 시작합니다...");

        List<Category> categories = categoryRepository.findByParentCategoryIdIsNotNull();

        if (categories.isEmpty()) {
            log.warn("테스트할 카테고리가 DB에 없습니다.");
            return;
        }

        Category testCategory = categories.get(0);
        log.warn("[TEST MODE] {} 카테고리 1개만 테스트 생성합니다.", testCategory.getCategoryName());

        // '인사이트' 타입 생성
        createInsightCurationForCategory(testCategory);
        // '크로스노트' 타입 생성
        createCrossNoteCurationForCategory(testCategory);

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
    private void createInsightCurationForCategory(Category category) {
        if (allClients.isEmpty()) {
            log.warn("CurationSourceClient 구현체가 없습니다.");
            return;
        }

        // 1. 모든 클라이언트 중 1개 랜덤 선택
        CurationSourceClient randomClient = allClients.get(random.nextInt(allClients.size()));
        log.info("{} 카테고리 '인사이트' 생성 시도 (소스: {})", category.getCategoryName(), randomClient.getSourceType());

        // 2. 선택된 클라이언트로부터 소스 가져오기
        CurationSourceDto source = randomClient.fetchSource(category.getCategoryName());
        if (source == null) {
            log.warn("{} 카테고리에서 소스({})를 찾을 수 없습니다.", category.getCategoryName(), randomClient.getSourceType());
            return;
        }

        // 3. (공통 로직) AI 생성 및 L1 저장
        AiGeneratedContentDto contentA = geminiService.generateContent(source.getOriginalText(), CurationLevel.LEVEL_1);
        terminologyService.assignLevel(contentA, category.getCategoryId());

        Curation curationA = Curation.builder()
                .category(category)
                .curationType(CurationType.INSIGHT)
                .sourceUrl(source.getSourceUrl())
                .imageUrl(source.getImageUrl())
                .title(contentA.getTitle())
                .description(contentA.getDescription())
                .curationLevel(contentA.getCurationLevel())
                .build();
        curationRepository.save(curationA);

        // 4. (공통 로직) L2 생성 및 저장
        AiGeneratedContentDto contentB = geminiService.generateContent(source.getOriginalText(), CurationLevel.LEVEL_2);
        terminologyService.assignLevel(contentB, category.getCategoryId());

        Curation curationB = Curation.builder()
                .category(category)
                .curationType(CurationType.INSIGHT)
                .sourceUrl(source.getSourceUrl())
                .imageUrl(source.getImageUrl())
                .title(contentB.getTitle())
                .description(contentB.getDescription())
                .curationLevel(contentB.getCurationLevel())
                .build();
        curationRepository.save(curationB);

        log.info("{} 카테고리 인사이트 큐레이션 (Level 1, 2) 생성 완료. (소스: {})", category.getCategoryName(), randomClient.getSourceType());
    }

    /*
     * '크로스노트' 큐레이션 생성 (기능 명세서 2.2-2: '공적문서' 제외)
     */
    private void createCrossNoteCurationForCategory(Category category) {

        // 1. 크로스노트용 클라이언트 필터링 ('DOCUMENT' 타입 제외)
        List<CurationSourceClient> crossNoteClients = allClients.stream()
                .filter(client -> !"DOCUMENT".equals(client.getSourceType()))
                .collect(Collectors.toList());

        if (crossNoteClients.isEmpty()) {
            log.warn("크로스노트용 CurationSourceClient 구현체가 없습니다.");
            return;
        }

        // 2. 크로스노트 클라이언트 중 1개 랜덤 선택
        CurationSourceClient randomClient = crossNoteClients.get(random.nextInt(crossNoteClients.size()));
        log.info("{} 카테고리 '크로스노트' 생성 시도 (소스: {})", category.getCategoryName(), randomClient.getSourceType());

        // 3. 소스 가져오기
        CurationSourceDto source = randomClient.fetchSource(category.getCategoryName());
        if (source == null) {
            log.warn("{} 카테고리에서 소스({})를 찾을 수 없습니다.", category.getCategoryName(), randomClient.getSourceType());
            return;
        }

        // 4. 크로스할 카테고리 찾기
        Category crossCategory = findRandomCrossCategory(category);
        if (crossCategory == null) {
            log.warn("크로스할 카테고리를 찾을 수 없습니다. (원본: {})", category.getCategoryName());
            return;
        }

        // 5. (공통 로직) AI 생성 및 L1 저장
        AiGeneratedContentDto contentA = geminiService.generateContent(source.getOriginalText(), CurationLevel.LEVEL_1);
        terminologyService.assignLevel(contentA);

        Curation curationA = Curation.builder()
                .category(category)
                .crossCategory(crossCategory) // (크로스 카테고리 설정)
                .curationType(CurationType.CROSSNOTE) // (타입: CROSSNOTE)
                .sourceUrl(source.getSourceUrl())
                .imageUrl(source.getImageUrl())
                .title(contentA.getTitle())
                .description(contentA.getDescription())
                .curationLevel(contentA.getCurationLevel())
                .build();
        curationRepository.save(curationA);

        // 6. (공통 로직) L2 생성 및 저장
        AiGeneratedContentDto contentB = geminiService.generateContent(source.getOriginalText(), CurationLevel.LEVEL_2);
        terminologyService.assignLevel(contentB);

        Curation curationB = Curation.builder()
                .category(category)
                .crossCategory(crossCategory)
                .curationType(CurationType.CROSSNOTE)
                .sourceUrl(source.getSourceUrl())
                .imageUrl(source.getImageUrl())
                .title(contentB.getTitle())
                .description(contentB.getDescription())
                .curationLevel(contentB.getCurationLevel())
                .build();
        curationRepository.save(curationB);

        log.info("{} x {} 카테고리 크로스노트 큐레이션 (Level 1, 2) 생성 완료. (소스: {})", category.getCategoryName(), crossCategory.getCategoryName(), randomClient.getSourceType());
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

        // 5. '큐레이션 생성 프로세스'의 우선순위에 따라 정렬
        candidates.sort(Comparator
                .comparing((Curation c) -> getPriority(c.getCurationType()))
                .thenComparing(Curation::getCreatedAt).reversed()
        );

        // 6. 최대 6개로 제한하여 반환
        return candidates.stream()
                .limit(6)
                .map(CurationFeedDto::new) // DTO로 변환
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