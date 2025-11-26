package com.swulion.crossnote.dto.Question;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QuestionSearchDto {
    Long categoryId;
    String keyword;
}
