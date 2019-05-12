package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.sql.RelationalPathBase;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.metamodel.TestUtil.foreignKeyPath;
import static com.github.chriweis.querydsl.util.metamodel.TestUtil.keyPath;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;

public class DbMetamodelTest {

    static int TABLE_COUNT = 4;
    static int FOREIGN_KEYS_REFERENCING_PERSON = 1;
    static int RELATIONSHIPS_OF_PERSON = 2;
    static int FOREIGN_KEYS_IN_ADDRESS = 2;
    static int RELATIONSHIPS_OF_ADDRESS = 2;

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
                .hasSize(RELATIONSHIPS_OF_PERSON)
                .extracting(foreignKeyPath)
                .contains(address);
        assertThat(metamodel.getRelationshipsOf(address))
                .hasSize(RELATIONSHIPS_OF_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }

    @Test
    public void shouldFindForeignKeys() {
        assertThat(metamodel.getForeignKeyRelationshipsIn(address))
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }

    @Test
    public void shouldFindInverseForeignKeys() {
        assertThat(metamodel.getInverseForeignKeyRelationshipsIn(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(foreignKeyPath)
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
        assertThat(metamodel.getRequiredTablesFor(personType)).isEmpty();
    }

    @Test
    public void shouldFindInverseForeignKeysAsDependentTables() {
        assertThat(metamodel.getDependentTablesOf(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) address);
    }
}
