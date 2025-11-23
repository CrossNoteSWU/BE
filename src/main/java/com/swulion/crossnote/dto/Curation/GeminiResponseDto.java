package com.swulion.crossnote.dto.Curation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// Gemini API의 응답을 받을 DTO
@Getter
@NoArgsConstructor
public class GeminiResponseDto {
    private List<Candidate> candidates;
    @Getter @NoArgsConstructor public static class Candidate { private Content content; }
    @Getter @NoArgsConstructor public static class Content { private List<Part> parts; }
    @Getter @NoArgsConstructor public static class Part { private String text; }

    // AI가 생성한 텍스트(JSON 문자열)를 반환
    public String getFirstText() {
        try { return candidates.get(0).content.parts.get(0).text; }
        catch (Exception e) { return null; }
    }
}