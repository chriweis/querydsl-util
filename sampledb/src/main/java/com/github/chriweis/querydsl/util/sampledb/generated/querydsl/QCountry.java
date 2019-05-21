package com.github.chriweis.querydsl.util.sampledb.generated.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QCountry is a Querydsl query type for QCountry
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QCountry extends com.querydsl.sql.RelationalPathBase<QCountry> {

    private static final long serialVersionUID = -1992503288;

    public static final QCountry country = new QCountry("COUNTRY");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QCountry> constraint63 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QAddress> _constraintE66 = createInvForeignKey(id, "COUNTRY_ID");

    public QCountry(String variable) {
        super(QCountry.class, forVariable(variable), "PUBLIC", "COUNTRY");
        addMetadata();
    }

    public QCountry(String variable, String schema, String table) {
        super(QCountry.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCountry(String variable, String schema) {
        super(QCountry.class, forVariable(variable), schema, "COUNTRY");
        addMetadata();
    }

    public QCountry(Path<? extends QCountry> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "COUNTRY");
        addMetadata();
    }

    public QCountry(PathMetadata metadata) {
        super(QCountry.class, metadata, "PUBLIC", "COUNTRY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    }

}

