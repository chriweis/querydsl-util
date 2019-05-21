package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.core.types.Path;
import org.junit.Test;

import java.util.List;

import static com.github.chriweis.querydsl.util.metamodel.DbMetamodelTest.RELATIONSHIPS_OF_ADDRESS;
import static com.github.chriweis.querydsl.util.metamodel.DbMetamodelTest.RELATIONSHIPS_OF_PERSON;
import static com.github.chriweis.querydsl.util.metamodel.TestUtil.foreignKeyPath;
import static com.github.chriweis.querydsl.util.metamodel.TestUtil.keyPath;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;

public class DbTableTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldFindRelationsships() {
        assertThat(metamodel.tableFor(person).relationships())
                .hasSize(RELATIONSHIPS_OF_PERSON)
                .extracting(foreignKeyPath)
                .contains(address);
        assertThat(metamodel.tableFor(address).relationships())
                .hasSize(RELATIONSHIPS_OF_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }

    @Test
    public void shoudlFindSimplePrimaryKey() {
        assertThat((List<Path<?>>) metamodel.tableFor(person).primaryKeyFields())
                .hasSize(1)
                .contains((Path<?>) person.id);
    }

    @Test
    public void shoudlFindCompositePrimaryKey() {
        assertThat((List<Path<?>>) metamodel.tableFor(personType).primaryKeyFields())
                .hasSize(2)
                .contains(personType.id1)
                .contains(personType.id2);
    }
}
