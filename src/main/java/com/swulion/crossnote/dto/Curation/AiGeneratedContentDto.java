package com.swulion.crossnote.dto.Curation;

import com.swulion.crossnote.entity.Curation.CurationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiGeneratedContentDto {
    private String title;
    private String description;
    private CurationLevel curationLevel;
}