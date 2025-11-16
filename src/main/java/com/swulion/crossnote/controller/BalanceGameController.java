package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.balance.BalanceQuizDto;
import com.swulion.crossnote.dto.balance.SubmitAnswerRequest;
import com.swulion.crossnote.dto.balance.AnswerResultDto;
import com.swulion.crossnote.service.BalanceGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/balance-games")
public class BalanceGameController {

	private final BalanceGameService balanceGameService;

	// 1) 오늘의 밸런스 게임 조회
	@GetMapping("/today")
	public ResponseEntity<BalanceQuizDto> getTodayQuiz() {
		return ResponseEntity.ok(balanceGameService.getTodayQuiz());
	}

	// 2) 정답 제출
	@PostMapping("/{quizId}/answer")
	public ResponseEntity<AnswerResultDto> submitAnswer(
		@PathVariable("quizId") Long quizId,
		@RequestBody SubmitAnswerRequest request
	) {
		return ResponseEntity.ok(balanceGameService.submitAnswer(quizId, request));
	}
}


