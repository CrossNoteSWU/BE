package com.swulion.crossnote.dto.Question;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnswerUpdateDto {
    private Long answerId;
    private String content;
}
