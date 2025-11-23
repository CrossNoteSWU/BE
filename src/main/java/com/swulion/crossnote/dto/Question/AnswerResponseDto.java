package com.swulion.crossnote.dto.Question;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnswerResponseDto {
    private Long answerId;
    private String answerer;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
