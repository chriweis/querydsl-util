package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.QuerydslUtil;
import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPath;
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
    private Set<DbTableLink> references = new LinkedHashSet<>();

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
        RelationalPath<?> relationalPath = table.getRelationalPath();
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
        references.addAll(new LinkedList<ForeignKey>(relationalPath.getForeignKeys()).stream()
                .map(mapper)
                .map(field -> DbTableLink.fromForeignKeyField(field))
                .collect(toSet()));
    }

    public int getTableCount() {
        return tables.size();
    }

    public DbTable getTableFor(RelationalPath<?> relationalPath) {
        return tables.stream()
                .filter(table -> table.getRelationalPath().equals(relationalPath))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public void seal() {
        sealed = true;
    }
}
