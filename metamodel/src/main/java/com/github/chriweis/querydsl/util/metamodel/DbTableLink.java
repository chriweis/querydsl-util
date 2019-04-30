package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.QuerydslUtil;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPath;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
public class DbTableLink {

    RelationalPath<?> from;
    List<? extends Path<?>> fromColumns;
    RelationalPath<?> to;
    List<? extends Path<?>> toColumns;

    public static DbTableLink fromForeignKeyField(Field field) {
        try {
            RelationalPath self = QuerydslUtil.relationalPath((Class<? extends RelationalPath<?>>) field.getDeclaringClass());

            Class<? extends RelationalPath<?>> otherType = (Class<? extends RelationalPath<?>>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            RelationalPath other = QuerydslUtil.relationalPath(otherType);

            ForeignKey<?> foreignKey = (ForeignKey<?>) field.get(self);
            List<? extends Path<?>> fromColumns = foreignKey.getLocalColumns();
            List<? extends Path<?>> toColumns = foreignKey.getForeignColumns().stream()
                    .map(columnName -> QuerydslUtil.column(other, columnName))
                    .collect(toList());

            return new DbTableLink(self, fromColumns, other, toColumns);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
