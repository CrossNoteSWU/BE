package com.swulion.crossnote.dto.Question;

import com.swulion.crossnote.entity.QA.Answer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class QuestionDetailGetDto {
    QuestionResponseDto question;
    List<AnswerResponseDto> answers;
}
