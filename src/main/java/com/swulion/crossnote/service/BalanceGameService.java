package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.balance.BalanceQuizDto;
import com.swulion.crossnote.dto.balance.SubmitAnswerRequest;
import com.swulion.crossnote.dto.balance.AnswerResultDto;
import com.swulion.crossnote.entity.balance.BalanceOption;
import com.swulion.crossnote.entity.balance.BalanceQuiz;
import com.swulion.crossnote.entity.balance.QuizType;
import com.swulion.crossnote.repository.BalanceOptionRepository;
import com.swulion.crossnote.repository.BalanceQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class BalanceGameService {
	private final BalanceQuizRepository quizRepository;
	private final BalanceOptionRepository optionRepository;

	// 오늘의 퀴즈 랜덤 조회
	public BalanceQuizDto getTodayQuiz() {
		List<BalanceQuiz> active = quizRepository.findByActiveTrue();
		if (active.isEmpty()) {
			throw new IllegalStateException("등록된 밸런스 퀴즈가 없습니다.");
		}
		int idx = ThreadLocalRandom.current().nextInt(active.size());
		BalanceQuiz quiz = active.get(idx);
		List<BalanceOption> options = optionRepository.findByQuizId(quiz.getId());
		return BalanceQuizDto.of(quiz, options);
	}

	// 정답 제출
	public AnswerResultDto submitAnswer(Long quizId, SubmitAnswerRequest request) {
		BalanceQuiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new IllegalArgumentException("퀴즈가 존재하지 않습니다."));

		if (quiz.getType() == QuizType.OX) {
			if (request.getOxAnswer() == null) {
				throw new IllegalArgumentException("OX 정답이 필요합니다.");
			}
			boolean correct = Boolean.TRUE.equals(quiz.getOxAnswer()) == Boolean.TRUE.equals(request.getOxAnswer());
			return AnswerResultDto.forOx(correct);
		}

		// 선호도
		if (request.getOptionId() == null) {
			throw new IllegalArgumentException("선호도 선택 optionId가 필요합니다.");
		}
		BalanceOption option = optionRepository.findById(request.getOptionId())
			.orElseThrow(() -> new IllegalArgumentException("선택지가 존재하지 않습니다."));
		if (!option.getQuiz().getId().equals(quizId)) {
			throw new IllegalArgumentException("퀴즈와 선택지가 일치하지 않습니다.");
		}
		return AnswerResultDto.forPreference();
	}
}


