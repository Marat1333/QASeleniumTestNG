package com.leroy.magmobile.api.tests.address;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.LsAddressClient;
import com.leroy.magmobile.api.clients.MagMobileClient;
import com.leroy.magmobile.api.data.address.*;
import com.leroy.magmobile.api.data.address.cellproducts.CellProductData;
import com.leroy.magmobile.api.data.address.cellproducts.CellProductDataList;
import com.leroy.magmobile.api.data.address.cellproducts.ReqCellProductData;
import com.leroy.magmobile.api.data.address.cellproducts.ReqCellProductDataList;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class LsAddressTest extends BaseProjectApiTest {

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    private LsAddressClient lsAddressClient;

    private AlleyData alleyData;

    private StandDataList standDataList;

    private CellDataList cellDataList;

    private CellProductDataList cellProductDataList;

    private int schemeType;

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
        StandDataList postStandDataList = new StandDataList();
        StandData item1 = new StandData(1, 2, 3);
        StandData item2 = new StandData(4, 3, 2);
        postStandDataList.setItems(Arrays.asList(item1, item2));
        postStandDataList.setAlleyCode(alleyData.getCode());
        postStandDataList.setAlleyType(alleyData.getType());
        postStandDataList.setEmail(RandomStringUtils.randomAlphanumeric(5) + "@mail.com");
        Response<StandDataList> resp = lsAddressClient.createStand(alleyData.getId(), postStandDataList);
        this.standDataList = lsAddressClient.assertThatStandIsCreatedAndGetData(resp, postStandDataList);
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
        Response<StandDataList> resp = lsAddressClient.searchForStand(alleyData.getId());
        lsAddressClient.assertThatDataMatches(resp, standDataList);
    }

    @Test(description = "Create cell")
    public void testCreateCell() {
        int standId = standDataList.getItems().get(0).getId();
        CellDataList cellDataList = new CellDataList();
        CellData itemData1 = new CellData(1, 2, "A");
        CellData itemData2 = new CellData(3, 4, "B");
        cellDataList.setItems(Arrays.asList(itemData1, itemData2));
        Response<CellDataList> resp = lsAddressClient.createCell(standId, cellDataList);
        this.cellDataList = lsAddressClient.assertThatCellIsCreatedAndGetData(resp, standId, cellDataList);
    }

    @Test(description = "Get cells")
    public void testGetCells() {
        int standId = cellDataList.getItems().get(0).getStandId();
        Response<CellDataList> resp = lsAddressClient.getCells(standId);
        lsAddressClient.assertThatDataMatches(resp, cellDataList);
    }

    @Test(description = "Update cells - Add item")
    public void testUpdateCells() {
        int standId = cellDataList.getItems().get(0).getStandId();

        CellData newCellData = new CellData();
        newCellData.setType(5);
        newCellData.setPosition(11);
        newCellData.setShelf("C");
        List<CellData> putCellItems = new ArrayList<>(cellDataList.getItems());
        putCellItems.add(newCellData);

        CellDataList updateCellDataList = new CellDataList();
        updateCellDataList.setItems(putCellItems);
        step("Add Item");
        Response<CellDataList> resp = lsAddressClient.updateCells(standId, updateCellDataList);
        cellDataList.addItem(newCellData);
        lsAddressClient.assertThatDataMatches(resp, cellDataList, MagMobileClient.ResponseType.PUT);
        cellDataList.updateLastItem(resp.asJson().getItems().get(2));
        step("Send Get request and check data");
        Response<CellDataList> getResp = lsAddressClient.getCells(standId);
        lsAddressClient.assertThatDataMatches(getResp, cellDataList);
    }

    @Test(description = "Delete cell")
    public void testDeleteCell() {
        int itemIndexToRemove = 0;
        int standId = cellDataList.getItems().get(0).getStandId();
        String cellIdToRemove = cellDataList.getItems().get(itemIndexToRemove).getId();

        Response<JsonNode> resp = lsAddressClient.deleteCell(cellIdToRemove);
        lsAddressClient.assertThatCellIsDeleted(resp, cellIdToRemove);
        cellDataList.getItems().remove(itemIndexToRemove);

        step("Send Get request and check data");
        Response<CellDataList> getResp = lsAddressClient.getCells(standId);
        lsAddressClient.assertThatDataMatches(getResp, cellDataList);
    }

    // Cell products
    @Test(description = "Create Cell Products")
    public void testCreateCellProducts() {
        // Test data
        CellData cellData = cellDataList.getItems().get(0);
        String cellId = cellData.getId();
        String lmCode = apiClientProvider.getProducts(1).get(0).getLmCode();
        int quantity = 2;

        ReqCellProductData reqCellProductData = new ReqCellProductData();
        reqCellProductData.setLmCode(lmCode);
        reqCellProductData.setQuantity(quantity);

        ReqCellProductDataList postData = new ReqCellProductDataList();
        postData.setItems(Collections.singletonList(reqCellProductData));

        Response<CellProductDataList> response = lsAddressClient.createCellProducts(
                cellId, postData);
        this.cellProductDataList = lsAddressClient.assertThatIsCellProductsIsCreatedAndGetData(response, postData, cellData);
    }

    @Test(description = "Get Cell Products")
    public void testGetCellProducts() {
        // Test data
        CellData cellData = cellDataList.getItems().get(0);
        String cellId = cellData.getId();

        Response<CellProductDataList> response = lsAddressClient.getCellProducts(cellId);
        lsAddressClient.assertThatDataMatches(response, cellProductDataList);
    }

    @Test(description = "Update Cell Products - Change quantity")
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

    @Test(description = "Move Cell Products - 1 Product")
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

    @Test(description = "Delete Cell Products")
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

    @Test(description = "Get Scheme")
    public void testGetScheme() {
        Response<SchemeData> resp = lsAddressClient.getScheme();
        assertThat(resp, successful());
        SchemeData schemeData = resp.asJson();
        assertThat("schemeType", schemeData.getSchemeType(), is(schemeType));
        assertThat("navigationType", schemeData.getNavigationType(), is(greaterThan(0)));
        assertThat("departmentId", schemeData.getDepartmentId(),
                is(Integer.parseInt(sessionData.getUserDepartmentId())));
        assertThat("shopId", schemeData.getShopId(),
                is(Integer.parseInt(sessionData.getUserShopId())));
    }

    @Test(description = "Put Scheme")
    public void testPutScheme() {
        schemeType = new Random().nextInt(10);
        Response<JsonNode> resp = lsAddressClient.putScheme(schemeType);
        lsAddressClient.assertThatSchemeIsUpdated(resp);
    }

}
