package com.leroy.magportal.api.tests.shops;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopsTest extends BaseMagPortalApiTest {

    @Inject
    private ShopsClient shopsClient;

    @Test(description = "C3182981 Get Shops List")
    public void testGetShops() {

        Response<ShopData> response = shopsClient.getShops();
        assertThat(response, successful());
        List<ShopData> dataList = response.asJsonList(ShopData.class);
        assertThat("count shops", dataList, hasSize(greaterThan(100)));

        for (ShopData shopData : dataList) {
            assertThat("id", shopData.getId(), notNullValue());
            assertThat("name for id=" + shopData.getId(), shopData.getName(),
                    not(emptyOrNullString()));
            assertThat("address for id=" + shopData.getId(), shopData.getAddress(),
                    not(emptyOrNullString()));
            assertThat("regionId for id=" + shopData.getId(), shopData.getRegionId(),
                    notNullValue());
            assertThat("timezone for id=" + shopData.getId(), shopData.getTimezone(),
                    not(emptyOrNullString()));
            assertThat("cityName for id=" + shopData.getId(), shopData.getCityName(),
                    not(emptyOrNullString()));
//            assertThat("regionKladr for id=" + shopData.getId(), shopData.getRegionKladr(), not(emptyOrNullString())); //ID 352 without Kladrs
//            assertThat("cityKladr for id=" + shopData.getId(), shopData.getCityKladr(), not(emptyOrNullString())); //ID 352 without Kladrs
            assertThat("lat for id=" + shopData.getId(), shopData.getLat(), notNullValue());
            assertThat("long for id=" + shopData.getId(), shopData.getLongitude(), notNullValue());
            assertThat("BuCode for id=" + shopData.getId(), shopData.getBuCode(), notNullValue());
            assertThat("BuName for id=" + shopData.getId(), shopData.getBuName(), notNullValue());
//            assertThat("Phone for id=" + shopData.getId(), shopData.getPhone(), notNullValue());//A few ID without Phone(92, 93)
            assertThat("availableFeature for id=" + shopData.getId(),
                    shopData.getAvailableFeatures(),
                    notNullValue());
        }
    }
}
