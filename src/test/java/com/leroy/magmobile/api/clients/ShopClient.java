package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.requests.shop.GetShopsRequest;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<ShopData> getShops() {
        GetShopsRequest req = new GetShopsRequest();
        return execute(req, ShopData.class);
    }
}
