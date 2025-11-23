package com.swulion.crossnote.repository;

import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.entity.QA.QuestionCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {
    List<QuestionCategory> findAllByQuestionId(Question questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionCategory qc WHERE qc.questionId.questionId = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);

    void deleteAllByQuestionId(Question questionId);
}
