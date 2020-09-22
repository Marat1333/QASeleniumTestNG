package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.requests.salesdoc.cart.*;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static com.leroy.core.matchers.Matchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CartClient extends BaseMashupClient {

    public enum RequestType {
        UPDATE, GET;
    }

    /**
     * ---------- Executable Requests -------------
     **/

    @Step("Get Cart info by cartId={cartId}")
    public Response<CartData> sendRequestGet(String cartId) {
        return execute(new CartGet().setCartId(cartId)
                .bearerAuthHeader(getUserSessionData().getAccessToken())
                .setShopId(getUserSessionData().getUserShopId()), CartData.class);
    }

    @Step("Create Cart")
    public Response<CartData> sendRequestCreate(List<CartProductOrderData> productOrderDataList) {
        CartData cartData = new CartData();
        cartData.setProducts(productOrderDataList);
        return execute(new CartPOST()
                .setShopId(getUserSessionData().getUserShopId())
                .jsonBody(cartData), CartData.class);
    }

    public Response<CartData> sendRequestCreate(
            CartProductOrderData productOrderData) {
        return sendRequestCreate(Collections.singletonList(productOrderData));
    }

    @Step("Add product for cartId={cartId}")
    public Response<CartData> addProduct(String cartId, Integer documentVersion,
                                         CartProductOrderData productData) {
        CartUpdateRequest req = new CartUpdateRequest();
        req.setCartId(cartId);
        req.setShopId(getUserSessionData().getUserShopId());
        CartData putDat = new CartData();
        putDat.setDocumentVersion(documentVersion);
        putDat.setProducts(Collections.singletonList(productData));
        req.jsonBody(putDat);
        return execute(req, CartData.class);
    }

    @Step("Confirm Quantity for cartId={cartId}")
    public Response<CartData> confirmQuantity(String cartId, int documentVersion, CartProductOrderData productData) {
        CartConfirmQuantityRequest req = new CartConfirmQuantityRequest();
        req.setCartId(cartId);
        req.setShopId(getUserSessionData().getUserShopId());
        req.setLdap(getUserSessionData().getUserLdap());

        CartData putData = new CartData();
        putData.setDocumentVersion(documentVersion);
        CartProductOrderData putProductCartData = new CartProductOrderData();
        putProductCartData.setType(null);
        putProductCartData.setLineId(productData.getLineId());
        putProductCartData.setStockAdditionBySalesman(productData.getStockAdditionBySalesman());
        putData.setProducts(Collections.singletonList(putProductCartData));
        req.jsonBody(putData);
        return execute(req, CartData.class);
    }

    @Step("Add discount for cartId={cartId}")
    public Response<CartData> addDiscount(
            String cartId, int documentVersion, List<CartProductOrderData> productDataList) {
        List<CartProductOrderData> putProducts = new ArrayList<>();
        for (CartProductOrderData productData : productDataList) {
            CartProductOrderData putProductData = new CartProductOrderData();
            putProductData.setLineId(productData.getLineId());
            putProductData.setLmCode(productData.getLmCode());
            putProductData.setPrice(productData.getPrice());
            putProductData.setDiscount(productData.getDiscount());
            putProducts.add(putProductData);
        }

        CartDiscountRequest req = new CartDiscountRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setLdap(getUserSessionData().getUserLdap());
        req.setCartId(cartId);
        CartData putData = new CartData();
        putData.setDocumentVersion(documentVersion);
        putData.setProducts(putProducts);
        req.jsonBody(putData);
        return execute(req, CartData.class);
    }

    public Response<CartData> addDiscount(String cartId, int documentVersion, CartProductOrderData productData) {
        return addDiscount(cartId, documentVersion, Collections.singletonList(productData));
    }

    @Step("Remove line item with lineId={lineId} for cartId={cartId}")
    public Response<CartData> removeItems(String cartId, Integer documentVersion, String lineId) {
        CartItemsRequest req = new CartItemsRequest();
        req.setCartId(cartId);
        req.setDocumentVersion(documentVersion);
        req.setLineId(lineId);
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, CartData.class);
    }

    @Step("Consolidate products for cartId={cartId}")
    public Response<JsonNode> consolidateProducts(String cartId, Integer documentVersion, String lineId) {
        CartConsolidateProductsRequest req = new CartConsolidateProductsRequest();
        req.setLdap(getUserSessionData().getUserLdap());
        req.setShopId(getUserSessionData().getUserShopId());
        req.setCartId(cartId);
        CartData putData = new CartData();
        putData.setDocumentVersion(documentVersion);
        CartProductOrderData productOrderData = new CartProductOrderData();
        productOrderData.setLineId(lineId);
        putData.setProducts(Collections.singletonList(productOrderData));
        req.jsonBody(putData);
        return execute(req, JsonNode.class);
    }

    @Step("Make DELETE status for cartId={cartId}")
    public Response<JsonNode> sendRequestDelete(String cartId, int documentVersion) {
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        body.put("documentVersion", String.valueOf(documentVersion));
        return execute(new CartChangeStatusRequest()
                .bearerAuthHeader(getUserSessionData().getAccessToken())
                .setCartId(cartId)
                .formBody(body), JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    @Step("Check that Cart is created and response body has valid data")
    public CartData assertThatIsCreatedAndGetData(Response<CartData> response, boolean shortCheck) {
        assertThatResponseIsOk(response);
        CartData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        if (!shortCheck) {
            assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
            assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
            assertThat("documentType", data.getDocumentType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
            assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
            assertThat("shopId", data.getShopId(), is(getUserSessionData().getUserShopId()));
            assertThat("cartId", data.getCartId(), is(data.getFullDocId()));
            assertThat("documentVersion", data.getDocumentVersion(), is(1));
            assertThat("groupingId", data.getGroupingId(), not(emptyOrNullString()));

            assertThat("products", data.getProducts(), hasSize(greaterThan(0)));
        }

        return data;
    }

    public CartData assertThatIsCreatedAndGetData(Response<CartData> response) {
        return assertThatIsCreatedAndGetData(response, false);
    }

    private void shortVerifyProducts(
            int i, CartEstimateProductOrderData actualProduct, CartProductOrderData expectedProduct) {
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

    @Step("Check that Response body contains products: {expectedProducts}")
    public CartClient assertThatResponseContainsAddedProducts(
            Response<CartData> resp, List<CartProductOrderData> expectedProducts) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        for (int i = 0; i < actualData.getProducts().size(); i++) {
            CartProductOrderData actualProduct = actualData.getProducts().get(i);
            CartProductOrderData expectedProduct = expectedProducts.get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
        }
        return this;
    }

    public CartData assertThatResponseMatches(Response<CartData> resp, CartData expectedData) {
        return assertThatResponseMatches(resp, RequestType.GET, expectedData);
    }

    @Step("Check that Response body matches expectedData")
    public CartData assertThatResponseMatches(Response<CartData> resp, RequestType reqType, CartData expectedData) {
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
            CartProductOrderData actualProduct = actualData.getProducts().get(i);
            CartProductOrderData expectedProduct = expectedData.getProducts().get(i);
            shortVerifyProducts(i, actualProduct, expectedProduct);
            assertThat(String.format("Product #%s - priceCategory", i + 1),
                    actualProduct.getBarCode(), not(nullValue()));
            if (!reqType.equals(RequestType.UPDATE)) {
                assertThat(String.format("Product #%s - lineId", i + 1),
                        actualProduct.getLineId(), is(expectedProduct.getLineId()));
            }
            assertThat(String.format("Product #%s - stockAdditionBySalesman", i + 1),
                    actualProduct.getStockAdditionBySalesman(),
                    is(expectedProduct.getStockAdditionBySalesman()));
            // Discount
            if (expectedProduct.getDiscount() != null) {
                assertThat(String.format("Product #%s - Discount", i + 1),
                        actualProduct.getDiscount(), notNullValue());
                assertThat(String.format("Product #%s - Discount.reason", i + 1),
                        actualProduct.getDiscount().getReason(),
                        is(expectedProduct.getDiscount().getReason()));
                assertThat(String.format("Product #%s - Discount.type", i + 1),
                        actualProduct.getDiscount().getType(),
                        is(expectedProduct.getDiscount().getType()));
                assertThat(String.format("Product #%s - Discount.typeValue", i + 1),
                        actualProduct.getDiscount().getTypeValue(),
                        is(expectedProduct.getDiscount().getTypeValue()));
                assertThat(String.format("Product #%s - Discount.actor", i + 1),
                        actualProduct.getDiscount().getActor(),
                        is(expectedProduct.getDiscount().getActor()));
                assertThat(String.format("Product #%s - Discount.updated", i + 1),
                        actualProduct.getDiscount().getUpdated(), notNullValue());
            }
        }
        return actualData;
    }

    @Step("Check that Response body matches expectedData")
    public void assertThatQuantityIsConfirmed(Response<CartData> resp, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        assertThat("fullDocId", actualData.getFullDocId(), is(expectedData.getFullDocId()));
        assertThat("cartId", actualData.getCartId(), is(expectedData.getCartId()));
        List<CartProductOrderData> actualProductDataList = actualData.getProducts();
        List<CartProductOrderData> expectedProductDataList = expectedData.getProducts();
        assertThat("products size", actualProductDataList, hasSize(expectedProductDataList.size()));
        for (int i = 0; i < actualProductDataList.size(); i++) {
            CartProductOrderData actualProductData = actualProductDataList.get(i);
            CartProductOrderData expectedProductData = expectedProductDataList.get(i);
            shortVerifyProducts(i, actualProductData, expectedProductData);
            assertThat("Product #" + (i + 1) + " lineId", actualProductData.getLineId(),
                    is(expectedProductData.getLineId()));
            assertThat("Product #" + (i + 1) + " lmCode", actualProductData.getLmCode(),
                    is(expectedProductData.getLmCode()));
            assertThat("Product #" + (i + 1) + " stockAdditionBySalesman",
                    actualProductData.getStockAdditionBySalesman(),
                    is(expectedProductData.getStockAdditionBySalesman()));
        }

    }

    @Step("Check that Response body matches expectedData")
    public void assertThatDiscountAdded(Response<CartData> resp, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        assertThat("fullDocId", actualData.getFullDocId(), is(expectedData.getFullDocId()));
        assertThat("cartId", actualData.getCartId(), is(expectedData.getCartId()));
        List<CartProductOrderData> actualProductDataList = actualData.getProducts();
        List<CartProductOrderData> expectedProductDataList = expectedData.getProducts();
        assertThat("products size", actualProductDataList, hasSize(expectedProductDataList.size()));
        for (int i = 0; i < actualProductDataList.size(); i++) {
            CartProductOrderData actualProductData = actualProductDataList.get(i);
            CartProductOrderData expectedProductData = expectedProductDataList.get(i);
            shortVerifyProducts(i, actualProductData, expectedProductData);
            assertThat("Product #" + (i + 1) + " lineId", actualProductData.getLineId(),
                    is(expectedProductData.getLineId()));
            assertThat("Product #" + (i + 1) + " lmCode", actualProductData.getLmCode(),
                    is(expectedProductData.getLmCode()));
            if (expectedProductData.getDiscount() == null) {
                assertThat("Product #" + (i + 1) + " Discount",
                        actualProductData.getDiscount(), nullValue());
            } else {
                assertThat("Product #" + (i + 1) + " Discount.type",
                        actualProductData.getDiscount().getType(),
                        is(expectedProductData.getDiscount().getType()));
                assertThat("Product #" + (i + 1) + " Discount.typeValue",
                        actualProductData.getDiscount().getTypeValue(),
                        is(expectedProductData.getDiscount().getTypeValue()));
                assertThat("Product #" + (i + 1) + " Discount.actor",
                        actualProductData.getDiscount().getActor(),
                        is(getUserSessionData().getUserLdap()));
                //assertThat("Product #" + (i + 1) + " Discount.updated",
                //        actualProductData.getDiscount().getUpdated(),
                //        approximatelyEqual(LocalDateTime.now())); // #bug ?
                assertThat("Product #" + (i + 1) + " Discount.reason",
                        actualProductData.getDiscount().getReason(),
                        is(expectedProductData.getDiscount().getReason()));
            }
        }
    }

    @Step("Check that Response body has 'Result - OK'")
    public void assertThatResponseResultIsOk(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("result", resp.asJson().get("result").asText(), is("OK"));
    }

}
