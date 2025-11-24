package com.swulion.crossnote.dto.MyPage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateExpertiseRequestDto {
    private List<String> expertiseNames; // 전문 분야 카테고리 이름 목록
}

