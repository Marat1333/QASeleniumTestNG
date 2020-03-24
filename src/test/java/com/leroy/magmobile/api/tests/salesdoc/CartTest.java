package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartDiscountReasonData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.leroy.constants.sales.DiscountConst.TYPE_NEW_PRICE;

public class CartTest extends BaseProjectApiTest {

    @Inject
    private CartClient cartClient;

    private CatalogSearchClient searchClient;

    private CartData cartData;

    private List<ProductItemData> products;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        searchClient = getCatalogSearchClient();
        cartClient.setSessionData(sessionData);
        products = searchClient.getProducts(3);
    }

    @Test(description = "C22906656 Create Cart - Lego_Cart_Post")
    public void testCreateCart() {
        // Prepare request data
        CartProductOrderData productOrderData1 = new CartProductOrderData(products.get(0));
        productOrderData1.setQuantity((double) new Random().nextInt(6) + 1);
        CartProductOrderData productOrderData2 = new CartProductOrderData(products.get(1));
        productOrderData2.setQuantity((double) new Random().nextInt(6) + 1);

        // Create
        Response<CartData> response = cartClient.sendRequestCreate(
                Arrays.asList(productOrderData1, productOrderData2));
        // Check Create
        cartData = cartClient.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        cartClient.assertThatResponseContainsAddedProducts(response,
                Arrays.asList(productOrderData1, productOrderData2));
    }

    @Test(description = "Cart - Confirm Quantity")
    public void testCartConfirmQuantity() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        CartProductOrderData cartProductOrderData = cartData.getProducts().get(0);
        cartProductOrderData.setStockAdditionBySalesman(999);
        Response<CartData> confirmQuantityResp = cartClient.confirmQuantity(cartData.getCartId(), 1,
                cartProductOrderData);
        cartClient.assertThatQuantityIsConfirmed(confirmQuantityResp, cartData);
    }

    @Test(description = "Cart - Add Discount")
    public void testCartDiscount() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        CartProductOrderData putCartProductOrderData = cartData.getProducts().get(0);

        CartDiscountData discountData = new CartDiscountData();
        discountData.setType(TYPE_NEW_PRICE);
        discountData.setTypeValue(189);
        discountData.setReason(new CartDiscountReasonData(DiscountConst.Reasons.PRODUCT_SAMPLE.getId()));
        putCartProductOrderData.setDiscount(discountData);
        Response<CartData> resp = cartClient.addDiscount(cartData.getCartId(), cartData.getDocumentVersion(),
                putCartProductOrderData);
        cartClient.assertThatDiscountAdded(resp, cartData);
        cartData.increaseDocumentVersion();
    }

    @Test(description = "Lego_Cart_Consolidate_Products")
    public void testCartConsolidateProducts() {
        // TODO #unfinished
        // Kak???
    }

    @Test(description = "Lego_Cart_Items")
    public void testCartItems() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        int removeProductIndex = 1;
        String removeLineId = cartData.getProducts().get(removeProductIndex).getLineId();
        Response<CartData> resp = cartClient.removeItems(
                cartData.getCartId(), cartData.getDocumentVersion(), removeLineId);
        cartData.increaseDocumentVersion();
        cartData.getProducts().remove(removeProductIndex);
        cartClient.assertThatResponseMatches(resp, cartData);
    }

    @Test(description = "Update Cart - Add product")
    public void testUpdateCart() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        CartProductOrderData newProductData = new CartProductOrderData(products.get(2));
        newProductData.setQuantity(1.0);
        Response<CartData> resp = cartClient.addProduct(
                cartData.getCartId(), cartData.getDocumentVersion(), newProductData);
        cartData.increaseDocumentVersion();
        cartData.addProduct(newProductData);
        cartData = cartClient.assertThatResponseMatches(resp, CartClient.RequestType.UPDATE, cartData);
    }

    @Test(description = "C22906657 Get Cart info - Lego_Cart_Get")
    public void testGetCart() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        Response<CartData> getResp = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(getResp, cartData);
    }

    @Test(description = "C22906658 Delete Cart - Lego_CartChangeStatus")
    public void testDeleteCart() {
        Response<JsonNode> response = cartClient.sendRequestDelete(cartData.getCartId(),
                cartData.getDocumentVersion());
        cartClient.assertThatResponseChangeStatusIsOk(response);

        Response<CartData> getResponse = cartClient.sendRequestGet(cartData.getCartId());
        cartData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.increaseDocumentVersion();
        cartClient.assertThatResponseMatches(getResponse, cartData);
    }
}
