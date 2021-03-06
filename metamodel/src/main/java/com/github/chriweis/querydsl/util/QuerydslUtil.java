package com.github.chriweis.querydsl.util;

import com.querydsl.core.types.Path;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

public class QuerydslUtil {

    public static Set<RelationalPathBase> tablesIn(Package package$) {
        Reflections reflections = new Reflections(package$.getName());
        return reflections.getSubTypesOf(RelationalPathBase.class).stream()
                .map(QuerydslUtil::instanceOf)
                .collect(toSet());
    }

    private static <T extends RelationalPathBase> T instanceOf(Class<T> clazz) {
        try {
            String instanceFieldName = clazz.getSimpleName().substring(1, 2).toLowerCase() + clazz.getSimpleName().substring(2);
            Field instanceField = clazz.getDeclaredField(instanceFieldName);
            instanceField.setAccessible(true);
            return clazz.cast(instanceField.get(clazz));
        } catch (Exception e) {
            throw new RuntimeException(format("Lookup of RelationalPathBase instance failed for class '%s'", clazz.getName()), e);
        }
    }

    public static Set<? extends RelationalPathBase> requiredTablesFor(RelationalPathBase<?> relationalPath) {
        return requiredTablesFor(relationalPath, new LinkedHashSet<>());
    }

    public static Set<? extends RelationalPathBase> requiredTablesFor(RelationalPathBase<?> relationalPath, Set<RelationalPathBase> relationalPaths) {
        Set<ForeignKey<?>> foreignKeyFields = Arrays.stream(relationalPath.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(ForeignKey.class))
                .map(field -> getForeignKey(field, relationalPath))
                .collect(toSet());
        Set<? extends RelationalPathBase> requiredRelationalPaths = foreignKeyFields.stream()
                .map(ForeignKey::getEntity)
                .map(RelationalPathBase.class::cast)
                .map(QuerydslUtil::requiredTablesFor)
                .flatMap(stream -> stream.stream())
                .collect(toSet());
        for (RelationalPathBase<?> relationalPath1 : requiredRelationalPaths) {
            if (relationalPaths.contains(relationalPath1)) {
                continue;
            }
            relationalPaths.addAll(requiredTablesFor(relationalPath1, relationalPaths));
        }
        return relationalPaths;
    }

    private static ForeignKey<?> getForeignKey(Field field, Object object) {
        return getValue(field, object, ForeignKey.class);
    }

    private static <T> T getValue(Field field, Object object, Class<T> type) {
        try {
            field.setAccessible(true);
            return type.cast(field.get(object));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static RelationalPathBase<?> relationalPath(Class<? extends RelationalPathBase<?>> clazz) {
        try {
            Field field = clazz.getDeclaredField(clazz.getSimpleName().substring(1, 2).toLowerCase() + clazz.getSimpleName().substring(2));
            field.setAccessible(true);
            return (RelationalPathBase<?>) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static Path<?> column(RelationalPathBase<?> relationalPath, String columnName) {
        try {
            for (Field field : relationalPath.getClass().getDeclaredFields()) {
                if (!Path.class.isAssignableFrom(field.getType())
                        || RelationalPathBase.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                Path path = getValue(field, relationalPath, Path.class);
                ColumnMetadata columnMetadata = relationalPath.getMetadata(path);
                if (columnMetadata.getName().equals(columnName)) {
                    return path;
                }
            }
            throw new NoSuchElementException(format("No column with name %s for %s", columnName, relationalPath.getTableName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
