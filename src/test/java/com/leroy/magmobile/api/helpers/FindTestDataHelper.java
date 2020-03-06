package com.leroy.magmobile.api.helpers;

import com.leroy.magmobile.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemResponse;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogServicesSearch;
import org.hamcrest.MatcherAssert;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.leroy.magmobile.api.tests.matchers.IsSuccessful.successful;

public class FindTestDataHelper {

    public static List<ServiceItemResponse> getServices(MagMobileClient client, String shopId,
                                                        int necessaryCount) {
        GetCatalogServicesSearch params = new GetCatalogServicesSearch();
        params.setShopId(shopId)
                .setStartFrom(1)
                .setPageSize(necessaryCount);
        Response<ServiceItemListResponse> resp = client.searchServicesBy(params);
        return resp.asJson().getItems();
    }

    public static List<ProductItemResponse> getProducts(MagMobileClient client, String shopId,
                                                        int necessaryCount, FiltersData filtersData) {
        if (filtersData == null)
            filtersData = new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE);
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(shopId)
                .setTopEM(filtersData.isTopEM())
                .setHasAvailableStock(filtersData.isHasAvailableStock());
        Response<ProductItemListResponse> resp = client.searchProductsBy(params);
        MatcherAssert.assertThat("Catalog search request:", resp, successful());
        List<ProductItemResponse> items = resp.asJson().getItems();
        List<ProductItemResponse> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemResponse item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode()))
                if (!filtersData.isAvs() && item.getAvsDate() == null) {
                    resultList.add(item);
                    i++;
                }
            if (necessaryCount == i)
                break;
        }
        return resultList;
    }

    public static List<ProductItemResponse> getProducts(MagMobileClient client, String shopId,
                                                        int necessaryCount) {
        return getProducts(client, shopId, necessaryCount, null);
    }

    public static List<String> getProductLmCodes(MagMobileClient client, String shopId,
                                                 int necessaryCount) {
        List<ProductItemResponse> productItemResponseList = getProducts(
                client, shopId, necessaryCount, null);
        return productItemResponseList.stream().map(ProductItemResponse::getLmCode).collect(Collectors.toList());
    }

}
