package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.Collections;

public class RupturesPostSessionTest extends BaseRuptureTest {

    @Test(description = "C3233579 Create  standard session")
    public void testCreateRuptureSessionProduct() {
        ActionData action1 = new ActionData();
        action1.setAction(0);
        action1.setState(false);
        action1.setUserPosition(0);
        ActionData action2 = new ActionData();
        action2.setAction(1);
        action2.setState(false);
        action2.setUserPosition(0);
        ActionData action3 = new ActionData();
        action3.setAction(2);
        action3.setState(true);
        action3.setUserPosition(0);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Arrays.asList(action1, action2, action3));

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        step("Создаем сессию");
        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.setTotalCount(1);
        ruptureProductDataListBody.setItems(Collections.singletonList(productData));

        step("GET /ruptures/sessions - Проверяем, что сессия была создана");
        rupturesClient.assertThatCreatedSessionPresentsInSessionsList(sessionId, "Standard");

        step("GET /ruptures/session/products - Проверяем, что товар был добавлен в сессию");
        Response<RuptureProductDataList> respGetProducts = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGetProducts, ruptureProductDataListBody);
    }

}
