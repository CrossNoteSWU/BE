package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.balance.BalanceQuizDto;
import com.swulion.crossnote.dto.balance.SubmitAnswerRequest;
import com.swulion.crossnote.dto.balance.AnswerResultDto;
import com.swulion.crossnote.dto.balance.CurationLinkDto;
import com.swulion.crossnote.dto.balance.BalanceHomeDto;
import com.swulion.crossnote.entity.balance.BalanceOption;
import com.swulion.crossnote.entity.balance.BalanceQuiz;
import com.swulion.crossnote.entity.balance.QuizType;
import com.swulion.crossnote.repository.BalanceOptionRepository;
import com.swulion.crossnote.repository.BalanceQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.repository.CategoryRepository;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BalanceGameService {
	private final BalanceQuizRepository quizRepository;
	private final BalanceOptionRepository optionRepository;
	private final CurationSelectorService curationSelectorService;
	private final CategoryRepository categoryRepository;

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

	// 상위 카테고리(인문사회 등) 하위의 세부 카테고리들에서 랜덤 퀴즈 조회
	public BalanceQuizDto getQuizByParentCategory(String parentNameOrNull) {
		String parentName = (parentNameOrNull == null || parentNameOrNull.isBlank()) ? "인문사회" : parentNameOrNull;
		Category parent = categoryRepository.findByCategoryName(parentName);
		if (parent == null) {
			throw new IllegalArgumentException("존재하지 않는 상위 카테고리: " + parentName);
		}
		List<Category> children = categoryRepository.findByParentCategoryId(parent);
		if (children.isEmpty()) {
			throw new IllegalStateException("해당 상위 카테고리에 세부 카테고리가 없습니다: " + parentName);
		}
		List<String> childNames = children.stream().map(Category::getCategoryName).collect(Collectors.toList());
		List<BalanceQuiz> quizzes = quizRepository.findByActiveTrueAndCategoryIn(childNames);
		if (quizzes.isEmpty()) {
			throw new IllegalStateException("해당 카테고리에서 진행 가능한 퀴즈가 없습니다: " + parentName);
		}
		int idx = ThreadLocalRandom.current().nextInt(quizzes.size());
		BalanceQuiz quiz = quizzes.get(idx);
		List<BalanceOption> options = optionRepository.findByQuizId(quiz.getId());
		return BalanceQuizDto.of(quiz, options);
	}

	// 홈: 오늘의 랜덤 + 분야별 랜덤 동시 조회
	public BalanceHomeDto getHome(String parentNameOrNull) {
		String parentName = (parentNameOrNull == null || parentNameOrNull.isBlank()) ? "인문사회" : parentNameOrNull;
		BalanceQuizDto today = getTodayQuiz();
		BalanceQuizDto byCategory = getQuizByParentCategory(parentName);
		return new BalanceHomeDto(today, byCategory, parentName);
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
			// 두 번째 연속 오답 처리: previousWrong=true 이고 이번에도 오답이면 이동 메시지 반환
			if (!correct && Boolean.TRUE.equals(request.getPreviousWrong())) {
				return new AnswerResultDto(false, "관련 큐레이션으로 이동할게요", null);
			}
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

	// OX 결과에서 동일 카테고리 큐레이션으로 이동
	public CurationLinkDto getCurationForOx(Long quizId) {
		BalanceQuiz quiz = quizRepository.findById(quizId)
			.orElseThrow(() -> new IllegalArgumentException("퀴즈가 존재하지 않습니다."));
		Long curationId = curationSelectorService
			.findRandomCurationIdByCategoryName(quiz.getCategory())
			.orElse(null);
		return new CurationLinkDto(curationId);
	}

	// 선호도 결과에서 선택지 카테고리 기반 큐레이션 이동
	public CurationLinkDto getCurationForPreference(Long quizId, Long optionId) {
		BalanceOption option = optionRepository.findById(optionId)
			.orElseThrow(() -> new IllegalArgumentException("선택지가 존재하지 않습니다."));
		if (!option.getQuiz().getId().equals(quizId)) {
			throw new IllegalArgumentException("퀴즈와 선택지가 일치하지 않습니다.");
		}
		// 규칙: 선호도 각 선택지는 반드시 '하위 카테고리 중 1개'를 가져야 한다
		String categoryName = option.getCategory();
		curationSelectorService.validateMustBeSubCategory(categoryName);
		Long curationId = curationSelectorService
			.findRandomCurationIdByCategoryName(categoryName)
			.orElse(null);
		return new CurationLinkDto(curationId);
	}

	// 선호도: 다른 선택지의 큐레이션
	public CurationLinkDto getOtherOptionCuration(Long quizId, Long currentOptionId) {
		List<BalanceOption> options = optionRepository.findByQuizId(quizId);
		BalanceOption other = options.stream()
			.filter(o -> !o.getId().equals(currentOptionId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("다른 선택지를 찾을 수 없습니다."));
		// 다른 선택지도 동일 규칙 적용: 반드시 하위 카테고리 1개
		String categoryName = other.getCategory();
		curationSelectorService.validateMustBeSubCategory(categoryName);
		Long curationId = curationSelectorService
			.findRandomCurationIdByCategoryName(categoryName)
			.orElse(null);
		return new CurationLinkDto(curationId);
	}
}


