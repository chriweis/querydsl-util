package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.querydsl.sql.RelationalPath;
import lombok.Getter;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public class DbMetamodelVisitorTest {

    @Test
    public void shouldVisitTables() {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

        assertThat(metamodel.getTableCount()).isEqualTo(2);
        assertThat(metamodel.getTableFor(person)).isNotNull();
        assertThat(metamodel.getTableFor(address)).isNotNull();
    }

    @Test
    public void shouldVisitRequiredTables() {
        assertThat(requiredTablesFor(address)).contains(person);
    }

    @Test
    public void shouldNotFindDependentTablesAsRequired() {
        assertThat(requiredTablesFor(person)).doesNotContain(address);
    }

    @Test
    public void shouldFindDependentTables() {
        assertThat(dependentTablesFor(person)).contains(address);
    }

    @Test
    public void shouldNotFindRequiredTablesAsDependent() {
        assertThat(dependentTablesFor(address)).doesNotContain(person);
    }

    private Set<RelationalPath<?>> requiredTablesFor(RelationalPath<?> relationalPath) {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());
        TestDbMetamodelVisitor visitor = new TestDbMetamodelVisitor();
        metamodel.getTableFor(relationalPath).visit(visitor);
        return visitor.getRequiredTables().stream().map(DbTable::getRelationalPath).collect(toSet());
    }

    private Set<RelationalPath<?>> dependentTablesFor(RelationalPath<?> relationalPath) {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());
        TestDbMetamodelVisitor visitor = new TestDbMetamodelVisitor();
        metamodel.getTableFor(relationalPath).visit(visitor);
        return visitor.getDependentTables().stream().map(DbTable::getRelationalPath).collect(toSet());
    }

    @Getter
    private static class TestDbMetamodelVisitor implements DbMetamodelVisitor {

        private Set<DbTable> requiredTables = new LinkedHashSet<>();
        private Set<DbTable> dependentTables = new LinkedHashSet<>();

        @Override
        public void visitRequired(DbTable table, DbTable requiredTable, DbTableLink tableLink) {
            if (requiredTables.contains(requiredTable)) {
                return;
            }
            requiredTables.add(requiredTable);
            requiredTable.visit(this);
        }

        @Override
        public void visitDependant(DbTable table, DbTable dependantTable, DbTableLink tableLink) {
            if (dependentTables.contains(dependantTable)) {
                return;
            }
            dependentTables.add(dependantTable);
            dependantTable.visit(this);
        }
    }
}
