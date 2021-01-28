package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.address.*;
import com.leroy.magmobile.api.data.address.cellproducts.*;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.helpers.LsAddressHelper;
import com.leroy.magmobile.api.requests.address.*;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LsAddressClient extends BaseMashupClient {

    @Inject
    private LsAddressHelper lsAddressHelper;

    // REQUESTS //

    // Alleys
    @Step("Create Alley")
    public Response<AlleyData> createAlley(AlleyData alleyData) {
        LsAddressAlleysPostRequest req = new LsAddressAlleysPostRequest();
        req.jsonBody(alleyData);
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, AlleyData.class);
    }

    @Step("Search for alleys")
    public Response<AlleyDataItems> searchForAlleys() {
        LsAddressAlleysRequest req = new LsAddressAlleysRequest();
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, AlleyDataItems.class);
    }


    @Step("Rename Alley")
    public Response<AlleyData> renameAlley(AlleyData alleyData) {
        LsAddressAlleysPutRequest req = new LsAddressAlleysPutRequest();
        req.setAlleyId(alleyData.getId());
        req.setShopId(alleyData.getStoreId());
        req.setDepartmentId(alleyData.getDepartmentId());
        req.setCode(alleyData.getCode());
        return execute(req, AlleyData.class);
    }

    @Step("Delete Alley")
    public Response<AlleyData> deleteAlley(AlleyData alleyData) {
        LsAddressAlleysDeleteRequest req = new LsAddressAlleysDeleteRequest();
        req.setAlleyId(alleyData.getId());
        req.setCode(alleyData.getCode());
        return execute(req, AlleyData.class);

    }

    // Stands

    @Step("Create stand with alleyId={alleyId}")
    public Response<StandDataList> createStand(Integer alleyId, StandDataList postData) {
        LsAddressStandsPostRequest req = new LsAddressStandsPostRequest();
        req.setAlleyId(alleyId);
        req.setShopId(getUserSessionData().getUserShopId());
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        req.jsonBody(postData);
        return execute(req, StandDataList.class);
    }

    @Step("Search for stands with alleyId={alleyId}")
    public Response<StandDataList> searchForStand(Integer alleyId) {
        LsAddressStandsRequest req = new LsAddressStandsRequest();
        req.setAlleyId(alleyId);
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, StandDataList.class);
    }

    // Scheme

    @Step("Get scheme")
    public Response<SchemeData> getScheme() {
        LsAddressSchemeRequest req = new LsAddressSchemeRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        return execute(req, SchemeData.class);
    }

    @Step("Update scheme with schemeType={schemeType}")
    public Response<JsonNode> putScheme(int schemeType) {
        LsAddressSchemePutRequest req = new LsAddressSchemePutRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        SchemeData schemeData = new SchemeData();
        schemeData.setSchemeType(schemeType);
        req.jsonBody(schemeData);
        return execute(req, JsonNode.class);
    }

    // Cells

    @Step("Create Cell for standId={standId}")
    public Response<CellDataList> createCell(int standId, CellDataList postData) {
        LsAddressCellsPostRequest req = new LsAddressCellsPostRequest();
        req.setStandId(standId);
        req.jsonBody(postData);
        return execute(req, CellDataList.class);
    }

    @Step("Get cells with standId={standId}")
    public Response<CellDataList> getCells(int standId) {
        LsAddressCellsRequest req = new LsAddressCellsRequest();
        req.setStandId(standId);
        return execute(req, CellDataList.class);
    }

    @Step("Update cells with standId={standId}")
    public Response<CellDataList> updateCells(int standId, CellDataList putData) {
        LsAddressCellsPutRequest req = new LsAddressCellsPutRequest();
        req.setStandId(standId);
        req.jsonBody(putData);
        return execute(req, CellDataList.class);
    }

    @Step("Delete cell with cellId={cellId}")
    public Response<JsonNode> deleteCell(String cellId) {
        LsAddressCellsDeleteRequest req = new LsAddressCellsDeleteRequest();
        req.setCellId(cellId);
        return execute(req, JsonNode.class);
    }

    @Step("Search product cells with lmCode={lmCode}")
    public Response<ProductCellDataList> searchProductCells(String lmCode) {
        LsAddressCellSearchRequest req = new LsAddressCellSearchRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setLmCode(lmCode);
        return execute(req, ProductCellDataList.class);
    }

    // Cell Products

    @Step("Create cell products for cellId={cellId}")
    public Response<CellProductDataList> createCellProducts(String cellId, ReqCellProductDataList postData) {
        LsAddressCellProductsPostRequest req = new LsAddressCellProductsPostRequest();
        req.setCellId(cellId);
        req.setLdapHeader(getUserSessionData().getUserLdap());
        req.setShopId(getUserSessionData().getUserShopId());
        req.jsonBody(postData);
        return execute(req, CellProductDataList.class);
    }

    @Step("Get cell products for cellId={cellId}")
    public Response<CellProductDataList> getCellProducts(String cellId) {
        LsAddressCellProductsRequest req = new LsAddressCellProductsRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setCellId(cellId);
        return execute(req, CellProductDataList.class);
    }

    @Step("Update cell products for cellId={cellId}, lmCode={lmCode}")
    public Response<CellProductData> updateCellProducts(String cellId, String lmCode, ReqCellProductData putData) {
        LsAddressCellProductsPut req = new LsAddressCellProductsPut();
        req.setLdapHeader(getUserSessionData().getUserLdap());
        req.setShopId(getUserSessionData().getUserShopId());
        req.setCellId(cellId);
        req.setLmCode(lmCode);
        req.jsonBody(putData);
        return execute(req, CellProductData.class);
    }

    @Step("Move cell products for cellId={cellId}")
    public Response<JsonNode> moveCellProducts(String cellId, ReqCellProductData putData) {
        LsAddressCellProductsMove req = new LsAddressCellProductsMove();
        req.setLdapHeader(getUserSessionData().getUserLdap());
        req.setCellId(cellId);
        req.jsonBody(putData);
        return execute(req, JsonNode.class);
    }

    @Step("Delete cell products for cellId={cellId}")
    public Response<JsonNode> deleteCellProducts(String cellId, String lmCode) {
        LsAddressCellProductsDelete req = new LsAddressCellProductsDelete();
        req.setCellId(cellId);
        req.setLmCode(lmCode);
        req.setLdapHeader(getUserSessionData().getUserLdap());
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, JsonNode.class);
    }


    // VERIFICATIONS ///

    // Alley

    @Step("Check that alley is created and response matches postData")
    public AlleyData assertThatAlleyIsCreatedAndGetData(Response<AlleyData> resp, AlleyData postData) {
        assertThatResponseIsOk(resp);
        AlleyData actualData = resp.asJson();
        assertThat("id", actualData.getId(), greaterThan(0));
        assertThat("count", actualData.getCount(), is(0));
        assertThat("type", actualData.getType(), is(postData.getType()));
        assertThat("storeId", actualData.getStoreId(), is(Integer.valueOf(getUserSessionData().getUserShopId())));
        assertThat("departmentId", actualData.getDepartmentId(),
                is(Integer.valueOf(getUserSessionData().getUserDepartmentId())));
        assertThat("code", actualData.getCode(), is(postData.getCode()));
        return actualData;
    }

    @Step("Check that alley is renamed")
    public void assertThatAlleyIsRenamed(Response<AlleyData> resp, AlleyData expectedData) {
        assertThatResponseIsOk(resp);
        AlleyData actualData = lsAddressHelper.searchAlleyById(expectedData.getId());
        assertThat("id", actualData.getId(), is(expectedData.getId()));
        assertThat("count", actualData.getCount(), is(0));
        assertThat("storeId", actualData.getStoreId(), is(Integer.valueOf(getUserSessionData().getUserShopId())));
        assertThat("departmentId", actualData.getDepartmentId(),
                is(Integer.valueOf(getUserSessionData().getUserDepartmentId())));
        assertThat("code", actualData.getCode(), is(expectedData.getCode()));
    }

    @Step("Check that alley is deleted")
    public void assertThatAlleyIsDeleted(Response<AlleyData> resp, int alleyId) {
        assertThatResponseIsOk(resp);
        AlleyData actualData = lsAddressHelper.searchAlleyById(alleyId, true);
        assertThat("id", actualData.getId(), nullValue());
        assertThat("count", actualData.getCount(), nullValue());
        assertThat("storeId", actualData.getStoreId(), nullValue());
        assertThat("departmentId", actualData.getDepartmentId(), nullValue());
        assertThat("code", actualData.getCode(), nullValue());
    }

    // Stand

    @Step("Check that stand is created and response matches postData")
    public StandDataList assertThatStandIsCreatedAndGetData(Response<StandDataList> resp, StandDataList postData) {
        assertThatResponseIsOk(resp);
        StandDataList actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(postData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            StandData actualItem = actualData.getItems().get(i);
            StandData expectedItem = postData.getItems().get(i);
            assertThat("code", actualItem.getCode(),
                    containsString(String.format("-%s-", postData.getAlleyCode())));
            assertThat("id", actualItem.getId(), greaterThan(0));
            assertThat("side", actualItem.getSide(), is(expectedItem.getSide()));
            assertThat("size", actualItem.getSize(), is(expectedItem.getSize()));
            assertThat("position", actualItem.getPosition(), is(expectedItem.getPosition()));
            assertThat("cellsCount", actualItem.getCellsCount(), is(0));
            assertThat("productsCount", actualItem.getProductsCount(), is(0));
            assertThat("equipmentId", actualItem.getEquipmentId(), is(0));
        }
        return actualData;
    }

    @Step("Check that response matches expected Data")
    public void assertThatDataMatches(Response<StandDataList> resp, StandDataList expectedData) {
        assertThatResponseIsOk(resp);
        StandDataList actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(expectedData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            StandData actualItem = actualData.getItems().get(i);
            StandData expectedItem = expectedData.getItems().get(i);
            assertThat("id", actualItem.getId(), is(expectedItem.getId()));
            assertThat("code", actualItem.getCode(), is(expectedItem.getCode()));
            assertThat("side", actualItem.getSide(), is(expectedItem.getSide()));
            assertThat("size", actualItem.getSize(), is(expectedItem.getSize()));
            assertThat("position", actualItem.getPosition(), is(expectedItem.getPosition()));
            assertThat("cellsCount", actualItem.getCellsCount(), is(expectedItem.getCellsCount()));
            assertThat("productsCount", actualItem.getProductsCount(), is(expectedItem.getProductsCount()));
            assertThat("equipmentId", actualItem.getEquipmentId(), is(expectedItem.getEquipmentId()));
        }
    }

    // Scheme
    @Step("Check that response is success")
    public void assertThatSchemeIsUpdated(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("success", resp.asJson().get("success").asText(), is("true"));
    }

    // Cell

    @Step("Check that cell is created and response has valid data")
    public CellDataList assertThatCellIsCreatedAndGetData(Response<CellDataList> resp, int standId, CellDataList postData) {
        assertThatResponseIsOk(resp);
        CellDataList actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(postData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            CellData actualItem = actualData.getItems().get(i);
            CellData expectedItem = postData.getItems().get(i);
            assertThat("id", actualItem.getId(), not(emptyOrNullString()));
            assertThat("code", actualItem.getCode(), not(emptyOrNullString()));
            assertThat("shelf", actualItem.getShelf(), is(expectedItem.getShelf()));
            assertThat("side", actualItem.getPosition(), is(expectedItem.getPosition()));
            assertThat("type", actualItem.getType(), is(expectedItem.getType()));
            assertThat("standId", actualItem.getStandId(), is(standId));
            assertThat("productsCount", actualItem.getProductsCount(), is(0));
        }
        return actualData;
    }

    public void assertThatDataMatches(Response<CellDataList> resp, CellDataList expectedData) {
        assertThatDataMatches(resp, expectedData, ResponseType.GET);
    }

    @Step("Check that response matches expected Data")
    public void assertThatDataMatches(Response<CellDataList> resp, CellDataList expectedData, ResponseType respType) {
        assertThatResponseIsOk(resp);
        CellDataList actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(expectedData.getItems().size()));
        // Check main Cell id
        if (respType.equals(ResponseType.GET)) {
            for (CellData cellData : expectedData.getItems()) {
                assertThat("Cell id", actualData.getStandId(), is(cellData.getStandId()));
            }
        }
        // Check items
        for (int i = 0; i < actualData.getItems().size(); i++) {
            CellData actualItem = actualData.getItems().get(i);
            CellData expectedItem = expectedData.getItems().get(i);
            if (ResponseType.PUT.equals(respType) && expectedItem.getId() == null) {
                assertThat("id", actualItem.getId(), not(emptyOrNullString()));
                assertThat("code", actualItem.getCode(), not(emptyOrNullString()));
                assertThat("productsCount", actualItem.getProductsCount(), is(0));
                assertThat("stand id", actualItem.getStandId(), notNullValue());
            } else {
                assertThat("id", actualItem.getId(), is(expectedItem.getId()));
                assertThat("code", actualItem.getCode(), is(expectedItem.getCode()));
                assertThat("productsCount", actualItem.getProductsCount(), is(expectedItem.getProductsCount()));
                assertThat("stand id", actualItem.getStandId(), is(expectedItem.getStandId()));
            }
            assertThat("shelf", actualItem.getShelf(), is(expectedItem.getShelf()));
            assertThat("position", actualItem.getPosition(), is(expectedItem.getPosition()));
            assertThat("type", actualItem.getType(), is(expectedItem.getType()));
        }
    }

    @Step("Check that cell is deleted. CellId={deletedCellId}")
    public void assertThatCellIsDeleted(Response<JsonNode> resp, String deletedCellId) {
        assertThatResponseIsOk(resp);
        JsonNode respData = resp.asJson();
        assertThat("item id", respData.get("items").get("items").get(0).asText(),
                is(deletedCellId));
    }

    // Cell products
    @Step("Check that cell products are created")
    public CellProductDataList assertThatIsCellProductsIsCreatedAndGetData(
            Response<CellProductDataList> resp, ReqCellProductDataList postData, CellData cellData) {
        assertThatResponseIsOk(resp);
        CellProductDataList actualRespData = resp.asJson();
        assertThat("items", actualRespData.getItems(), hasSize(postData.getItems().size()));
        for (int i = 0; i < postData.getItems().size(); i++) {
            CellProductData actualCellProductData = actualRespData.getItems().get(i);
            ReqCellProductData expectedCellProductData = postData.getItems().get(i);
            assertThat("quantity", actualCellProductData.getQuantity(), is(expectedCellProductData.getQuantity()));
            assertThat("lmCode", actualCellProductData.getLmCode(), is(expectedCellProductData.getLmCode()));
            assertThat("lsAddressCells", actualCellProductData.getLsAddressCells(), notNullValue());
            assertThat("lsAddressCells count", actualCellProductData.getLsAddressCells(), hasSize(1));
            ProductCellData actualCellData = actualCellProductData.getLsAddressCells().get(0);
            assertThat("lsAddress Cell - Id", actualCellData.getId(), is(cellData.getId()));
            assertThat("lsAddress Cell - Code", actualCellData.getCode(), is(cellData.getCode()));
            assertThat("lsAddress Cell - Position", actualCellData.getPosition(), is(cellData.getPosition()));
            assertThat("lsAddress Cell - Quantity", actualCellData.getQuantity(),
                    is(expectedCellProductData.getQuantity()));
            assertThat("lsAddress Cell - Shelf", actualCellData.getShelf(), is(cellData.getShelf()));
            assertThat("lsAddress Cell - Stand Id", actualCellData.getStandId(), is(cellData.getStandId()));
            assertThat("lsAddress Cell - Type", actualCellData.getType(), is(cellData.getType()));
        }
        return actualRespData;
    }

    @Step("Check that response matches expected Data")
    public void assertThatDataMatches(Response<CellProductDataList> resp, CellProductDataList expectedData) {
        assertThatResponseIsOk(resp);
        CellProductDataList actualRespData = resp.asJson();
        assertThat("Cell Products items", actualRespData, equalTo(expectedData));
    }

    @Step("Check that response matches expected Data")
    public void assertThatDataMatches(Response<CellProductData> resp, CellProductData expectedData) {
        assertThatResponseIsOk(resp);
        CellProductData actualRespData = resp.asJson();
        assertThat("Cell Product - quantity", actualRespData.getQuantity(), equalTo(expectedData.getQuantity()));
        assertThat("Cell Product - lm code", actualRespData.getLmCode(), equalTo(expectedData.getLmCode()));
        // to be continued if necessary
    }

    @Step("Check that response matches expected Data")
    public void assertThatDataMatches(Response<ProductCellDataList> resp, List<ProductCellData> expectedData) {
        assertThatResponseIsOk(resp);
        ProductCellDataList actualRespData = resp.asJson();
        assertThat("Ls Address Cells", actualRespData.getItems(), equalTo(expectedData));
    }

    @Step("Check that cell products are deleted for cellId={deletedCellId}")
    public void assertThatCellProductsIsDeleted(Response<JsonNode> resp, String deletedCellId) {
        assertThatResponseIsOk(resp);
        JsonNode respData = resp.asJson();
        assertThat("item id", respData.get("items").get(0).asText(),
                is(deletedCellId));
    }

}
