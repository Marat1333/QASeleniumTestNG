package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponseData;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocApiTest extends BaseProjectTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    @Inject
    private AuthClient authClient;


    private String productLmCode;
    private String serviceLmCode;
    private SalesDocumentResponseData salesDocument;


    //TODO Вынести в какой-нибудь helper:

    // Получить ЛМ код для услуги
    protected String getAnyLmCodeOfService() {
        return "49055102";
    }


    // Получить ЛМ код для обычного продукта без специфичных опций
    protected List<String> getAnyLmCodesProductWithoutSpecificOptions(SessionData sessionData,
                                                                      int necessaryCount, Boolean hasAvailableStock) {
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(sessionData.getUserShopId())
                .setTopEM(false)
                .setHasAvailableStock(hasAvailableStock);
        List<ProductItemData> items = magMobileClient.get().searchProductsBy(params).asJson().getItems();
        List<String> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (item.getAvsDate() == null && !Arrays.asList(badLmCodes).contains(item.getLmCode())) {
                if (necessaryCount > i)
                    resultList.add(item.getLmCode());
                else
                    break;
                i++;
            }
        }
        return resultList;
    }

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS));
        productLmCode = getAnyLmCodesProductWithoutSpecificOptions(
                sessionData, 1, false).get(0);
        serviceLmCode = getAnyLmCodeOfService();
    }

    @Test(description = "C3232445 SalesDoc add product")
    public void testSalesDocAddProduct() {
        ProductOrderData productOrderData = new ProductOrderData();
        productOrderData.setLmCode(productLmCode);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);
        Response<SalesDocumentResponseData> resp = magMobileClient.get().createSalesDocProducts(sessionData,
                new ProductOrderDataList(Collections.singletonList(productOrderData)));
        assertThat(resp.asString(), resp.getStatusCode(), equalTo(StatusCodes.ST_200_OK));
        salesDocument = resp.asJson();
        assertThat("docId field", salesDocument.getDocId(), not(isEmptyOrNullString()));
        assertThat("fullDocId field", salesDocument.getFullDocId(),
                allOf(not(isEmptyOrNullString()), endsWith(salesDocument.getDocId())));
        assertThat("Document Status", salesDocument.getSalesDocStatus(),
                equalTo(SalesDocumentsConst.States.DRAFT.getApiVal()));
    }

    @Test(description = "C3232448 SalesDoc product GET")
    public void testSalesDocProductGET() {
        if (salesDocument == null)
            throw new IllegalArgumentException("No information about fullDocId");
        Response<SalesDocumentResponseData> resp = magMobileClient.get()
                .getSalesDocProductsByFullDocId(salesDocument.getFullDocId());
        SalesDocumentResponseData data = resp.asJson();
        assertThatResponseIsOK(resp);
        assertThat("FullDocId", data.getFullDocId(), equalTo(salesDocument.getFullDocId()));
        assertThat("DocId", data.getDocId(), equalTo(salesDocument.getDocId()));
        assertThat("Document status", data.getSalesDocStatus(), equalTo(salesDocument.getSalesDocStatus()));
        assertThat("products", data.getProducts(), hasSize(1));
        assertThat("Product #1", data.getProducts().get(0).getLmCode(), equalTo(productLmCode));
    }

}
