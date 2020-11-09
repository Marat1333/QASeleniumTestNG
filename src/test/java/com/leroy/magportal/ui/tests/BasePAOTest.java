package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.common_mashups.clients.CustomerClient;
import com.leroy.common_mashups.data.customer.CustomerData;
import com.leroy.common_mashups.data.customer.CustomerResponseBodyData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public abstract class BasePAOTest extends WebBaseSteps {

    // Test groups
    protected final static String NEED_ACCESS_TOKEN_GROUP = "need_access_token";
    protected final static String NEED_PRODUCTS_GROUP = "need_products";

    @Inject
    SearchProductHelper searchProductHelper;

    protected List<ProductItemData> productList;

    protected String customerPhone = "1111111111";

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    protected void findProducts() {
        productList = apiClientProvider.getProducts(3);
    }

    @BeforeMethod
    protected void setUserSessionDataByGroup(Method method) {
        List<String> groups = Arrays.asList(method.getAnnotation(Test.class).groups());
        UserSessionData userSessionData = getUserSessionData();
        if (groups.contains(NEED_ACCESS_TOKEN_GROUP) || isNeedAccessToken()) {
            userSessionData.setAccessToken(getAccessToken());
        }
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

    @Step("Ищем ЛМ код для продукта с признаком AVS")
    protected String getAnyLmCodeProductWithAvs(Boolean hasAvailableStock) {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(true);
        filtersData.setHasAvailableStock(hasAvailableStock);
        return searchProductHelper.getProducts(1, filtersData).get(0).getLmCode();
    }

    protected String getAnyLmCodeProductWithAvs() {
        return getAnyLmCodeProductWithAvs(null);
    }

    @Step("Ищем ЛМ код для продукта с опцией TopEM")
    protected String getAnyLmCodeProductWithTopEM(Boolean hasAvailableStock) {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setTopEM(true);
        filtersData.setAvs(false);
        filtersData.setHasAvailableStock(hasAvailableStock);
        getUserSessionData().setUserDepartmentId("15");
        return apiClientProvider.getProducts(1, filtersData).get(0).getLmCode();
    }

    protected String getAnyLmCodeProductWithTopEM() {
        return getAnyLmCodeProductWithTopEM(null);
    }

}
