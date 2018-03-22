package com.stayfit.queryorm.lib;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Администратор on 7/5/2016.
 */

@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
@Documented
public @interface MapTable {
    String value();
}
