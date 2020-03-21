package com.leroy.core.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Duration;
import java.time.temporal.Temporal;

public class IsApproximatelyEqual extends TypeSafeMatcher<Temporal> {

    private final Temporal expectedDate;
    private int allowableDifferenceInMinutes = 5;

    private static boolean datesAreEqual(Temporal date1, Temporal date2, Duration allowableDifference) {
        return Duration.between(date1, date2).getSeconds() < allowableDifference.getSeconds();
    }

    public IsApproximatelyEqual(Temporal equalArg) {
        this.expectedDate = equalArg;
    }

    @Override
    public boolean matchesSafely(Temporal actualDate) {
        return datesAreEqual(actualDate, expectedDate, Duration.ofMinutes(allowableDifferenceInMinutes));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Date should be approximately equal ");
        description.appendValue(expectedDate);
        description.appendText(" with difference of ");
        description.appendValue(allowableDifferenceInMinutes);
        description.appendText(" minutes");
    }

    public static IsApproximatelyEqual approximatelyEqual(Temporal equalArg) {
        return new IsApproximatelyEqual(equalArg);
    }

}
