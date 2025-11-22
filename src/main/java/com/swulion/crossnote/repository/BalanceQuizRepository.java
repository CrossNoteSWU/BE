package com.swulion.crossnote.repository;

import com.swulion.crossnote.entity.balance.BalanceQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceQuizRepository extends JpaRepository<BalanceQuiz, Long> {
	List<BalanceQuiz> findByActiveTrue();
	List<BalanceQuiz> findByActiveTrueAndCategoryIn(List<String> categories);
}


