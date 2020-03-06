package com.leroy.magmobile.api.tests.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import ru.leroymerlin.qa.core.clients.base.Response;

public class IsSuccessful extends TypeSafeMatcher<Response<?>> {

    @Override
    public boolean matchesSafely(Response<?> resp) {
        return resp.isSuccessful();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Response is successful");
    }

    public static BaseMatcher<Response<?>> successful() {
        return new IsSuccessful();
    }

}
