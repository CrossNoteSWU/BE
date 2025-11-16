package com.swulion.crossnote.dto.Follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowUserSummaryDto {

    private final Long userId;
    private final String name;
    private final String profileImageUrl;
}

