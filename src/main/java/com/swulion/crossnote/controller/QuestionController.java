package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Question.*;
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
    public ResponseEntity<List<QuestionListDto>> getQuestionHome(@RequestParam(defaultValue = "latest") String sort) {
        List<QuestionListDto> questionListDtos = questionService.getQnaHome(sort);
        return ResponseEntity.ok(questionListDtos);
    }

    @GetMapping("/detail/{columnId}")
    public ResponseEntity<QuestionDetailGetDto> getQuestionDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long columnId) {
        Long userId = userDetails.getUser().getUserId();
        QuestionDetailGetDto questionDetailGetDto = questionService.getQuestionDetail(userId, columnId);
        return ResponseEntity.ok(questionDetailGetDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<QuestionResponseDto> updateQuestion(@RequestBody QuestionUpdateDto questionUpdateDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        QuestionResponseDto questionResponseDto = questionService.updateQuestion(userId, questionUpdateDto);
        return ResponseEntity.ok(questionResponseDto);
    }

    @DeleteMapping("/delete/{columnId}")
    public ResponseEntity<String> deleteQuestion(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long columnId) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(questionService.deleteQuestion(userId, columnId));
    }
}

