package com.github.chriweis.querydsl.util.sampledb.generated.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QAddress is a Querydsl query type for QAddress
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QAddress extends com.querydsl.sql.RelationalPathBase<QAddress> {

    private static final long serialVersionUID = 196940902;

    public static final QAddress address = new QAddress("ADDRESS");

    public final NumberPath<Long> countryId = createNumber("countryId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> personId = createNumber("personId", Long.class);

    public final StringPath value = createString("value");

    public final com.querydsl.sql.PrimaryKey<QAddress> constraintE = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QPerson> constraintE6 = createForeignKey(personId, "ID");

    public final com.querydsl.sql.ForeignKey<QCountry> constraintE66 = createForeignKey(countryId, "ID");

    public QAddress(String variable) {
        super(QAddress.class, forVariable(variable), "PUBLIC", "ADDRESS");
        addMetadata();
    }

    public QAddress(String variable, String schema, String table) {
        super(QAddress.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAddress(String variable, String schema) {
        super(QAddress.class, forVariable(variable), schema, "ADDRESS");
        addMetadata();
    }

    public QAddress(Path<? extends QAddress> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "ADDRESS");
        addMetadata();
    }

    public QAddress(PathMetadata metadata) {
        super(QAddress.class, metadata, "PUBLIC", "ADDRESS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(countryId, ColumnMetadata.named("COUNTRY_ID").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(personId, ColumnMetadata.named("PERSON_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(value, ColumnMetadata.named("VALUE").withIndex(4).ofType(Types.VARCHAR).withSize(255));
    }

}

