package com.leroy.magmobile.api.tests.address;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.clients.LsAddressClient;
import com.leroy.magmobile.api.data.address.*;
import com.leroy.magmobile.api.data.address.cellproducts.*;
import com.leroy.magmobile.api.helpers.LsAddressHelper;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.lsaddress.LsAddressBackClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;


public class LsAddressTest extends BaseProjectApiTest {


    @Inject
    private LsAddressBackClient lsAddressBackClient;

    @Inject
    private LsAddressClient lsAddressClient;

    @Inject
    private SearchProductHelper searchProductHelper;

    @Inject
    private AlleyData alleyData;

    @Inject
    private LsAddressHelper lsAddressHelper;

    @Inject
    private StandData standData;

    @Inject
    private StandDataList standDataList;

    @Inject
    private CellData cellData;

    @Inject
    private CellDataList cellDataList;

    private CellProductDataList cellProductDataList;

    private int createdAlleyId;

    private void prepareDefaultData(boolean requiredStand, boolean requiredCell) {
        step("Create a new alley");
        alleyData = lsAddressHelper.createRandomAlley();
        createdAlleyId = alleyData.getId();

        if (requiredStand) {
            step("Create new stands");
            standDataList = lsAddressHelper.createDefaultStands(alleyData);

            step("Get first stand from list");
            standData = lsAddressHelper.getStandFromList(0, alleyData.getId());

            if (requiredCell) {
                step("Create new cells");
                cellDataList = lsAddressHelper.createDefaultCells(standData.getId());
            }
        }
    }

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("22");
        return sessionData;
    }

    @AfterMethod
    private void deleteAlley() {
        if (createdAlleyId > 0) {
            Response<JsonNode> deleteResp = lsAddressBackClient.deleteAlley(createdAlleyId);
            isResponseOk(deleteResp);
            createdAlleyId = 0;
        }

    }

    @Test(description = "C3316285 lsAddress POST alleys")
    public void testCreateAlley() {
        step("Create new alley");
        alleyData.setType(0);
        alleyData.setCode(RandomStringUtils.randomNumeric(5));
        Response<AlleyData> resp = lsAddressClient.createAlley(alleyData);
        lsAddressClient.assertThatAlleyIsCreated(resp, alleyData);
        alleyData = resp.asJson();
        createdAlleyId = alleyData.getId();
    }

    @Test(description = "C3316284 lsAddress GET alleys")
    public void testGetAlleys() {
        step("Get list of alleys");
        Response<AlleyDataItems> resp = lsAddressClient.searchForAlleys();
        lsAddressClient.assertThatGetAlleyList(resp);
    }

    @Test(description = "C23415877 lsAddress PUT alleys - rename alleys")
    public void testRenameAlleys() {
        prepareDefaultData(false, false);
        step("Rename alley");
        alleyData.setCode("24700");
        Response<AlleyData> renameResp = lsAddressClient.renameAlley(alleyData);
        lsAddressClient.assertThatAlleyIsRenamed(renameResp, alleyData);
    }

    @Test(description = "C23415876 lsAddress DELETE alleys - delete alley")
    public void testDeleteAlleys() {
        prepareDefaultData(false, false);

        step("Delete alley");
        Response<AlleyData> deleteResp = lsAddressClient.deleteAlley(alleyData);
        lsAddressClient.assertThatAlleyIsDeleted(deleteResp, createdAlleyId);
        createdAlleyId = 0;
    }

    @Test(description = "C3316291 lsAddress POST stands")
    public void testCreateStand() {
        step("Search for alley id");
        Response<AlleyDataItems> searchResp = lsAddressClient.searchForAlleys();
        assertThat(searchResp, successful());
        List<AlleyData> items = searchResp.asJson().getItems();
        assertThat("items count", items, hasSize(greaterThan(0)));
        AlleyData alleyData = items.get(0);

        step("Create stand");
        StandDataList postStandDataList = new StandDataList();
        StandData item1 = new StandData(1, 2, 3);
        StandData item2 = new StandData(4, 3, 2);
        postStandDataList.setItems(Arrays.asList(item1, item2));
        postStandDataList.setAlleyCode(alleyData.getCode());
        postStandDataList.setAlleyType(alleyData.getType());
        postStandDataList.setEmail(RandomStringUtils.randomAlphanumeric(5) + "@mail.com");
        Response<StandDataList> resp = lsAddressClient.createStand(alleyData.getId(), postStandDataList);

        standDataList = lsAddressClient.assertThatStandIsCreatedAndGetData(resp, postStandDataList);
    }

    @Test(description = "C3316290 lsAddress GET stand")
    public void testGetStand() {
        step("Get first alley from list");
        AlleyData alleyData = lsAddressHelper.getAlleyFromList(0);

        step("Create new stands");
        standDataList = lsAddressHelper.createDefaultStands(alleyData);

        step("Get Stand");
        Response<StandDataList> resp = lsAddressClient.searchForStand(alleyData.getId());
        lsAddressClient.assertThatDataMatches(resp, standDataList);
    }

    @Test(description = "C3316322 lsAddress POST cells")
    public void testCreateCell() {
        prepareDefaultData(true, false);

        step("Create new cells");
        int standId = standDataList.getItems().get(0).getId();
        CellData itemData1 = new CellData(1, 2, "A");
        CellData itemData2 = new CellData(3, 4, "B");
        cellDataList.setItems(Arrays.asList(itemData1, itemData2));
        Response<CellDataList> resp = lsAddressClient.createCell(standId, cellDataList);
        lsAddressClient.assertThatCellIsCreated(resp, standId, cellDataList);
    }

    @Test(description = "C3316323 lsAddress GET cells")
    public void testGetCells() {
        prepareDefaultData(true, true);

        step("Get cells from list");
        Response<CellDataList> resp = lsAddressClient.getCells(standData.getId());
        lsAddressClient.assertThatDataMatches(resp, cellDataList);
    }

    @Test(description = "C23194975 lsAddress PUT cells - Add item")
    public void testUpdateCells() {
        prepareDefaultData(true, true);

        step("Prepare cells data to update");
        cellData.setType(5);
        cellData.setPosition(11);
        cellData.setShelf("C");
        List<CellData> putCellItems = new ArrayList<>(cellDataList.getItems());
        putCellItems.add(cellData);
        CellDataList updateCellDataList = new CellDataList();
        updateCellDataList.setItems(putCellItems);

        step("Update cells");
        Response<CellDataList> resp = lsAddressClient.updateCells(standData.getId(), updateCellDataList);
        cellDataList.addItem(cellData);
        lsAddressClient.assertThatDataMatches(resp, cellDataList, BaseMashupClient.ResponseType.PUT);
        cellDataList.updateLastItem(resp.asJson().getItems().get(2));

        step("Send Get request and check data");
        Response<CellDataList> getResp = lsAddressClient.getCells(standData.getId());
        lsAddressClient.assertThatDataMatches(getResp, cellDataList);
    }

    @Test(description = "C23194977 lsAddress DELETE cells")
    public void testDeleteCell() {
        prepareDefaultData(true, true);

        step("Delete cell");
        int itemIndexToRemove = 0;
        String cellIdToRemove = cellDataList.getItems().get(itemIndexToRemove).getId();
        Response<JsonNode> resp = lsAddressClient.deleteCell(cellIdToRemove);
        lsAddressClient.assertThatCellIsDeleted(resp, cellIdToRemove);
        cellDataList.getItems().remove(itemIndexToRemove);

        step("Send Get request to delete cell");
        Response<CellDataList> getResp = lsAddressClient.getCells(standData.getId());
        lsAddressClient.assertThatDataMatches(getResp, cellDataList);
    }

    // Cell products
    @Test(description = "C23194989 lsAddress POST Cell products")
    public void testCreateCellProducts() {
        prepareDefaultData(true, true);

        step("Prepare test data");
        cellData = cellDataList.getItems().get(0);
        String lmCode = searchProductHelper.getProductLmCode();
        int quantity = 2;

        step("Prepare request body to add product");
        ReqCellProductData reqCellProductData = new ReqCellProductData();
        reqCellProductData.setLmCode(lmCode);
        reqCellProductData.setQuantity(quantity);

        ReqCellProductDataList postData = new ReqCellProductDataList();
        postData.setItems(Collections.singletonList(reqCellProductData));

        step("Send POST request to add product");
        Response<CellProductDataList> response = lsAddressClient.createCellProducts(cellData.getId(), postData);
        lsAddressClient.assertThatIsCellProductsIsCreated(response, postData, cellData);
    }

    @Test(description = "C23194985 lsAddress GET Cell products")
    public void testGetCellProducts() {
        // Test data
        prepareDefaultData(true, true);
        cellData = cellDataList.getItems().get(0);
        cellProductDataList = lsAddressHelper.addDefaultProductToCell(cellData, 5);

        step("Get products from list");
        Response<CellProductDataList> response = lsAddressClient.getCellProducts(cellData.getId());
        lsAddressClient.assertThatDataMatches(response, cellProductDataList);
    }

    @Test(description = "C23194986 lsAddress PUT Cell products - Change quantity")
    public void testUpdateCellProducts() {
        // Test data
        prepareDefaultData(true, true);
        cellData = cellDataList.getItems().get(0);
        cellProductDataList = lsAddressHelper.addDefaultProductToCell(cellData, 5);
        CellProductData cellProductData = cellProductDataList.getItems().get(0);

        step("Prepare request body to update quantity");
        int quantity = 7;
        ReqCellProductData updateCellProductData = new ReqCellProductData();
        updateCellProductData.setQuantity(quantity);
        updateCellProductData.setUsername(getUserSessionData().getUserLdap());

        step("Send PUT request to update a product quantity");
        Response<CellProductData> response = lsAddressClient.updateCellProducts(
                cellData.getId(), cellProductData.getLmCode(), updateCellProductData);
        cellProductData.setQuantity(quantity);
        lsAddressClient.assertThatDataMatches(response, cellProductData, cellData.getCode());
    }

    @Test(description = "C23194987 Move Cell Products - 1 Product")
    public void testMoveCellProducts() {
        // Test data
        prepareDefaultData(true, true);
        cellData = cellDataList.getItems().get(0);
        CellData newCellData = cellDataList.getItems().get(1);
        String newCellId = newCellData.getId();
        int quantity = 5;
        cellProductDataList = lsAddressHelper.addDefaultProductToCell(cellData, quantity);
        CellProductData cellProductData = cellProductDataList.getItems().get(0);

        step("Prepare data to move product");
        ReqCellProductData moveCellProductData = new ReqCellProductData();
        moveCellProductData.setNewCellId(newCellId);
        moveCellProductData.setLmCode(cellProductData.getLmCode());
        moveCellProductData.setQuantity(quantity);
        moveCellProductData.setUsername(getUserSessionData().getUserLdap());

        step("Send request to move product in new cell");
        Response<JsonNode> response = lsAddressClient.moveCellProducts(cellData.getId(), moveCellProductData);
        assertThat(response, successful());

        cellProductData.setAddressCells(newCellData);

        step("Send Get request and check data");
        Response<CellProductDataList> getResponse = lsAddressClient.getCellProducts(newCellId);
        lsAddressClient.assertThatProductMovedToNewCell(getResponse, cellProductData);
    }

    @Test(description = "C23194988 lsAddress DELETE Cell products")
    public void testDeleteCellProducts() {
        // Test data
        prepareDefaultData(true, true);
        cellData = cellDataList.getItems().get(0);
        cellProductDataList = lsAddressHelper.addDefaultProductToCell(cellData, 5);
        CellProductData cellProductData = cellProductDataList.getItems().get(0);
        String cellId = cellData.getId();

        step("Delete cell products");
        Response<JsonNode> response = lsAddressClient.deleteCellProducts(cellId, cellProductData.getLmCode());
        lsAddressClient.assertThatCellProductsIsDeleted(response, cellId);
        cellProductDataList.getItems().remove(0);

        step("Send get request and check data");
        Response<CellProductDataList> getResponse = lsAddressClient.getCellProducts(cellId);
        lsAddressClient.assertThatDataMatches(getResponse, cellProductDataList);
    }

    @Test(description = "C6638969 lsAddress GET cells search")
    public void testSearchCells() {
        prepareDefaultData(true, true);
        cellData = cellDataList.getItems().get(0);
        cellProductDataList = lsAddressHelper.addDefaultProductToCell(cellData, 5);
        CellProductData cellProductData = cellProductDataList.getItems().get(0);

        List<ProductCellData> expectedSearchData = cellProductData.getLsAddressCells();
        for (ProductCellData productCellData : expectedSearchData) {
            productCellData.setQuantity(0);
        }

        Response<ProductCellDataList> resp = lsAddressClient.searchProductCells(cellProductData.getLmCode());
        lsAddressClient.assertThatDataMatches(resp, expectedSearchData);
    }

    @Test(description = "C3316286 lsAddress GET scheme")
    public void testGetScheme() {
        step("Send request to get scheme and check data");
        Response<SchemeData> resp = lsAddressClient.getScheme();
        lsAddressClient.assertThatGetScheme(resp);

    }

    @Test(description = "C3316287 lsAddress PUT scheme")
    public void testPutScheme() {
        step("Send request to update scheme and check data");
        Response<JsonNode> resp = lsAddressClient.putScheme((int) (Math.random() * 3));
        lsAddressClient.assertThatSchemeIsUpdated(resp);

    }

}
