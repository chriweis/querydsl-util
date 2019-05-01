package com.github.chriweis.querydsl.util.metamodel;

import com.querydsl.sql.RelationalPath;
import org.assertj.core.api.iterable.Extractor;

class TestUtil {

    public static Extractor<DbTableRelationship, RelationalPath> keyPath =
            dbTableRelationship -> dbTableRelationship.getKeyTable().getRelationalPath();

    public static Extractor<DbTableRelationship, RelationalPath> foreignKeyPath =
            dbTableRelationship -> dbTableRelationship.getForeignKeyTable().getRelationalPath();
}
