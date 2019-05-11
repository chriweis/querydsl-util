package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.metamodel.DbMetamodelTest.RELATIONSHIPS_OF_ADDRESS;
import static com.github.chriweis.querydsl.util.metamodel.DbMetamodelTest.RELATIONSHIPS_OF_PERSON;
import static com.github.chriweis.querydsl.util.metamodel.TestUtil.foreignKeyPath;
import static com.github.chriweis.querydsl.util.metamodel.TestUtil.keyPath;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static org.assertj.core.api.Assertions.assertThat;

public class DbTableTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldFindRelationsships() {
        assertThat(metamodel.getTableFor(person).getRelationships())
                .hasSize(RELATIONSHIPS_OF_PERSON)
                .extracting(foreignKeyPath)
                .contains(address);
        assertThat(metamodel.getTableFor(address).getRelationships())
                .hasSize(RELATIONSHIPS_OF_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }
}
