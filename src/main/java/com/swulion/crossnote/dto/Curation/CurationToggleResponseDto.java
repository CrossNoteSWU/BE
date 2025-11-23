package com.swulion.crossnote.dto.Curation;

import lombok.AllArgsConstructor;
import lombok.Getter;

// FE 버튼 상태 갱신 시
@Getter
@AllArgsConstructor
public class CurationToggleResponseDto {
    private boolean isToggled; // true=좋아요/스크랩됨, false=취소됨
    private Long count; // 갱신된 총 카운트
}
