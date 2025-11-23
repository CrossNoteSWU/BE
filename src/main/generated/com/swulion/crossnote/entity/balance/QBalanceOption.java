package com.swulion.crossnote.entity.balance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBalanceOption is a Querydsl query type for BalanceOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBalanceOption extends EntityPathBase<BalanceOption> {

    private static final long serialVersionUID = 1030261278L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBalanceOption balanceOption = new QBalanceOption("balanceOption");

    public final StringPath category = createString("category");

    public final NumberPath<Long> curationId = createNumber("curationId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath label = createString("label");

    public final QBalanceQuiz quiz;

    public final StringPath text = createString("text");

    public QBalanceOption(String variable) {
        this(BalanceOption.class, forVariable(variable), INITS);
    }

    public QBalanceOption(Path<? extends BalanceOption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBalanceOption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBalanceOption(PathMetadata metadata, PathInits inits) {
        this(BalanceOption.class, metadata, inits);
    }

    public QBalanceOption(Class<? extends BalanceOption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.quiz = inits.isInitialized("quiz") ? new QBalanceQuiz(forProperty("quiz")) : null;
    }

}

