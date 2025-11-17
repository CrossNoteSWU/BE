package com.swulion.crossnote.dto.balance;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class AnswerResultDto {
	private boolean correct;
	private String message;
	private Long curationId; // 추후 사용자 큐레이션 신청 기능에서 사용

	public static AnswerResultDto forOx(boolean correct) {
		String msg = correct ? "정답입니다! 관련 큐레이션으로 이동할게요" : "다시 생각해볼까요?";
		return new AnswerResultDto(correct, msg, null);
	}

	public static AnswerResultDto forPreference() {
		return new AnswerResultDto(true, "관련 큐레이션으로 이동할게요", null);
	}
}


