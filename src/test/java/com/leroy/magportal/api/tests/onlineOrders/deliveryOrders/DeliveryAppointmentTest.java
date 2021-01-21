package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.DeliveryCustomerData;
import com.leroy.magportal.api.data.onlineOrders.DeliveryData;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.data.onlineOrders.ShipToData;
import com.leroy.magportal.api.data.timeslot.AppointmentData;
import com.leroy.magportal.api.data.timeslot.AppointmentResponseData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class DeliveryAppointmentTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;
    private List<AppointmentData> appointmentDataList;
    private AppointmentData appointmentData;
    private DeliveryData deliveryData;


    @BeforeClass
    private void setUp() {
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;
        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();
    }

    @Test(description = "C23425904 Get Appointment for Several Products", priority = 1)
    public void testGetAppointmentSeveralProducts() {
        Response<AppointmentResponseData> response = orderClient.getAppointments(currentOrderId);
        assertAppointmentResult(response);
        appointmentDataList = response.asJson().getData().getAppointments();
    }

    @Test(description = "C23426801 Update Appointment", dependsOnMethods = {
            "testGetAppointmentSeveralProducts"}, priority = 2)
    public void testUpdateAppointment() {
        assignAppointment(true);
        Response<JsonNode> response = orderClient
                .updateAppointment(currentOrderId, appointmentData);
        assertAppointmentUpdateResult(response);
    }

    @Test(description = "C23426802 Update Appointment for PAID order", dependsOnMethods = {
            "testGetAppointmentSeveralProducts"}, priority = 3)
    public void testUpdateAppointmentPaid() {
        assignAppointment(false);
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<JsonNode> response = orderClient
                .updateAppointment(currentOrderId, appointmentData);
        assertAppointmentUpdateResult(response);
    }

    @Test(description = "C23425905 Get Appointment for One Product", priority = 4)
    public void testGetAppointmentOneProduct() {
        makeDimensionalOrder();
        Response<AppointmentResponseData> response = orderClient.getAppointments(currentOrderId);
        assertAppointmentResult(response);
        appointmentDataList = response.asJson().getData().getAppointments();
    }

    @Test(description = "C23426804 Update Delivery Data", dependsOnMethods = {
            "testGetAppointmentOneProduct"}, priority = 5)
    public void testUpdateDeliveryData() {
        makeDeliveryData("intercom", "entrance", "FullName", "89152537253");
        Response<?> response = orderClient.updateDeliveryData(currentOrderId, deliveryData);
        assertDeliveryDataUpdateResult(response);
    }

    @Test(description = "C23426803 Update Appointment and Delivery Data", dependsOnMethods = {
            "testGetAppointmentOneProduct"}, priority = 6)
    public void testUpdateDeliveryDataAndAppointment() {
        makeDeliveryData("intercom", null, "FullName", null);
        assignAppointment(false);
        Response<?> response = orderClient
                .updateDeliveryDataAndAppointment(currentOrderId, deliveryData, appointmentData);
        assertAppointmentUpdateResult(response);
        assertDeliveryDataUpdateResult(response);
    }

    @Test(description = "C23426805 Update Delivery Data for PAID order", dependsOnMethods = {
            "testGetAppointmentOneProduct"}, priority = 7)
    public void testUpdateDeliveryDataPaid() {
        makeDeliveryData(null, "1234", null, null);
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.updateDeliveryData(currentOrderId, deliveryData);
        assertDeliveryDataUpdateResult(response);
    }

    @Test(description = "C23426847 Update Appointment and Delivery Data for PAID order", dependsOnMethods = {
            "testGetAppointmentOneProduct"}, priority = 8)
    public void testUpdateDeliveryDataAndAppointmentPaid() {
        makeDeliveryData(null, null, null, "89152537253");
        assignAppointment(true);
        Response<?> response = orderClient
                .updateDeliveryDataAndAppointment(currentOrderId, deliveryData, appointmentData);
        assertAppointmentUpdateResult(response);
        assertDeliveryDataUpdateResult(response);
    }

    //TODO: Add cases with empty values
    private void makeDimensionalOrder() {
        currentOrderId = onlineOrderHelper.createDimensionalOnlineOrder(currentOrderType)
                .getSolutionId();
    }

    private void makeDeliveryData(String intercom, String entrance, String fullName, String phone) {
        deliveryData = new DeliveryData();
        ShipToData shipTo = new ShipToData();
        shipTo.setEntrance(entrance);
        shipTo.setIntercom(intercom);

        DeliveryCustomerData customerData = new DeliveryCustomerData();
        customerData.setFullName(fullName);
        customerData.setPhone(phone);

        deliveryData.setReceiver(customerData);
        deliveryData.setShipTo(shipTo);
    }

    private void assignAppointment(boolean isFirst) {
        try {
            if (isFirst) {
                appointmentData = appointmentDataList.get(0);
            } else {
                int i = appointmentDataList.size();
                appointmentData = appointmentDataList.get(i - 1);
            }
        } catch (Exception e) {
            assertThat("There are NO Appointments available", 1, is(2));
        }
    }

    //Verification
    @Step("GET Appointment results verification")
    public void assertAppointmentResult(Response<AppointmentResponseData> response) {
        assertThat("Request to Appointment has Failed.", response, successful());
        List<AppointmentData> responseData = (response.asJson().getData()).getAppointments();
        assertThat("There are NO Appointment Appointments", responseData.size(),
                greaterThanOrEqualTo(1));
        for (AppointmentData appointmentData : responseData) {
            assertThat("There are NO available Start date",
                    appointmentData.getStart(), not(emptyOrNullString()));
            assertThat("There are NO available END date",
                    appointmentData.getEnd(), not(emptyOrNullString()));
        }
    }

    @Step("Update Appointment results verification")
    public void assertAppointmentUpdateResult(Response<?> response) {
        assertThat("Request to Appointment Update has Failed.", response, successful());
        OnlineOrderData orderData = orderClient.getOnlineOrder(currentOrderId).asJson();
        assertThat("Delivery Date was NOT updated",
                orderData.getDeliveryData().getPlanDate(), equalTo(appointmentData.getEnd()));
        //TODO: Add interval verification
    }

    @Step("Update Delivery Data results verification")
    public void assertDeliveryDataUpdateResult(Response<?> response) {
        assertThat("Request to Appointment Update has Failed.", response, successful());
        DeliveryData updatedDeliveryData = orderClient.getOnlineOrder(currentOrderId).asJson()
                .getDeliveryData();
        if (deliveryData.getReceiver().getFullName() != null) {
            assertThat("Receiver Name was NOT updated",
                    updatedDeliveryData.getReceiver().getFullName(),
                    equalTo(deliveryData.getReceiver().getFullName()));
        } else {
            assertThat("Receiver Name was updated to NULL",
                    updatedDeliveryData.getReceiver().getFullName(), not(emptyOrNullString()));
        }

        if (deliveryData.getReceiver().getPhone() != null) {
            assertThat("Receiver Phone was NOT updated",
                    updatedDeliveryData.getReceiver().getPhone()
                            .equals(deliveryData.getReceiver().getPhone()));
        } else {
            assertThat("Receiver Phone was updated to NULL",
                    updatedDeliveryData.getReceiver().getPhone(), not(emptyOrNullString()));
        }

        if (deliveryData.getShipTo().getIntercom() != null) {
            assertThat("Intercom was NOT updated", updatedDeliveryData.getShipTo().getIntercom(),
                    equalTo(deliveryData.getShipTo().getIntercom()));
        } else {
            assertThat("Intercom was updated to NULL",
                    updatedDeliveryData.getShipTo().getIntercom(),
                    not(emptyOrNullString()));
        }

        if (deliveryData.getShipTo().getEntrance() != null) {
            assertThat("Entrance was NOT updated", updatedDeliveryData.getShipTo().getEntrance(),
                    equalTo(deliveryData.getShipTo().getEntrance()));
        } else {
            assertThat("Entrance was updated to NULL",
                    updatedDeliveryData.getShipTo().getEntrance(),
                    not(emptyOrNullString()));
        }
    }
}