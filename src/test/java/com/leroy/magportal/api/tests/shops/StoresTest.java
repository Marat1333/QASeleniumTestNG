package com.leroy.magportal.api.tests.shops;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.data.shops.StoreData;
import com.leroy.magportal.api.helpers.ShopsHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class StoresTest extends BaseMagPortalApiTest {

    @Inject
    private ShopsClient shopsClient;
    @Inject
    private ShopsHelper shopsHelper;

    @Test(description = "C23749488 Get Stores")
    @AllureId("2090")
    public void testGetStores() {
        Response<StoreData> response = shopsClient.getStories();
        shopsClient.assertGetShopsResult(response);
    }

    @Test(description = "C23749489 Get Stores For Region")
    @AllureId("2091")
    public void testGetStoresForRegion() {
        int regionId = shopsHelper.getRandomRegionId();
        Response<StoreData> response = shopsClient.getStories(regionId);
        shopsClient.assertGetRegionStoresResult(response, regionId);
    }

    @Test(description = "C23749490 Get Store")
    @AllureId("2092")
    public void testGetStore() {
        int shopId = Integer.parseInt(getUserSessionData().getUserShopId());
        Response<StoreData> response = shopsClient.getStore(shopId);
        shopsClient.assertGetStoreResult(response, shopId);
    }

    @Test(description = "C23749491 Get Random Store")
    @AllureId("2093")
    public void testGetRandomStore() {
        int shopId = shopsHelper.getRandomShopId();
        Response<StoreData> response = shopsClient.getStore(shopId);
        shopsClient.assertGetStoreResult(response, shopId);
    }
}
