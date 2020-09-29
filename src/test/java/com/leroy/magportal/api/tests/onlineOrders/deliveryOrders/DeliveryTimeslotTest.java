package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.timeslot.TimeslotResponseData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class DeliveryTimeslotTest extends BaseMagPortalApiTest {

    @Inject
    private BitrixHelper bitrixHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;


    @BeforeClass
    private void setUp() {
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;
        currentOrderId = bitrixHelper.createOnlineOrder(currentOrderType).getSolutionId();
    }

    @Test(description = "C23425904 Get Timeslot for Several Products")
    public void testGetTimeslotSeveralProducts() {
        Response<?> response = orderClient.getTimeslot(currentOrderId);
        orderClient.assertTimeslotResult(response);
    }

    @Test(description = "C23425905 Get Timeslot for One Product")
    public void testGetTimeslotOneProduct() {
        makeDimensionalOrder();
        Response<?> response = orderClient.getTimeslot(currentOrderId);
        orderClient.assertTimeslotResult(response);
    }

    private void makeDimensionalOrder() {
        currentOrderId = bitrixHelper.createDimensionalOnlineOrder(currentOrderType)
                .getSolutionId();
    }
}