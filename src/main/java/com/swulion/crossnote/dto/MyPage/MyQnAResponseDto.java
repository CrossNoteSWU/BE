package com.swulion.crossnote.dto.MyPage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MyQnAResponseDto {
    private Long questionId;
    private String questionTitle;
    private String questionContent;
    private LocalDateTime questionCreatedAt;
    private List<AnswerDto> answers;
    
    @Getter
    @AllArgsConstructor
    public static class AnswerDto {
        private Long answerId;
        private String content;
        private LocalDateTime createdAt;
        private Long authorId; // 작성자 ID (본인 작성 여부 확인용)
    }
}

