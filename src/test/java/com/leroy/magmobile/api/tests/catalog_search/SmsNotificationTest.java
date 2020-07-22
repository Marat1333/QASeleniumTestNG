package com.leroy.magmobile.api.tests.catalog_search;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.SmsNotificationClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.notification.NotificationCustomerData;
import com.leroy.magmobile.api.data.notification.SmsNotificationData;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SmsNotificationTest extends BaseProjectApiTest {

    private SmsNotificationClient smsNotificationClient;

    @BeforeClass
    private void initClients() {
        smsNotificationClient = apiClientProvider.getSmsNotificationClient();
    }

    private SmsNotificationData smsNotificationData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "C3175887 SMS post")
    public void testCreateNotification() {
        // Get test data:
        ProductItemData product = apiClientProvider.getProducts(1).get(0);
        CustomerData customerData = apiClientProvider.getAnyCustomer();

        NotificationCustomerData notifyCustomerData = new NotificationCustomerData();
        notifyCustomerData.setCustomerNumber(customerData.getCustomerNumber());
        notifyCustomerData.setName(customerData.getFirstName());
        notifyCustomerData.setSurname(customerData.getLastName());
        notifyCustomerData.setPrimaryPhone(customerData.getMainPhoneFromCommunication());

        BaseProductOrderData productOrderData = new BaseProductOrderData();
        productOrderData.setLmCode(product.getLmCode());
        productOrderData.setTitle(product.getTitle());
        productOrderData.setQuantity(5.0);
        productOrderData.setPrice(2.0);

        smsNotificationData = new SmsNotificationData();
        smsNotificationData.setCustomer(notifyCustomerData);
        smsNotificationData.setShopId(Integer.valueOf(
                getUserSessionData().getUserShopId()));
        smsNotificationData.setProduct(productOrderData);
        Response<JsonNode> resp = smsNotificationClient.createNotification(smsNotificationData);
        smsNotificationClient.assertThatIsCreated(resp);
    }

    @Test(description = "C3175886 SMS get")
    public void testGetNotification() {
        Response<SmsNotificationData> resp = smsNotificationClient.getNotification(
                smsNotificationData.getProduct().getLmCode());
        smsNotificationClient.assertThatGetResponseMatches(resp, smsNotificationData);
    }
}
