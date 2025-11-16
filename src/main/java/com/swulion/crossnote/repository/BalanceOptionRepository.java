package com.swulion.crossnote.repository;

import com.swulion.crossnote.entity.balance.BalanceOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceOptionRepository extends JpaRepository<BalanceOption, Long> {
	List<BalanceOption> findByQuizId(Long quizId);
}


