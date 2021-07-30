package com.leroy.magmobile.api.tests.ruptures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.ErrorTextConst;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.data.CommonErrorResponseData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesDeleteProductTest extends BaseRuptureTest {

    private String removedProductLmCode;

    @Override
    protected boolean isDeleteSessionAfterEveryMethod() {
        return false;
    }

    @BeforeClass
    public void setUp() {
        ruptureProductDataListBody = new RuptureProductDataList();
        List<RuptureProductData> ruptureItems = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RuptureProductData productData = new RuptureProductData();
            productData.generateRandomData();
            ruptureItems.add(productData);
        }
        ruptureProductDataListBody.setItems(ruptureItems);
        ruptureProductDataListBody.setTotalCount(ruptureItems.size());

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(ruptureItems.get(0));
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        rupturePostData.setSessionId(sessionId);
        for (int i = 1; i < ruptureItems.size(); i++) {
            rupturePostData.setProduct(ruptureItems.get(i));
            resp = rupturesClient.addProductToSession(rupturePostData);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        }

    }

    @Test(description = "C3233586 DELETE ruptures product")
    @AllureId("13205")
    public void testDeleteRupturesProduct() {
        removedProductLmCode = ruptureProductDataListBody.getItems().get(0).getLmCode();
        step("Удаляем товар из сессии");
        Response<JsonNode> resp = rupturesClient.deleteProductInSession(removedProductLmCode, sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.removeItem(0);

        step("Отправляем GET запрос и проверяем, что товар был удален");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

    @Test(description = "C3293173 DELETE ruptures product for deleted product")
    @AllureId("13206")
    public void testDeleteRupturesProductForDeletedProduct() {
        if (removedProductLmCode == null) {
            removedProductLmCode = ruptureProductDataListBody.getItems().get(0).getLmCode();
            step("Удаляем товар из сессии");
            Response<JsonNode> resp = rupturesClient.deleteProductInSession(removedProductLmCode, sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
            ruptureProductDataListBody.removeItem(0);
        }

        step("Пытаемся удалить повторно товар из сессии");
        Response<JsonNode> resp = rupturesClient.deleteProductInSession(removedProductLmCode, sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("Отправляем GET запрос и проверяем, что товар был удален");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

    @Test(description = "C3298403 DELETE ruptures product from finished session")
    @AllureId("13207")
    public void testDeleteRupturesProductFromFinishedSession() {
        step("Завершаем сессию");
        Response<JsonNode> respFinishSession = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(respFinishSession);

        step("Удаляем товар из завершенной сессии");
        Response<JsonNode> resp = rupturesClient.deleteProductInSession(
                ruptureProductDataListBody.getItems().get(0).getLmCode(), sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.removeItem(0);

        step("Отправляем GET запрос и проверяем, что товар был удален");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

    @Test(description = "C23409766 DELETE ruptures product mashup validation")
    @AllureId("13208")
    public void testDeleteRupturesProductMashupValidation() {

        step("Отправляем запрос на удаление без параметров");
        Response<JsonNode> resp = rupturesClient.deleteProductInSession("", "");
        assertThat("Response code", resp.getStatusCode(), equalTo(StatusCodes.ST_400_BAD_REQ));
        CommonErrorResponseData errorResp = resp.asJson(CommonErrorResponseData.class);
        assertThat("error text", errorResp.getError(),
                equalTo(ErrorTextConst.WRONG_PATH));
        assertThat("validation sessionId", errorResp.getValidation().getSessionId(),
                equalTo(ErrorTextConst.REQUIRED));
        assertThat("validation lmCode", errorResp.getValidation().getLmCode(),
                equalTo(ErrorTextConst.REQUIRED));
    }

    @Test(description = "C23409767 DELETE ruptures product for not existing session")
    @AllureId("13209")
    public void testDeleteRupturesProductForNotExistingSession() {

        step("Отправляем запрос на удаление товара из несуществующей сессии");
        Response<JsonNode> resp = rupturesClient.deleteProductInSession(
                ruptureProductDataListBody.getItems().get(0).getLmCode(), Integer.MAX_VALUE);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("Отправляем GET запрос и проверяем, что в существующей сессии ничего не изменилось");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

}
