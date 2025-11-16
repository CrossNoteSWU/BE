package com.swulion.crossnote.dto.Column;

import java.time.LocalDateTime;

public record ColumnDetailResponseDto(Long columnAuthorId, String title, String content,
                                      Integer likeCount, Integer commentCount, Boolean isBestColumn, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt,
                                      Long categoryId1, Long categoryId2, Long categoryId3) {
}
