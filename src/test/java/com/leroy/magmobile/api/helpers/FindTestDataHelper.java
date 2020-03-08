package com.leroy.magmobile.api.helpers;

import com.leroy.magmobile.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemDataList;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemData;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemDataList;
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

    public static List<ServiceItemData> getServices(MagMobileClient client, String shopId,
                                                    int necessaryCount) {
        GetCatalogServicesSearch params = new GetCatalogServicesSearch();
        params.setShopId(shopId)
                .setStartFrom(1)
                .setPageSize(necessaryCount);
        Response<ServiceItemDataList> resp = client.searchServicesBy(params);
        return resp.asJson().getItems();
    }

    public static List<ProductItemData> getProducts(MagMobileClient client, String shopId,
                                                    int necessaryCount, FiltersData filtersData) {
        if (filtersData == null)
            filtersData = new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE);
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(shopId)
                .setTopEM(filtersData.isTopEM())
                .setHasAvailableStock(filtersData.isHasAvailableStock());
        Response<ProductItemDataList> resp = client.searchProductsBy(params);
        MatcherAssert.assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
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

    public static List<ProductItemData> getProducts(MagMobileClient client, String shopId,
                                                    int necessaryCount) {
        return getProducts(client, shopId, necessaryCount, null);
    }

    public static List<String> getProductLmCodes(MagMobileClient client, String shopId,
                                                 int necessaryCount) {
        List<ProductItemData> productItemResponseList = getProducts(
                client, shopId, necessaryCount, null);
        return productItemResponseList.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
    }

}
