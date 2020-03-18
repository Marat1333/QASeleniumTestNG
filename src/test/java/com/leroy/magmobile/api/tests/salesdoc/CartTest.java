package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
import java.util.Random;

public class CartTest extends BaseProjectApiTest {

    @Inject
    private CartClient cartClient;

    private CatalogSearchClient searchClient;

    private CartData cartData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        searchClient = getCatalogSearchClient();
        cartClient.setSessionData(sessionData);
    }

    @Test(description = "C22906656 Create Cart - Lego_Cart_Post")
    public void testCreateCart() {
        // Prepare request data
        CartEstimateProductOrderData productOrderData = new CartEstimateProductOrderData(searchClient.getProducts(1).get(0));
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create
        Response<CartData> response = cartClient.sendRequestCreate(productOrderData);
        // Check Create
        cartData = cartClient.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        cartClient.assertThatResponseContainsAddedProducts(response,
                Collections.singletonList(productOrderData));
    }

    @Test(description = "C22906657 Get Cart info - Lego_Cart_Get")
    public void testGetCart() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        Response<CartData> getResp = cartClient.sendRequestGet(cartData.getCartId());
        cartClient.assertThatGetResponseMatches(getResp, cartData);
    }

    @Test(description = "C22906658 Delete Cart - Lego_CartChangeStatus")
    public void testDeleteCart() {
        Response<JsonNode> response = cartClient.sendRequestDelete(cartData.getCartId(), cartData.getDocumentVersion());
        cartClient.assertThatResponseChangeStatusIsOk(response);

        Response<CartData> getResponse = cartClient.sendRequestGet(cartData.getCartId());
        cartData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.increaseDocumentVersion();
        cartClient.assertThatGetResponseMatches(getResponse, cartData);
    }
}
