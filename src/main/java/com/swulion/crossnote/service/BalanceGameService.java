package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.balance.BalanceQuizDto;
import com.swulion.crossnote.entity.balance.BalanceOption;
import com.swulion.crossnote.entity.balance.BalanceQuiz;
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
}


