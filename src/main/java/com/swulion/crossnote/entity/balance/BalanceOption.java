package com.swulion.crossnote.entity.balance;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class BalanceOption {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quiz_id")
	private BalanceQuiz quiz;

	// "A", "B" 등 표기용
	@Column(length = 10)
	private String label;

	@Column(nullable = false, length = 500)
	private String text;

	// 선택지 자체의 세부 카테고리 (예: 사회학). 없으면 퀴즈의 category 사용
	@Column(length = 50)
	private String category;

	// 연결될 큐레이션 식별자(외부 서비스 기준)
	private Long curationId;

	// JPA용, 비즈니스 생성자는 정적 팩토리로
	public BalanceOption(BalanceQuiz quiz, String label, String text, String category, Long curationId) {
		this.quiz = quiz;
		this.label = label;
		this.text = text;
		this.category = category;
		this.curationId = curationId;
	}
}


