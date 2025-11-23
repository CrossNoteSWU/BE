package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.MyPage.KnowledgeReportResponseDto;
import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.entity.Curation.Scrap;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.entity.balance.UserBalanceChoice;
import com.swulion.crossnote.repository.CategoryRepository;
import com.swulion.crossnote.repository.Curation.CurationRepository;
import com.swulion.crossnote.repository.Curation.ScrapRepository;
import com.swulion.crossnote.repository.UserRepository;
import com.swulion.crossnote.repository.balance.UserBalanceChoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KnowledgeReportService {

    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final CurationRepository curationRepository;
    private final UserBalanceChoiceRepository userBalanceChoiceRepository;
    private final CategoryRepository categoryRepository;

    // 6개 상위 분야
    private static final String[] UPPER_LEVEL_CATEGORIES = {
            "인문사회", "자연과학", "공학·기술", "경제·경영", "예술·문화", "스포츠·라이프스타일"
    };

    public KnowledgeReportResponseDto getKnowledgeReport(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 초기화: 6개 상위 분야별 점수
        Map<String, Integer> scores = new HashMap<>();
        for (String category : UPPER_LEVEL_CATEGORIES) {
            scores.put(category, 0);
        }

        // 1. 스크랩한 큐레이션으로부터 점수 계산
        List<Scrap> scraps = scrapRepository.findByUserAndTargetTypeOrderByCreatedAtDesc(user, ScrapTargetType.CURATION);
        for (Scrap scrap : scraps) {
            Curation curation = curationRepository.findById(scrap.getTargetId()).orElse(null);
            if (curation == null) continue;

            String categoryName = curation.getCategory().getCategoryName();
            Category category = categoryRepository.findByCategoryName(categoryName);
            if (category == null) continue;

            String upperLevelCategory = getUpperLevelCategory(category);
            if (upperLevelCategory == null) continue;

            // 조건 1: Insight 큐레이션 스크랩 시 +1
            if (curation.getCurationType() == CurationType.INSIGHT) {
                scores.put(upperLevelCategory, scores.get(upperLevelCategory) + 1);
            }
            // 조건 2: Crossnote 큐레이션 스크랩 시
            else if (curation.getCurationType() == CurationType.CROSSNOTE) {
                Category crossCategory = curation.getCrossCategory();
                if (crossCategory != null) {
                    String crossCategoryName = crossCategory.getCategoryName();
                    Category crossCategoryEntity = categoryRepository.findByCategoryName(crossCategoryName);
                    if (crossCategoryEntity != null) {
                        String crossUpperLevelCategory = getUpperLevelCategory(crossCategoryEntity);
                        if (crossUpperLevelCategory != null) {
                            // 같은 상위 분야면 +2, 다르면 각각 +1
                            if (upperLevelCategory.equals(crossUpperLevelCategory)) {
                                scores.put(upperLevelCategory, scores.get(upperLevelCategory) + 2);
                            } else {
                                scores.put(upperLevelCategory, scores.get(upperLevelCategory) + 1);
                                scores.put(crossUpperLevelCategory, scores.get(crossUpperLevelCategory) + 1);
                            }
                        }
                    }
                }
            }
        }

        // 2. 밸런스 게임 선택으로부터 점수 계산
        List<UserBalanceChoice> choices = userBalanceChoiceRepository.findByUser(user);
        for (UserBalanceChoice choice : choices) {
            String categoryName = choice.getCategory();
            if (categoryName == null) continue;

            Category category = categoryRepository.findByCategoryName(categoryName);
            if (category == null) continue;

            String upperLevelCategory = getUpperLevelCategory(category);
            if (upperLevelCategory == null) continue;

            // 조건 3: 밸런스 게임 선택 시 +1
            scores.put(upperLevelCategory, scores.get(upperLevelCategory) + 1);
        }

        // 차트 데이터 생성 (0, 10, 20, 30, 40, 50 스케일로 변환)
        Map<String, Integer> chartData = new HashMap<>();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            int score = entry.getValue();
            // 50 이상이면 50으로 제한
            if (score >= 50) {
                chartData.put(entry.getKey(), 50);
            } else {
                // 가장 가까운 스케일 값으로 변환 (0, 10, 20, 30, 40, 50)
                int scaledValue = ((score + 5) / 10) * 10; // 반올림
                if (scaledValue > 50) scaledValue = 50;
                chartData.put(entry.getKey(), scaledValue);
            }
        }

        return new KnowledgeReportResponseDto(scores, chartData);
    }

    private String getUpperLevelCategory(Category category) {
        if (category == null) return null;
        
        // 상위 카테고리까지 올라가기
        Category current = category;
        while (current.getParentCategoryId() != null) {
            current = current.getParentCategoryId();
        }
        
        String categoryName = current.getCategoryName();
        // 6개 상위 분야 중 하나인지 확인
        for (String upper : UPPER_LEVEL_CATEGORIES) {
            if (upper.equals(categoryName)) {
                return categoryName;
            }
        }
        return null;
    }
}

