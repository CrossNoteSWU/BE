package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Curation.AiGeneratedContentDto;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminologyServiceTest {

    private TerminologyService terminologyService;

    @BeforeEach
    void setUp() {
        terminologyService = new TerminologyService();
        terminologyService.init();   // @PostConstruct 수동 호출
    }

    @Test
    void testAssignLevel_forPhilosophy() {

        AiGeneratedContentDto dto = new AiGeneratedContentDto();
        dto.setDescription(
                "이 논문은 존재론과 인식론적 관점에서 형이상학적 질문을 다룬다. " +
                        "윤리학적 분석 또한 포함한다."
        );

        terminologyService.assignLevel(dto, 7L);  // 철학 categoryId

        // density 측정이 0보다 큰지 확인
        assertTrue(dto.getTerminologyDensity() > 0.0,
                "terminologyDensity should be > 0");

        // Level_2인지 확인
        assertEquals(CurationLevel.LEVEL_2, dto.getCurationLevel());
    }
}
