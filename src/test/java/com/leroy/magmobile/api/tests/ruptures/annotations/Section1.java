package com.leroy.magmobile.api.tests.ruptures.annotations;

import io.qameta.allure.LabelAnnotation;

@java.lang.annotation.Documented
@java.lang.annotation.Inherited
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@LabelAnnotation(name = "section1")
public @interface Section1 {
    String value();
}