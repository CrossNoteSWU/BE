package com.swulion.crossnote.repository.Curation;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.entity.Curation.QCuration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CurationRepositoryImpl implements CurationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QCuration curation = QCuration.curation;

    @Override
    public Page<Curation> findDynamicFeed(List<Long> categoryIds, String curationTypeStr, String query, LocalDateTime thirtyDaysAgo, Pageable pageable) {
        List<Curation> content = jpaQueryFactory
                .selectFrom(curation)
                .where(
                        curation.createdAt.after(thirtyDaysAgo),
                        categoryIdIn(categoryIds),
                        curationTypeEq(curationTypeStr),
                        queryContains(query)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(curation.count())
                .from(curation)
                .where(
                        curation.createdAt.after(thirtyDaysAgo),
                        categoryIdIn(categoryIds),
                        curationTypeEq(curationTypeStr),
                        queryContains(query)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0L);
    }

    // 분야 필터 (categoryId)
    private BooleanExpression categoryIdIn(List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            return curation.category.categoryId.in(categoryIds);
        }
        return null;
    }

    // 유형 필터 (curationType)
    private BooleanExpression curationTypeEq(String curationTypeStr) {
        if (StringUtils.hasText(curationTypeStr)) {
            try {
                CurationType type = CurationType.valueOf(curationTypeStr.toUpperCase());
                return curation.curationType.eq(type);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    // 검색어 필터 (제목 or 내용)
    private BooleanExpression queryContains(String query) {
        if (StringUtils.hasText(query)) {
            return curation.title.containsIgnoreCase(query)
                    .or(curation.description.containsIgnoreCase(query));
        }
        return null;
    }

    // 정렬
    private OrderSpecifier<?> getOrderSpecifier(Sort sort) {
        if (sort.isEmpty()) {
            return curation.createdAt.desc();
        }

        for (Sort.Order order : sort) {
            String property = order.getProperty();

            // 최신순 정렬 (createdAt)
            if (property.equals("createdAt")) {
                return order.isAscending() ? curation.createdAt.asc() : curation.createdAt.desc();
            }
            // 인기순 정렬 (likeCount 기준)
            if (property.equals("likeCount") || property.equals("popularity")) {
                return order.isAscending() ? curation.likeCount.asc() : curation.likeCount.desc();
            }
        }

        // 요청된 정렬 속성이 위 조건에 해당하지 않으면 기본값 (최신순) 반환
        return curation.createdAt.desc();
    }
}