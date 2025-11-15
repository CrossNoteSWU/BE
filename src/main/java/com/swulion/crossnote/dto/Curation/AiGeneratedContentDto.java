package com.swulion.crossnote.dto.Curation;

import com.swulion.crossnote.entity.Curation.CurationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AiGeneratedContentDto {
    private String title;
    private String description;
    private CurationLevel curationLevel;
    private Double terminologyDensity = 0.0;

    //GeminiService에서 사용하는 3파라미터 생성자 추가
    public AiGeneratedContentDto(String title, String description, CurationLevel level) {
        this.title = title;
        this.description = description;
        this.curationLevel = level;
        this.terminologyDensity = 0.0; // 기본값
    }
}
