package com.leroy.common_mashups.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.ProductDataList;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.ServiceItemData;
import com.leroy.common_mashups.catalogs.data.ServiceItemDataList;
import com.leroy.common_mashups.catalogs.data.CatalogComplementaryProductsDataV2;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSearchRequest;
import com.leroy.common_mashups.catalogs.requests.GetCatalogServicesRequest;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SearchProductHelper extends BaseHelper {

    @Inject
    private CatalogProductClient catalogProductClient;

    private final static int MAX_PAGE_SIZE = 90;

    @Step("Find {necessaryCount} services")
    public List<ServiceItemData> getServices(int necessaryCount) {
        GetCatalogServicesRequest params = new GetCatalogServicesRequest();
        params.setShopId(userSessionData().getUserShopId())
                .setStartFrom(1)
                .setPageSize(necessaryCount); // TODO не работает. Почему?
        Response<ServiceItemDataList> resp = catalogProductClient.searchServicesBy(params);
        List<ServiceItemData> services =
                resp.asJson().getItems().stream().limit(necessaryCount)
                        .collect(Collectors.toList());
        return services;
    }

    @Step("Find {necessaryCount} products")
    public List<ProductData> getProducts(int necessaryCount, CatalogSearchFilter filtersData) {
        String[] badLmCodes = {"10008698",
                "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        if (filtersData == null) {
            filtersData = new CatalogSearchFilter();
        }
        int i = 0;
        int startFrom = 1;
        List<ProductData> resultList = new ArrayList<>();
        while (i < necessaryCount) {
            Response<ProductDataList> resp = catalogProductClient
                    .searchProductsBy(filtersData, startFrom, MAX_PAGE_SIZE);
            assertThat("Product search request has failed.", resp, successful());
            List<ProductData> items = resp.asJson().getItems();
            assertThat("Product search request does NOT contain any data.", items, hasSize(greaterThan(0)));
            for (ProductData item : items) {
                if (!Arrays.asList(badLmCodes).contains(item.getLmCode())
                        && Strings.isNotNullAndNotEmpty(item.getTitle())) {
                    if (filtersData.getAvs() == null
                            || !filtersData.getAvs() && item.getAvsDate() == null
                            || filtersData.getAvs() && item.getAvsDate() != null) {
                        if (filtersData.getHasAvailableStock() == null ||
                                (filtersData.getHasAvailableStock() && item.getAvailableStock() > 0
                                        ||
                                        !filtersData.getHasAvailableStock()
                                                && item.getAvailableStock() <= 0)) {
                            resultList.add(item);
                            i++;
                        }
                    }
                }
                if (necessaryCount == i) {
                    break;
                }
            }
            startFrom += MAX_PAGE_SIZE;
        }

        return resultList.stream().limit(necessaryCount).collect(Collectors.toList());
    }

    public List<ProductData> getProducts(int necessaryCount) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        return getProducts(necessaryCount, filter);
    }

    public List<ProductData> getProducts(int necessaryCount, boolean isAvs, boolean isTopEm) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        filter.setTopEM(isTopEm);
        filter.setAvs(isAvs);
        return getProducts(necessaryCount, filter);
    }

    public String getProductLmCode() {
        return getProductLmCodes(1).get(0);
    }

    public List<String> getProductLmCodes(int necessaryCount) {
        List<ProductData> productItemResponseList = getProducts(necessaryCount, null);
        return productItemResponseList.stream().map(ProductData::getLmCode)
                .collect(Collectors.toList());
    }

    public List<String> getProductLmCodes(int necessaryCount, boolean isAvs, boolean isTopEm) {
        List<ProductData> productItemResponseList = getProducts(necessaryCount, isAvs, isTopEm);
        return productItemResponseList.stream().map(ProductData::getLmCode)
                .collect(Collectors.toList());
    }

    @Step("Return list of products for specified ShopId")
    public List<ProductData> getProductsForShop(int countOfProducts,
            String shopId) {
        return catalogProductClient
                .searchProductsBy(new GetCatalogProductSearchRequest().setPageSize(countOfProducts)
                        .setHasAvailableStock(true).setShopId(shopId)).asJson().getItems();
    }

    @Step("Return first product for specified lmCode")
    public ProductData searchProductByLmCode(String lmCode) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setLmCode(lmCode);
        return catalogProductClient.searchProductsBy(filter).asJson().getItems().stream()
                .findFirst()
                .orElse(null);
    }

    @Step("Return product for specified lmCode")
    public ProductData getProductByLmCode(String lmCode) {
        Response<ProductData> response = catalogProductClient.getProduct(lmCode);
        assertThat(response, successful());
        return response.asJson();
    }

    @Step("Return product for specified lmCode")
    public CatalogProductData getProductV2ByLmCode(String lmCode) {
        Response<CatalogProductData> response = catalogProductClient.getProductV2(lmCode);
        assertThat(response, successful());
        return response.asJson();
    }

    @Step("Return random product")
    public ProductData getRandomProduct() {
        ProductDataList productDataList = catalogProductClient
                .searchProductsBy(new GetCatalogProductSearchRequest().setPageSize(10)
                        .setHasAvailableStock(true).setShopId(userSessionData().getUserShopId()))
                .asJson();
        List<ProductData> productData = productDataList.getItems();
        productData = productData.stream().filter(i -> i.getTitle() != null)
                .collect(Collectors.toList());
        return productData.get((int) (Math.random() * productData.size()));
    }

    @Step("Get product with{withoutComplimentary==false} OR without complementary product")
    public CatalogComplementaryProductsDataV2 getComplementaryProductData(
            boolean withoutComplimentary) {
        List<ProductData> itemsList;
        List<String> lmCodes = this.getProductLmCodes(MAX_PAGE_SIZE);
        for (String lmCode : lmCodes) {
            Response<CatalogComplementaryProductsDataV2> response = catalogProductClient
                    .getComplementaryProducts(lmCode);
            if (response.isSuccessful()) {
                CatalogComplementaryProductsDataV2 result = response.asJson();
                result.setParentLmCode(lmCode);
                itemsList = result.getItems();
                if (itemsList.size() > 0 && !withoutComplimentary) {
                    return result;
                } else if (itemsList.size() == 0 && withoutComplimentary) {
                    return result;
                }
            }
        }
        Assert.fail("No products with complementary products were found");
        return null;
    }
}
