package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import com.leroy.common_mashups.helpers.CustomerHelper;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import io.qameta.allure.Step;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public abstract class BasePAOTest extends WebBaseSteps {

    // Test groups
    protected final static String NEED_ACCESS_TOKEN_GROUP = "need_access_token";
    protected final static String NEED_PRODUCTS_GROUP = "need_products";

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private CustomerHelper customerHelper;

    protected List<ProductData> productList;

    protected String customerPhone = "1111111111";

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    protected void findProducts() {
        productList = searchProductHelper.getProducts(3);
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
        CustomerData customerData = customerHelper.createCustomer();

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
        return searchProductHelper.getProducts(1, filtersData).get(0).getLmCode();
    }

    protected String getAnyLmCodeProductWithTopEM() {
        return getAnyLmCodeProductWithTopEM(null);
    }

    protected List<CartProductOrderData> makeCartProductsList(int productCount, Double quantity) {
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(productList.get(i));
            productOrderData.setQuantity(quantity);
            productOrderDataList.add(productOrderData);
        }
        return productOrderDataList;
    }

}
