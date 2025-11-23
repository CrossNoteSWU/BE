package com.swulion.crossnote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 2107442860L;

    public static final QUser user = new QUser("user");

    public final QBaseTimeEntity _super = new QBaseTimeEntity(this);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<com.swulion.crossnote.entity.Curation.CurationLevel> curationLevel = createEnum("curationLevel", com.swulion.crossnote.entity.Curation.CurationLevel.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> followersCount = createNumber("followersCount", Long.class);

    public final NumberPath<Long> followingsCount = createNumber("followingsCount", Long.class);

    public final EnumPath<Gender> gender = createEnum("gender", Gender.class);

    public final EnumPath<LoginType> loginType = createEnum("loginType", LoginType.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final SetPath<UserCategoryPreference, QUserCategoryPreference> preferences = this.<UserCategoryPreference, QUserCategoryPreference>createSet("preferences", UserCategoryPreference.class, QUserCategoryPreference.class, PathInits.DIRECT2);

    public final StringPath profileImageUrl = createString("profileImageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

