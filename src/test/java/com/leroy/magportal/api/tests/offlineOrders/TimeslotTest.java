package com.leroy.magportal.api.tests.offlineOrders;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.LmCodeTypeEnum;
import com.leroy.magportal.api.data.timeslot.TimeslotData;
import com.leroy.magportal.api.data.timeslot.TimeslotResponseData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class TimeslotTest extends BaseMagPortalApiTest {

    @Inject
    private OrderClient orderClient;
    @Inject
    private PAOHelper paoHelper;

    private String currentOrderId;
    private TimeslotData timeslotData;


    @BeforeClass
    private void setUp() {
        OrderData orderData = paoHelper.createConfirmedOrder(paoHelper.makeCartProducts(3), true);
        currentOrderId = orderData.getOrderId();
    }

    @Test(description = "C23426854 Get Timeslot for Several Products", priority = 1)
    public void testGetTimeslotSeveralProducts() {
        Response<TimeslotResponseData> response = orderClient.getTimeslots(currentOrderId);
        assertTimeslotResult(response);
        timeslotData = response.asJson().getData().get(0);
    }

    @Test(description = "C23426856 Update Timeslot", dependsOnMethods = {
            "testGetTimeslotSeveralProducts"}, priority = 2)
    public void testUpdateTimeslot() {
        Response<?> response = orderClient.updateTimeslot(currentOrderId, timeslotData);
        assertTimeslotUpdateResult(response);
    }

    @Test(description = "C23426857 Update Timeslot for PAID", dependsOnMethods = {
            "testGetTimeslotSeveralProducts"}, priority = 3)
    public void testUpdateTimeslotPaid() {
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.updateTimeslot(currentOrderId, timeslotData);
        assertTimeslotUpdateResult(response);
    }

    @Test(description = "C23426855 Get Timeslot for One Product", priority = 4)
    public void testGetTimeslotOneProduct() throws Exception {
        makeDimensionalOrder();
        Response<TimeslotResponseData> response = orderClient.getTimeslots(currentOrderId);
        assertTimeslotResult(response);
        timeslotData = response.asJson().getData().get(0);
    }

    private void makeDimensionalOrder() throws Exception {
        OrderData orderData = paoHelper.createConfirmedOrder(
                paoHelper.makeCartProductByLmCode(LmCodeTypeEnum.DIMENSIONAL.getValue()), true);
        currentOrderId = orderData.getOrderId();
    }

    //Verification
    @Step("GET Timeslot results verification")
    public void assertTimeslotResult(Response<TimeslotResponseData> response) {
        assertThat("Request to Timeslot has Failed.", response, successful());
        List<TimeslotData> responseData = response.asJson().getData();
        assertThat("There are NO available timeslots", responseData.size(),
                greaterThanOrEqualTo(1));
        for (TimeslotData timeslotData : responseData) {
            assertThat("There are NO available date",
                    timeslotData.getAvailableDate(), not(emptyOrNullString()));
        }
    }

    @Step("Update Timeslot results verification")
    public void assertTimeslotUpdateResult(Response<?> response) {
        assertThat("Request to Timeslot Update has Failed.", response, successful());
        OrderData orderData = orderClient.getOnlineOrder(currentOrderId).asJson();
        assertThat("Pickup Date was NOT updated",
                orderData.getGiveAway().getDate(), equalTo(timeslotData.getAvailableDate()));
        assertThat("Pickup Shop was NOT updated", orderData.getGiveAway().getShopId().toString(),
                equalTo(timeslotData.getStoreId()));
    }
}