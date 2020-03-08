package com.leroy.magmobile.api.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Duration;
import java.time.temporal.Temporal;

public class IsApproximatelyEqual extends TypeSafeMatcher<Temporal> {

    private final Temporal expectedDate;

    private static boolean datesAreEqual(Temporal date1, Temporal date2, Duration allowableDifference) {
        return Duration.between(date1, date2).getSeconds() < allowableDifference.getSeconds();
    }

    public IsApproximatelyEqual(Temporal equalArg) {
        this.expectedDate = equalArg;
    }

    @Override
    public boolean matchesSafely(Temporal actualDate) {
        return datesAreEqual(actualDate, expectedDate, Duration.ofMinutes(5));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Date should be approximately equal ");
        description.appendValue(expectedDate);
        description.appendText(" with difference of ");
        description.appendValue(5);
        description.appendText(" minutes");
    }

    public static IsApproximatelyEqual approximatelyEqual(Temporal equalArg) {
        return new IsApproximatelyEqual(equalArg);
    }

}
