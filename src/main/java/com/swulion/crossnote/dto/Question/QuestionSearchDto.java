package com.swulion.crossnote.dto.Question;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class QuestionSearchDto {
    List<Long> categoryIds;
    String keyword;
}
