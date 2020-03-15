package com.leroy.core.matchers;

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

    public static IsSuccessful successful() {
        return new IsSuccessful();
    }

}
