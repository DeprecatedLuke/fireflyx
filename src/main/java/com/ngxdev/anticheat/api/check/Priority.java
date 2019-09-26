/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ngxdev.anticheat.api.check.Priority.Value.MAX;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Priority {
    byte value() default MAX;

    public static class Value {
        public static final byte MIN = Byte.MIN_VALUE;
        public static final byte LOWER = -1;
        public static final byte NORMAL = 0;
        public static final byte HIGHER = 1;
        public static final byte MAX = Byte.MAX_VALUE;
    }
}
