package com.swulion.crossnote.dto.Column;

import com.swulion.crossnote.entity.User;

public record ColumnListDto(Long columnId, User columnAutherId,
                            String title, String content, String imageUrl, Integer likeCount, Integer commentCount) {
}
