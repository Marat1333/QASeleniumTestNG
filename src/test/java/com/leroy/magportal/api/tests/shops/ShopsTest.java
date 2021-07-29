package com.leroy.magportal.api.tests.shops;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopsTest extends BaseMagPortalApiTest {

    @Inject
    private ShopsClient shopsClient;

    @Test(description = "C3182981 Get Shops List V1")
    public void testGetShops() {

        Response<ShopData> response = shopsClient.getShops();
        shopsClient.assertGetShopsResult(response);
    }
}
