package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.balance.BalanceQuizDto;
import com.swulion.crossnote.dto.balance.SubmitAnswerRequest;
import com.swulion.crossnote.dto.balance.AnswerResultDto;
import com.swulion.crossnote.dto.balance.CurationLinkDto;
import com.swulion.crossnote.dto.balance.BalanceHomeDto;
import com.swulion.crossnote.service.BalanceGameService;
import com.swulion.crossnote.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

	// 홈: 오늘의 랜덤 + 분야별 랜덤 동시 조회
	@GetMapping("/home")
	public ResponseEntity<BalanceHomeDto> getHome(
		@RequestParam(name = "parentName", required = false) String parentName
	) {
		return ResponseEntity.ok(balanceGameService.getHome(parentName));
	}

	// 1-1) 상위 카테고리별 밸런스 게임 조회 (기본값: 인문사회)
	@GetMapping("/by-category")
	public ResponseEntity<BalanceQuizDto> getByParentCategory(
		@RequestParam(name = "parentName", required = false) String parentName
	) {
		return ResponseEntity.ok(balanceGameService.getQuizByParentCategory(parentName));
	}

	// 2) 정답 제출
	@PostMapping("/{quizId}/answer")
	public ResponseEntity<AnswerResultDto> submitAnswer(
		@PathVariable("quizId") Long quizId,
		@RequestBody SubmitAnswerRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		// 인증된 사용자인 경우에만 선택 저장
		com.swulion.crossnote.entity.User user = (userDetails != null && userDetails.getUser() != null) 
				? userDetails.getUser() : null;
		return ResponseEntity.ok(balanceGameService.submitAnswer(quizId, request, user));
	}

	// 3) OX 결과 → 동일 카테고리 큐레이션으로 이동
	@GetMapping("/{quizId}/curation/ox")
	public ResponseEntity<CurationLinkDto> getCurationForOx(@PathVariable("quizId") Long quizId) {
		return ResponseEntity.ok(balanceGameService.getCurationForOx(quizId));
	}

	// 4) 선호도 결과 → 선택지 카테고리 큐레이션으로 이동
	@GetMapping("/{quizId}/curation/preference")
	public ResponseEntity<CurationLinkDto> getCurationForPreference(
		@PathVariable("quizId") Long quizId,
		@RequestParam("optionId") Long optionId
	) {
		return ResponseEntity.ok(balanceGameService.getCurationForPreference(quizId, optionId));
	}

	// 5) 선호도 → 다른 선택지 큐레이션
	@GetMapping("/{quizId}/other-curation")
	public ResponseEntity<CurationLinkDto> getOtherCuration(
		@PathVariable("quizId") Long quizId,
		@RequestParam("currentOptionId") Long currentOptionId
	) {
		return ResponseEntity.ok(balanceGameService.getOtherOptionCuration(quizId, currentOptionId));
	}
}


