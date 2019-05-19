package com.github.chriweis.querydsl.util.tools;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLMergeClause;

import java.sql.Timestamp;

public class DataInserter {

    private final SQLQueryFactory sqlQueryFactory;

    public DataInserter(SQLQueryFactory sqlQueryFactory) {
        this.sqlQueryFactory = sqlQueryFactory;
    }

    public void insert(RelationalPathBase relationalPath, Tuple tuple) {
        SQLMergeClause merge = sqlQueryFactory.merge(relationalPath);
        relationalPath.getColumns().stream()
                .forEach(column -> {
                    Path path = (Path) column;
                    Object value = tuple.get((Expression) column);
                    if (value == null) {
                        return;
                    }
                    if (path instanceof DateTimePath && value instanceof Long) {
                        value = new Timestamp((Long) value);
                    } else if (path instanceof DateTimePath && value instanceof String) {
                        value = timestampFrom((String) value);
                    }
                    merge.set(path, value);
                });
        merge.execute();
    }

    private Timestamp timestampFrom(String value) {
        throw new UnsupportedOperationException();
    }
}
