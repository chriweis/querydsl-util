package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.QuerydslUtil;
import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.chriweis.querydsl.util.util.BooleanUtil.not;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor(access = PACKAGE)
@AllArgsConstructor(access = PRIVATE)
public class DbMetamodel {

    private Set<DbTable> tables = new LinkedHashSet<>();
    private Set<DbTableRelationship> foreignKeys = new LinkedHashSet<>();

    private boolean sealed = false;

    public static DbMetamodel for$(Package package$) {
        DbMetamodel metamodel = new DbMetamodel();
        QuerydslUtil.tablesIn(package$).stream()
                .map(path -> DbTable.forRelationalPath(path))
                .forEach(metamodel::addTable);
        metamodel.seal();
        return metamodel;
    }

    public DbMetamodel with(DbTableRelationship... tableRelationship) {
        DbMetamodel result = new DbMetamodel();
        this.tables.stream()
                .map(DbTable::freshClone)
                .forEach(result::addTable);
        Stream.of(tableRelationship).forEach(r -> {
            r.setMetamodel(result);
            result.foreignKeys.add(r);
        });
        return result;
    }

    public void addTable(DbTable table) {
        Assert.state(not(sealed));

        table.setMetamodel(this);
        tables.add(table);
        RelationalPathBase<?> relationalPath = table.getRelationalPath();
        Function<Object, Field> mapper = foreignKey -> {
            try {
                for (Field field : relationalPath.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.get(relationalPath) == foreignKey) {
                        return field;
                    }
                }
                throw new NoSuchElementException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Set<DbTableRelationship> foreignKeysForTable = new LinkedList<ForeignKey>(relationalPath.getForeignKeys()).stream()
                .map(mapper)
                .map(field -> DbTableRelationship.fromForeignKeyField(field))
                .collect(toSet());
        foreignKeysForTable.stream()
                .forEach(foreignKey -> foreignKey.setMetamodel(this));
        foreignKeys.addAll(foreignKeysForTable);
    }

    public int getTableCount() {
        return tables.size();
    }

    public DbTable getTableFor(RelationalPathBase<?> relationalPath) {
        return tables.stream()
                .filter(table -> table.getRelationalPath().equals(relationalPath))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    protected void seal() {
        this.tables = Collections.unmodifiableSet(this.tables);
        this.foreignKeys = Collections.unmodifiableSet(this.foreignKeys);
        sealed = true;
    }

    public Set<DbTableRelationship> getRelationshipsOf(DbTable table) {
        return getForeignKeys().stream()
                .filter(relationship -> relationship.hasTable(table))
                .collect(toSet());
    }

    public Set<DbTableRelationship> getRelationshipsOf(RelationalPathBase<?> relationalPath) {
        return getRelationshipsOf(getTableFor(relationalPath));
    }

    public Optional<DbTableRelationship> getRelationshipBetween(DbTable table1, DbTable table2) {
        return foreignKeys.stream()
                .filter(rel -> rel.getKeyTable() == table1 || rel.getForeignKeyTable() == table1)
                .filter(rel -> rel.getKeyTable() == table2 || rel.getForeignKeyTable() == table2)
                .findAny();
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsIn(DbTable table) {
        return getForeignKeys().stream()
                .filter(relationship -> relationship.getForeignKeyRelationalPath() == table.getRelationalPath())
                .collect(toSet());
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsIn(RelationalPathBase<?> relationalPath) {
        return getForeignKeyRelationshipsIn(getTableFor(relationalPath));
    }

    public Set<DbTable> getRequiredTablesFor(DbTable table) {
        return getForeignKeyRelationshipsIn(table).stream()
                .map(DbTableRelationship::getKeyTable)
                .collect(toSet());
    }

    public Set<DbTable> getRequiredTablesFor(RelationalPathBase<?> relationalPath) {
        return getRequiredTablesFor(getTableFor(relationalPath));
    }

    public Set<DbTable> getAllRequiredTablesFor(DbTable table) {
        Set<DbTable> requiredTables = getRequiredTablesFor(table);
        for (DbTable requiredTable : new ArrayList<>(requiredTables)) {
            requiredTables.addAll(getAllRequiredTablesFor(requiredTable));
        }
        return requiredTables;
    }

    public Set<DbTable> getAllRequiredTablesFor(RelationalPathBase<?> relationalPath) {
        return getAllRequiredTablesFor(getTableFor(relationalPath));
    }

    public Set<DbTableRelationship> getInverseForeignKeyRelationshipsIn(DbTable table) {
        return getForeignKeys().stream()
                .filter(relationship -> relationship.getKeyTable() == table)
                .collect(toSet());
    }

    public Set<DbTableRelationship> getInverseForeignKeyRelationshipsIn(RelationalPathBase<?> relationalPath) {
        return getForeignKeyRelationshipsReferencing(getTableFor(relationalPath));
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsReferencing(DbTable table) {
        return getInverseForeignKeyRelationshipsIn(table);
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsReferencing(RelationalPathBase<?> relationalPath) {
        return getForeignKeyRelationshipsReferencing(getTableFor(relationalPath));
    }

    public void visit(DbMetamodelVisitor visitor) {
        getTables().stream()
                .forEach(table -> {
                    visitor.visitTable(table);
                    table.visit(visitor);
                    visitor.afterVisitTable(table);
                });
    }

    public Set<DbTable> getDependentTablesOf(RelationalPathBase<?> relationalPath) {
        return getForeignKeyRelationshipsReferencing(relationalPath).stream()
                .map(DbTableRelationship::getForeignKeyTable)
                .collect(toSet());
    }

    public List<DbTable> orderTablesForInsertion(Collection<DbTable> tables) {
        List<DbTable> result = new ArrayList<>(tables);
        result.sort(new InsertionOrderComparator(this));
        return result;
    }

    public List<RelationalPathBase<?>> orderPathsForInsertion(Collection<RelationalPathBase<?>> relationalPathBases) {
        List<DbTable> orderedTables = relationalPathBases.stream()
                .map(this::getTableFor)
                .collect(toList());
        List<RelationalPathBase<?>> result = orderTablesForInsertion(orderedTables).stream()
                .map(DbTable::getRelationalPath)
                .collect(toList());
        return result;
    }

    public List<RelationalPathBase<?>> orderPathsForInsertion(RelationalPathBase<?>... relationalPathBases) {
        return orderPathsForInsertion(asList(relationalPathBases));
    }

    private static class InsertionOrderComparator implements Comparator<DbTable> {

        private final DbMetamodel metamodel;

        private InsertionOrderComparator(DbMetamodel metamodel) {
            this.metamodel = metamodel;
        }

        @Override
        public int compare(DbTable table1, DbTable table2) {
            boolean p1RequiresP2 = metamodel.getAllRequiredTablesFor(table1).contains(table2);
            boolean p2RequiresP1 = metamodel.getAllRequiredTablesFor(table2).contains(table1);
            if (p1RequiresP2) {
                return 1;
            } else if (p2RequiresP1) {
                return -1;
            }
            return 0;
        }
    }
}
