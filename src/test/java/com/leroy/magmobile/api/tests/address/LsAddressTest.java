package com.leroy.magmobile.api.tests.address;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.LsAddressClient;
import com.leroy.magmobile.api.data.address.*;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static com.leroy.core.matchers.Matchers.*;


public class LsAddressTest extends BaseProjectApiTest {

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    private LsAddressClient lsAddressClient;

    private AlleyData alleyData;

    private StandData standData;

    @BeforeClass
    public void setUp() {
        sessionData.setUserShopId("25");
        lsAddressClient = apiClientProvider.getLsAddressClient();
    }

    @Test(description = "Create Alley")
    public void testCreateAlley() {
        AlleyData postAlleyData = new AlleyData();
        postAlleyData.setType(0);
        postAlleyData.setCode("kas3");
        Response<AlleyData> resp = lsAddressClient.createAlley(postAlleyData);
        this.alleyData = lsAddressClient.assertThatAlleyIsCreatedAndGetData(resp, postAlleyData);
    }

    @Test(description = "Search for Alleys")
    public void testSearchForAlleys() {
        Response<AlleyDataItems> resp = lsAddressClient.searchForAlleys();
        assertThat(resp, successful());
        List<AlleyData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(greaterThan(0)));
        for (AlleyData alleyData : items) {
            assertThat("productsCount", alleyData.getProductsCount(), notNullValue());
            assertThat("id", alleyData.getId(), greaterThan(0));
            assertThat("count", alleyData.getId(), notNullValue());
            assertThat("type", alleyData.getType(), notNullValue());
            assertThat("storeId", alleyData.getStoreId(), is(Integer.parseInt(sessionData.getUserShopId())));
            assertThat("departmentId", alleyData.getDepartmentId(), is(Integer.parseInt(sessionData.getUserDepartmentId())));
            assertThat("code", alleyData.getCode(), not(emptyOrNullString()));
        }
    }

    @Test(description = "Create Stand")
    public void testCreateStand() {
        step("Search for alley id");
        Response<AlleyDataItems> searchResp = lsAddressClient.searchForAlleys();
        assertThat(searchResp, successful());
        List<AlleyData> items = searchResp.asJson().getItems();
        assertThat("items count", items, hasSize(greaterThan(0)));
        AlleyData alleyData = items.get(0);

        step("Create stand");
        StandData postStandData = new StandData();
        StandItemData item1 = new StandItemData(1, 2, 3);
        StandItemData item2 = new StandItemData(4, 3, 2);
        postStandData.setItems(Arrays.asList(item1, item2));
        postStandData.setAlleyCode(alleyData.getCode());
        postStandData.setAlleyType(alleyData.getType());
        postStandData.setEmail(RandomStringUtils.randomAlphanumeric(5) + "@mail.com");
        Response<StandData> resp = lsAddressClient.createStand(alleyData.getId(), postStandData);
        this.standData = lsAddressClient.assertThatStandIsCreatedAndGetData(resp, postStandData);
    }

    @Test(description = "Get Stand")
    public void testGetStand() {
        step("Search for alley id");
        Response<AlleyDataItems> searchResp = lsAddressClient.searchForAlleys();
        assertThat(searchResp, successful());
        List<AlleyData> items = searchResp.asJson().getItems();
        assertThat("items count", items, hasSize(greaterThan(0)));
        AlleyData alleyData = items.get(0);

        step("Get Stand");
        Response<StandData> resp = lsAddressClient.searchForStand(alleyData.getId());
        lsAddressClient.assertThatDataMatches(resp, standData);
    }

    @Test(description = "Create cell")
    public void testCreateCell() {
        int standId = standData.getItems().get(0).getId();
        CellData cellData = new CellData();
        CellItemData itemData1 = new CellItemData(1, 2, "A");
        CellItemData itemData2 = new CellItemData(3, 4, "B");
        cellData.setItems(Arrays.asList(itemData1, itemData2));
        Response<CellData> resp = lsAddressClient.createCell(standId, cellData);
        lsAddressClient.assertThatCellIsCreatedAndGetData(resp, standId, cellData);
    }

}
