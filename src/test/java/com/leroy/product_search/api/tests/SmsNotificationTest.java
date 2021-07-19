package com.leroy.product_search.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import com.leroy.common_mashups.helpers.CustomerHelper;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magmobile.api.clients.SmsNotificationClient;
import com.leroy.magmobile.api.data.notification.NotificationCustomerData;
import com.leroy.magmobile.api.data.notification.SmsNotificationData;
import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SmsNotificationTest extends BaseProjectApiTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private SmsNotificationClient smsNotificationClient;
    @Inject
    private CustomerHelper customerHelper;

    private SmsNotificationData smsNotificationData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "C3175887 SMS post", groups = "productSearch")
    @TmsLink("3457")
    public void testCreateNotification() {
        // Get test data:
        ProductData product = searchProductHelper.getProducts(1).get(0);
        CustomerData customerData = customerHelper.getAnyCustomer();

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

    @Test(description = "C3175886 SMS get", groups = "productSearch")
    @TmsLink("3456")
    public void testGetNotification() {
        Response<SmsNotificationData> resp = smsNotificationClient.getNotification(
                smsNotificationData.getProduct().getLmCode());
        smsNotificationClient.assertThatGetResponseMatches(resp, smsNotificationData);
    }
}
