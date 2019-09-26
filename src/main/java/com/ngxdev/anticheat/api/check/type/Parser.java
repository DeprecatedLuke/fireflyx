/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check.type;

import com.ngxdev.anticheat.utils.DynamicInit;
import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Parser {
    class Dynamic {
        public static Set<Class<?>> get() throws Throwable {
            return DynamicInit.getClasses("com.ngxdev", Parser.class);
        }
    }
}
