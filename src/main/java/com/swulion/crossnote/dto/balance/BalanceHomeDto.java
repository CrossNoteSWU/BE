package com.swulion.crossnote.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BalanceHomeDto {
	private BalanceQuizDto todayQuiz;
	private BalanceQuizDto categoryQuiz;
	private String parentCategory; // 사용된 상위 카테고리명
}


