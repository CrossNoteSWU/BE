package com.swulion.crossnote.dto.MyPage;

import com.swulion.crossnote.entity.Curation.CurationLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserPreferencesResponseDto {
    private List<String> interestNames; // 관심 분야 목록
    private List<String> expertiseNames; // 전문 분야 목록
    private CurationLevel curationLevel; // 큐레이션 수준
}

