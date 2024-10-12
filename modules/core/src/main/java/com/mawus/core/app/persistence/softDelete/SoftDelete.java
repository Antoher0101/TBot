package com.mawus.core.app.persistence.softDelete;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SoftDelete {

    String property() default "";

    Class type() default Boolean.class;
}
