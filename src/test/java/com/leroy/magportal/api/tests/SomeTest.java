package com.leroy.magportal.api.tests;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.Is4AuthClient;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.Test;

// Тестовый класс надо будет переименовать и дать соответствующее название
public class SomeTest extends BaseMagPortalApiTest {

    @Inject
    CatalogSearchClient paymentHelper;
    @Inject
    private BitrixHelper bitrixHelper;
    @Inject
    private AuthClient authClient;
    @Inject
    private Is4AuthClient is4AuthClient;

    @Test(description = "C1 Название теста")
    public void test() throws Exception {
        //String code = authClient.authAndGetCode(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        //Response<Is4TokenData> response = is4AuthClient.sendPostCodeRequest(code);
//        bitrixHelper.createOnlineOrders(2, DELIVERY_TO_DOOR, 2);
//        String id = bitrixHelper.createOnlineOrder();
//        paymentHelper.makePaymentCard(id);
//        PickingTaskClient client = apiClientProvider.getPickingTaskClient();
//        Response<PickingTaskData> resp = client.startPicking("3003520080271822");
//        Response<PickingTaskData> resp2 = client.completePicking("3003520090279058", true);

    }
}