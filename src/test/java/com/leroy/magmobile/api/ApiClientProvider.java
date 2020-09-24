package com.leroy.magmobile.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.common_mashups.clients.CustomerClient;
import com.leroy.common_mashups.data.customer.CustomerData;
import com.leroy.common_mashups.data.customer.CustomerListData;
import com.leroy.common_mashups.data.customer.CustomerResponseBodyData;
import com.leroy.common_mashups.data.customer.CustomerSearchFilters;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.clients.*;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateCustomerData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

@Deprecated
public class ApiClientProvider {

    @Inject
    SearchProductHelper searchProductHelper;

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
    private Provider<SupplyPlanClient> supplyPlanClientProvider;

    protected UserSessionData userSessionData() {
        return ContextProvider.getContext().getUserSessionData();
    }

    private <J extends BaseMashupClient> J getClient(Provider<J> provider) {
        J cl = provider.get();
        cl.setUserSessionData(userSessionData());
        return cl;
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

    public ShopKladrClient getShopKladrClient() {
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

    public SupplyPlanClient getSupplyPlanClient() {
        return getClient(supplyPlanClientProvider);
    }

    /// --------------- HELP METHODS ------------------ //

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


    // ESTIMATE

    @Step("Создаем черновик Сметы через API")
    private String createDraftEstimateAndGetCartId(
            CustomerData newCustomerData, List<String> lmCodes, int productCount) {
        if (lmCodes == null)
            lmCodes = searchProductHelper.getProductLmCodes(productCount);
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

    public String createDraftEstimateAndGetCartId(CustomerData newCustomerData, List<String> lmCodes) {
        return createDraftEstimateAndGetCartId(newCustomerData, lmCodes, 1);
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
    public String createConfirmedEstimateAndGetCartId(CustomerData newCustomerData, List<String> lmCodes) {
        EstimateClient client = getEstimateClient();
        String cartId = createDraftEstimateAndGetCartId(newCustomerData, lmCodes);
        Response<JsonNode> resp = client.confirm(cartId);
        client.assertThatResponseChangeStatusIsOk(resp);
        return cartId;
    }

    public String createConfirmedEstimateAndGetCartId(List<String> lmCodes) {
        return createConfirmedEstimateAndGetCartId(null, lmCodes);
    }

}
