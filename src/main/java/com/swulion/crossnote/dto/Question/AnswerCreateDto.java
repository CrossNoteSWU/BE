package com.swulion.crossnote.dto.Question;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerCreateDto {
    private Long questionId;
    private String content;
}
