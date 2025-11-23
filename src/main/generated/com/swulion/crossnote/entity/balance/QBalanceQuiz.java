package com.swulion.crossnote.entity.balance;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBalanceQuiz is a Querydsl query type for BalanceQuiz
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBalanceQuiz extends EntityPathBase<BalanceQuiz> {

    private static final long serialVersionUID = 2106161726L;

    public static final QBalanceQuiz balanceQuiz = new QBalanceQuiz("balanceQuiz");

    public final BooleanPath active = createBoolean("active");

    public final StringPath category = createString("category");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath oxAnswer = createBoolean("oxAnswer");

    public final StringPath question = createString("question");

    public final EnumPath<QuizType> type = createEnum("type", QuizType.class);

    public QBalanceQuiz(String variable) {
        super(BalanceQuiz.class, forVariable(variable));
    }

    public QBalanceQuiz(Path<? extends BalanceQuiz> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBalanceQuiz(PathMetadata metadata) {
        super(BalanceQuiz.class, metadata);
    }

}

