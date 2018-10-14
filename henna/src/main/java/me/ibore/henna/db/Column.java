package me.ibore.henna.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name();

    String type() default "UNKNOW";

    boolean primaryKey() default false;

    boolean autoIncrement() default false;

    boolean unique() default false;

    boolean index() default false;

    boolean notNull() default false;

    String defaultVal() default "";

}
