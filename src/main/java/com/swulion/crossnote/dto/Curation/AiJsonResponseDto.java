package com.swulion.crossnote.dto.Curation;

import lombok.Getter;
import lombok.NoArgsConstructor;

// AI가 생성한 JSON 문자열을 파싱할 DTO
@Getter
@NoArgsConstructor
public class AiJsonResponseDto {
    private String title;
    private String description;
}