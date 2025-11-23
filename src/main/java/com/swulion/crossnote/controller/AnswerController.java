package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Question.AnswerCreateDto;
import com.swulion.crossnote.dto.Question.AnswerResponseDto;
import com.swulion.crossnote.dto.Question.AnswerUpdateDto;
import com.swulion.crossnote.entity.QA.Answer;
import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.service.AnswerService;
import com.swulion.crossnote.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("/create")
    public ResponseEntity<AnswerResponseDto> createAnswer(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AnswerCreateDto answerCreateDto) {
        Long userId = userDetails.getUser().getUserId();
        AnswerResponseDto answerResponseDto = answerService.createAnswer(userId, answerCreateDto);
        return ResponseEntity.ok(answerResponseDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<AnswerResponseDto> updateAnswer(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AnswerUpdateDto answerUpdateDto) {
        Long userId = userDetails.getUser().getUserId();
        AnswerResponseDto answerResponseDto = answerService.updateAnswer(userId, answerUpdateDto);
        return ResponseEntity.ok(answerResponseDto);
    }

    @DeleteMapping("/delete/{answerId}")
    public ResponseEntity<String> deleteAnswer(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long answerId) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(answerService.deleteAnswer(userId, answerId));
    }

}
