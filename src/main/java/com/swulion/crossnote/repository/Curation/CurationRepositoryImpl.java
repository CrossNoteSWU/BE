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
    public Page<Curation> findDynamicFeed(List<Long> categoryIds, List<String> curationTypeStrs, String query, LocalDateTime thirtyDaysAgo, Pageable pageable) {
        List<Curation> content = jpaQueryFactory
                .selectFrom(curation)
                .where(
                        curation.createdAt.after(thirtyDaysAgo),
                        categoryIdIn(categoryIds),
                        curationTypeIn(curationTypeStrs),
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
                        curationTypeIn(curationTypeStrs),
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
    private BooleanExpression curationTypeIn(List<String> curationTypeStrs) {
        if(curationTypeStrs == null || curationTypeStrs.isEmpty()){
            return null;
        }
        try {
            // 리스트 내부에 콤마가 있을 수도 있으므로 모두 split하여 flat하게 만듦
            List<CurationType> types = curationTypeStrs.stream()
                    .flatMap(s -> List.of(s.split(",")).stream())
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(CurationType::valueOf)
                    .toList();

            if (types.isEmpty()) {
                return null;
            }
            return curation.curationType.in(types);

        } catch (IllegalArgumentException e) {
            return null;
        }
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