package com.swulion.crossnote.dto.MyPage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyProfileResponseDto {
    private Long userId;
    private String name;
    private String profileImageUrl;
    private long followersCount;
    private long followingsCount;
}

