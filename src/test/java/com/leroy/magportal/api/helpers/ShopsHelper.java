package com.leroy.magportal.api.helpers;

import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.core.configuration.Log;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.data.onlineOrders.GagarinStoreInfo;
import com.leroy.magportal.api.data.shops.ShopData;
import io.qameta.allure.Step;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.gagarin.GagarinClient;
import ru.leroymerlin.qa.core.clients.gagarin.data.StoreInfo;

public class ShopsHelper extends BaseHelper {

    @Inject
    private GagarinClient gagarinClient;
    @Inject
    private ShopsClient shopsClient;

    @Step("Get all shops")
    public List<ShopData> getShops() {
        Response<ShopData> response = shopsClient.getShops();
        assertThatResponseIsOk(response);
        return response.asJsonList(ShopData.class);
    }

    @Step("Get shop by Id")
    public ShopData getShopById(String shopId) {
        Optional<ShopData> shopData = this.getShops().stream()
                .filter(x -> x.getId().equals(shopId)).findFirst();
        assertThat(shopId + " was NOT found.", shopData.isPresent());
        return shopData.get();
    }

    @Step("Get random shop")
    public String getRandomShopId() {
        List<ShopData> shopsData = getShops();
        int id = new Random().nextInt(shopsData.size());
        return shopsData.get(id).getId();
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
        GagarinStoreInfo storeInfo = response.asJsonList(GagarinStoreInfo.class).stream()
                .filter(GagarinStoreInfo::getIsRef)
                .findFirst()
                .orElse(new GagarinStoreInfo());//TODO: Convert Response<List<StoreInfo>> to List<StoreInfo> ignores isRef. Mb lombok
        try {
            return storeInfo.getStoreId();
        } catch (Exception e) {
            Log.warn("RefStoreId was NOT found for Shop: " + shopId);
            return Integer.parseInt(shopId);
        }
    }
}
