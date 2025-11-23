package com.swulion.crossnote.entity.balance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserBalanceChoice is a Querydsl query type for UserBalanceChoice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserBalanceChoice extends EntityPathBase<UserBalanceChoice> {

    private static final long serialVersionUID = 1267254975L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserBalanceChoice userBalanceChoice = new QUserBalanceChoice("userBalanceChoice");

    public final com.swulion.crossnote.entity.QBaseTimeEntity _super = new com.swulion.crossnote.entity.QBaseTimeEntity(this);

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QBalanceOption option;

    public final BooleanPath oxAnswer = createBoolean("oxAnswer");

    public final QBalanceQuiz quiz;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.swulion.crossnote.entity.QUser user;

    public QUserBalanceChoice(String variable) {
        this(UserBalanceChoice.class, forVariable(variable), INITS);
    }

    public QUserBalanceChoice(Path<? extends UserBalanceChoice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserBalanceChoice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserBalanceChoice(PathMetadata metadata, PathInits inits) {
        this(UserBalanceChoice.class, metadata, inits);
    }

    public QUserBalanceChoice(Class<? extends UserBalanceChoice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.option = inits.isInitialized("option") ? new QBalanceOption(forProperty("option"), inits.get("option")) : null;
        this.quiz = inits.isInitialized("quiz") ? new QBalanceQuiz(forProperty("quiz")) : null;
        this.user = inits.isInitialized("user") ? new com.swulion.crossnote.entity.QUser(forProperty("user")) : null;
    }

}

