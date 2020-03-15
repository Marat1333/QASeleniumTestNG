package com.leroy.magmobile.api.builders;

import com.google.inject.Inject;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.helpers.FindTestDataHelper;
import com.leroy.magmobile.ui.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.magmobile.api.MagMobileClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ServiceItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.ServiceOrderData;
import lombok.Setter;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class BaseApiBuilder {

    @Inject
    protected MagMobileClient apiClient;

    @Setter
    protected SessionData sessionData;

    protected void assertThatResponseIsOk(Response<?> response) {
        assertThat(response, successful());
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

    public List<ServiceOrderData> findServices(int count) {
        List<ServiceOrderData> result = new ArrayList<>();
        List<ServiceItemData> serviceItemDataList = FindTestDataHelper.getServices(apiClient,
                sessionData.getUserShopId(), count);
        for (ServiceItemData serviceItemData : serviceItemDataList) {
            result.add(new ServiceOrderData(serviceItemData));
        }
        return result;
    }

}
