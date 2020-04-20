package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.kladr.KladrItemDataList;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.requests.kladr.GetKladrRequest;
import com.leroy.magmobile.api.requests.shop.GetShopsRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopKladrClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    @Step("Get shops list")
    public Response<ShopData> getShops() {
        GetShopsRequest req = new GetShopsRequest();
        return execute(req, ShopData.class);
    }

    @Step("Get Kladr by city with regionId={regionId}, limit={limit}")
    public Response<KladrItemDataList> getKladrByCity(String regionId, int limit) {
        GetKladrRequest req = new GetKladrRequest();
        req.setType("city");
        req.setLimit(limit);
        req.setRegionId(regionId);
        return execute(req, KladrItemDataList.class);
    }
}
