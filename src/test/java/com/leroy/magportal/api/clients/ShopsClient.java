package com.leroy.magportal.api.clients;


import static org.hamcrest.MatcherAssert.assertThat;

import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.api.requests.shop.GetShopsRequest;
import io.qameta.allure.Step;
import java.util.Optional;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopsClient extends BaseMashupClient {

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
}
