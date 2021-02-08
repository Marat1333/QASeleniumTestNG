package com.leroy.magportal.api.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import ru.leroymerlin.qa.core.clients.base.Response;

public class BaseHelper {

    protected UserSessionData userSessionData() {
        return ContextProvider.getContext().getUserSessionData();
    }

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
    }
}
