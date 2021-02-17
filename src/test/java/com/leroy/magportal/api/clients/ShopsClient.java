package com.leroy.magportal.api.clients;


import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.api.requests.shop.GetShopsRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopsClient extends BaseMashupClient {

    @Step("Get shops list")
    public Response<ShopData> getShops() {
        GetShopsRequest req = new GetShopsRequest();
        return execute(req, ShopData.class);
    }
}
