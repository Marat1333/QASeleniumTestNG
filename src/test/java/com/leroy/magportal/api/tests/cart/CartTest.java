package com.leroy.magportal.api.tests.cart;

import static com.leroy.constants.sales.DiscountConst.TYPE_NEW_PRICE;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountReasonData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CartTest extends BaseMagPortalApiTest {

    private CartClient cartClient;

    private CartData cartData;
    private List<ProductItemData> products;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        cartClient = apiClientProvider.getCartClient();
        products = apiClientProvider.getProducts(3);
    }

    @AfterMethod
    private void cartAfterMethod(ITestResult result) {
        if (result.getStatus() != TestResult.SUCCESS) {
            cartData = null;
        }
    }

    private List<CartProductOrderData> getTestProductData(int productCount) {
        List<CartProductOrderData> cartProducts = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(products.get(0));
            productOrderData.setQuantity((double) new Random().nextInt(6) + 1);
            cartProducts.add(productOrderData);
        }
        return cartProducts;
    }

    @Step("Pre-condition: Создаем корзину")
    private void initPreConditionCartData(int productCount) {
        Response<CartData> resp = cartClient.sendRequestCreate(
                getTestProductData(productCount));
        isResponseOk(resp);
        cartData = resp.asJson();
    }

    @Test(description = "C23411545 Create with One Product")
    public void testCreateCart() {
        // Prepare request data
        List<CartProductOrderData> cartProductDataList = getTestProductData(1);

        // Step 1
        step("POST cart");
        Response<CartData> response = cartClient.sendRequestCreate(
                cartProductDataList);
        // Check Create
        cartData = cartClient.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        cartClient.assertThatResponseContainsAddedProducts(response,
                cartProductDataList);

        // Step 2
        step("GET cart");
        Response<CartData> respGet = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(respGet, cartData);
    }

    @Test(description = "C23194966 Cart - Add Discount")
    public void testCartDiscount() {
        if (cartData == null) {
            initPreConditionCartData(1);
        }
        CartProductOrderData putCartProductOrderData = cartData.getProducts().get(0);

        // Step 1
        step("Добавить скидку для товара в корзине");
        CartDiscountData discountData = new CartDiscountData();
        discountData.setType(TYPE_NEW_PRICE);
        discountData.setTypeValue(putCartProductOrderData.getPrice() - 1);
        discountData.setReason(
                new CartDiscountReasonData(DiscountConst.Reasons.PRODUCT_SAMPLE.getId()));
        putCartProductOrderData.setDiscount(discountData);
        Response<CartData> resp = cartClient
                .addDiscount(cartData.getCartId(), cartData.getDocumentVersion(),
                        putCartProductOrderData);
        cartClient.assertThatDiscountAdded(resp, cartData);
        cartData.increaseDocumentVersion();
        discountData.setActor(getUserSessionData().getUserLdap());

        // Step 2
        step("GET cart");
        Response<CartData> respGet = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(respGet, cartData);
    }

    @Test(description = "C23411546 Update Cart - Add product")
    public void testUpdateCartAddProduct() {
        if (cartData == null) {
            initPreConditionCartData(1);
        }

        // Step 1
        step("PUT Cart - Обновляем корзину");
        CartProductOrderData newProductData = new CartProductOrderData(products.get(1));
        newProductData.setQuantity(1.0);
        Response<CartData> resp = cartClient.addProduct(
                cartData.getCartId(), cartData.getDocumentVersion(), newProductData);
        cartData.increaseDocumentVersion();
        cartData.addProduct(newProductData);
        cartData = cartClient
                .assertThatResponseMatches(resp, CartClient.RequestType.UPDATE, cartData);

        // Step 2
        step("GET Cart");
        Response<CartData> respGet = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(respGet, cartData);
    }

    @Test(description = "C23411548 Update Cart - Add Customer")
    public void testUpdateCartAddCustomer() {
        if (cartData == null) {
            initPreConditionCartData(1);
        }

        // Step 1
        step("PUT Cart - Обновляем корзину");
        // TODO
        /*Response<CartData> resp = cartClient.addProduct(
                cartData.getCartId(), cartData.getDocumentVersion(), newProductData);
        cartData.increaseDocumentVersion();
        cartData.addProduct(newProductData);
        cartData = cartClient.assertThatResponseMatches(resp, CartClient.RequestType.UPDATE, cartData);

        // Step 2
        step("GET Cart");
        Response<CartData> respGet = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(respGet, cartData);*/
    }

    @Test(description = "C23411547 Remove 1 product from 2 from the Cart")
    public void testCartItems() {
        if (cartData == null) {
            initPreConditionCartData(2);
        }
        int removeProductIndex = 1;
        String removeLineId = cartData.getProducts().get(removeProductIndex).getLineId();

        // Step 1
        step("Удаляем 1 товар из корзины");
        Response<CartData> resp = cartClient.removeItems(
                cartData.getCartId(), cartData.getDocumentVersion(), removeLineId);
        cartData.increaseDocumentVersion();
        cartData.getProducts().remove(removeProductIndex);
        cartClient.assertThatResponseMatches(resp, cartData);

        // Step 2
        step("GET Cart");
        Response<CartData> respGet = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(respGet, cartData);
    }

    @Test(description = "C23411549 Change status to DELETED")
    public void testDeleteCart() {
        if (cartData == null) {
            initPreConditionCartData(1);
        }

        // Step 1
        step("Изменяем статус на DELETED");
        Response<JsonNode> response = cartClient.sendRequestDelete(cartData.getCartId(),
                cartData.getDocumentVersion());
        cartClient.assertThatResponseResultIsOk(response);

        // Step 2
        step("GET Cart");
        Response<CartData> getResponse = cartClient.sendRequestGet(cartData.getCartId());
        cartData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.increaseDocumentVersion();
        cartClient.assertThatResponseMatches(getResponse, cartData);
    }

}
