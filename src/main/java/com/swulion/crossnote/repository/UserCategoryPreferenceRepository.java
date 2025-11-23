package com.swulion.crossnote.repository;

import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.entity.UserCategoryPreference;
import com.swulion.crossnote.entity.UserCategoryPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCategoryPreferenceRepository extends JpaRepository<UserCategoryPreference, UserCategoryPreferenceId> {

    // user 객체로 해당 유저의 모든 선호도를 찾음
    List<UserCategoryPreference> findByUser(User user);
}