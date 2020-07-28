package com.leroy.core.api;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.leroy.constants.EnvConstants;
import ru.leroymerlin.qa.core.base.PostConstructTypeListener;
import ru.leroymerlin.qa.core.commons.enums.Environment;
import ru.leroymerlin.qa.core.commons.ssl.ClientFactory;
import ru.leroymerlin.qa.core.config.client.EnvironmentConfig;

import javax.ws.rs.client.Client;

public class Module extends AbstractModule {
    private final Client client = ClientFactory.buildUnsecureWebClient();

    public Module() {
    }

    protected void configure() {
        this.bindListener(Matchers.any(), new PostConstructTypeListener());
        this.bind(Client.class).toInstance(this.client);
        this.bind(EnvironmentConfig.class).toInstance(EnvironmentConfig.get(
                Environment.getEnvironment(EnvConstants.BACKEND_CLIENT_ENV)));
    }
}
