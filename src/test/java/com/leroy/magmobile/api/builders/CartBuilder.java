package com.leroy.magmobile.api.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ProductOrderData;
import com.leroy.magmobile.api.requests.salesdoc.cart.CartChangeStatusPut;
import com.leroy.magmobile.api.requests.salesdoc.cart.CartGet;
import com.leroy.magmobile.api.requests.salesdoc.cart.CartPOST;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CartBuilder extends BaseApiBuilder {


    /**
     * ---------- Executable Requests -------------
     **/

    public Response<CartData> sendRequestGet(String cartId) {
        return apiClient.execute(new CartGet().setCartId(cartId)
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId()), CartData.class);
    }

    public Response<CartData> sendRequestCreate(List<ProductOrderData> productOrderDataList) {
        CartData cartData = new CartData();
        cartData.setProducts(productOrderDataList);
        return apiClient.execute(new CartPOST()
                .bearerAuthHeader(sessionData.getAccessToken())
                .setShopId(sessionData.getUserShopId())
                .jsonBody(cartData), CartData.class);
    }

    public Response<CartData> sendRequestCreate(
            ProductOrderData productOrderData) {
        return sendRequestCreate(Collections.singletonList(productOrderData));
    }

    public Response<JsonNode> sendRequestDelete(String cartId, int documentVersion) {
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        body.put("documentVersion", String.valueOf(documentVersion));
        return apiClient.execute(new CartChangeStatusPut()
                .bearerAuthHeader(sessionData.getAccessToken())
                .setCartId(cartId)
                .formBody(body), JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    public CartData assertThatIsCreatedAndGetData(Response<CartData> response) {
        assertThatResponseIsOk(response);
        CartData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
        assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("documentType", data.getDocumentType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("shopId", data.getShopId(), is(sessionData.getUserShopId()));
        assertThat("cartId", data.getCartId(), is(data.getFullDocId()));
        assertThat("documentVersion", data.getDocumentVersion(), is(1));
        assertThat("groupingId", data.getGroupingId(), not(isEmptyOrNullString()));

        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));

        return data;
    }

    private void shortVerifyProducts(
            int i, ProductOrderData actualProduct, ProductOrderData expectedProduct) {
        assertThat(String.format("Product #%s - lmCode", i + 1),
                actualProduct.getLmCode(), is(expectedProduct.getLmCode()));
        assertThat(String.format("Product #%s - title", i + 1),
                actualProduct.getTitle(), is(expectedProduct.getTitle()));
        assertThat(String.format("Product #%s - Quantity", i + 1),
                actualProduct.getQuantity(), is(expectedProduct.getQuantity()));
        /*assertThat(String.format("Product #%s - Available stock", i + 1),
                actualProduct.getAvailableStock(), is(expectedProduct.getAvailableStock()));*/
        assertThat(String.format("Product #%s - Price", i + 1),
                actualProduct.getPrice(), is(expectedProduct.getPrice()));
        /*assertThat(String.format("Product #%s - PriceUnit", i + 1),
                actualProduct.getPriceUnit(), is(expectedProduct.getPriceUnit()));*/
        assertThat(String.format("Product #%s - BarCode", i + 1),
                actualProduct.getBarCode(), is(expectedProduct.getBarCode()));
        assertThat(String.format("Product #%s - Type", i + 1),
                actualProduct.getType(), is(expectedProduct.getType()));
        assertThat(String.format("Product #%s - TopEm", i + 1),
                actualProduct.getTopEM(), is(expectedProduct.getTopEM()));
    }

    public CartBuilder assertThatResponseContainsAddedProducts(
            Response<CartData> resp, List<ProductOrderData> expectedProducts) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            ProductOrderData actualProduct = actualData.getProducts().get(i);
            ProductOrderData expectedProduct = expectedProducts.get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
        }
        return this;
    }

    public CartBuilder assertThatGetResponseMatches(Response<CartData> resp, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        assertThat("FullDocId", actualData.getFullDocId(), equalTo(expectedData.getFullDocId()));
        assertThat("docType", actualData.getDocType(), is(expectedData.getDocType()));
        assertThat("salesDocStatus", actualData.getSalesDocStatus(), is(expectedData.getSalesDocStatus()));
        assertThat("documentType", actualData.getDocumentType(), is(expectedData.getDocumentType()));
        assertThat("status", actualData.getStatus(), is(expectedData.getStatus()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        assertThat("cartId", actualData.getCartId(), is(expectedData.getCartId()));
        assertThat("documentVersion", actualData.getDocumentVersion(), is(expectedData.getDocumentVersion()));
        assertThat("groupingId", actualData.getGroupingId(), is(expectedData.getGroupingId()));

        assertThat("products", actualData.getProducts(), hasSize(expectedData.getProducts().size()));

        for (int i = 0; i < actualData.getProducts().size(); i++) {
            ProductOrderData actualProduct = actualData.getProducts().get(i);
            ProductOrderData expectedProduct = expectedData.getProducts().get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
            assertThat(String.format("Product #%s - priceCategory", i + 1),
                    actualProduct.getBarCode(), not(nullValue()));
            assertThat(String.format("Product #%s - lineId", i + 1),
                    actualProduct.getLineId(), is(expectedProduct.getLineId()));
        }
        return this;
    }

    public void assertThatResponseChangeStatusIsOk(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("result", resp.asJson().get("result").asText(), is("OK"));
    }

}
