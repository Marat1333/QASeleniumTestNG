package com.leroy.magmobile.api;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.clients.*;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemData;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateCustomerData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.ui.models.search.FiltersData;
import lombok.Setter;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class ApiClientProvider {

    @Setter
    private SessionData sessionData;

    @Inject
    private Provider<CatalogSearchClient> catalogSearchClientProvider;
    @Inject
    private Provider<CartClient> cartClientProvider;
    @Inject
    private Provider<CustomerClient> customerClientProvider;
    @Inject
    private Provider<EstimateClient> estimateClientProvider;
    @Inject
    private Provider<OrderClient> orderClientProvider;
    @Inject
    private Provider<SalesDocProductClient> salesDocProductClientProvider;
    @Inject
    private Provider<SalesDocSearchClient> salesDocSearchClientProvider;
    @Inject
    private Provider<SalesDocTransferClient> salesDocTransferClientProvider;
    @Inject
    private Provider<SmsNotificationClient> smsNotificationClientProvider;

    private <J extends MagMobileClient> J getClient(Provider<J> provider) {
        MagMobileClient cl = provider.get();
        cl.setSessionData(sessionData);
        return (J) cl;
    }

    public CatalogSearchClient getCatalogSearchClient() {
        return getClient(catalogSearchClientProvider);
    }

    public CartClient getCartClient() {
        return getClient(cartClientProvider);
    }

    public CustomerClient getCustomerClient() {
        return getClient(customerClientProvider);
    }

    public EstimateClient getEstimateClient() {
        return getClient(estimateClientProvider);
    }

    public OrderClient getOrderClient() {
        return getClient(orderClientProvider);
    }

    public SalesDocProductClient getSalesDocProductClient() {
        return getClient(salesDocProductClientProvider);
    }

    public SalesDocSearchClient getSalesDocSearchClient() {
        return getClient(salesDocSearchClientProvider);
    }

    public SalesDocTransferClient getSalesDocTransferClient() {
        return getClient(salesDocTransferClientProvider);
    }

    public SmsNotificationClient getSmsNotificationClient() {
        return getClient(smsNotificationClientProvider);
    }


    /// --------------- HELP METHODS ------------------ //

    // SEARCH PRODUCTS

    public List<ServiceItemData> getServices(int necessaryCount) {
        GetCatalogServicesSearch params = new GetCatalogServicesSearch();
        params.setShopId(sessionData.getUserShopId())
                .setStartFrom(1)
                .setPageSize(necessaryCount); // TODO не работает. Почему?
        Response<ServiceItemDataList> resp = getCatalogSearchClient().searchServicesBy(params);
        List<ServiceItemData> services =
                resp.asJson().getItems().stream().limit(necessaryCount).collect(Collectors.toList());
        return services;
    }

    public List<ProductItemData> getProducts(int necessaryCount, FiltersData filtersData) {
        if (filtersData == null)
            filtersData = new FiltersData();
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(sessionData.getUserShopId())
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setTopEM(filtersData.isTopEM())
                .setHasAvailableStock(filtersData.isHasAvailableStock());
        Response<ProductItemDataList> resp = getCatalogSearchClient().searchProductsBy(params);
        assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode()))
                if (!filtersData.isAvs() && item.getAvsDate() == null ||
                        filtersData.isAvs() && item.getAvsDate() != null) {
                    resultList.add(item);
                    i++;
                }
            if (necessaryCount == i)
                break;
        }
        assertThat("Catalog search request:", resultList, hasSize(greaterThan(0)));
        return resultList;
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        return getProducts(necessaryCount, null);
    }

    public List<String> getProductLmCodes(int necessaryCount) {
        List<ProductItemData> productItemResponseList = getProducts(necessaryCount, null);
        return productItemResponseList.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
    }

    // SEARCH CUSTOMERS

    public CustomerData getAnyCustomer() {
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
        customerSearchFilters.setDiscriminantValue("+71111111111");
        Response<CustomerListData> resp = getCustomerClient().searchForCustomers(customerSearchFilters);
        assertThat("GetAnyCustomer Method. Response: " + resp.toString(), resp.isSuccessful());
        List<CustomerData> customers = resp.asJson().getItems();
        assertThat("GetAnyCustomer Method. Count of customers", customers,
                hasSize(greaterThan(0)));
        return customers.get(0);
    }

    // ESTIMATE

    public String createDraftEstimateAndGetCartId() {
        String lmCode = getProducts(1).get(0).getLmCode();
        CustomerData customerData = getAnyCustomer();
        EstimateProductOrderData productOrderData = new EstimateProductOrderData();
        productOrderData.setLmCode(lmCode);
        productOrderData.setQuantity(1.0);
        EstimateCustomerData estimateCustomerData = new EstimateCustomerData();
        estimateCustomerData.setCustomerNumber(customerData.getCustomerNumber());
        estimateCustomerData.setFirstName(customerData.getFirstName());
        estimateCustomerData.setLastName(customerData.getLastName());
        estimateCustomerData.setType("PERSON");
        estimateCustomerData.setRoles(Collections.singletonList("PAYER"));
        Response<EstimateData> estimateDataResponse = getEstimateClient().sendRequestCreate(
                estimateCustomerData, productOrderData);
        assertThat(estimateDataResponse, successful());
        return estimateDataResponse.asJson().getEstimateId();
    }

}
