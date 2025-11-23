package com.swulion.crossnote.entity.balance;

import com.swulion.crossnote.entity.BaseTimeEntity;
import com.swulion.crossnote.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_balance_choice")
public class UserBalanceChoice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private BalanceQuiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private BalanceOption option; // 선호도 선택의 경우

    private Boolean oxAnswer; // OX 선택의 경우

    @Column(length = 50)
    private String category; // 선택된 카테고리 (옵션의 category 또는 퀴즈의 category)

    @Builder
    public UserBalanceChoice(User user, BalanceQuiz quiz, BalanceOption option, Boolean oxAnswer, String category) {
        this.user = user;
        this.quiz = quiz;
        this.option = option;
        this.oxAnswer = oxAnswer;
        this.category = category;
    }
}

