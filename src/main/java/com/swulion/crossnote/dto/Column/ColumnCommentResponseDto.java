package com.swulion.crossnote.dto.Column;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ColumnCommentResponseDto {
    private Long columnCommentId;
    private Long columnId;
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
