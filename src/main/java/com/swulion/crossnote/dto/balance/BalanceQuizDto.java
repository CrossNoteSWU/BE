package com.swulion.crossnote.dto.balance;

import com.swulion.crossnote.entity.balance.BalanceOption;
import com.swulion.crossnote.entity.balance.BalanceQuiz;
import com.swulion.crossnote.entity.balance.QuizType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BalanceQuizDto {
	private Long quizId;
	private QuizType type;
	private String question;
	private List<OptionDto> options; // PREFERENCE일 때 A,B

	public static BalanceQuizDto of(BalanceQuiz quiz, List<BalanceOption> options) {
		List<OptionDto> optionDtos = options.stream()
			.map(o -> new OptionDto(o.getId(), o.getLabel(), o.getText()))
			.toList();
		return new BalanceQuizDto(quiz.getId(), quiz.getType(), quiz.getQuestion(), optionDtos);
	}
}


