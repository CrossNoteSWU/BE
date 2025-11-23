package com.swulion.crossnote.entity.Column;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QColumnComment is a Querydsl query type for ColumnComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColumnComment extends EntityPathBase<ColumnComment> {

    private static final long serialVersionUID = 2017480274L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QColumnComment columnComment = new QColumnComment("columnComment");

    public final QColumnEntity columnId;

    public final StringPath comment = createString("comment");

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final com.swulion.crossnote.entity.QUser userId;

    public QColumnComment(String variable) {
        this(ColumnComment.class, forVariable(variable), INITS);
    }

    public QColumnComment(Path<? extends ColumnComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QColumnComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QColumnComment(PathMetadata metadata, PathInits inits) {
        this(ColumnComment.class, metadata, inits);
    }

    public QColumnComment(Class<? extends ColumnComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.columnId = inits.isInitialized("columnId") ? new QColumnEntity(forProperty("columnId"), inits.get("columnId")) : null;
        this.userId = inits.isInitialized("userId") ? new com.swulion.crossnote.entity.QUser(forProperty("userId")) : null;
    }

}

