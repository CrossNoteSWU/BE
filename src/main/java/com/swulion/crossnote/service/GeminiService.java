// service/GeminiService.java
package com.swulion.crossnote.service;

import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.dto.Curation.AiGeneratedContentDto;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final ApiKeys apiKeys;
    // private final RestTemplate restTemplate; // 실제로는 Gemini API용 RestTemplate 필요

    // (Mock) 원본 텍스트를 받아 레벨(A/B)을 분석하는 메서드
    public CurationLevel analyzeLevel(String originalText) {
        // Mock 로직: 텍스트에 "논문" 단어가 포함되면 LEVEL_2로 판단
        if (originalText.contains("논문")) {
            return CurationLevel.LEVEL_2;
        }
        return CurationLevel.LEVEL_1;
    }

    // (Mock) 원본 텍스트와 목표 레벨을 받아 제목/요약을 생성하는 메서드
    public AiGeneratedContentDto generateContent(String originalText, CurationLevel targetLevel) {
        // restTemplate.postForObject(...) 등을 사용하여 Gemini API에 요청

        // Mock 로직 시작
        String mockTitle, mockDesc;
        if (targetLevel == CurationLevel.LEVEL_1) {
            mockTitle = "[쉬움] AI가 만든 제목 (최대 100자)";
            mockDesc = "이것은 AI가 요약한 쉬운 소개글입니다. (최대 300자)";
        } else {
            mockTitle = "[전문] AI 생성 제목 (최대 100자)";
            mockDesc = "AI가 방법론과 함의를 포함해 요약한 전문 소개글입니다. (최대 300자)";
        }
        // Mock 로직 종료

        return new AiGeneratedContentDto(mockTitle, mockDesc, targetLevel);
    }
}