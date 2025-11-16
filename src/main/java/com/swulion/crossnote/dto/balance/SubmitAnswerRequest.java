package com.swulion.crossnote.dto.balance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {
	// OX일 때 사용
	private Boolean oxAnswer;
	// 선호도일 때 사용
	private Long optionId;
}


