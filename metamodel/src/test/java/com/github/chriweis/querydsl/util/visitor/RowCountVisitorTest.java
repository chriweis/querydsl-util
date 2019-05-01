package com.github.chriweis.querydsl.util.visitor;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.metamodel.DbTable;
import com.github.chriweis.querydsl.util.sampledb.generated.hibernate.Address;
import com.github.chriweis.querydsl.util.sampledb.generated.hibernate.Person;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import java.util.Map;

import static com.github.chriweis.querydsl.util.metamodel.TestUtil.set;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static org.assertj.core.api.Assertions.assertThat;

public class RowCountVisitorTest extends AbstractTestDbTest {

    RowCountVisitor visitor = new RowCountVisitor(testDb().sqlQueryFactory());

    @Test
    public void shouldReturnCountStatementsForAllTables() {
        Person somePerson = new Person();
        somePerson.setName("Person 1");
        somePerson.setAddresses(set(
                new Address(somePerson, "Address 1"),
                new Address(somePerson, "Address 2")));

        testDb().doInTransaction(session -> session.save(somePerson));

        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

        metamodel.visit(visitor);

        Map<DbTable, Long> rowCounts = visitor.getRowCounts();
        assertThat(rowCounts.get(metamodel.getTableFor(person))).isEqualTo(1);
        assertThat(rowCounts.get(metamodel.getTableFor(address))).isEqualTo(2);
    }
}
