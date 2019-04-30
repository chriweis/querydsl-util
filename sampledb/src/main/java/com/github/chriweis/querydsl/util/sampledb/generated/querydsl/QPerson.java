package com.github.chriweis.querydsl.util.sampledb.generated.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPerson is a Querydsl query type for QPerson
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPerson extends com.querydsl.sql.RelationalPathBase<QPerson> {

    private static final long serialVersionUID = 852774051;

    public static final QPerson person = new QPerson("PERSON");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QPerson> constraint8 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QAddress> _constraintE6 = createInvForeignKey(id, "PERSON_ID");

    public QPerson(String variable) {
        super(QPerson.class, forVariable(variable), "PUBLIC", "PERSON");
        addMetadata();
    }

    public QPerson(String variable, String schema, String table) {
        super(QPerson.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPerson(String variable, String schema) {
        super(QPerson.class, forVariable(variable), schema, "PERSON");
        addMetadata();
    }

    public QPerson(Path<? extends QPerson> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PERSON");
        addMetadata();
    }

    public QPerson(PathMetadata metadata) {
        super(QPerson.class, metadata, "PUBLIC", "PERSON");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    }

}

