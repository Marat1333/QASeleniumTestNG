package com.leroy.magmobile.api.tests.catalog_search;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.clients.SmsNotificationClient;
import com.leroy.magmobile.api.data.catalog.NotificationCustomerData;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.SmsNotificationData;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SmsNotificationTest extends BaseProjectApiTest {

    @Inject
    private SmsNotificationClient smsNotificationClient;

    @Inject
    private CustomerClient customerClient;

    private SmsNotificationData smsNotificationData;

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @BeforeClass
    private void setUp() {
        smsNotificationClient.setSessionData(sessionData);
        customerClient.setSessionData(sessionData);
    }

    @Test(description = "Create Notification")
    public void testCreateNotification() {
        // Get test data:
        ProductItemData product = apiClientProvider.getProducts(1).get(0);
        CustomerData customerData = customerClient.getAnyCustomer();

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
        smsNotificationData.setShopId(Integer.valueOf(sessionData.getUserShopId()));
        smsNotificationData.setProduct(productOrderData);
        Response<SmsNotificationData> resp = smsNotificationClient.createNotification(smsNotificationData);
        smsNotificationClient.assertThatIsCreated(resp, smsNotificationData);
    }

    @Test(description = "Get Notification")
    public void testGetNotification() {
        Response<SmsNotificationData> resp = smsNotificationClient.getNotification(
                smsNotificationData.getProduct().getLmCode());
        smsNotificationClient.assertThatGetResponseMatches(resp, smsNotificationData);
    }
}
