package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.metamodel.DbMetamodelTest.FOREIGN_KEYS_IN_ADDRESS;
import static com.github.chriweis.querydsl.util.metamodel.DbMetamodelTest.FOREIGN_KEYS_REFERENCING_PERSON;
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
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(foreignKeyPath)
                .contains(address);
        assertThat(metamodel.getTableFor(address).getRelationships())
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }
}
