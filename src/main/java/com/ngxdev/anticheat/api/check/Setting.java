/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {

}
