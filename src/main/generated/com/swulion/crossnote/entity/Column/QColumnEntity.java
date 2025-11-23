package com.swulion.crossnote.entity.Column;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QColumnEntity is a Querydsl query type for ColumnEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColumnEntity extends EntityPathBase<ColumnEntity> {

    private static final long serialVersionUID = 1091451280L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QColumnEntity columnEntity = new QColumnEntity("columnEntity");

    public final com.swulion.crossnote.entity.QUser columnAutherId;

    public final NumberPath<Long> columnId = createNumber("columnId", Long.class);

    public final NumberPath<Integer> commentCount = createNumber("commentCount", Integer.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isBestColumn = createBoolean("isBestColumn");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Integer> scrapCount = createNumber("scrapCount", Integer.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QColumnEntity(String variable) {
        this(ColumnEntity.class, forVariable(variable), INITS);
    }

    public QColumnEntity(Path<? extends ColumnEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QColumnEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QColumnEntity(PathMetadata metadata, PathInits inits) {
        this(ColumnEntity.class, metadata, inits);
    }

    public QColumnEntity(Class<? extends ColumnEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.columnAutherId = inits.isInitialized("columnAutherId") ? new com.swulion.crossnote.entity.QUser(forProperty("columnAutherId")) : null;
    }

}

