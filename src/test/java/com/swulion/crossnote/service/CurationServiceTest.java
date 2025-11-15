package com.swulion.crossnote.service;

import com.swulion.crossnote.client.CurationSourceClient;
import com.swulion.crossnote.dto.Curation.AiGeneratedContentDto;
import com.swulion.crossnote.dto.Curation.CurationSourceDto;
import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.entity.Curation.CurationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CurationServiceLogUnitTest {

    private List<CurationSourceClient> allClients;
    private GeminiService geminiService;
    private TerminologyService terminologyService;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        allClients = new ArrayList<>();
        geminiService = mock(GeminiService.class);
        terminologyService = mock(TerminologyService.class);

        // Mock client 생성
        CurationSourceClient client = mock(CurationSourceClient.class);
        CurationSourceDto sourceDto = mock(CurationSourceDto.class);
        when(sourceDto.getOriginalText()).thenReturn("원문 테스트");
        when(sourceDto.getSourceUrl()).thenReturn("https://source.url");
        when(sourceDto.getImageUrl()).thenReturn("https://image.url");
        when(client.fetchSource(anyString())).thenReturn(sourceDto);
        when(client.getSourceType()).thenReturn("INSIGHT");
        allClients.add(client);

        // 29개 테스트 카테고리 생성
        categories = new ArrayList<>();
        for (int i = 1; i <= 29; i++) {
            Category category = new Category();
            category.setCategoryName("테스트카테고리" + i);
            categories.add(category);
        }

        // GeminiService mock
        when(geminiService.generateContent(anyString(), any()))
                .thenAnswer(invocation -> new AiGeneratedContentDto(
                        "제목 " + invocation.getArgument(1),
                        "설명 " + invocation.getArgument(1),
                        invocation.getArgument(1)
                ));
    }

    @Test
    void testCreateDailyCurationsLogsOnly() {
        System.out.println("=== 데일리 큐레이션 생성 시작 ===");

        for (Category category : categories) {
            logCuration(category, CurationType.INSIGHT, CurationLevel.LEVEL_1);
            logCuration(category, CurationType.CROSSNOTE, CurationLevel.LEVEL_1);
            logCuration(category, CurationType.INSIGHT, CurationLevel.LEVEL_2);
            logCuration(category, CurationType.CROSSNOTE, CurationLevel.LEVEL_2);
        }

        System.out.println("=== 데일리 큐레이션 생성 완료 ===");
    }

    private void logCuration(Category category, CurationType type, CurationLevel level) {
        CurationSourceClient client = allClients.get(0);
        CurationSourceDto source = client.fetchSource(category.getCategoryName());
        AiGeneratedContentDto content = geminiService.generateContent(source.getOriginalText(), level);
        terminologyService.assignLevel(content);

        System.out.printf("[LOG] 카테고리: %s, 타입: %s, 레벨: %s, 제목: %s%n",
                category.getCategoryName(),
                type,
                level,
                content.getTitle()
        );
    }
}
