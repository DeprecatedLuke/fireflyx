/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.utils;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
	class Dynamic {
		public static Set<Class<?>> get() throws Throwable {
			return DynamicInit.getClasses("com.ngxdev", Init.class);
		}
	}
}
