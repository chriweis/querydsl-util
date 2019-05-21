package com.github.chriweis.querydsl.util.sampledb.generated.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QCommentOnPerson is a Querydsl query type for QCommentOnPerson
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCommentOnPerson extends com.querydsl.sql.RelationalPathBase<QCommentOnPerson> {

    private static final long serialVersionUID = 899079173;

    public static final QCommentOnPerson commentOnPerson = new QCommentOnPerson("COMMENT_ON_PERSON");

    public final StringPath comment = createString("comment");

    public final NumberPath<Long> personId = createNumber("personId", Long.class);

    public final com.querydsl.sql.PrimaryKey<QCommentOnPerson> constraint68 = createPrimaryKey(personId);

    public QCommentOnPerson(String variable) {
        super(QCommentOnPerson.class, forVariable(variable), "PUBLIC", "COMMENT_ON_PERSON");
        addMetadata();
    }

    public QCommentOnPerson(String variable, String schema, String table) {
        super(QCommentOnPerson.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCommentOnPerson(String variable, String schema) {
        super(QCommentOnPerson.class, forVariable(variable), schema, "COMMENT_ON_PERSON");
        addMetadata();
    }

    public QCommentOnPerson(Path<? extends QCommentOnPerson> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "COMMENT_ON_PERSON");
        addMetadata();
    }

    public QCommentOnPerson(PathMetadata metadata) {
        super(QCommentOnPerson.class, metadata, "PUBLIC", "COMMENT_ON_PERSON");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(comment, ColumnMetadata.named("COMMENT").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(personId, ColumnMetadata.named("PERSON_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

