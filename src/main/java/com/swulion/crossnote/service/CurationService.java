package com.swulion.crossnote.service;

import com.swulion.crossnote.client.NaverNewsClient;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

    private final NaverNewsClient naverNewsClient;
    private final GeminiService geminiService;
    // (YoutubeClient, DbpiaClient 등 추가 주입 필요)
    
    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시 0분 0초
    @Transactional
    public void createDailyCurations() {
        log.info("데일리 큐레이션 생성 작업을 시작합니다...");

        // 카테고리 목록 가져오기
        List<Category> categories = categoryRepository.findByParentCategoryIdIsNotNull();

        for (Category category : categories) {
            // 인사이트 타입 생성
            createInsightCurationForCategory(category);
            // 크로스노트 타입 생성
            createCrossNoteCurationForCategory(category);
        }
        log.info("데일리 큐레이션 생성 작업 완료.");
    }

    private void createInsightCurationForCategory(Category category) {
        NaverNewsResponseDto.Item newsItem = naverNewsClient.fetchNews(category.getCategoryName());
        if (newsItem == null) {
            log.warn("{} 카테고리의 뉴스를 찾을 수 없습니다.", category.getCategoryName());
            return;
        }
        String originalText = newsItem.getTitle() + " " + newsItem.getDescription();
        String sourceUrl = newsItem.getLink();

        // LEVEL_1 (일반/기초) 큐레이션 생성
        AiGeneratedContentDto contentA = geminiService.generateContent(originalText, CurationLevel.LEVEL_1);
        Curation curationA = Curation.builder()
                .category(category)
                .curationType(CurationType.INSIGHT)
                .sourceUrl(sourceUrl)
                .imageUrl(null)
                .title(contentA.getTitle())
                .description(contentA.getDescription())
                .curationLevel(CurationLevel.LEVEL_1)
                .build();
        curationRepository.save(curationA);

        // LEVEL_2 (전문/심화) 큐레이션 생성
        AiGeneratedContentDto contentB = geminiService.generateContent(originalText, CurationLevel.LEVEL_2);
        Curation curationB = Curation.builder()
                .category(category)
                .curationType(CurationType.INSIGHT)
                .sourceUrl(sourceUrl)
                .imageUrl(null)
                .title(contentB.getTitle())
                .description(contentB.getDescription())
                .curationLevel(CurationLevel.LEVEL_2)
                .build();
        curationRepository.save(curationB);

        log.info("{} 카테고리 인사이트 큐레이션 (Level 1, 2) 생성 완료.", category.getCategoryName());
    }

    private void createCrossNoteCurationForCategory(Category category) {
        // TODO: 크로스노트 로직 구현 (RestTemplate 사용)
        log.info("{} 카테고리 크로스노트 큐레이션 (Level 1, 2) 생성 완료.", category.getCategoryName());
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