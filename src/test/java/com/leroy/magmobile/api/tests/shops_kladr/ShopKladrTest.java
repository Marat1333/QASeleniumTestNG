package com.leroy.magmobile.api.tests.shops_kladr;

import com.leroy.magmobile.api.clients.ShopKladrClient;
import com.leroy.magmobile.api.data.kladr.KladrItemData;
import com.leroy.magmobile.api.data.kladr.KladrItemDataList;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ShopKladrTest extends BaseProjectApiTest {

    private ShopKladrClient shopKladrClient() {
        return apiClientProvider.getShopKladrClient();
    }

    @Test(description = "C23195091 GET shops")
    public void testGetShops() {
        ShopKladrClient shopKladrClient = shopKladrClient();
        Response<ShopData> resp = shopKladrClient.getShops();
        assertThat(resp, successful());
        List<ShopData> dataList = resp.asJsonList(ShopData.class);
        assertThat("count shops", dataList, hasSize(greaterThan(100)));
        for (ShopData shopData : dataList) {
            assertThat("id", shopData.getId(), notNullValue());
            assertThat("name for id=" + shopData.getId(), shopData.getName(), not(emptyOrNullString()));
            assertThat("address for id=" + shopData.getId(), shopData.getAddress(), not(emptyOrNullString()));
            assertThat("regionId for id=" + shopData.getId(), shopData.getRegionId(), notNullValue());
            assertThat("timezone for id=" + shopData.getId(), shopData.getTimezone(), not(emptyOrNullString()));
            assertThat("cityName for id=" + shopData.getId(), shopData.getCityName(), not(emptyOrNullString()));
            assertThat("regionKladr for id=" + shopData.getId(), shopData.getRegionKladr(), not(emptyOrNullString()));
            assertThat("cityKladr for id=" + shopData.getId(), shopData.getCityKladr(), not(emptyOrNullString()));
            assertThat("lat for id=" + shopData.getId(), shopData.getLat(), notNullValue());
            assertThat("long for id=" + shopData.getId(), shopData.getLongitude(), notNullValue());
            assertThat("availableFeature for id=" + shopData.getId(), shopData.getAvailableFeatures(), notNullValue());
        }
    }

    @Test(description = "C3165821 Kladr gets a city that exists")
    public void testKladrGetsExistedCity() {
        int limit = 12;
        ShopKladrClient shopKladrClient = shopKladrClient();
        Response<KladrItemDataList> resp = shopKladrClient.getKladrByCity("2900000000000", 12);
        assertThat(resp, successful());
        KladrItemDataList dataList = resp.asJson();
        assertThat("total count", dataList.getTotalCount(), is(limit));
        assertThat("items count", dataList.getItems(), hasSize(limit));
        for (KladrItemData kladrItemData : dataList.getItems()) {
            assertThat("id", kladrItemData.getId(), not(emptyOrNullString()));
            assertThat("label", kladrItemData.getLabel(), not(emptyOrNullString()));
            assertThat("value", kladrItemData.getValue(), not(emptyOrNullString()));
        }
    }

}
