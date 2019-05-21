package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCommentOnPerson.commentOnPerson;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DbMetamodelVisitorTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldVisitMetamodel() {
        DbMetamodelVisitor visitorMock = mock(DbMetamodelVisitor.class);
        DbTableRelationship personPersonTypeRelationship = metamodel.relationshipBetween(personType, person).orElseThrow(NoSuchElementException::new);
        DbTableRelationship addressPersonRelationship = metamodel.relationshipBetween(person, address).orElseThrow(NoSuchElementException::new);
        DbTableRelationship addressCountryRelationship = metamodel.relationshipBetween(address, country).orElseThrow(NoSuchElementException::new);

        metamodel.visit(visitorMock);

        verify(visitorMock).visitTable(tableFor(personType));
        verify(visitorMock).afterVisitTable(tableFor(personType));
        verify(visitorMock).visitTable(tableFor(person));
        verify(visitorMock).afterVisitTable(tableFor(person));
        verify(visitorMock).visitTable(tableFor(address));
        verify(visitorMock).afterVisitTable(tableFor(address));
        verify(visitorMock).visitTable(tableFor(country));
        verify(visitorMock).afterVisitTable(tableFor(country));
        verify(visitorMock).visitTable(tableFor(commentOnPerson));
        verify(visitorMock).afterVisitTable(tableFor(commentOnPerson));

        verify(visitorMock).visitForeignKey(personPersonTypeRelationship, tableFor(person), tableFor(personType));
        verify(visitorMock).afterVisitForeignKey(personPersonTypeRelationship, tableFor(person), tableFor(personType));
        verify(visitorMock).visitInverseForeignKey(personPersonTypeRelationship, tableFor(personType), tableFor(person));
        verify(visitorMock).afterVisitInverseForeignKey(personPersonTypeRelationship, tableFor(personType), tableFor(person));

        verify(visitorMock).visitForeignKey(addressPersonRelationship, tableFor(address), tableFor(person));
        verify(visitorMock).afterVisitForeignKey(addressPersonRelationship, tableFor(address), tableFor(person));
        verify(visitorMock).visitInverseForeignKey(addressPersonRelationship, tableFor(person), tableFor(address));
        verify(visitorMock).afterVisitInverseForeignKey(addressPersonRelationship, tableFor(person), tableFor(address));

        verify(visitorMock).visitForeignKey(addressCountryRelationship, tableFor(address), tableFor(country));
        verify(visitorMock).afterVisitForeignKey(addressCountryRelationship, tableFor(address), tableFor(country));
        verify(visitorMock).visitInverseForeignKey(addressCountryRelationship, tableFor(country), tableFor(address));
        verify(visitorMock).afterVisitInverseForeignKey(addressCountryRelationship, tableFor(country), tableFor(address));

        verifyNoMoreInteractions(visitorMock);
    }

    @Test
    public void shouldFindRequiredTables() {
        assertThat(requiredTablesFor(address)).contains(table(person));
    }

    @Test
    public void shouldNotFindDependentTablesAsRequired() {
        assertThat(requiredTablesFor(person)).doesNotContain(table(address));
    }

    @Test
    public void shouldFindDependentTables() {
        assertThat(dependentTablesFor(person)).contains(table(address));
    }

    @Test
    public void shouldNotFindRequiredTablesAsDependent() {
        assertThat(dependentTablesFor(address)).doesNotContain(table(person));
    }

    private Set<DbTable> requiredTablesFor(RelationalPathBase<?> relationalPath) {
        TestDbMetamodelVisitor visitor = new TestDbMetamodelVisitor();
        metamodel.tableFor(relationalPath).visit(visitor);
        return visitor.getRequiredTables();
    }

    private Set<DbTable> dependentTablesFor(RelationalPathBase<?> relationalPath) {
        TestDbMetamodelVisitor visitor = new TestDbMetamodelVisitor();
        metamodel.tableFor(relationalPath).visit(visitor);
        return visitor.getDependentTables();
    }

    private DbTable table(RelationalPathBase<?> relationalPath) {
        return metamodel.tableFor(relationalPath);
    }

    private DbTable tableFor(RelationalPathBase<?> relationalPath) {
        return metamodel.tableFor(relationalPath);
    }

    @Getter
    private static class TestDbMetamodelVisitor extends DbMetamodelVisitorAdapter {

        private Set<DbTable> tables = new LinkedHashSet<>();
        private Set<DbTable> requiredTables = new LinkedHashSet<>();
        private Set<DbTable> dependentTables = new LinkedHashSet<>();

        @Override
        public void visitTable(DbTable table) {
            table.visit(this);
        }

        @Override
        public void visitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable) {
            if (requiredTables.contains(keyTable)) {
                return;
            }
            requiredTables.add(keyTable);
        }

        @Override
        public void visitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable) {
            if (dependentTables.contains(foreignKeyTable)) {
                return;
            }
            dependentTables.add(foreignKeyTable);
        }
    }
}
