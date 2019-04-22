package com.github.chriweis.querydsl_util.meta_model;

import com.querydsl.sql.RelationalPathBase;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Qutil {

    public static Set<? super RelationalPathBase<?>> tables(Package package$) {
        Reflections reflections = new Reflections(package$.getName());
        return Collections.unmodifiableSet(reflections.getSubTypesOf(RelationalPathBase.class)).stream()
                .map(Qutil::instanceOf)
                .collect(Collectors.toSet());
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
}
