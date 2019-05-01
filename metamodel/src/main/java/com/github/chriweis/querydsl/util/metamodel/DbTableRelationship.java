package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.QuerydslUtil;
import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
@ToString(exclude = "metamodel")
@EqualsAndHashCode(exclude = {"metamodel"})
public class DbTableRelationship {

    private RelationalPath<?> foreignKeyRelationalPath;
    private List<? extends Path<?>> foreignKeyColumns;
    private RelationalPath<?> keyRelationalPath;
    private List<? extends Path<?>> keyColumns;
    private DbMetamodel metamodel;

    public static DbTableRelationship fromForeignKeyField(Field field) {
        try {
            RelationalPath foreignKeyPath = QuerydslUtil.relationalPath((Class<? extends RelationalPath<?>>) field.getDeclaringClass());

            Class<? extends RelationalPath<?>> keyPathType = (Class<? extends RelationalPath<?>>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            RelationalPath keyPath = QuerydslUtil.relationalPath(keyPathType);

            ForeignKey<?> foreignKey = (ForeignKey<?>) field.get(foreignKeyPath);
            List<? extends Path<?>> foreignKeyColumns = foreignKey.getLocalColumns();
            List<? extends Path<?>> keyColumns = foreignKey.getForeignColumns().stream()
                    .map(columnName -> QuerydslUtil.column(keyPath, columnName))
                    .collect(toList());

            return new DbTableRelationship(foreignKeyPath, foreignKeyColumns, keyPath, keyColumns, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DbTableRelationship setMetamodel(DbMetamodel metamodel) {
        Assert.state(this.metamodel == null);

        this.metamodel = metamodel;

        return this;
    }

    public DbTable getForeignKeyTable() {
        return metamodel.getTableFor(foreignKeyRelationalPath);
    }

    public DbTable getKeyTable() {
        return metamodel.getTableFor(keyRelationalPath);
    }

    public boolean hasTable(DbTable table) {
        return getForeignKeyTable() == table || getKeyTable() == table;
    }
}
