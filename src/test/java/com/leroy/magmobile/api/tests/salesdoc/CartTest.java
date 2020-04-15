package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    @Test(description = "C22906656 Creating Cart with 2 products")
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

    @Test(description = "C23194964 Cart - Confirm Quantity - happy path (with simple product - no AVS, no TOP EM)")
    public void testCartConfirmQuantity() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        CartProductOrderData cartProductOrderData = cartData.getProducts().get(0);
        cartProductOrderData.setStockAdditionBySalesman(999);
        Response<CartData> confirmQuantityResp = cartClient.confirmQuantity(cartData.getCartId(), 1,
                cartProductOrderData);
        cartClient.assertThatQuantityIsConfirmed(confirmQuantityResp, cartData);
    }

    @Test(description = "C23194966 Cart - Add Discount")
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

    @Test(description = "C23194968 Lego_Cart_Consolidate_Products - happy path")
    public void testCartConsolidateProducts() {
        step("Find products");
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(false);
        filtersData.setTopEM(false);
        filtersData.setHasAvailableStock(false);
        CartProductOrderData productWithNegativeBalance = new CartProductOrderData(
                apiClientProvider.getProducts(1, filtersData).get(0));
        productWithNegativeBalance.setQuantity(1.0);
        filtersData.setHasAvailableStock(true);
        CartProductOrderData productWithPositiveBalance = new CartProductOrderData(
                apiClientProvider.getProducts(1, filtersData).get(0));
        productWithPositiveBalance.setQuantity(1.0);

        step("Create Cart");
        Response<CartData> response = cartClient.sendRequestCreate(
                Arrays.asList(productWithNegativeBalance, productWithPositiveBalance));
        // Check Create
        cartData = cartClient.assertThatIsCreatedAndGetData(response);
        assertThat("Group count", cartData.getGroups(), hasSize(2));
        step("Consolidate Products");
        Response<JsonNode> consolidateResp = cartClient.consolidateProducts(
                cartData.getCartId(), cartData.getDocumentVersion(), cartData.getProducts().get(1).getLineId());
        cartClient.assertThatResponseResultIsOk(consolidateResp);

        step("Send get request and check data");
        Response<CartData> getResp = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatResponseMatches(getResp, cartData);
        assertThat("Group count", getResp.asJson().getGroups(), hasSize(1));
    }

    @Test(description = "C23194965 Lego_Cart_Items - Remove 1 product from 2 from the Cart")
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

    @Test(description = "C23194967 Update Cart - Add product")
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

    @Test(description = "C22906658 Lego_CartChangeStatus - make status is DELETED")
    public void testDeleteCart() {
        Response<JsonNode> response = cartClient.sendRequestDelete(cartData.getCartId(),
                cartData.getDocumentVersion());
        cartClient.assertThatResponseResultIsOk(response);

        Response<CartData> getResponse = cartClient.sendRequestGet(cartData.getCartId());
        cartData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.increaseDocumentVersion();
        cartClient.assertThatResponseMatches(getResponse, cartData);
    }
}
