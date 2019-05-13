package com.github.chriweis.querydsl.util.visitors;

import com.github.chriweis.querydsl.util.metamodel.DbMetamodelVisitorAdapter;
import com.github.chriweis.querydsl.util.metamodel.DbTable;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class RowCountVisitor extends DbMetamodelVisitorAdapter {

    private final SQLQueryFactory queryFactory;
    private final Map<DbTable, SQLQuery<Long>> rowCountQueries = new LinkedHashMap<>();

    private Map<DbTable, Long> rowCounts;

    public RowCountVisitor(SQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public void visitTable(DbTable table) {
        RelationalPathBase<?> relationalPath = table.getRelationalPath();
        SQLQuery<Long> countQuery = queryFactory.select(relationalPath.count())
                .from(relationalPath);
        rowCountQueries.put(table, countQuery);
    }

    public Map<DbTable, SQLQuery<Long>> getRowCountQueries() {
        return rowCountQueries;
    }

    public Map<DbTable, Long> getRowCounts() {
        if (rowCounts == null) {
            rowCounts = fetchCounts();
        }
        return rowCounts;
    }

    protected Map<DbTable, Long> fetchCounts() {
        Map<DbTable, Long> result = new LinkedHashMap<>();
        for (Map.Entry<DbTable, SQLQuery<Long>> queryEntry : getRowCountQueries().entrySet()) {
            result.put(queryEntry.getKey(), queryEntry.getValue().fetchCount());
        }
        return result;
    }
}
