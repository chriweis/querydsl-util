package com.github.chriweis.querydsl.util.visitor;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.metamodel.DbTable;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import java.util.Map;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;

public class RowCountVisitorTest extends AbstractTestDbTest {

    RowCountVisitor visitor = new RowCountVisitor(testDb().sqlQueryFactory());

    @Test
    public void shouldReturnCountStatementsForAllTables() {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

        metamodel.visit(visitor);

        Map<DbTable, Long> rowCounts = visitor.getRowCounts();
        assertThat(rowCounts.get(metamodel.getTableFor(personType))).isEqualTo(2);
        assertThat(rowCounts.get(metamodel.getTableFor(person))).isEqualTo(4);
        assertThat(rowCounts.get(metamodel.getTableFor(country))).isEqualTo(4);
        assertThat(rowCounts.get(metamodel.getTableFor(address))).isEqualTo(8);
    }
}
