package com.swulion.crossnote.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.config.GenerationConfig;
import com.swulion.crossnote.dto.Curation.*;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate;
    private final ApiKeys apiKeys;
    private final ObjectMapper objectMapper;

    // 2.0 모델 URL
    private static final String GEMINI_2_0_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent?key=";

    // 2.5 모델 URL
    // private static final String GEMINI_2_5_API_URL =
    //        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";

    public AiGeneratedContentDto generateContent(String originalText, CurationLevel targetLevel) {

        String prompt = createPrompt(originalText, targetLevel);

        GenerationConfig config = new GenerationConfig(); // responseMimeType=application/json 자동 포함됨

        GeminiRequestDto requestBody = new GeminiRequestDto(prompt, config);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(requestBody, headers);
        String apiUrl = GEMINI_2_0_API_URL + apiKeys.getGemini(); // 2.0 모델 호출

        try {
            ResponseEntity<GeminiResponseDto> response = restTemplate.postForEntity(
                    apiUrl, entity, GeminiResponseDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {

                String jsonText = response.getBody().getFirstText();

                // 개행/비정상 문자 제거 (예방장치)
                jsonText = jsonText.replace("\n", " ").replace("\r", " ");

                // 2.0
                AiJsonResponseDto[] aiJsonArray = objectMapper.readValue(jsonText, AiJsonResponseDto[].class);
                AiJsonResponseDto aiJson = aiJsonArray[0]; // 첫 번째 항목 사용

                // 2.5
                //AiJsonResponseDto aiJson = objectMapper.readValue(jsonText, AiJsonResponseDto.class);

                return new AiGeneratedContentDto(
                        aiJson.getTitle(),
                        aiJson.getDescription(),
                        targetLevel
                );
            }

            log.warn("Gemini API가 200이 아닌 응답 반환: {}", response.getStatusCode());

        } catch (Exception e) {
            log.error("Gemini API 호출 또는 파싱 실패: {}", e.getMessage(), e);
        }

        return new AiGeneratedContentDto("[AI 호출 실패]", "AI 서버와 통신 중 오류가 발생했습니다.", targetLevel);
    }

    // Prompt 생성
    private String createPrompt(String originalText, CurationLevel targetLevel) {

        final String LEVEL_A_PROMPT =
                "다음 텍스트를 분석해서, 일반인과 초심자를 위한 Level A 제목과 요약을 만들어주세요. " +
                        "- 반드시 JSON 형식으로만 출력: {\"title\": \"...\", \"description\": \"...\"} " +
                        "- 제목: 공백 제외 20자 이내, 호기심을 유발하는 자연스러운 한 줄 " +
                        "- 요약: 공백 제외 100자 이내, 핵심 메시지와 큰 그림 중심, 어려운 전문 용어는 쉬운 표현으로 대체 " +
                        "- JSON 외 다른 출력 금지, 개행/줄바꿈 없이 한 줄로 작성 " +
                        "[원본 텍스트]: %s";

        final String LEVEL_B_PROMPT =
                "다음 텍스트를 분석해서, 전공자와 전문가를 위한 Level B 제목과 요약을 만들어주세요. " +
                        "- 반드시 JSON 형식으로만 출력: {\"title\": \"...\", \"description\": \"...\"} " +
                        "- 제목: 공백 제외 20자 이내, 학술적이고 정보 집중형 " +
                        "- 요약: 공백 제외 100자 이내, 연구 배경, 방법론, 결과 및 함의 포함, 핵심 전문 용어 유지 " +
                        "- JSON 외 다른 출력 금지, 개행/줄바꿈 없이 한 줄로 작성 " +
                        "[원본 텍스트]: %s";

        // Level에 따라 프롬프트 선택
        String promptTemplate = targetLevel == CurationLevel.LEVEL_1 ? LEVEL_A_PROMPT : LEVEL_B_PROMPT;

        return String.format(promptTemplate, originalText);
    }
}
