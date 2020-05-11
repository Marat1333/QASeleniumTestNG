package com.leroy.magportal.api;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.core.SessionData;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.*;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import io.qameta.allure.Step;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
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

    private <J extends MagMobileClient> J getClient(Provider<J> provider) {
        J cl = provider.get();
        cl.setSessionData(sessionData);
        return cl;
    }

    public com.leroy.magmobile.api.clients.CatalogSearchClient getCatalogSearchClient() {
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
                .setShopId(sessionData.getUserShopId())
                .setDepartmentId(sessionData.getUserDepartmentId())
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
        List<ProductItemData> productItemResponseList = getProducts(necessaryCount, null);
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
}

