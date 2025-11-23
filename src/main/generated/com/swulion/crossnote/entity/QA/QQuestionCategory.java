package com.swulion.crossnote.entity.QA;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuestionCategory is a Querydsl query type for QuestionCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestionCategory extends EntityPathBase<QuestionCategory> {

    private static final long serialVersionUID = 1938847329L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuestionCategory questionCategory = new QQuestionCategory("questionCategory");

    public final com.swulion.crossnote.entity.QCategory categoryId;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> questionCategoryId = createNumber("questionCategoryId", Long.class);

    public final QQuestion questionId;

    public QQuestionCategory(String variable) {
        this(QuestionCategory.class, forVariable(variable), INITS);
    }

    public QQuestionCategory(Path<? extends QuestionCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuestionCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuestionCategory(PathMetadata metadata, PathInits inits) {
        this(QuestionCategory.class, metadata, inits);
    }

    public QQuestionCategory(Class<? extends QuestionCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryId = inits.isInitialized("categoryId") ? new com.swulion.crossnote.entity.QCategory(forProperty("categoryId"), inits.get("categoryId")) : null;
        this.questionId = inits.isInitialized("questionId") ? new QQuestion(forProperty("questionId"), inits.get("questionId")) : null;
    }

}

