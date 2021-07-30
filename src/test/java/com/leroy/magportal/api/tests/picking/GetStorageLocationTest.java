package com.leroy.magportal.api.tests.picking;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.picking.StorageLocationData;
import com.leroy.magportal.api.data.picking.StorageLocationData.ZoneLocation;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class GetStorageLocationTest extends BaseMagPortalApiTest {

    @Inject
    private PickingTaskClient pickingTaskClient;

    private StorageLocationData storageLocationData;
    private final String shopId = "13";

    @Test(description = "C23427263 Get Storage Location")
    @AllureId("16174")
    public void testGetStorageLocation() {
        Response<StorageLocationData> response = pickingTaskClient
                .getStorageLocation(getUserSessionData().getUserShopId());
        assertStorageResult(response);
        storageLocationData = response.asJson();
    }

    @Test(description = "C23427264 Get Storage Location for non-default ShopId", dependsOnMethods = {
            "testGetStorageLocation"})
    @AllureId("16175")
    public void testGetStorageLocationDiffShop() {
        Response<StorageLocationData> response = pickingTaskClient.getStorageLocation(shopId);
        assertStorageResult(response);
        storageLocationData = null;
    }

    //Verification
    @Step("GET Storage results verification")
    public void assertStorageResult(Response<StorageLocationData> response) {
        assertThat("Request to Storage has Failed.", response, successful());
        StorageLocationData responseData = response.asJson();
        assertThat("There are NO available zones", responseData.getZones().size(),
                greaterThanOrEqualTo(1));
        for (ZoneLocation zoneLocation : responseData.getZones()) {
            assertThat("There are NO available zoneCells",
                    zoneLocation.getZoneCells().size(), greaterThanOrEqualTo(1));
            assertThat("There are NO data in zoneCells",
                    (int) zoneLocation.getZoneCells().stream().filter(x -> !x.isEmpty()).count(),
                    greaterThanOrEqualTo(1));
        }

        if (storageLocationData != null) {
            assertThat("Shop ID is the same for different shop with ID: "
                    + shopId, responseData.getShopId(), not(storageLocationData.getShopId()));
            assertThat("Storage Location Data is the same for different shop with ID: "
                    + shopId, responseData.getZones(), not(storageLocationData.getZones()));
        }
    }
}