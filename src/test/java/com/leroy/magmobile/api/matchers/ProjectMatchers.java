package com.leroy.magmobile.api.matchers;

import java.time.temporal.Temporal;

public class ProjectMatchers {

    public static IsSuccessful successful() {
        return IsSuccessful.successful();
    }

    public static IsValid valid(Class<?> pojoClass) {
        return IsValid.valid(pojoClass);
    }

    public static IsApproximatelyEqual approximatelyEqual(Temporal equalArg) {
        return IsApproximatelyEqual.approximatelyEqual(equalArg);
    }

}
