package com.swulion.crossnote.entity.Column;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QColumnCategory is a Querydsl query type for ColumnCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColumnCategory extends EntityPathBase<ColumnCategory> {

    private static final long serialVersionUID = -1229694101L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QColumnCategory columnCategory = new QColumnCategory("columnCategory");

    public final com.swulion.crossnote.entity.QCategory categoryId;

    public final NumberPath<Long> columnCategoryId = createNumber("columnCategoryId", Long.class);

    public final QColumnEntity columnId;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QColumnCategory(String variable) {
        this(ColumnCategory.class, forVariable(variable), INITS);
    }

    public QColumnCategory(Path<? extends ColumnCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QColumnCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QColumnCategory(PathMetadata metadata, PathInits inits) {
        this(ColumnCategory.class, metadata, inits);
    }

    public QColumnCategory(Class<? extends ColumnCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.categoryId = inits.isInitialized("categoryId") ? new com.swulion.crossnote.entity.QCategory(forProperty("categoryId"), inits.get("categoryId")) : null;
        this.columnId = inits.isInitialized("columnId") ? new QColumnEntity(forProperty("columnId"), inits.get("columnId")) : null;
    }

}

