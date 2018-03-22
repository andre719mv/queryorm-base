package com.stayfit.queryorm.lib;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Администратор on 7/5/2016.
 * Column is deleted in new version but can axists in new old one
 */

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnDeleted {
}
