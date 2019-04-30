package com.github.chriweis.querydsl.util.util;

import com.github.chriweis.querydsl.util.util.BooleanUtil;

public class Assert {

    public static void state(boolean condition) {
        if (BooleanUtil.not(condition)) {
            throw new IllegalStateException();
        }
    }
}
