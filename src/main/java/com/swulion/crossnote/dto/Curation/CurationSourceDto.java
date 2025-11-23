package com.swulion.crossnote.dto.Curation;

import lombok.Builder;
import lombok.Getter;

// 모든 API 클라이언트가 CurationService에 반환할 표준 DTO
@Getter
@Builder
public class CurationSourceDto {
    private String originalText; // 원본 텍스트 (Gemini가 요약할 재료)
    private String sourceUrl;    // 원본 링크 (사용자가 클릭할 URL)
    private String imageUrl;     // 썸네일 이미지 URL

    private String title;
}