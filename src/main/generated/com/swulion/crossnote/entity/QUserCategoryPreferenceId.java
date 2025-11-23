package com.swulion.crossnote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserCategoryPreferenceId is a Querydsl query type for UserCategoryPreferenceId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserCategoryPreferenceId extends BeanPath<UserCategoryPreferenceId> {

    private static final long serialVersionUID = 2059250752L;

    public static final QUserCategoryPreferenceId userCategoryPreferenceId = new QUserCategoryPreferenceId("userCategoryPreferenceId");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final EnumPath<PreferenceType> preferenceType = createEnum("preferenceType", PreferenceType.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserCategoryPreferenceId(String variable) {
        super(UserCategoryPreferenceId.class, forVariable(variable));
    }

    public QUserCategoryPreferenceId(Path<? extends UserCategoryPreferenceId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserCategoryPreferenceId(PathMetadata metadata) {
        super(UserCategoryPreferenceId.class, metadata);
    }

}

