package com.swulion.crossnote.repository.QnA;

import com.swulion.crossnote.entity.QA.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    //최신순
    List<Question> findAllByOrderByCreatedAtDesc();

    //인기 많은 순
    List<Question> findAllByOrderByLikeCountDesc();

    //댓글 많은 순
    List<Question> findAllByOrderByAnswerCountDesc();
}
