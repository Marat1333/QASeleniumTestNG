package com.leroy.magportal.api.tests.onlineOrders.pickupOrders;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.timeslot.TimeslotData;
import com.leroy.magportal.api.data.timeslot.TimeslotResponseData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PickupTimeslotTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PAOHelper paoHelper;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;
    private TimeslotData timeslotData;


    @BeforeClass
    private void setUp() {
        currentOrderType = OnlineOrderTypeConst.PICKUP_POSTPAYMENT;
        currentOrderId = onlineOrderHelper.createOnlineOrder(currentOrderType).getSolutionId();
    }

    @Test(description = "C23425906 Get Timeslot for Several Products", priority = 1)
    @TmsLink("1874")
    public void testGetTimeslotSeveralProducts() {
        Response<TimeslotResponseData> response = orderClient.getTimeslots(currentOrderId);
        assertTimeslotResult(response);
        timeslotData = paoHelper.getLatestTimeslot(response);
    }

    @Test(description = "C23426763 Postpayment: Update Timeslot", dependsOnMethods = {
            "testGetTimeslotSeveralProducts"}, priority = 2)
    @TmsLink("1876")
    public void testUpdateTimeslotPostpayment() {
        Response<?> response = orderClient.updateTimeslot(currentOrderId, timeslotData);
        assertTimeslotUpdateResult(response);
    }

    @Test(description = "C23426797 Postpayment: Update Timeslot for PAID", dependsOnMethods = {
            "testGetTimeslotSeveralProducts"}, priority = 3)
    @TmsLink("1877")
    public void testUpdateTimeslotPostpaymentPaid() {
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.updateTimeslot(currentOrderId, timeslotData);
        assertTimeslotUpdateResult(response);
    }

    @Test(description = "C23425907 Get Timeslot for One Product", priority = 4)
    @TmsLink("1875")
    public void testGetTimeslotOneProduct() {
        makeDimensionalOrder();
        Response<TimeslotResponseData> response = orderClient.getTimeslots(currentOrderId);
        assertTimeslotResult(response);
        timeslotData = paoHelper.getLatestTimeslot(response);
    }

    @Test(description = "C23426764 PrePayment: Update Timeslot", dependsOnMethods = {
            "testGetTimeslotOneProduct"}, priority = 6)
    @TmsLink("1878")
    public void testUpdateTimeslotPrepayment() {
        Response<?> response = orderClient.updateTimeslot(currentOrderId, timeslotData);
        assertTimeslotUpdateResult(response);
    }

    @Test(description = "C23426798 PrePayment: Update Timeslot for PAID", dependsOnMethods = {
            "testGetTimeslotOneProduct"}, priority = 7)
    @TmsLink("1879")
    public void testUpdateTimeslotPrepaymentPaid() {
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.updateTimeslot(currentOrderId, timeslotData);
        assertTimeslotUpdateResult(response);
    }

    private void makeDimensionalOrder() {
        currentOrderType = OnlineOrderTypeConst.PICKUP_PREPAYMENT;
        currentOrderId = onlineOrderHelper.createDimensionalOnlineOrder(currentOrderType)
                .getSolutionId();
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
                    timeslotData.getDate(), not(emptyOrNullString()));
        }
    }

    @Step("Update Timeslot results verification")
    public void assertTimeslotUpdateResult(Response<?> response) {
        assertThat("Request to Timeslot Update has Failed.", response, successful());
        OrderData orderData = orderClient.getOnlineOrder(currentOrderId).asJson();
        assertThat("Pickup Date was NOT updated",
                getDateAsLocalDateTime(orderData.getGiveAway().getDate())
                        .equals(getDateAsLocalDateTime(timeslotData.getDate())));
    }

    private Instant getDateAsLocalDateTime(String date) {
        Instant instant;
        try {
            instant = ZonedDateTime
                    .parse(date, DateTimeFormatter.ISO_INSTANT).toInstant();
        } catch (Exception ex) {
            instant = ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toInstant();
        }
        return instant;
    }
}