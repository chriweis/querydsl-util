package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.QuerydslUtil;
import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import lombok.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;

@Data
@AllArgsConstructor(access = PACKAGE)
@RequiredArgsConstructor
@ToString(exclude = "metamodel")
@EqualsAndHashCode(exclude = {"metamodel"})
public class DbTableRelationship {

    private final RelationalPathBase<?> foreignKeyRelationalPath;
    private final List<? extends Path<?>> foreignKeyColumns;
    private final RelationalPathBase<?> keyRelationalPath;
    private final List<? extends Path<?>> keyColumns;
    private DbMetamodel metamodel;

    public static DbTableRelationship fromForeignKeyField(Field field) {
        try {
            RelationalPathBase foreignKeyPath = QuerydslUtil.relationalPath((Class<? extends RelationalPathBase<?>>) field.getDeclaringClass());

            Class<? extends RelationalPathBase<?>> keyPathType = (Class<? extends RelationalPathBase<?>>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            RelationalPathBase keyPath = QuerydslUtil.relationalPath(keyPathType);

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
        return metamodel.tableFor(foreignKeyRelationalPath);
    }

    public DbTable getKeyTable() {
        return metamodel.tableFor(keyRelationalPath);
    }

    public boolean hasTable(DbTable table) {
        return getForeignKeyTable() == table || getKeyTable() == table;
    }
}
