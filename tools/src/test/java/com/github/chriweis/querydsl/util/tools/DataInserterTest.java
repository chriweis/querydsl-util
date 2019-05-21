package com.github.chriweis.querydsl.util.tools;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.TestDb;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.metamodel.DbTable;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import java.util.Map;

import static com.github.chriweis.querydsl.util.TestDb.InitializationMode.WithData;
import static com.github.chriweis.querydsl.util.TestDb.InitializationMode.WithoutData;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;

public class DataInserterTest extends AbstractTestDbTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    TestDb testDb1 = newTestDb(WithData);
    TestDb testDb2 = newTestDb(WithoutData);

    @Test
    public void shouldInsertData() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb1.sqlQueryFactory(), country, country.id.eq(1L))
                .extractFrom(metamodel);

        DataInserter dataInserter = new DataInserter(testDb2.sqlQueryFactory());

        dataSetExtractor
                .tuplesFor(country)
                .forEach(extractedTuple -> dataInserter.insert(extractedTuple.getRelationalPath(), extractedTuple.getTuple()));

        Map<DbTable, Long> rowCounts = rowCounts(testDb2);
        assertThat(rowCounts.get(metamodel.tableFor(country))).isEqualTo(1);
    }

    @Test
    public void shouldInsertDataWithouthViolatingForeignKeyRelationships() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb1.sqlQueryFactory(), person, person.id.eq(1L))
                .extractFrom(metamodel);

        DataInserter dataInserter = new DataInserter(testDb2.sqlQueryFactory());

        dataSetExtractor
                .extractedTuples()
                .forEach(extractedTuple -> dataInserter.insert(extractedTuple.getRelationalPath(), extractedTuple.getTuple()));

        Map<DbTable, Long> rowCounts = rowCounts(testDb2);
        assertThat(rowCounts.get(metamodel.tableFor(personType))).isEqualTo(1);
    }

    private Map<DbTable, Long> rowCounts(TestDb testDb2) {
        RowCounter rowCounter = new RowCounter(testDb2.sqlQueryFactory())
                .visit(metamodel);
        return rowCounter.getRowCounts();
    }
}
