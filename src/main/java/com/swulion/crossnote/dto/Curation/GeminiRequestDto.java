package com.swulion.crossnote.dto.Curation;

import com.swulion.crossnote.config.GenerationConfig;
import lombok.Getter;
import java.util.List;

// Gemini API에 보낼 요청 DTO
@Getter
public class GeminiRequestDto {
    private List<Content> contents;
    private GenerationConfig generationConfig; // JSON 응답을 위한 설정

    public GeminiRequestDto(String text, GenerationConfig config) {
        this.contents = List.of(new Content(List.of(new Part(text))));
        this.generationConfig = config;
    }
    @Getter static class Content { private List<Part> parts; public Content(List<Part> p) { this.parts = p; }}
    @Getter static class Part { private String text; public Part(String t) { this.text = t; }}
}