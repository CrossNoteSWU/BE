package com.swulion.crossnote.repository.QnA;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swulion.crossnote.dto.Question.QuestionResponseDto;
import com.swulion.crossnote.entity.QA.QQuestion;
import com.swulion.crossnote.entity.QA.QQuestionCategory;
import com.swulion.crossnote.entity.QA.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl {

    private final JPAQueryFactory jpaQueryFactory;
    private final QQuestion qQuestion = QQuestion.question;
    private final QQuestionCategory qQuestionCategory = QQuestionCategory.questionCategory;

    public List<Question> findWithKeyword(List<Long> categoryIds ,String keyword) {
        List<Question> questions = jpaQueryFactory
                .selectFrom(qQuestion)
                .leftJoin(qQuestionCategory)
                .on(qQuestion.questionId.eq(qQuestionCategory.questionId.questionId))
                .where(
                        categoryIdIn(categoryIds),
                        queryContains(keyword)
                )
                .fetch();
        return questions;

    }

    private BooleanExpression categoryIdEqual(Long categoryId) {
        return categoryId != null ? qQuestionCategory.categoryId.categoryId.eq(categoryId) : null;
    }

    private BooleanExpression categoryIdIn(List<Long> categoryIds){
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return qQuestionCategory.categoryId.categoryId.in(categoryIds);
        }
        return null;
    }

    private BooleanExpression queryContains(String keyword) {
        if (StringUtils.hasText(keyword)){
            return qQuestion.title.containsIgnoreCase(keyword)
                    .or(qQuestion.content.containsIgnoreCase(keyword))
                    .or(qQuestion.questionerId.name.containsIgnoreCase(keyword));
        }
        return null;
    }
}
