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
    @PostMapping("")
    public ResponseEntity<QuestionResponseDto> createQuestion(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody QuestionRequestDto questionRequestDto) {
        Long userId = userDetails.getUser().getUserId();
        QuestionResponseDto questionResponseDto = questionService.createQuestion(userId, questionRequestDto);
        return ResponseEntity.ok(questionResponseDto);
    }

    /* QNA 홈 */
    @GetMapping("/home")
    public ResponseEntity<List<QuestionResponseDto>> getQuestionHome(@RequestParam(defaultValue = "latest") String sort) {
        List<QuestionResponseDto> questionListDtos = questionService.getQnaHome(sort);
        return ResponseEntity.ok(questionListDtos);
    }

    /* Question 상세 보기 */
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailGetDto> getQuestionDetail(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long questionId) {
        Long userId = userDetails.getUser().getUserId();
        QuestionDetailGetDto questionDetailGetDto = questionService.getQuestionDetail(userId, questionId);
        return ResponseEntity.ok(questionDetailGetDto);
    }

    /* Question 수정 */
    @PatchMapping("")
    public ResponseEntity<QuestionResponseDto> updateQuestion(@RequestBody QuestionUpdateDto questionUpdateDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        QuestionResponseDto questionResponseDto = questionService.updateQuestion(userId, questionUpdateDto);
        return ResponseEntity.ok(questionResponseDto);
    }

    /* Question 삭제 */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<String> deleteQuestion(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long questionId) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(questionService.deleteQuestion(userId, questionId));
    }

    /* Question 좋아요 */
    @PatchMapping("/{questionId}/like")
    public ResponseEntity<String> likeQuestion(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long questionId) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(questionService.likeQuestion(userId, questionId));
    }

    /* Question 검색 */
    @GetMapping("/search")
    public ResponseEntity<List<QuestionResponseDto>> searchQuestion(@RequestBody QuestionSearchDto questionSearchDto) {
        return ResponseEntity.ok(questionService.searchQuestion(questionSearchDto));
    }
}

