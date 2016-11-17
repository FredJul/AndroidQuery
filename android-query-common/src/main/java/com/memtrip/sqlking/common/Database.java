package com.memtrip.sqlking.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: Creates a new database to use in the application.
 * <p>
 * If we specify one DB, then all models do not need to specify a DB. As soon as we specify two, then each
 * model needs to define what DB it points to.
 * </p>
 * <p>
 * Models will specify which DB it belongs to,
 * but they currently can only belong to one DB.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Database {

    /**
     * @return The current version of the DB. Increment it to trigger a DB update.
     */
    int version();

    /**
     * @return The name of the DB. Optional as it will default to the class name.
     */
    String name() default "";

    /**
     * @return true if you want it to be in-memory, false if not.
     */
    boolean inMemory() default false;
}
