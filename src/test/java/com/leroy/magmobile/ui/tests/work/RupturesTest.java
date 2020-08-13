package com.leroy.magmobile.ui.tests.work;

import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.ui.AppBaseSteps;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;

@Guice(modules = {Module.class})
public class RupturesTest extends AppBaseSteps {
    RupturesClient client;
    CatalogSearchClient searchClient;

    @BeforeClass
    private void initClients(){
        client = apiClientProvider.getRupturesClient();
        searchClient = apiClientProvider.getCatalogSearchClient();
    }
}
