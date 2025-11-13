package com.swulion.crossnote.repository.Curation;

// 1. (오류 수정) com.querydsl.core.QueryFactory 임포트 제거
// import com.querydsl.core.QueryFactory;
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
public class CurationRepositoryImpl implements CurationRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    private final QCuration curation = QCuration.curation;

    @Override
    public Page<Curation> findDynamicFeed(Long categoryId, String curationTypeStr, String query, LocalDateTime thirtyDaysAgo, Pageable pageable){
        List<Curation> content = jpaQueryFactory
                .selectFrom(curation)
                .where(
                        curation.createdAt.after(thirtyDaysAgo),
                        categoryIdEq(categoryId),
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
                        categoryIdEq(categoryId),
                        curationTypeEq(curationTypeStr),
                        queryContains(query)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0L);
    }

    // 분야 필터 (categoryId)
    private BooleanExpression categoryIdEq(Long categoryId){
        return categoryId != null ? curation.category.categoryId.eq(categoryId) : null;
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
    private BooleanExpression queryContains(String query){
        if(StringUtils.hasText(query)){
            return curation.title.containsIgnoreCase(query)
                    .or(curation.description.containsIgnoreCase(query));
        }
        return null;
    }

    // 정렬
    private OrderSpecifier<?> getOrderSpecifier(Sort sort){
        if(sort.isEmpty()){
            return curation.createdAt.desc();
        }

        for(Sort.Order order : sort){
            String property = order.getProperty();
            if(property.equals("createdAt")){
                return order.isAscending() ? curation.createdAt.asc() : curation.createdAt.desc();
            }
//          // 인기순 정렬 (추후 구현)
//          if(property.equals("popularity"))
        }
        return curation.createdAt.desc();
    }
}