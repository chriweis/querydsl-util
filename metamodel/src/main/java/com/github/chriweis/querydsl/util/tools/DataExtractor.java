package com.github.chriweis.querydsl.util.tools;

import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.metamodel.DbTable;
import com.github.chriweis.querydsl.util.metamodel.DbTableRelationship;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;

public class DataExtractor {

    private final SQLQueryFactory queryFactory;
    private final RelationalPathBase<?> relationalPath;
    private final BooleanExpression expression;

    @Getter
    private final Map<DbTable, ExtractionQuery> queries = new LinkedHashMap<>();
    private List<DbTable> insertionOrder;

    public DataExtractor(SQLQueryFactory queryFactory, RelationalPathBase<?> relationalPath, BooleanExpression expression) {
        this.queryFactory = queryFactory;
        this.relationalPath = relationalPath;
        this.expression = expression;
    }

    private SQLQuery<Tuple> join(RelationalPathBase<?> relationalPath, DbTableRelationship foreignKey, SQLQuery<Tuple> from) {
        SQLQuery<Tuple> selectQuery = from
                .join(relationalPath);
        for (int index = 0; index < foreignKey.getKeyColumns().size(); index++) {
            Path<?> keyColumn = foreignKey.getKeyColumns().get(index);
            Path<?> foreignKeyColumn = foreignKey.getForeignKeyColumns().get(index);
            if (keyColumn instanceof NumberExpression) {
                selectQuery.on(NumberExpression.class.cast(keyColumn).eq(foreignKeyColumn));
            } else {
                throw new RuntimeException("TODO");
            }
        }
        return selectQuery;
    }

    public DataExtractor extractFrom(DbMetamodel metamodel) {
        DbTable table = metamodel.getTableFor(relationalPath);
        extractFrom(table, true, q -> q, emptyList());
        insertionOrder = metamodel.orderTablesForInsertion(queries.keySet());
        return this;
    }

    public void extractFrom(DbTable table, boolean follow, Function<SQLQuery<Tuple>, SQLQuery<Tuple>> joined, List<DbTableRelationship> noFollow) {
        SQLQuery<Tuple> query = joined.apply(queryFactory.select(table.getRelationalPath().all())
                .from(table.getRelationalPath()))
                .where(expression);

        queries.put(table, new ExtractionQuery(table, query, expression));

        table.getForeignKeyRelationships().stream()
                .filter(rel -> !noFollow.contains(rel))
                .forEach(rel -> {
                    extractFrom(rel.getKeyTable(), false, q -> {
                        RelationalPathBase<?> path = rel.getForeignKeyTable().getRelationalPath();
                        SQLQuery<Tuple> sqlQuery = joined.apply(q);
                        return join(path, rel, sqlQuery);
                    }, plus(noFollow, rel));
                });

        if (follow) {
            table.getInverseForeignKeyRelationships().stream()
                    .filter(rel -> !noFollow.contains(rel))
                    .forEach(rel -> {
                        extractFrom(rel.getForeignKeyTable(), true, q -> {
                            RelationalPathBase<?> path = rel.getKeyTable().getRelationalPath();
                            SQLQuery<Tuple> sqlQuery = joined.apply(q);
                            return join(path, rel, sqlQuery);
                        }, plus(noFollow, rel));
                    });
        }
    }

    private List<DbTableRelationship> plus(List<DbTableRelationship> noFollow, DbTableRelationship rel) {
        LinkedList<DbTableRelationship> result = new LinkedList<>(noFollow);
        result.add(rel);
        return result;
    }

    public Stream<Tuple> tuplesFor(RelationalPathBase<?> relationalPathBase) {
        return queries.values()
                .stream().filter(eq -> eq.getRelationalPath() == relationalPathBase)
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .tuples();
    }

    public Stream<ExtractedTuple> extractedTuples() {
        return insertionOrder.stream()
                .map(queries::get)
                .map(ExtractionQuery::extractedTuples)
                .flatMap(identity());
    }

    @Data
    @AllArgsConstructor
    public static class ExtractionQuery {

        private DbTable table;
        private SQLQuery<Tuple> query;
        private BooleanExpression where;

        public RelationalPathBase getRelationalPath() {
            return table.getRelationalPath();
        }

        public Stream<Tuple> tuples() {
            return query.fetch().stream();
        }

        public Stream<ExtractedTuple> extractedTuples() {
            return tuples()
                    .map(tuple -> new ExtractedTuple(table.getRelationalPath(), tuple));
        }
    }

    @Data
    @AllArgsConstructor
    public static class ExtractedTuple {

        private RelationalPathBase<?> relationalPath;
        private Tuple tuple;
    }
}
