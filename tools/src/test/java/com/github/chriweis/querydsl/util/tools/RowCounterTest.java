package com.github.chriweis.querydsl.util.tools;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RowCounterTest extends AbstractTestDbTest {

    RowCounter visitor = new RowCounter(testDb().sqlQueryFactory());

    @Test
    public void shouldReturnCountStatementsForAllTables() {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

        visitor.visit(metamodel);

        Map<DbTable, Long> rowCounts = visitor.getRowCounts();
        assertThat(rowCounts.get(metamodel.tableFor(personType))).isEqualTo(2);
        assertThat(rowCounts.get(metamodel.tableFor(person))).isEqualTo(4);
        assertThat(rowCounts.get(metamodel.tableFor(country))).isEqualTo(4);
        assertThat(rowCounts.get(metamodel.tableFor(address))).isEqualTo(8);
    }

    @Test
    public void shouldComplainWhenNotRunYet() {
        assertThatThrownBy(() -> visitor.getRowCounts()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> visitor.getRowCountQueries()).isInstanceOf(IllegalStateException.class);
    }
}
