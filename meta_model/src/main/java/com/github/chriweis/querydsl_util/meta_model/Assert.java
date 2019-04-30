package com.github.chriweis.querydsl_util.meta_model;

import static com.github.chriweis.querydsl_util.meta_model.BooleanUtil.not;

public class Assert {

    public static void state(boolean condition) {
        if (not(condition)) {
            throw new IllegalStateException();
        }
    }
}
