package com.leroy.magportal.api.clients;


import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.api.requests.shop.GetShopsRequest;
import io.qameta.allure.Step;
import java.util.List;
import java.util.Optional;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.gagarin.GagarinClient;
import ru.leroymerlin.qa.core.clients.gagarin.data.StoreInfo;

public class ShopsClient extends BaseMashupClient {

    @Inject
    private GagarinClient gagarinClient;

    @Step("Get shops list")
    public Response<ShopData> getShops() {
        GetShopsRequest req = new GetShopsRequest();
        return execute(req, ShopData.class);
    }

    @Step("Get shop by Id")
    public ShopData getShopById(String shopId) {
        Response<ShopData> response = getShops();
        assertThatResponseIsOk(response);
        Optional<ShopData> shopData = response.asJsonList(ShopData.class).stream()
                .filter(x -> x.getId().equals(shopId)).findFirst();
        assertThat(shopId + " was NOT found.", shopData.isPresent());
        return shopData.get();
    }

    @Step("Get regionId by shopId")
    public int getRegionIdByShopId(String shopId) {
        ShopData shopData = getShopById(shopId);
        return Integer.parseInt(shopData.getRegionId());
    }

    @Step("Get RefStoreId by shopId")
    public int getRefStoreIdByShopId(String shopId) {
        int regionId = getRegionIdByShopId(shopId);
        Response<List<StoreInfo>> response = gagarinClient.getStoreByRegion(regionId);
        assertThatResponseIsOk(response);
        StoreInfo storeInfo = response.asJsonList(StoreInfo.class).stream().filter(StoreInfo::isRef)
                .findFirst().get();
        try {
            return storeInfo.getStoreId();
        } catch (Exception e) {
            assertThat("RefStoreId was NOT found for Shop: " + shopId, false);
            return Integer.parseInt(shopId);
        }
    }
}
