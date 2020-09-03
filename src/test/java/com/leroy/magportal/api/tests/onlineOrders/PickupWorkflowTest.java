package com.leroy.magportal.api.tests.onlineOrders;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

public class PickupWorkflowTest extends BaseMagPortalApiTest {

    @Inject
    private BitrixHelper bitrixHelper;
    @Inject
    private PaymentHelper paymentHelper;

    private OrderClient orderClient;
    private PickingTaskClient pickingTaskClient;
    private List<BitrixSolutionResponse> bitrixSolutionResponses;
    private String currentOrderId;


    @BeforeClass
    private void setUp() {
        orderClient = apiClientProvider.getOrderClient();
        pickingTaskClient = apiClientProvider.getPickingTaskClient();
        bitrixSolutionResponses = bitrixHelper
                .createOnlineOrders(1, OnlineOrderTypeConst.PICKUP_POSTPAYMENT, 3);
    }

    @Test(description = "C1 Start Picking the Order")
    public void testStartPicking() {

    }
}