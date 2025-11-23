package com.swulion.crossnote.repository.balance;

import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.entity.balance.UserBalanceChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBalanceChoiceRepository extends JpaRepository<UserBalanceChoice, Long> {
    List<UserBalanceChoice> findByUser(User user);
}

