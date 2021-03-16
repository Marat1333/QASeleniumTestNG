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

import java.util.*;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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

    private int schemeType;

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

        step("Send Get request and check data");
        Response<CellDataList> getResp = lsAddressClient.getCells(standData.getId());
        lsAddressClient.assertThatDataMatches(getResp, cellDataList);
    }

    // Cell products
    @Test(description = "C23194989 lsAddress POST Cell products")
    public void testCreateCellProducts() {
        prepareDefaultData(true, true);

        step("Prepare test data");
        cellData = cellDataList.getItems().get(0);
        String lmCode = searchProductHelper.getRandomProduct().getLmCode();
        int quantity = 2;

        ReqCellProductData reqCellProductData = new ReqCellProductData();
        reqCellProductData.setLmCode(lmCode);
        reqCellProductData.setQuantity(quantity);

        ReqCellProductDataList postData = new ReqCellProductDataList();
        postData.setItems(Collections.singletonList(reqCellProductData));

        Response<CellProductDataList> response = lsAddressClient.createCellProducts(cellData.getId(), postData);
        lsAddressClient.assertThatIsCellProductsIsCreated(response, postData, cellData);
    }

    @Test(description = "C23194985 lsAddress GET Cell products")
    public void testGetCellProducts() {
        // Test data

        CellData cellData = cellDataList.getItems().get(0);
        String cellId = cellData.getId();

        Response<CellProductDataList> response = lsAddressClient.getCellProducts(cellId);
        lsAddressClient.assertThatDataMatches(response, cellProductDataList);
    }

    @Test(description = "C23194986 lsAddress PUT Cell products - Change quantity")
    public void testUpdateCellProducts() {
        // Test data
        CellData cellData = cellDataList.getItems().get(0);
        String cellId = cellData.getId();
        CellProductData cellProductData = cellProductDataList.getItems().get(0);
        String lmCode = cellProductData.getLmCode();
        int quantity = 7;

        ReqCellProductData updateCellProductData = new ReqCellProductData();
        updateCellProductData.setQuantity(quantity);
        updateCellProductData.setUsername("Auto_" + RandomStringUtils.randomAlphabetic(5));

        Response<CellProductData> response = lsAddressClient.updateCellProducts(
                cellId, lmCode, updateCellProductData);
        cellProductData.setQuantity(quantity);
        lsAddressClient.assertThatDataMatches(response, cellProductData);
        cellProductData.setQuantity(quantity);
    }

    @Test(description = "C23194987 Move Cell Products - 1 Product")
    public void testMoveCellProducts() {
        // Test data
        CellData cellData = cellDataList.getItems().get(0);
        String cellId = cellData.getId();
        CellData newCellData = cellDataList.getItems().get(1);
        String newCellId = newCellData.getId();
        CellProductData cellProductData = cellProductDataList.getItems().get(0);
        String lmCode = cellProductData.getLmCode();
        int quantity = 1;

        ReqCellProductData moveCellProductData = new ReqCellProductData();
        moveCellProductData.setNewCellId(newCellId);
        moveCellProductData.setLmCode(lmCode);
        moveCellProductData.setQuantity(quantity);
        moveCellProductData.setUsername("Auto_" + RandomStringUtils.randomAlphabetic(5));

        step("Move");
        Response<JsonNode> response = lsAddressClient.moveCellProducts(cellId, moveCellProductData);
        assertThat(response, successful());

        cellProductData.setAddressCells(cellData, newCellData);
        cellProductData.setQuantity(cellProductData.getQuantity() - quantity);
        cellProductData.getLsAddressCells().get(0).setQuantity(cellProductData.getQuantity());
        cellProductData.getLsAddressCells().get(1).setQuantity(quantity);

        step("Send Get request and check data");
        Response<CellProductDataList> getResponse = lsAddressClient.getCellProducts(cellId);
        lsAddressClient.assertThatDataMatches(getResponse, cellProductDataList);
    }

    @Test(description = "C23194988 lsAddress DELETE Cell products")
    public void testDeleteCellProducts() {
        // Test data
        CellData cellData = cellDataList.getItems().get(1);
        String cellId = cellData.getId();
        CellProductData cellProductData = cellProductDataList.getItems().get(0);
        String lmCode = cellProductData.getLmCode();

        step("Delete cell products");
        Response<JsonNode> response = lsAddressClient.deleteCellProducts(cellId, lmCode);
        lsAddressClient.assertThatCellProductsIsDeleted(response, cellId);
        cellProductDataList.getItems().remove(0);

        step("Send get request and check data");
        Response<CellProductDataList> getResponse = lsAddressClient.getCellProducts(cellId);
        lsAddressClient.assertThatDataMatches(getResponse, cellProductDataList);
    }

    @Test(description = "C6638969 lsAddress GET cells search")
    public void testSearchCells() {
        CellProductData cellProductData = cellProductDataList.getItems().get(0);
        String lmCode = cellProductData.getLmCode();

        List<ProductCellData> expectedSearchData = cellProductData.getLsAddressCells();
        for (ProductCellData productCellData : expectedSearchData) {
            productCellData.setQuantity(0);
        }

        Response<ProductCellDataList> resp = lsAddressClient.searchProductCells(lmCode);
        lsAddressClient.assertThatDataMatches(resp, expectedSearchData);
    }

    @Test(description = "C3316286 lsAddress GET scheme")
    public void testGetScheme() {
        Response<SchemeData> resp = lsAddressClient.getScheme();
        assertThat(resp, successful());
        SchemeData schemeData = resp.asJson();
        assertThat("schemeType", schemeData.getSchemeType(), is(schemeType));
        assertThat("navigationType", schemeData.getNavigationType(), is(greaterThan(0)));
        assertThat("departmentId", schemeData.getDepartmentId(),
                is(Integer.parseInt(getUserSessionData().getUserDepartmentId())));
        assertThat("shopId", schemeData.getShopId(),
                is(Integer.parseInt(getUserSessionData().getUserShopId())));
    }

    @Test(description = "C3316287 lsAddress PUT scheme")
    public void testPutScheme() {
        schemeType = new Random().nextInt(10);
        Response<JsonNode> resp = lsAddressClient.putScheme(schemeType);
        lsAddressClient.assertThatSchemeIsUpdated(resp);
    }

}
