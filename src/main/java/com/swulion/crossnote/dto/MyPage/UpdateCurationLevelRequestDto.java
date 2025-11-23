package com.swulion.crossnote.dto.MyPage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCurationLevelRequestDto {
    private String curationLevel; // "LEVEL_1" 또는 "LEVEL_2"
}

