package com.leroy.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE, CONSTRUCTOR})
public @interface Team {
    /**
     * The team in charge of these testNG tests. default is empty
     *
     * @return
     */
    String name() default "";
}
