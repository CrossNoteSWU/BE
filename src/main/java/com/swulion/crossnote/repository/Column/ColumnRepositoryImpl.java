package com.swulion.crossnote.repository.Column;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import com.swulion.crossnote.entity.Column.QColumnCategory;
import com.swulion.crossnote.entity.Column.QColumnEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ColumnRepositoryImpl {
    private final JPAQueryFactory jpaQueryFactory;
    private final QColumnCategory qColumnCategory = QColumnCategory.columnCategory;
    private final QColumnEntity qColumnEntity = QColumnEntity.columnEntity;

    public List<ColumnEntity> findWithKeyword(List<Long> categoryIds, String keyword) {
        List<ColumnEntity> columnEntities = jpaQueryFactory
                .selectFrom(qColumnEntity)
                .leftJoin(qColumnCategory).on(qColumnEntity.columnId.eq(qColumnCategory.columnId.columnId))
                .where(
                        categoryIdIn(categoryIds)
                ).fetch();
        return columnEntities;
    }

    private BooleanExpression categoryIdEqual(Long categoryId) {
        return categoryId != null ? qColumnCategory.categoryId.categoryId.eq(categoryId) : null;
    }

    private BooleanExpression categoryIdIn(List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return qColumnCategory.categoryId.categoryId.in(categoryIds);
        }
        return null;
    }

    private BooleanExpression queryContains(String keyword) {
        if (StringUtils.hasText(keyword)){
            return qColumnEntity.title.containsIgnoreCase(keyword)
                    .or(qColumnEntity.content.containsIgnoreCase(keyword))
                    .or(qColumnEntity.columnAutherId.name.containsIgnoreCase(keyword));
        }
        return null;
    }
}
