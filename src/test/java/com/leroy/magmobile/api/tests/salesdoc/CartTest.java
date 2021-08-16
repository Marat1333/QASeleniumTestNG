package com.leroy.magmobile.api.tests.salesdoc;

import static com.leroy.constants.sales.DiscountConst.TYPE_NEW_PRICE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountReasonData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.qameta.allure.AllureId;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CartTest extends BaseProjectApiTest {

    @Inject
    private CartClient cartClient;
    @Inject
    private SearchProductHelper searchProductHelper;

    private CartData cartData;

    private List<ProductData> products;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        products = searchProductHelper.getProducts(3);
    }

    @AfterMethod
    private void cartAfterMethod(ITestResult result) {
        if (result.getStatus() != TestResult.SUCCESS)
            cartData = null;
    }

    private List<CartProductOrderData> getTestProductData() {
        CartProductOrderData productOrderData1 = new CartProductOrderData(products.get(0));
        productOrderData1.setQuantity((double) new Random().nextInt(6) + 1);
        CartProductOrderData productOrderData2 = new CartProductOrderData(products.get(1));
        productOrderData2.setQuantity((double) new Random().nextInt(6) + 1);
        return Arrays.asList(productOrderData1, productOrderData2);
    }

    private void initPreConditionCartData() {
        Response<CartData> resp = cartClient.createCartRequest(
                getTestProductData());
        isResponseOk(resp);
        cartData = resp.asJson();
    }

    @Test(description = "C22906656 Creating Cart with 2 products")
    @AllureId("12910")
    public void testCreateCart() {
        // Prepare request data
        List<CartProductOrderData> cartProductDataList = getTestProductData();

        // Create
        Response<CartData> response = cartClient.createCartRequest(
                cartProductDataList);
        // Check Create
        cartData = cartClient.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        cartClient.assertThatResponseContainsAddedProducts(response,
                cartProductDataList);
    }

    @Test(description = "C23194964 Cart - Confirm Quantity - happy path (with simple product - no AVS, no TOP EM)")
    @AllureId("12913")
    public void testCartConfirmQuantity() {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(false);
        ProductData product = searchProductHelper.getProducts(1, filter).get(0);
        CartProductOrderData cartProductOrderData = new CartProductOrderData(product);
        cartProductOrderData.setQuantity(product.getAvailableStock() + 1.0);
        step("Create Cart with product quantity greater than Available stock");
        Response<CartData> resp = cartClient.createCartRequest(cartProductOrderData);
        isResponseOk(resp);
        cartData = resp.asJson();
        assertThat("Product counts when Cart created", cartData.getProducts(), hasSize(1));
        cartProductOrderData = cartData.getProducts().get(0);

        step("Confirm quantity");
        cartProductOrderData.setStockAdditionBySalesman(
                cartProductOrderData.getQuantity() - cartProductOrderData.getAvailableStock());
        Response<CartData> confirmQuantityResp = cartClient.confirmQuantity(cartData.getCartId(), 1,
                cartProductOrderData);
        cartClient.assertThatQuantityIsConfirmed(confirmQuantityResp, cartData);
    }

    @Test(description = "C23194966 Cart - Add Discount")
    @AllureId("12915")
    public void testCartDiscount() {
        if (cartData == null)
            initPreConditionCartData();
        CartProductOrderData putCartProductOrderData = cartData.getProducts().get(0);

        CartDiscountData discountData = new CartDiscountData();
        discountData.setType(TYPE_NEW_PRICE);
        discountData.setTypeValue(putCartProductOrderData.getPrice() - 1);
        discountData.setReason(new CartDiscountReasonData(DiscountConst.Reasons.PRODUCT_SAMPLE.getId()));
        putCartProductOrderData.setDiscount(discountData);
        Response<CartData> resp = cartClient.addDiscount(cartData.getCartId(), cartData.getDocumentVersion(),
                putCartProductOrderData);
        cartClient.assertThatDiscountAdded(resp, cartData);
        cartData.increaseDocumentVersion();
        discountData.setActor(getUserSessionData().getUserLdap());
    }

    @Test(description = "C23194968 Lego_Cart_Consolidate_Products - happy path")
    @AllureId("12917")
    public void testCartConsolidateProducts() {
        step("Find products");
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

        step("Create Cart");
        Response<CartData> response = cartClient.createCartRequest(
                Arrays.asList(productWithNegativeBalance, productWithPositiveBalance));
        // Check Create
        cartData = cartClient.assertThatIsCreatedAndGetData(response);
        assertThat("Group count", cartData.getGroups(), hasSize(2));
        step("Consolidate Products");
        Response<JsonNode> consolidateResp = cartClient.consolidateProducts(
                cartData.getCartId(), cartData.getDocumentVersion(), cartData.getProducts().get(1).getLineId());
        cartClient.assertThatResponseResultIsOk(consolidateResp);

        step("Send get request and check data");
        Response<CartData> getResp = cartClient.getCartRequest(cartData.getCartId());
        cartClient.assertThatResponseMatches(getResp, cartData);
        assertThat("Group count", getResp.asJson().getGroups(), hasSize(1));
    }

    @Test(description = "C23194965 Lego_Cart_Items - Remove 1 product from 2 from the Cart")
    @AllureId("12914")
    public void testCartItems() {
        if (cartData == null)
            initPreConditionCartData();
        int removeProductIndex = 1;
        String removeLineId = cartData.getProducts().get(removeProductIndex).getLineId();
        Response<CartData> resp = cartClient.removeItems(
                cartData.getCartId(), cartData.getDocumentVersion(), removeLineId);
        cartData.increaseDocumentVersion();
        cartData.getProducts().remove(removeProductIndex);
        cartClient.assertThatResponseMatches(resp, cartData);
    }

    @Test(description = "C23194967 Update Cart - Add product")
    @AllureId("12916")
    public void testUpdateCart() {
        if (cartData == null)
            initPreConditionCartData();
        CartProductOrderData newProductData = new CartProductOrderData(products.get(2));
        newProductData.setQuantity(1.0);
        Response<CartData> resp = cartClient.addProduct(
                cartData.getCartId(), cartData.getDocumentVersion(), newProductData);
        cartData.increaseDocumentVersion();
        cartData.addProduct(newProductData);
        cartData = cartClient.assertThatResponseMatches(resp, CartClient.RequestType.UPDATE, cartData);
    }

    @Test(description = "C22906657 Get Cart info - Lego_Cart_Get")
    @AllureId("12911")
    public void testGetCart() {
        if (cartData == null)
            initPreConditionCartData();
        Response<CartData> getResp = cartClient.getCartRequest(cartData.getCartId());
        cartClient.assertThatResponseMatches(getResp, cartData);
    }

    @Test(description = "C22906658 Lego_CartChangeStatus - make status is DELETED")
    @AllureId("12912")
    public void testDeleteCart() {
        if (cartData == null)
            initPreConditionCartData();
        Response<JsonNode> response = cartClient.sendRequestDelete(cartData.getCartId(),
                cartData.getDocumentVersion());
        cartClient.assertThatResponseResultIsOk(response);

        Response<CartData> getResponse = cartClient.getCartRequest(cartData.getCartId());
        cartData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.increaseDocumentVersion();
        cartClient.assertThatResponseMatches(getResponse, cartData);
    }
}
