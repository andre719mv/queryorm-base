package com.stayfit.queryorm.lib;
import java.lang.annotation.*;

/**
 * Created by Администратор on 7/5/2016.
 */

@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
@Documented
public @interface MapColumn {
    String value();
}
