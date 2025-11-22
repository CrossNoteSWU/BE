package com.swulion.crossnote.dto.Question;

public record QuestionListDto(Long questionId, String title, String content, Integer likeCount, Integer answerCount) {
}
