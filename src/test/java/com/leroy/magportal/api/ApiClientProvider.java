package com.leroy.magportal.api;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.clients.MagMobileClient;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import lombok.Setter;

public class ApiClientProvider {
    @Setter
    private SessionData sessionData;

    @Inject
    private Provider<CatalogSearchClient> catalogSearchClientProvider;

    private <J extends MagMobileClient> J getClient(Provider<J> provider) {
        MagMobileClient cl = provider.get();
        cl.setSessionData(sessionData);
        return (J) cl;
    }

    public CatalogSearchClient getCatalogSearchClient() {
        return getClient(catalogSearchClientProvider);
    }
}
