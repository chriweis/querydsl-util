package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.sql.RelationalPathBase;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static org.assertj.core.api.Assertions.assertThat;

public class DbMetamodelTest {

    static int TABLE_COUNT = 2;
    static int FOREIGN_KEYS_REFERENCING_PERSON = 1;
    static int FOREIGN_KEYS_IN_ADDRESS = 1;

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldDetermineTableCount() {
        assertThat(metamodel.getTableCount()).isEqualTo(TABLE_COUNT);
    }

    @Test
    public void shouldFindTables() {
        assertThat(metamodel.getTableFor(person)).isNotNull();
        assertThat(metamodel.getTableFor(address)).isNotNull();
    }

    @Test
    public void shouldFindRelationships() {
        assertThat(metamodel.getRelationshipsOf(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(TestUtil.foreignKeyPath)
                .contains(address);
        assertThat(metamodel.getRelationshipsOf(address))
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(TestUtil.keyPath)
                .contains(person);
    }

    @Test
    public void shouldFindForeignKeys() {
        assertThat(metamodel.getForeignKeyRelationshipsIn(address))
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(TestUtil.keyPath)
                .contains(person);
    }

    @Test
    public void shouldFindInverseForeignKeys() {
        assertThat(metamodel.getForeignKeyRelationshipsReferencing(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(TestUtil.foreignKeyPath)
                .contains(address);
    }

    @Test
    public void shouldFindForeignKeysAsRequiredTables() {
        assertThat(metamodel.getRequiredTablesFor(address))
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) person);
    }

    @Test
    public void shouldNotFindInverseForeignKeysAsRequiredTables() {
        assertThat(metamodel.getRequiredTablesFor(person)).isEmpty();
    }

    @Test
    public void shouldFindInverseForeignKeysAsDependentTables() {
        assertThat(metamodel.getDependentTablesOf(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) address);
    }
}
