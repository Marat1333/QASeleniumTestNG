package com.leroy.magmobile.api.helpers;

import com.google.common.collect.Table;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magmobile.api.clients.LsAddressClient;
import com.leroy.magmobile.api.data.address.*;
import com.leroy.magmobile.api.data.address.cellproducts.CellProductDataList;
import com.leroy.magmobile.api.data.address.cellproducts.ReqCellProductData;
import com.leroy.magmobile.api.data.address.cellproducts.ReqCellProductDataList;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class LsAddressHelper extends BaseHelper {

    @Inject
    private LsAddressClient lsAddressClient;

    @Inject
    private SearchProductHelper searchProductHelper;

    @Step("Create default alley")
    public AlleyData createDefaultAlley(String name) {
        AlleyData alleyData = new AlleyData();
        alleyData.setType(0);
        alleyData.setCode(name);
        Response<AlleyData> resp = lsAddressClient.createAlley(alleyData);
        assertThat("Create alley request has failed.", resp, successful());
        return resp.asJson();
    }

    @Step("Create default alley with random name")
    public AlleyData createRandomAlley() {
        return createDefaultAlley(RandomStringUtils.randomNumeric(5));
    }

    @Step("Search alley by id")
    public AlleyData searchAlleyById(int alleyId, boolean allowEmpty) {
        Response<AlleyDataItems> resp = lsAddressClient.searchForAlleys();
        assertThat("Catalog search request has failed.", resp, successful());
        List<AlleyData> items = resp.asJson().getItems();
        for (AlleyData item : items) {
            if (item.getId() == alleyId) {
                return item;
            }
        }
        if (allowEmpty) {
            return new AlleyData();
        } else {
            assertThat("Required alley not found by id", false);
            return null;
        }
    }

    @Step("Search alley by id")
    public AlleyData searchAlleyById(int alleyId) {
        return searchAlleyById(alleyId, false);
    }

    @Step("Create default stand")
    public StandDataList createDefaultStands(AlleyData alleyData) {
        StandDataList postStandDataList = new StandDataList();
        StandData item1 = new StandData(3, 0, 0);
        StandData item2 = new StandData(6, 1, 0);
        postStandDataList.setItems(Arrays.asList(item1, item2));
        postStandDataList.setAlleyCode(alleyData.getCode());
        postStandDataList.setAlleyType(alleyData.getType());
        postStandDataList.setEmail(RandomStringUtils.randomAlphanumeric(5) + "@mail.com");
        Response<StandDataList> resp = lsAddressClient.createStand(alleyData.getId(), postStandDataList);
        assertThat("Failed to create stand for AlleyId: " + alleyData.getId(), resp, successful());
        return resp.asJson();
    }

    @Step("Get alley from list")
    public AlleyData getAlleyFromList(int position) {
        Response<AlleyDataItems> searchResp = lsAddressClient.searchForAlleys();
        assertThat(searchResp, successful());
        List<AlleyData> items = searchResp.asJson().getItems();
        assertThat("items count = 0", items, hasSize(greaterThan(0)));
        return items.get(position);
    }

    @Step("Get stand from list")
    public StandData getStandFromList(int position, int alleyId) {
        Response<StandDataList> searchResp = lsAddressClient.searchForStand(alleyId);
        assertThat(searchResp, successful());
        List<StandData> items = searchResp.asJson().getItems();
        assertThat("items count = 0", items, hasSize(greaterThan(0)));
        return items.get(position);
    }


    @Step("Create default cells")
    public CellDataList createDefaultCells(int standId) {
        CellDataList cellDataList = new CellDataList();
        CellData itemData1 = new CellData(1, 0, "A");
        CellData itemData2 = new CellData(1, 0, "B");
        cellDataList.setItems(Arrays.asList(itemData1, itemData2));
        Response<CellDataList> resp = lsAddressClient.createCell(standId, cellDataList);
        assertThat("Failed to create cells for StandId: " + standId, resp, successful());
        return resp.asJson();
    }

    @Step("Add products to cell")
    public CellProductDataList addDefaultProductToCell(CellData cellData, int quantity){
        String lmCode = searchProductHelper.getProductLmCode();

        ReqCellProductData reqCellProductData = new ReqCellProductData();
        reqCellProductData.setLmCode(lmCode);
        reqCellProductData.setQuantity(quantity);

        ReqCellProductDataList postData = new ReqCellProductDataList();
        postData.setItems(Collections.singletonList(reqCellProductData));

        Response<CellProductDataList> response = lsAddressClient.createCellProducts(cellData.getId(), postData);
        assertThat("Failed to add a product to cell: " + cellData.getCode(), response, successful());
        return response.asJson();
    }
}
