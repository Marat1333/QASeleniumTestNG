package com.leroy.magmobile.ui.tests.sales;

import static com.leroy.constants.sales.DiscountConst.TYPE_NEW_PRICE;
import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Smoke;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountReasonData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocumentPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep1Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep2Page;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep3Page;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.utils.RandomUtil;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SalesBaseTest extends AppBaseSteps {

    @Inject
    private PAOHelper paoHelper;
    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private SalesDocSearchClient salesDocSearchClient;
    @Inject
    private CartClient cartClient;
    @Inject
    private OrderClient orderClient;

    // ?????????? ?????????? ?? ???????????? ??????????????:
    @Step("Pre-condition: ???????????? ???????? ?? ???????????? ???????????? ??????????????")
    protected void startFromScreenWithEmptyCart() throws Exception {
        if (!Cart35Page.isThisPage()) {
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
            modalPage.clickBasketMenuItem();
        }
    }

    protected void startFromScreenWithCreatedCart() throws Exception {
        startFromScreenWithCreatedCart(null, false);
    }

    protected void startFromScreenWithCreatedCart(boolean hasDiscount) throws Exception {
        startFromScreenWithCreatedCart(null, hasDiscount);
    }

    @Step("Pre-condition: ???????????????? ??????????????")
    protected void startFromScreenWithCreatedCart(List<String> lmCodes, boolean hasDiscount) throws Exception {
        if (!Cart35Page.isThisPage()) {
            String cartDocNumber = createDraftCart(lmCodes, hasDiscount);
            SalesDocumentsPage salesDocumentsPage;
            boolean isStartFromScratch = isStartFromScratch();
            if (isStartFromScratch) {
                MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                        MainSalesDocumentsPage.class);
                salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            } else {
                salesDocumentsPage = new SalesDocumentsPage();
            }
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocNumber, !isStartFromScratch);
        }
    }

    @Step("Pre-condition: ???????????????? ??????????????")
    protected void startFromScreenWithCreatedCart(List<CartProductOrderData> productDataList) throws Exception {
        if (!Cart35Page.isThisPage()) {
            Response<CartData> response = cartClient.createCartRequest(productDataList);
            if (!response.isSuccessful())
                response = cartClient.createCartRequest(productDataList);
            CartData cartData = cartClient.assertThatIsCreatedAndGetData(response, true);
            String cartDocNumber = cartData.getCartId();
            SalesDocumentsPage salesDocumentsPage;
            boolean isStartFromScratch = isStartFromScratch();
            if (isStartFromScratch) {
                MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                        MainSalesDocumentsPage.class);
                salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            } else {
                salesDocumentsPage = new SalesDocumentsPage();
            }
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocNumber, !isStartFromScratch);
        }
    }

    // ?????????? ??????????????

    // ???????????????? ???? ?????? ?????? ????????????
    protected String getAnyLmCodeOfService() {
        return EnvConstants.SERVICE_1_LM_CODE;
    }

    // ???????????????? ???? ?????? ?????? ???????????????? ???????????????? ?????? ?????????????????????? ??????????
    protected List<String> getAnyLmCodesProductWithoutSpecificOptions(
            int necessaryCount) {
        return searchProductHelper.getProducts(necessaryCount, false, false)
                .stream().map(ProductData::getLmCode).collect(Collectors.toList());
    }

    protected String getAnyLmCodeProductWithoutSpecificOptions() {
        return getAnyLmCodesProductWithoutSpecificOptions(1).get(0);
    }

    @Step("???????? ???? ?????? ?????? ???????????????? ?? ?????????????????? AVS")
    protected String getAnyLmCodeProductWithAvs(Boolean hasAvailableStock) {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(true);
        filtersData.setHasAvailableStock(hasAvailableStock);
        return searchProductHelper.getProducts(1, filtersData).get(0).getLmCode();
    }

    protected String getAnyLmCodeProductWithAvs() {
        return getAnyLmCodeProductWithAvs(null);
    }

    @Step("???????? ???? ?????? ?????? ???????????????? ?? ???????????? TopEM")
    protected String getAnyLmCodeProductWithTopEM(Boolean hasAvailableStock) {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setTopEM(true);
        filtersData.setAvs(false);
        filtersData.setHasAvailableStock(hasAvailableStock);
        String lmCode = searchProductHelper.getProducts(1, filtersData).get(0).getLmCode();
        return lmCode;
    }

    protected String getAnyLmCodeProductWithTopEM() {
        return getAnyLmCodeProductWithTopEM(null);
    }

    // ???????????????? ???? ?????? ?????? ????????????????, ???????????????????? ?????? ???????????? ?? RM
    protected String getAnyLmCodeProductIsAvailableForWithdrawalFromRM() {
        return "15163427";
    }

    // ?????????? ?????????????????? ?????? ???????????????? ?????????????? ?? ?????????????????????? ????????????????:
    @Step("???????? ???????????????????? ???????????????? ?????? ???????????????? ?????????????? ?? ?????????????????????? ????????????????")
    protected List<CartProductOrderData> findProductsForSeveralOrdersInCart() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(false);
        filtersData.setTopEM(false);
        filtersData.setHasAvailableStock(true);
        List<ProductData> productIDataList = searchProductHelper.getProducts(2, filtersData);
        CartProductOrderData productWithNegativeBalance = new CartProductOrderData(
                productIDataList.get(0));
        productWithNegativeBalance.setQuantity(productIDataList.get(0).getAvailableStock() + 10.0);
        CartProductOrderData productWithPositiveBalance = new CartProductOrderData(
                productIDataList.get(1));
        productWithPositiveBalance.setQuantity(1.0);

        return Arrays.asList(productWithNegativeBalance, productWithPositiveBalance);
    }

    protected String getValidPinCode(boolean isPickup) {
        int tryCount = 10;
        for (int i = 0; i < tryCount; i++) {
            String generatedPinCode = RandomUtil.randomPinCode(isPickup);
            SalesDocumentListResponse salesDocumentsResponse = salesDocSearchClient
                    .getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
                    .asJson();
            if (salesDocumentsResponse.getTotalCount() == 0) {
                Log.info("API: ???? ?????????????? ???? ???????????? ?????????????????? ?? PIN ??????????: " + generatedPinCode);
                return generatedPinCode;
            }
            List<SalesDocumentResponseData> salesDocs = salesDocumentsResponse.getSalesDocuments();
            if (!generatedPinCode.equals(salesDocs.get(0).getPinCode())) {
                return generatedPinCode;
            }
        }
        throw new RuntimeException("???? ???? ???????????? ???? " + tryCount + " ?????????????? ?????????????????? ???????????????????????????????? PIN ??????");
    }

    // CREATING PRE-CONDITIONS:

    protected String createDraftCart(int productCount) {
        return createDraftCart(productCount, false);
    }

    protected String createDraftCart(int productCount, boolean hasDiscount) {
        return createDraftCart(null, productCount, hasDiscount);
    }

    protected String createDraftCart(List<String> lmCodes, boolean hasDiscount) {
        return createDraftCart(lmCodes, 1, hasDiscount);
    }

    private String createDraftCart(List<String> lmCodes, int productCount, boolean hasDiscount) {
        if (lmCodes == null)
            lmCodes = getAnyLmCodesProductWithoutSpecificOptions(productCount);
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (String lmCode : lmCodes) {
            CartProductOrderData productOrderData = new CartProductOrderData();
            productOrderData.setLmCode(lmCode);
            productOrderData.setQuantity(1.0);
            productOrderDataList.add(productOrderData);
        }
        Response<CartData> cartDataResponse = cartClient.createCartRequest(productOrderDataList);
        if (!cartDataResponse.isSuccessful()) {
            getUserSessionData().setAccessToken(getAccessToken());
            cartDataResponse = cartClient.createCartRequest(productOrderDataList);
        }
        assertThat(cartDataResponse, successful());
        CartData cartData = cartDataResponse.asJson();
        if (hasDiscount) {
            productOrderDataList = cartData.getProducts();
            for (CartProductOrderData putProduct : productOrderDataList) {
                CartDiscountData discountData = new CartDiscountData();
                discountData.setType(TYPE_NEW_PRICE);
                discountData.setTypeValue(putProduct.getPrice() - 1);
                discountData.setReason(new CartDiscountReasonData(DiscountConst.Reasons.PRODUCT_SAMPLE.getId()));
                putProduct.setDiscount(discountData);
            }
            Response<CartData> respDiscount = cartClient.addDiscount(
                    cartData.getCartId(), cartData.getDocumentVersion(), productOrderDataList);
            assertThat(respDiscount, successful());
        }
        return cartData.getFullDocId();
    }

    protected String createConfirmedOrder(List<String> lmCodes, List<CartProductOrderData> productDataList) throws Exception {
        // ???????????????? ??????????????
        List<CartProductOrderData> productOrderDataList = productDataList == null ? new ArrayList<>() : productDataList;
        if (productDataList == null) {
            if (lmCodes == null)
                lmCodes = searchProductHelper.getProductLmCodes(1);
            for (String lmCode : lmCodes) {
                CartProductOrderData productOrderData = new CartProductOrderData();
                productOrderData.setLmCode(lmCode);
                productOrderData.setQuantity(2.0);
                productOrderDataList.add(productOrderData);
            }
        }
        return paoHelper.createConfirmedPickupOrder(productOrderDataList, false).getOrderId();
    }

    protected void cancelOrder(String orderId, String expectedStatusBefore) throws Exception {
        orderClient.waitUntilOrderCanBeCancelled(orderId);
        Response<JsonNode> r = orderClient.cancelOrder(orderId);
        anAssert().isTrue(r.isSuccessful(),
                "???? ???????????? ?????????????? ?????????? ???" + orderId + ". ????????????: " + r.toString());
        orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderId,
                SalesDocumentsConst.States.CANCELLED.getApiVal());
    }

    protected void cancelOrder(String orderId) throws Exception {
        cancelOrder(orderId, SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
    }

    // Product Types
    protected enum ProductTypes {
        NORMAL, AVS, TOP_EM;
    }

    // TESTS

    @Smoke
    @Test(description = "C3201029 ???????????????? ?????????????????? ??????????????", groups = OLD_SHOP_GROUP)
    @AllureId("12777")
    public void testCreateDocumentSales() throws Exception {
        // Step #1
        step("???? ?????????????? ???????????? ???????????????? ???????????? ?????????????????? ??????????????");
        MainSalesDocumentsPage salesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);
        salesDocumentsPage.verifyRequiredElements();

        // Step #2
        step("?????????????? '?????????????? ???????????????? ??????????????'");
        SearchProductPage searchProductPage = salesDocumentsPage.clickCreateSalesDocumentButton();
        searchProductPage.verifyRequiredElements();

        // Step #3
        step("?????????????? ???? ????????-???????????????? ???????????? 16410291");
        searchProductPage.searchProductAndSelect("16410291");
        AddProductPage addProductPage = new AddProductPage()
                .verifyRequiredElements();

        // Step #4
        step("?????????????? ???? ???????? ????????????????????");
        addProductPage.clickEditQuantityField()
                .shouldKeyboardVisible();
        addProductPage.shouldEditQuantityFieldIs("1,00")
                .shouldTotalPriceIs(addProductPage.getPrice());

        // Step #5
        step("?????????????? ???????????????? 20,5 ???????????????????? ????????????");
        Double expectedTotalPrice = addProductPage.getPrice() * 20.5;
        addProductPage.enterQuantityOfProduct("20,5")
                .shouldTotalPriceIs(expectedTotalPrice);

        // Step #6
        step("?????????????? ???????????? ????????????????");
        CartStep1Page basketStep1Page = addProductPage.clickAddButton()
                .verifyRequiredElements();
        basketStep1Page.shouldDocumentTypeIs(CartPage.Constants.DRAFT_DOCUMENT_TYPE);
        String documentNumber = basketStep1Page.getDocumentNumber();

        // Step #7
        step("?????????????? ?????????? ?? ????????????????????");
        CartStep2Page basketStep2Page = basketStep1Page.clickNextParametersButton()
                .verifyRequiredElements()
                .shouldFieldsHaveDefaultValues();

        // Step #8
        step("?????????????? ???????????? ?????????????? ???????????????? ??????????????");
        CartStep3Page basketStep3Page = basketStep2Page.clickCreateSalesDocumentButton()
                .verifyRequiredElements();
        basketStep3Page.shouldKeyboardVisible();

        // Step #9
        step("?????????????? 5 ???????? PIN-????????");
        String testPinCode = getValidPinCode(true);
        basketStep3Page.enterPinCode(testPinCode)
                .shouldPinCodeFieldIs(testPinCode)
                .shouldSubmitButtonIsActive();

        // Step #10
        step("?????????????? ???????????? ??????????????????????");
        SubmittedSalesDocumentPage submittedSalesDocumentPage = basketStep3Page.clickSubmitButton()
                .verifyRequiredElements()
                .shouldPinCodeIs(testPinCode)
                .shouldDocumentNumberIs(documentNumber);

        // Step #11
        step("?????????????? ???????????? ?????????????? ?? ???????????? ????????????????????");
        ShortSalesDocumentData expectedSalesDocument = new ShortSalesDocumentData();
        expectedSalesDocument.setDocumentTotalPrice(expectedTotalPrice);
        expectedSalesDocument.setPin(testPinCode);
        expectedSalesDocument.setDocumentState(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        expectedSalesDocument.setTitle("???? ?????????????????? ????????");
        expectedSalesDocument.setNumber(documentNumber);
        submittedSalesDocumentPage.clickSubmitButton()
                .shouldSalesDocumentIsPresentAndDataMatches(expectedSalesDocument);
    }

}
