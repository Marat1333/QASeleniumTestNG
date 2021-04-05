package com.leroy.magportal.api.clients;


import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.constants.ShopProductsEnum;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.api.data.shops.StoreData;
import com.leroy.magportal.api.requests.shop.GetShopsRequest;
import com.leroy.magportal.api.requests.shop.GetStoreRequest;
import com.leroy.magportal.api.requests.shop.GetStoriesRequest;
import io.qameta.allure.Step;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopsClient extends BaseMashupClient {

    private String oldUrl;

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.SHOPS_API_HOST;
        oldUrl = EnvConstants.MAIN_API_HOST;
        jaegerHost = EnvConstants.PAO_JAEGER_HOST;
        jaegerService = EnvConstants.PAO_JAEGER_SERVICE;
    }

    @Step("Get shops list v1")
    public Response<ShopData> getShops() {
        GetShopsRequest req = new GetShopsRequest();
        return execute(req, ShopData.class, oldUrl);
    }

    @Step("Get shops list v2")
    public Response<StoreData> getStories() {
        return execute(new GetStoriesRequest(), StoreData.class);
    }

    @Step("Get shops list v2")
    public Response<StoreData> getStories(Integer regionId) {
        return execute(new GetStoriesRequest().setRegionId(regionId), StoreData.class);
    }

    @Step("Get shops list v2")
    public Response<StoreData> getStories(Integer regionId, ShopProductsEnum productId,
            Integer byDistanceTo, String ifModifiedSince) {
        GetStoriesRequest req = new GetStoriesRequest()
                .setRegionId(regionId)
                .setProductId(productId.getValue())
                .setByDistanceTo(byDistanceTo)
                .setIfModifiedSince(ifModifiedSince);
        return execute(req, StoreData.class);
    }

    @Step("Get shops list v2")
    public Response<StoreData> getStories(Integer regionId, ShopProductsEnum productId,
            Integer byDistanceTo) {
        return getStories(regionId, productId, byDistanceTo, null);
    }

    @Step("Get shops list v2")
    public Response<StoreData> getStories(Integer regionId, ShopProductsEnum productId) {
        return getStories(regionId, productId, null, null);
    }

    @Step("Get store with id == {id}")
    public Response<StoreData> getStore(Integer storeId) {
        return execute(new GetStoreRequest().setId(storeId), StoreData.class);
    }

    ////Verification
    @Step("Verifies Get Region's Stores")
    public void assertGetRegionStoresResult(Response<?> response, Integer regionId) {

        assertThat("Get Stores request was failed", response, successful());
        List<ShopData> dataList = response.asJsonList(ShopData.class);
        List<ShopData> wrongStores = dataList.stream()
                .filter(x -> !x.getRegionId().equals(regionId)).collect(Collectors.toList());
        assertThat("There are invalid Stores are present", wrongStores, hasSize(0));
        for (ShopData shopData : dataList) {
            softAssertShopData(shopData);
        }
        softAssert().verifyAll();
    }

    public void assertGetShopsResult(Response<?> response) {
        assertThat("Get Stores request was failed", response, successful());
        List<ShopData> dataList = response.asJsonList(ShopData.class);
        assertThat("Stores are not enough", dataList, hasSize(greaterThanOrEqualTo(107)));

        for (ShopData shopData : dataList) {
            softAssertShopData(shopData);
        }
        softAssert().verifyAll();
    }

    public void assertGetStoreResult(Response<?> response, Integer storeId) {
        assertThat("Get Store request was failed", response, successful());
        ShopData store = response.asJson(ShopData.class);
        assertThat("Stores are not enough", store.getId().equals(storeId));
        softAssertShopData(store);
        softAssert().verifyAll();
    }

    private void softAssertShopData(ShopData shopData) {
        int id = shopData.getId();
        String desc = String.format("StoreId: %s, No value for field: ", id);
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getName()), desc + "Name");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getAddress()), desc + "Address");
        softAssert().isTrue(shopData.getRegionId() != null, desc + "RegionId");
        softAssert()
                .isTrue(Strings.isNotNullAndNotEmpty(shopData.getTimezone()), desc + "Timezone");
        softAssert()
                .isTrue(Strings.isNotNullAndNotEmpty(shopData.getCityName()), desc + "CityName");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getBuName()), desc + "BuName");
        softAssert().isTrue(shopData.getLat() != null, desc + "Latitude");
        softAssert().isTrue(shopData.getLongitude() != null, desc + "Longitude");
        softAssert().isTrue(shopData.getBuCode() != null, desc + "BuCode");
        softAssert().isTrue(shopData.getAvailableFeatures() != null, desc + "AvailableFeatures");
        //TODO GET CONFIRMATION
        if (id < 350) {
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getRegionKladr()),
                    desc + "RegionKladr");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getCityKladr()),
                    desc + "CityKladr");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getPhone()), desc + "Phone");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(shopData.getStoreType()),
                    desc + "StoreType");
            softAssert().isTrue(shopData.getZipcode() != null, desc + "Zipcode");
        }
    }
}
