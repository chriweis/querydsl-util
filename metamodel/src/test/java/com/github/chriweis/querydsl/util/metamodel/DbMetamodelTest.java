package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.sql.RelationalPathBase;
import org.junit.Test;

import java.util.List;

import static com.github.chriweis.querydsl.util.metamodel.TestUtil.foreignKeyPath;
import static com.github.chriweis.querydsl.util.metamodel.TestUtil.keyPath;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCommentOnPerson.commentOnPerson;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class DbMetamodelTest {

    static int TABLE_COUNT = 5;
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
        assertThat(metamodel.tableFor(person)).isNotNull();
        assertThat(metamodel.tableFor(address)).isNotNull();
    }

    @Test
    public void shouldFindRelationships() {
        assertThat(metamodel.relationshipsOf(person))
                .hasSize(RELATIONSHIPS_OF_PERSON)
                .extracting(foreignKeyPath)
                .contains(address);
        assertThat(metamodel.relationshipsOf(address))
                .hasSize(RELATIONSHIPS_OF_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }

    @Test
    public void shouldFindForeignKeys() {
        assertThat(metamodel.foreignKeyRelationshipsIn(address))
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(keyPath)
                .contains(person);
    }

    @Test
    public void shouldFindInverseForeignKeys() {
        assertThat(metamodel.inverseForeignKeyRelationshipsIn(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(foreignKeyPath)
                .contains(address);
        assertThat(metamodel.inverseForeignKeyRelationshipsIn(person))
                .isEqualTo(metamodel.inverseForeignKeyRelationshipsIn(metamodel.tableFor(person)));
        assertThat(metamodel.inverseForeignKeyRelationshipsIn(person))
                .isEqualTo(metamodel.foreignKeyRelationshipsReferencing(person));
        assertThat(metamodel.inverseForeignKeyRelationshipsIn(person))
                .isEqualTo(metamodel.foreignKeyRelationshipsReferencing(metamodel.tableFor(person)));
    }

    @Test
    public void shouldFindForeignKeysAsRequiredTables() {
        assertThat(metamodel.requiredTablesFor(address))
                .hasSize(FOREIGN_KEYS_IN_ADDRESS)
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) person);
    }

    @Test
    public void shouldNotFindInverseForeignKeysAsRequiredTables() {
        assertThat(metamodel.requiredTablesFor(personType)).isEmpty();
    }

    @Test
    public void shouldFindInverseForeignKeysAsDependentTables() {
        assertThat(metamodel.dependentTablesOf(person))
                .hasSize(FOREIGN_KEYS_REFERENCING_PERSON)
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) address);
    }

    @Test
    public void shouldFindAllRequiredTables() {
        assertThat(metamodel.allRequiredTablesFor(address))
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) country)
                .contains((RelationalPathBase) person)
                .contains((RelationalPathBase) personType);
    }

    @Test
    public void shouldOrderTablesByInsertionOrder1() {
        List<RelationalPathBase<?>> insertionOrder = metamodel.pathsOrderedForInsertion(address, personType, person, country);
        assertThat(insertionOrder.indexOf(personType)).isLessThan(insertionOrder.indexOf(person));
        assertThat(insertionOrder.indexOf(person)).isLessThan(insertionOrder.indexOf(address));
        assertThat(insertionOrder.indexOf(country)).isLessThan(insertionOrder.indexOf(address));
    }

    @Test
    public void shouldOrderTablesByInsertionOrder2() {
        List<RelationalPathBase<?>> insertionOrder = metamodel.pathsOrderedForInsertion(person, personType, address, country);
        assertThat(insertionOrder.indexOf(personType)).isLessThan(insertionOrder.indexOf(person));
        assertThat(insertionOrder.indexOf(person)).isLessThan(insertionOrder.indexOf(address));
        assertThat(insertionOrder.indexOf(country)).isLessThan(insertionOrder.indexOf(address));
    }

    @Test
    public void shouldSupportAdditionalForeignKeys() {
        DbMetamodel metamodel = this.metamodel
                .with(new DbTableRelationship(commentOnPerson, singletonList(commentOnPerson.personId), person, singletonList(person.id)));
        assertThat(metamodel.getTableCount()).isEqualTo(this.metamodel.getTableCount());
        assertThat(metamodel.getTables()).isEqualTo(this.metamodel.getTables());
        assertThat(metamodel.getForeignKeys().size()).isEqualTo(this.metamodel.getForeignKeys().size() + 1);
        assertThat(metamodel.dependentTablesOf(person))
                .extracting(DbTable::getRelationalPath)
                .contains((RelationalPathBase) commentOnPerson);
        assertThat(metamodel.relationshipsOf(commentOnPerson))
                .extracting(keyPath)
                .contains(person);
    }
}
