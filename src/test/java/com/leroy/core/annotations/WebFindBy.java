package com.leroy.core.annotations;

import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface WebFindBy {
    String id() default "";

    String xpath() default "";

    String containsText() default "";

    String text() default "";

    String className() default "";

    String cssSelector() default "";

    Class<? extends BaseWidget> clazz() default Element.class;

    String metaName() default "";

    boolean cacheLookup() default false;

    boolean refreshEveryTime() default false;
}
