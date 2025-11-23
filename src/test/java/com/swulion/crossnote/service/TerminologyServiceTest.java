package com.swulion.crossnote.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TerminologyServiceTest {

    private TerminologyService terminologyService;

    // TerminologyService의 임계값 정의를 가져옵니다. (테스트에 필요)
    private static final double DENSITY_THRESHOLD = 0.03; // 3%
    private static final int ABS_TERM_COUNT_THRESHOLD = 2; // 전문용어 최소 2개

    @BeforeEach
    void setUp() {
        // 실제 TerminologyService 객체 초기화
        terminologyService = new TerminologyService();
        terminologyService.init();
    }

    @Test
    void testAnalyzeSourceText_forPhilosophy_shouldBeLevel2() {

        // 1. 테스트할 원문 텍스트와 카테고리 ID 리스트 준비
        String sourceText =
                "이 논문은 존재론과 인식론적 관점에서 형이상학적 질문을 다룬다. 윤리학적 분석 또한 포함한다. 아주 평이한 논문.";

        List<Long> categoryIds = List.of(7L);  // 철학 categoryId

        // 2. 새로운 메서드 analyzeSourceText 호출
        TerminologyService.TermCountResult result =
                terminologyService.analyzeSourceText(sourceText, categoryIds);

        // 3. 용어 밀도 및 개수 검증 (Level_2 기준 수동 검증)

        // 밀도가 0보다 큰지 확인
        assertTrue(result.getDensity() > 0.0,
                "TerminologyDensity should be > 0");

        // 전문 용어 개수가 임계값(2개) 이상인지 확인 (존재론, 인식론, 형이상학, 윤리학 등)
        assertTrue(result.getCount() >= ABS_TERM_COUNT_THRESHOLD,
                "Term count should meet the LEVEL_2 threshold.");

        // Level_2 기준 (개수 >= 2개 OR 밀도 >= 3%)을 수동으로 검증
        boolean isLevel2 = result.getCount() >= ABS_TERM_COUNT_THRESHOLD ||
                result.getDensity() >= DENSITY_THRESHOLD;

        // Level_2로 분류될 것이라고 가정하고 검증 (assert_true는 Level_2를 의미)
        assertTrue(isLevel2, "The content should be classified as LEVEL_2 based on terminology analysis.");
    }
}