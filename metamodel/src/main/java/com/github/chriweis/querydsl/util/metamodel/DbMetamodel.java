package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.QuerydslUtil;
import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import static com.github.chriweis.querydsl.util.util.BooleanUtil.not;
import static java.util.stream.Collectors.toSet;

@Data
public class DbMetamodel {

    private Set<DbTable> tables = new LinkedHashSet<>();
    private Set<DbTableRelationship> foreignKeys = new LinkedHashSet<>();

    private boolean sealed = false;

    public static DbMetamodel for$(Package package$) {
        DbMetamodel metamodel = new DbMetamodel();
        QuerydslUtil.tablesIn(package$).stream()
                .map(path -> DbTable.forRelationalPath(path))
                .forEach(metamodel::addTable);
        return metamodel;
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
        foreignKeys.addAll(new LinkedList<ForeignKey>(relationalPath.getForeignKeys()).stream()
                .map(mapper)
                .map(field -> DbTableRelationship.fromForeignKeyField(field))
                .collect(toSet()));
        foreignKeys.stream()
                .forEach(foreignKey -> foreignKey.setMetamodel(this));
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

    public void seal() {
        sealed = true;
    }

    public Set<DbTableRelationship> getRelationshipsOf(RelationalPathBase<?> relationalPath) {
        return getRelationshipsOf(getTableFor(relationalPath));
    }

    public Set<DbTableRelationship> getRelationshipsOf(DbTable table) {
        return getForeignKeys().stream()
                .filter(relationship -> relationship.hasTable(table))
                .collect(toSet());
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsIn(DbTable table) {
        return getForeignKeyRelationshipsIn(table.getRelationalPath());
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsIn(RelationalPathBase<?> relationalPath) {
        return getForeignKeys().stream()
                .filter(relationship -> relationship.getForeignKeyRelationalPath() == relationalPath)
                .collect(toSet());
    }

    public Set<DbTable> getRequiredTablesFor(RelationalPathBase<?> relationalPath) {
        return getForeignKeyRelationshipsIn(relationalPath).stream()
                .map(DbTableRelationship::getKeyTable)
                .collect(toSet());
    }

    public Set<DbTableRelationship> getInverseForeignKeyRelationshipsIn(DbTable table) {
        return getForeignKeyRelationshipsReferencing(table);
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsReferencing(DbTable table) {
        return getForeignKeyRelationshipsReferencing(table.getRelationalPath());
    }

    public Set<DbTableRelationship> getForeignKeyRelationshipsReferencing(RelationalPathBase<?> relationalPath) {
        return getForeignKeys().stream()
                .filter(relationship -> relationship.getKeyRelationalPath() == relationalPath)
                .collect(toSet());
    }

    public void visit(DbMetamodelVisitor visitor) {
        getTables().stream()
                .forEach(visitor::visitTable);
    }

    public Set<DbTable> getDependentTablesOf(RelationalPathBase<?> relationalPath) {
        return getForeignKeyRelationshipsReferencing(relationalPath).stream()
                .map(DbTableRelationship::getForeignKeyTable)
                .collect(toSet());
    }
}
