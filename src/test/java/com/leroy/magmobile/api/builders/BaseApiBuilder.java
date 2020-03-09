package com.leroy.magmobile.api.builders;

import com.google.inject.Inject;
import com.leroy.magmobile.api.SessionData;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import lombok.Setter;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.leroy.magmobile.api.matchers.ProjectMatchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class BaseApiBuilder {

    @Inject
    protected MagMobileClient apiClient;

    @Setter
    protected SessionData sessionData;

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
    }

}
