package com.leroy.core.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsNumber extends TypeSafeMatcher<String> {

    @Override
    public boolean matchesSafely(String val) {
        return val.matches("[-+]?\\d+");
    }

    public void describeTo(Description description) {
        description.appendText("is a number");
    }

    public static IsNumber isNumber() {
        return new IsNumber();
    }

}
