package com.github.chriweis.querydsl.util.sampledb.generated.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import java.util.*;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPersonType is a Querydsl query type for QPersonType
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPersonType extends com.querydsl.sql.RelationalPathBase<QPersonType> {

    private static final long serialVersionUID = -521189763;

    public static final QPersonType personType = new QPersonType("PERSON_TYPE");

    public final NumberPath<Long> id1 = createNumber("id1", Long.class);

    public final NumberPath<Long> id2 = createNumber("id2", Long.class);

    public final StringPath label = createString("label");

    public final com.querydsl.sql.PrimaryKey<QPersonType> personTypePk = createPrimaryKey(id1, id2);

    public final com.querydsl.sql.ForeignKey<QPerson> _personTypeFk = createInvForeignKey(Arrays.asList(id1, id2), Arrays.asList("PERSON_TYPE_FK_1", "PERSON_TYPE_FK_2"));

    public QPersonType(String variable) {
        super(QPersonType.class, forVariable(variable), "PUBLIC", "PERSON_TYPE");
        addMetadata();
    }

    public QPersonType(String variable, String schema, String table) {
        super(QPersonType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPersonType(String variable, String schema) {
        super(QPersonType.class, forVariable(variable), schema, "PERSON_TYPE");
        addMetadata();
    }

    public QPersonType(Path<? extends QPersonType> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PERSON_TYPE");
        addMetadata();
    }

    public QPersonType(PathMetadata metadata) {
        super(QPersonType.class, metadata, "PUBLIC", "PERSON_TYPE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id1, ColumnMetadata.named("ID_1").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id2, ColumnMetadata.named("ID_2").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(label, ColumnMetadata.named("LABEL").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    }

}

