package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.address.*;
import com.leroy.magmobile.api.requests.address.*;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LsAddressClient extends MagMobileClient {

    // REQUESTS //

    // Alleys
    public Response<AlleyData> createAlley(AlleyData alleyData) {
        LsAddressAlleysPostRequest req = new LsAddressAlleysPostRequest();
        req.jsonBody(alleyData);
        req.setDepartmentId(sessionData.getUserDepartmentId());
        req.setShopId(sessionData.getUserShopId());
        return execute(req, AlleyData.class);
    }

    public Response<AlleyDataItems> searchForAlleys() {
        LsAddressAlleysRequest req = new LsAddressAlleysRequest();
        req.setDepartmentId(sessionData.getUserDepartmentId());
        req.setShopId(sessionData.getUserShopId());
        return execute(req, AlleyDataItems.class);
    }

    // Stands

    public Response<StandData> createStand(Integer alleyId, StandData postData) {
        LsAddressStandsPostRequest req = new LsAddressStandsPostRequest();
        req.setAlleyId(alleyId);
        req.setShopId(sessionData.getUserShopId());
        req.setDepartmentId(sessionData.getUserDepartmentId());
        req.jsonBody(postData);
        return execute(req, StandData.class);
    }

    public Response<StandData> searchForStand(Integer alleyId) {
        LsAddressStandsRequest req = new LsAddressStandsRequest();
        req.setAlleyId(alleyId);
        req.setDepartmentId(sessionData.getUserDepartmentId());
        req.setShopId(sessionData.getUserShopId());
        return execute(req, StandData.class);
    }

    // Cells

    public Response<CellData> createCell(Integer standId, CellData postData) {
        LsAddressCellsPostRequest req = new LsAddressCellsPostRequest();
        req.setStandId(standId);
        req.jsonBody(postData);
        return execute(req, CellData.class);
    }


    public Response<JsonNode> createCellProducts(String cellId) {
        LsAddressCellProductsPostRequest req = new LsAddressCellProductsPostRequest();
        req.setCellId(cellId);
        req.setLdap(sessionData.getUserLdap());
        req.setShopId(sessionData.getUserShopId());
        return execute(req, JsonNode.class);
    }


    // VERIFICATIONS ///

    // Alley

    public AlleyData assertThatAlleyIsCreatedAndGetData(Response<AlleyData> resp, AlleyData postData) {
        assertThatResponseIsOk(resp);
        AlleyData actualData = resp.asJson();
        assertThat("id", actualData.getId(), greaterThan(0));
        assertThat("count", actualData.getCount(), is(0));
        assertThat("type", actualData.getType(), is(postData.getType()));
        assertThat("storeId", actualData.getStoreId(), is(Integer.valueOf(sessionData.getUserShopId())));
        assertThat("departmentId", actualData.getDepartmentId(),
                is(Integer.valueOf(sessionData.getUserDepartmentId())));
        assertThat("code", actualData.getCode(), is(postData.getCode()));
        return actualData;
    }

    // Stand

    public StandData assertThatStandIsCreatedAndGetData(Response<StandData> resp, StandData postData) {
        assertThatResponseIsOk(resp);
        StandData actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(postData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            StandItemData actualItem = actualData.getItems().get(i);
            StandItemData expectedItem = postData.getItems().get(i);
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

    public void assertThatDataMatches(Response<StandData> resp, StandData postData) {
        assertThatResponseIsOk(resp);
        StandData actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(postData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            StandItemData actualItem = actualData.getItems().get(i);
            StandItemData expectedItem = postData.getItems().get(i);
            assertThat("id", actualItem.getId(), is(expectedItem.getId()));
            assertThat("code", actualItem.getCode(), is(expectedItem.getCode()));
            assertThat("side", actualItem.getSide(), is(expectedItem.getSide()));
            assertThat("size", actualItem.getSize(), is(expectedItem.getSize()));
            assertThat("position", actualItem.getPosition(), is(expectedItem.getPosition()));
            assertThat("cellsCount", actualItem.getCellsCount(),  is(expectedItem.getCellsCount()));
            assertThat("productsCount", actualItem.getProductsCount(),  is(expectedItem.getProductsCount()));
            assertThat("equipmentId", actualItem.getEquipmentId(),  is(expectedItem.getEquipmentId()));
        }
    }

    // Cell

    public CellData assertThatCellIsCreatedAndGetData(Response<CellData> resp, int standId, CellData postData) {
        assertThatResponseIsOk(resp);
        CellData actualData = resp.asJson();
        assertThat("items count", actualData.getItems(), hasSize(postData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            CellItemData actualItem = actualData.getItems().get(i);
            CellItemData expectedItem = postData.getItems().get(i);
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


}
