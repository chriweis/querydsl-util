package com.github.chriweis.querydsl.util.metamodel;

import com.querydsl.sql.RelationalPathBase;
import org.assertj.core.api.iterable.Extractor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestUtil {

    public static Extractor<DbTableRelationship, RelationalPathBase> keyPath =
            dbTableRelationship -> dbTableRelationship.getKeyTable().getRelationalPath();

    public static Extractor<DbTableRelationship, RelationalPathBase> foreignKeyPath =
            dbTableRelationship -> dbTableRelationship.getForeignKeyTable().getRelationalPath();

    public static <T> Set<T> set(T... items) {
        LinkedHashSet<T> result = new LinkedHashSet<>();
        Arrays.stream(items).forEach(result::add);
        return result;
    }
}
