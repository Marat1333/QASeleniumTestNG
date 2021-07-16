package com.leroy.magportal.api.tests.offlineOrders;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.PaoClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;

import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SetPinCodeTest extends BaseMagPortalApiTest {

    @Inject
    private PAOHelper paoHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PaoClient paoClient;

    private String currentPinCode;
    private OnlineOrderData currentOrderData;
    private String currentOrderId;
    private List<CartProductOrderData> cartProducts;


    @BeforeClass
    private void setUp() {
        cartProducts = paoHelper.makeCartProducts(3);

        currentOrderId = paoHelper.createDraftOrder(cartProducts).getOrderId();
        currentOrderData = orderClient.getOnlineOrder(currentOrderId).asJson();
    }

    @BeforeMethod
    private void prepareTest() {
        if (currentOrderId == null) {
            setUp();
        }
    }

    @Test(description = "C23441744 Set Invalid PinCode", priority = 1)
    @TmsLink("2016")
    public void testSetInvalidPinCode() {
        currentPinCode = "1test";
        Response<?> response = paoClient
                .setPinCode(currentOrderData.getOrderId(), currentPinCode);
        assertThat("It's possible to set invalid PinCode.", !response.isSuccessful());
    }

    @Test(description = "C23441740 First Set PinCode for Pickup Order", priority = 2)
    @TmsLink("2012")
    public void testSetPinCodePickupFirst() {
        currentPinCode = paoHelper.getValidPinCode(currentOrderData.getDelivery());
        Response<?> response = paoClient
                .setPinCode(currentOrderData.getOrderId(), currentPinCode);
        assertPinCodeResult(response);
    }

    @Test(description = "C23441741 Second Set PinCode for Pickup Order", priority = 3)
    @TmsLink("2013")
    public void testSetPinCodePickupSecond() {
        currentPinCode = paoHelper.getValidPinCode(currentOrderData.getDelivery());
        Response<?> response = paoClient
                .setPinCode(currentOrderData.getOrderId(), currentPinCode);
        assertPinCodeResult(response);
    }

    @Test(description = "C23441742 Set PinCode Duplicate for Delivery Order", priority = 4)
    @TmsLink("2014")
    public void testSetPinCodeDeliveryDuplicate() {
        createOfflineDeliveryOrder();
        Response<?> response = paoClient
                .setPinCode(currentOrderData.getOrderId(), currentPinCode);
        assertThat("Request to set PinCode has Failed.", !response.isSuccessful());
    }

    @Test(description = "C23441743 Set PinCode for Delivery Order", priority = 5)
    @TmsLink("2015")
    public void testSetPinCodeDeliveryFirst() {
        currentPinCode = paoHelper.getValidPinCode(currentOrderData.getDelivery());
        Response<?> response = paoClient
                .setPinCode(currentOrderData.getOrderId(), currentPinCode);
        assertPinCodeResult(response);
    }

    private void createOfflineDeliveryOrder() {
        currentOrderId = paoHelper.createDraftOrder(cartProducts, true).getOrderId();
        currentOrderData = orderClient.getOnlineOrder(currentOrderId).asJson();
    }

    //Verification
    public void assertPinCodeResult(Response<?> response) {
        assertThat("Request to set PinCode has Failed.", response, successful());
        OnlineOrderData orderData = orderClient.getOnlineOrder(currentOrderData.getOrderId())
                .asJson();
        assertThat("PinCode was NOT updated.", orderData.getPinCode().equals(currentPinCode));
    }
}