package com.swulion.crossnote.dto.Question;

import java.time.LocalDateTime;


public record QuestionResponseDto (
        Long questionerId, String title, String content, Integer likeCount,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        String category1, String category2, String category3) {

}