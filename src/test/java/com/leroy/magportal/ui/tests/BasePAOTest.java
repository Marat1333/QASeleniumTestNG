package com.leroy.magportal.ui.tests;

import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerResponseBodyData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeGroups;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

public abstract class BasePAOTest extends WebBaseSteps {

    // Test groups
    protected final static String NEED_ACCESS_TOKEN_GROUP = "need_access_token";
    protected final static String NEED_PRODUCTS_GROUP = "need_products";

    protected List<ProductItemData> productList;

    protected String customerPhone = "1111111111";

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    protected void findProducts() {
        productList = apiClientProvider.getProducts(3);
    }

    @BeforeGroups(NEED_ACCESS_TOKEN_GROUP)
    protected void addAccessTokenToSessionData() {
        getUserSessionData().setAccessToken(getAccessToken());
    }

    @Step("Создаем клиента через API")
    protected SimpleCustomerData createCustomerByApi() {
        CustomerClient customerClient = apiClientProvider.getCustomerClient();
        CustomerData customerData = new CustomerData();
        customerData.generateRandomValidRequiredData(true, true);
        Response<CustomerResponseBodyData> resp = customerClient.createCustomer(customerData);
        customerData = customerClient.assertThatIsCreatedAndGetData(resp, customerData);

        SimpleCustomerData uiCustomerData = new SimpleCustomerData();
        uiCustomerData.setName(customerData.getFirstName(), customerData.getLastName());
        uiCustomerData.setPhoneNumber(customerData.getMainPhoneFromCommunication());
        uiCustomerData.setEmail(customerData.getMainEmailFromCommunication());

        return uiCustomerData;
    }

}
