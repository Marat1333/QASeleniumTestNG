package com.leroy.magmobile.api.tests.shops;

import com.leroy.magmobile.api.clients.ShopClient;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ShopTest extends BaseProjectApiTest {

    @Test(description = "C23195091 GET shops")
    public void testGetShops() {
        ShopClient shopClient = apiClientProvider.getShopClient();
        Response<ShopData> resp = shopClient.getShops();
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

}
