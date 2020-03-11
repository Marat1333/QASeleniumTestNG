package com.leroy.magmobile.api.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.helpers.FindTestDataHelper;
import com.leroy.magmobile.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.umbrella_extension.magmobile.data.catalog.ProductItemData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.CartData;
import com.leroy.umbrella_extension.magmobile.data.sales.cart_estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart.CartChangeStatusPut;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart.CartGet;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart.CartPOST;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static com.leroy.magmobile.api.matchers.ProjectMatchers.isNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CartBuilder extends BaseApiBuilder {


    /**
     * ---------- Executable Requests -------------
     **/

    public Response<CartData> sendRequestGet(String cartId) {
        return apiClient.execute(new CartGet().setCartId(cartId), CartData.class);
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

    public Response<JsonNode> sendRequestDelete(String cartId) {
        Map<String, String> body = new HashMap<>();
        body.put("status", SalesDocumentsConst.States.DELETED.getApiVal());
        return apiClient.execute(new CartChangeStatusPut().setCartId(cartId).formBody(body), JsonNode.class);
    }

    /**
     * ------------  Verifications -----------------
     **/
    public CartData assertThatIsCreated(Response<CartData> response) {
        assertThatResponseIsOk(response);
        CartData data = response.asJson();
        assertThat("fullDocId", data.getFullDocId(), isNumber());
        assertThat("fullDocId", "", isNumber());
        assertThat("fullDocId", "123a123", isNumber());

        assertThat("docType", data.getDocType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
        assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("documentType", data.getDocumentType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("shopId", data.getShopId(), is(sessionData.getUserShopId()));
        assertThat("cartId", data.getFullDocId(), is(data.getFullDocId()));
        assertThat("documentVersion", data.getDocumentVersion(), is(1));
        assertThat("groupingId", data.getGroupingId(), not(isEmptyOrNullString()));

        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));

        return data;
    }

    public CartBuilder assertThatGetResponseMatches(Response<CartData> resp, CartData expectedData) {
        assertThatResponseIsOk(resp);
        CartData actualData = resp.asJson();
        assertThat("FullDocId", actualData.getFullDocId(), equalTo(expectedData.getFullDocId()));
        /*assertThat("docType", actualData.getDocType(), is();
        assertThat("salesDocStatus", data.getSalesDocStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("documentType", data.getDocumentType(), is(SalesDocumentsConst.Types.CART.getApiVal()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.DRAFT.getApiVal()));
        assertThat("shopId", data.getShopId(), is(sessionData.getUserShopId()));
        assertThat("cartId", data.getFullDocId(), is(data.getFullDocId()));
        assertThat("documentVersion", data.getDocumentVersion(), is(1));
        assertThat("groupingId", data.getGroupingId(), not(isEmptyOrNullString()));

        assertThat("products", data.getProducts(), hasSize(greaterThan(0)));*/
        return this;
    }

    /**
     * ------------  Help Methods -----------------
     **/

    public List<ProductOrderData> findProducts(int count) {
        List<ProductOrderData> result = new ArrayList<>();
        List<ProductItemData> productItemDataList = FindTestDataHelper.getProducts(apiClient,
                sessionData, count, new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE));
        for (ProductItemData productItemData : productItemDataList) {
            result.add(new ProductOrderData(productItemData));
        }
        return result;
    }
}
