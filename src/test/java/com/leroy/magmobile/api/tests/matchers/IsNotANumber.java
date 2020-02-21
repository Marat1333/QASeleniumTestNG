package com.leroy.magmobile.api.tests.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsNotANumber extends TypeSafeMatcher<Double> {

    @Override
    public boolean matchesSafely(Double number) {
        return number.isNaN();
    }

    public void describeTo(Description description) {
        description.appendText("not a number");
    }

    public static BaseMatcher<Double> notANumber() {
        return new IsNotANumber();
    }

}
