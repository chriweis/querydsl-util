package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static org.assertj.core.api.Assertions.assertThat;

public class DbMetamodelVisitorTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldVisitTables() {
        TestDbMetamodelVisitor visitor = new TestDbMetamodelVisitor();
        metamodel.visit(visitor);
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
        metamodel.getTableFor(relationalPath).visit(visitor);
        return visitor.getRequiredTables();
    }

    private Set<DbTable> dependentTablesFor(RelationalPathBase<?> relationalPath) {
        TestDbMetamodelVisitor visitor = new TestDbMetamodelVisitor();
        metamodel.getTableFor(relationalPath).visit(visitor);
        return visitor.getDependentTables();
    }

    private DbTable table(RelationalPathBase<?> relationalPath) {
        return metamodel.getTableFor(relationalPath);
    }

    @Getter
    private static class TestDbMetamodelVisitor implements DbMetamodelVisitor {

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
            keyTable.visit(this);
        }

        @Override
        public void visitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable) {
            if (dependentTables.contains(foreignKeyTable)) {
                return;
            }
            dependentTables.add(foreignKeyTable);
            foreignKeyTable.visit(this);
        }
    }
}
