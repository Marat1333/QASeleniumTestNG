package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.builders.CartBuilder;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.CartData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.ProductOrderData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

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

    @Test(description = "Create Cart")
    public void testCreateCart() {
        // Prepare request data
        ProductOrderData productOrderData = cartBuilder.findProducts(1).get(0);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        Response<CartData> cartDataResponse = cartBuilder.sendRequestCreate(productOrderData);
        cartData = cartBuilder.assertThatIsCreated(cartDataResponse);

        cartBuilder.sendRequestGet(cartData.getCartId());
        //cartBuilder.assertThatIsCreated()

        cartData = cartDataResponse.asJson();

    }

    @Test(description = "Get Cart")
    public void testGetCart() {
        // Prepare request data
        ProductOrderData productOrderData = cartBuilder.findProducts(1).get(0);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);

        Response<CartData> cartDataResponse = cartBuilder.sendRequestCreate(productOrderData);
        String s= "";

    }

    @Test(description = "Delete status")
    public void testDeleteCart() {
        Response<JsonNode> r = cartBuilder.sendRequestDelete(cartData.getCartId());
        String s = "";
    }
}
