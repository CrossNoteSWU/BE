package com.swulion.crossnote.entity.balance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class BalanceQuiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private QuizType type; // OX or PREFERENCE

	@Column(nullable = false, length = 500)
	private String question;

	// OX용 정답 (true = O, false = X). PREFERENCE일 경우 null
	private Boolean oxAnswer;

	// 세부 분야명 예: 사회학, 언어, 철학, 심리, 역사
	@Column(length = 50)
	private String category;

	private boolean active = true;

	public BalanceQuiz(QuizType type, String question, Boolean oxAnswer, String category, boolean active) {
		this.type = type;
		this.question = question;
		this.oxAnswer = oxAnswer;
		this.category = category;
		this.active = active;
	}
}


