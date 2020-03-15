package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.builders.CartBuilder;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.CartData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.ProductOrderData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
import java.util.Random;

public class CartTest extends BaseProjectTest {

    @Inject
    private Provider<CartBuilder> provider;

    private CartBuilder cartBuilder;

    @Inject
    private AuthClient authClient;

    private CartData cartData;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS));

        cartBuilder = provider.get();
        cartBuilder.setSessionData(sessionData);
    }

    @Test(description = "C22906656 Create Cart - Lego_Cart_Post")
    public void testCreateCart() {
        // Prepare request data
        ProductOrderData productOrderData = cartBuilder.findProducts(1).get(0);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        // Create
        Response<CartData> response = cartBuilder.sendRequestCreate(productOrderData);
        // Check Create
        cartData = cartBuilder.assertThatIsCreatedAndGetData(response);
        // Check that created data contains added product
        cartBuilder.assertThatResponseContainsAddedProducts(response,
                Collections.singletonList(productOrderData));
    }

    @Test(description = "C22906657 Get Cart info - Lego_Cart_Get")
    public void testGetCart() {
        if (cartData == null)
            throw new IllegalArgumentException("cart data hasn't been created");
        Response<CartData> getResp = cartBuilder.sendRequestGet(cartData.getCartId());
        cartBuilder.assertThatGetResponseMatches(getResp, cartData);
    }

    @Test(description = "C22906658 Delete Cart - Lego_CartChangeStatus")
    public void testDeleteCart() {
        Response<JsonNode> response = cartBuilder.sendRequestDelete(cartData.getCartId(), cartData.getDocumentVersion());
        cartBuilder.assertThatResponseChangeStatusIsOk(response);

        Response<CartData> getResponse = cartBuilder.sendRequestGet(cartData.getCartId());
        cartData.setSalesDocStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.setStatus(SalesDocumentsConst.States.DELETED.getApiVal());
        cartData.increaseDocumentVersion();
        cartBuilder.assertThatGetResponseMatches(getResponse, cartData);
    }
}
