package com.github.chriweis.querydsl.util.tools;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import lombok.Data;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

@Data
public class DataExtractor {

    private final SQLQueryFactory queryFactory;
    private final RelationalPathBase<?> relationalPath;
    private final SQLQuery<Tuple> query;
    private List<OrderSpecifier<?>> primaryKeyOrder;

    public DataExtractor(SQLQueryFactory queryFactory, RelationalPathBase<?> relationalPath, SQLQuery<Tuple> query) {
        this.queryFactory = queryFactory;
        this.relationalPath = relationalPath;
        this.query = query;

        this.primaryKeyOrder = new ArrayList<>(relationalPath.getPrimaryKey().getLocalColumns().size());
        for (Path<?> path : relationalPath.getPrimaryKey().getLocalColumns()) {
            if (path instanceof NumberPath) {
                this.primaryKeyOrder.add(((NumberPath) path).asc());
            } else {
                throw new UnsupportedOperationException(format("ID path type '%s' not implemented yet", path.getClass().getSimpleName()));
            }
        }
    }

    public Stream<Tuple> tuples() {
        return query()
                .orderBy(primaryKeyOrder.toArray(new OrderSpecifier[0]))
                .fetch()
                .stream();
    }

    public Stream<ExtractedTuple> extractedTuples() {
        return tuples()
                .map(tuple -> new ExtractedTuple(relationalPath, tuple));
    }

    public Stream<ExtractedTuple> extractedTuplesBatched(int batchSize) {
        Iterator<Tuple> iterator = new TupleIterator(this, batchSize);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(tuple -> new ExtractedTuple(relationalPath, tuple));
    }

    protected List<Tuple> tuplesBatch(int batchSize, int offset) {
        return query()
                .orderBy(primaryKeyOrder.toArray(new OrderSpecifier[0]))
                .limit(batchSize)
                .offset(offset)
                .fetch();
    }

    private SQLQuery<Tuple> query() {
        return query.clone(queryFactory.getConnection());
    }

    static class TupleIterator implements Iterator<Tuple> {

        private final DataExtractor query;
        private final int batchSize;

        private int count = 0;
        private int position = 0;
        private List<Tuple> batch;

        public TupleIterator(DataExtractor query, int batchSize) {
            this.batchSize = batchSize;
            this.query = query;
        }

        @Override
        public boolean hasNext() {
            ensureTuple();
            return batch.size() > position;
        }

        @Override
        public Tuple next() {
            ensureTuple();
            return batch.get(position++);
        }

        private void ensureTuple() {
            if (batch == null || position >= batchSize) {
                batch = query.tuplesBatch(batchSize + 1, batchSize * count);
                count++;
                position = 0;
            }
        }
    }
}
