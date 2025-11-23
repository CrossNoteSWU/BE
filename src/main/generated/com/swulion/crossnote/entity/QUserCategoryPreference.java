package com.swulion.crossnote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCategoryPreference is a Querydsl query type for UserCategoryPreference
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCategoryPreference extends EntityPathBase<UserCategoryPreference> {

    private static final long serialVersionUID = 1097113669L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCategoryPreference userCategoryPreference = new QUserCategoryPreference("userCategoryPreference");

    public final QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QUserCategoryPreferenceId id;

    public final QUser user;

    public QUserCategoryPreference(String variable) {
        this(UserCategoryPreference.class, forVariable(variable), INITS);
    }

    public QUserCategoryPreference(Path<? extends UserCategoryPreference> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCategoryPreference(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCategoryPreference(PathMetadata metadata, PathInits inits) {
        this(UserCategoryPreference.class, metadata, inits);
    }

    public QUserCategoryPreference(Class<? extends UserCategoryPreference> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category"), inits.get("category")) : null;
        this.id = inits.isInitialized("id") ? new QUserCategoryPreferenceId(forProperty("id")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

