package com.leroy.magportal.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.*;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.customer.CustomerResponseBodyData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateCustomerData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.api.clients.MagPortalCatalogProductClient;
import io.qameta.allure.Step;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.Assert;
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

    @Inject
    private Provider<com.leroy.magportal.api.clients.CatalogSearchClient> catalogSearchClientProvider;
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
    private Provider<TransferClient> salesDocTransferClientProvider;
    @Inject
    private Provider<SmsNotificationClient> smsNotificationClientProvider;
    @Inject
    private Provider<PrintPriceClient> printPriceClientProvider;
    @Inject
    private Provider<CatalogProductClient> catalogProductClientProvider;
    @Inject
    private Provider<PickingTaskClient> pickingTaskClientProvider;
    @Inject
    private Provider<ShopKladrClient> shopClientProvider;
    @Inject
    private Provider<LsAddressClient> lsAddressClientProvider;
    @Inject
    private Provider<RupturesClient> rupturesClientProvider;
    @Inject
    private Provider<SupportClient> supportClientProvider;
    @Inject
    private Provider<MagPortalCatalogProductClient> magPortalCatalogProductClientProvider;


    private UserSessionData userSessionData() {
        return ContextProvider.getContext().getUserSessionData();
    }

    private <J extends BaseMashupClient> J getClient(Provider<J> provider) {
        J cl = provider.get();
        cl.setUserSessionData(ContextProvider.getContext().getUserSessionData());
        return cl;
    }

    public com.leroy.magportal.api.clients.CatalogSearchClient getCatalogSearchClient() {
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

    public TransferClient getTransferClient() {
        return getClient(salesDocTransferClientProvider);
    }

    public SmsNotificationClient getSmsNotificationClient() {
        return getClient(smsNotificationClientProvider);
    }

    public PrintPriceClient getPrintPriceClient() {
        return getClient(printPriceClientProvider);
    }

    public CatalogProductClient getCatalogProductClient() {
        return getClient(catalogProductClientProvider);
    }

    public PickingTaskClient getPickingTaskClient() {
        return getClient(pickingTaskClientProvider);
    }

    public ShopKladrClient getShopClient() {
        return getClient(shopClientProvider);
    }

    public LsAddressClient getLsAddressClient() {
        return getClient(lsAddressClientProvider);
    }

    public RupturesClient getRupturesClient() {
        return getClient(rupturesClientProvider);
    }

    public SupportClient getSupportClient() {
        return getClient(supportClientProvider);
    }

    public MagPortalCatalogProductClient getMagPortalCatalogProductClientProvider() {
        return getClient(magPortalCatalogProductClientProvider);
    }

    // Help methods
    // TODO copy paste (as Mobile)
    @Step("Find {necessaryCount} products")
    public List<ProductItemData> getProducts(int necessaryCount, CatalogSearchFilter filtersData) {
        if (filtersData == null) {
            filtersData = new CatalogSearchFilter();
            filtersData.setAvs(false);
        }
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(userSessionData().getUserShopId())
                .setDepartmentId(userSessionData().getUserDepartmentId())
                .setTopEM(filtersData.getTopEM())
                .setPageSize(50)
                .setHasAvailableStock(filtersData.getHasAvailableStock());
        Response<ProductItemDataList> resp = getCatalogSearchClient().searchProductsBy(params);
        assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode()))
                if (filtersData.getAvs() == null || !filtersData.getAvs() && item.getAvsDate() == null ||
                        filtersData.getAvs() && item.getAvsDate() != null) {
                    if (filtersData.getHasAvailableStock() == null ||
                            (filtersData.getHasAvailableStock() && item.getAvailableStock() > 0 ||
                                    !filtersData.getHasAvailableStock() && item.getAvailableStock() <= 0)) {
                        resultList.add(item);
                        i++;
                    }
                }
            if (necessaryCount == i)
                break;
        }
        assertThat("Catalog search request:", resultList, hasSize(greaterThan(0)));
        return resultList;
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        return getProducts(necessaryCount, filter);
    }

    public List<String> getProductLmCodes(int necessaryCount) {
        List<ProductItemData> productItemResponseList = getProducts(necessaryCount);
        return productItemResponseList.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
    }

    @Step("Try to get nonexistent Pin Code")
    public String getValidPinCode() {
        int tryCount = 10;
        for (int i = 0; i < tryCount; i++) {
            String generatedPinCode;
            do {
                generatedPinCode = RandomStringUtils.randomNumeric(5);
            } while (generatedPinCode.startsWith("9"));
            SalesDocumentListResponse salesDocumentsResponse = getSalesDocSearchClient()
                    .getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
                    .asJson();
            if (salesDocumentsResponse.getTotalCount() == 0) {
                Log.info("API: None documents found with PIN: " + generatedPinCode);
                return generatedPinCode;
            }
            List<SalesDocumentResponseData> salesDocs = salesDocumentsResponse.getSalesDocuments();
            if (!generatedPinCode.equals(salesDocs.get(0).getPinCode())) {
                return generatedPinCode;
            }
        }
        throw new RuntimeException("Couldn't find valid pin code for " + tryCount + " trying");
    }

    // SEARCH CUSTOMERS

    @Step("Find any customer")
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

    @Step("Try to find phone number which is connected to no one customer")
    public String findUnusedPhoneNumber() {
        int attemptsCount = 10;

        CustomerClient customerClient = getCustomerClient();
        CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
        customerSearchFilters.setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
        customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);

        for (int i = 0; i < attemptsCount; i++) {
            String phoneNumber = "+7" + RandomStringUtils.randomNumeric(10);
            customerSearchFilters.setDiscriminantValue(phoneNumber);
            Response<CustomerListData> resp = customerClient.searchForCustomers(customerSearchFilters);
            if (resp.isSuccessful()) {
                if (resp.asJson().getItems().size() == 0)
                    return phoneNumber;
            } else {
                Log.error(resp.toString());
                return phoneNumber;
            }
        }
        Assert.fail("Couldn't find unused phone number for " + attemptsCount + " attempts");
        return null;
    }

    // ESTIMATE

    @Step("Создаем черновик Сметы через API")
    private String createDraftEstimateAndGetCartId(
            CustomerData newCustomerData, List<String> lmCodes, int productCount) {
        if (lmCodes == null)
            lmCodes = getProductLmCodes(productCount);
        CustomerData customerData;
        if (newCustomerData == null) {
            customerData = getAnyCustomer();
        } else {
            Response<CustomerResponseBodyData> respCustomer = getCustomerClient()
                    .createCustomer(newCustomerData);
            assertThat(respCustomer, successful());
            customerData = respCustomer.asJson().getEntity();
        }
        List<EstimateProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 1; i <= lmCodes.size(); i++) {
            EstimateProductOrderData productOrderData = new EstimateProductOrderData();
            productOrderData.setLmCode(lmCodes.get(i - 1));
            productOrderData.setQuantity((double) i);
            productOrderDataList.add(productOrderData);
        }
        EstimateCustomerData estimateCustomerData = new EstimateCustomerData();
        estimateCustomerData.setCustomerNumber(customerData.getCustomerNumber());
        estimateCustomerData.setFirstName(customerData.getFirstName());
        estimateCustomerData.setLastName(customerData.getLastName());
        estimateCustomerData.setType("PERSON");
        estimateCustomerData.setRoles(Collections.singletonList("PAYER"));
        Response<EstimateData> estimateDataResponse = getEstimateClient().sendRequestCreate(
                Collections.singletonList(estimateCustomerData), productOrderDataList);
        assertThat(estimateDataResponse, successful());
        return estimateDataResponse.asJson().getEstimateId();
    }

    public String createDraftEstimateAndGetCartId(List<String> lmCodes) {
        return createDraftEstimateAndGetCartId(null, lmCodes, 1);
    }

    public String createDraftEstimateAndGetCartId(
            CustomerData newCustomerData, int productCount) {
        return createDraftEstimateAndGetCartId(newCustomerData, null, productCount);
    }

    public String createDraftEstimateAndGetCartId(int productCount) {
        return createDraftEstimateAndGetCartId(null, null, productCount);
    }

    public String createDraftEstimateAndGetCartId() {
        return createDraftEstimateAndGetCartId(1);
    }

    @Step("Создаем подтвержденную Смету через API")
    public String createConfirmedEstimateAndGetCartId(List<String> lmCodes) {
        EstimateClient client = getEstimateClient();
        String cartId = createDraftEstimateAndGetCartId(lmCodes);
        Response<JsonNode> resp = client.confirm(cartId);
        client.assertThatResponseChangeStatusIsOk(resp);
        return cartId;
    }

    public String createConfirmedEstimateAndGetCartId() {
        return createConfirmedEstimateAndGetCartId(getProductLmCodes(1));
    }
}

