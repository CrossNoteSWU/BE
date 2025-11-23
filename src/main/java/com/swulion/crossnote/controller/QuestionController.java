package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Question.QuestionListDto;
import com.swulion.crossnote.dto.Question.QuestionRequestDto;
import com.swulion.crossnote.dto.Question.QuestionResponseDto;
import com.swulion.crossnote.service.CustomUserDetails;
import com.swulion.crossnote.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    /* 질문 생성 */
    @PostMapping("/create")
    public ResponseEntity<QuestionResponseDto> createQuestion(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody QuestionRequestDto questionRequestDto) {
        Long userId = userDetails.getUser().getUserId();
        QuestionResponseDto questionResponseDto = questionService.createQuestion(userId, questionRequestDto);
        return ResponseEntity.ok(questionResponseDto);
    }

    /* QNA 홈 */
    @GetMapping("/home")
    public ResponseEntity<List<QuestionListDto>> getQuestionHome(){
        List<QuestionListDto> questionListDtos = questionService.getQnaHome();
        return ResponseEntity.ok(questionListDtos);
    }
}
