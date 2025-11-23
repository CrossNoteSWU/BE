package com.swulion.crossnote.entity.Curation;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCuration is a Querydsl query type for Curation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCuration extends EntityPathBase<Curation> {

    private static final long serialVersionUID = 1884925101L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCuration curation = new QCuration("curation");

    public final com.swulion.crossnote.entity.QUser author;

    public final com.swulion.crossnote.entity.QCategory category;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.swulion.crossnote.entity.QCategory crossCategory;

    public final EnumPath<CurationLevel> curationLevel = createEnum("curationLevel", CurationLevel.class);

    public final EnumPath<CurationType> curationType = createEnum("curationType", CurationType.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final NumberPath<Long> originalColumnId = createNumber("originalColumnId", Long.class);

    public final NumberPath<Long> scrapCount = createNumber("scrapCount", Long.class);

    public final StringPath sourceUrl = createString("sourceUrl");

    public final NumberPath<Double> terminologyDensity = createNumber("terminologyDensity", Double.class);

    public final StringPath title = createString("title");

    public QCuration(String variable) {
        this(Curation.class, forVariable(variable), INITS);
    }

    public QCuration(Path<? extends Curation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCuration(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCuration(PathMetadata metadata, PathInits inits) {
        this(Curation.class, metadata, inits);
    }

    public QCuration(Class<? extends Curation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.swulion.crossnote.entity.QUser(forProperty("author")) : null;
        this.category = inits.isInitialized("category") ? new com.swulion.crossnote.entity.QCategory(forProperty("category"), inits.get("category")) : null;
        this.crossCategory = inits.isInitialized("crossCategory") ? new com.swulion.crossnote.entity.QCategory(forProperty("crossCategory"), inits.get("crossCategory")) : null;
    }

}

