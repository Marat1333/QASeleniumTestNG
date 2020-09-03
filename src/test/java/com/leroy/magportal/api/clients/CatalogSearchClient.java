package com.leroy.magportal.api.clients;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogSearchClient extends com.leroy.magmobile.api.clients.CatalogSearchClient {

  @Override
  @Step("Search for products")
  public Response<ProductItemDataList> searchProductsBy(GetCatalogSearch params) {
    params.setLdapHeader(userSessionData.getUserLdap());//TODO: DELETE
    return execute(params, ProductItemDataList.class);
  }

  @Step("Return random product for specified ShopId")
  public List<ProductItemData> getRandomUniqueProductsWithTitlesForShop(int countOfProducts, String shopId) {
    List<ProductItemData> randomProductsList = new ArrayList<>();
    ProductItemDataList productItemDataList = this.searchProductsBy(new GetCatalogSearch().setPageSize(MAX_PAGE_SIZE)
        .setHasAvailableStock(true).setShopId(shopId)).asJson();
    List<ProductItemData> productItemData = productItemDataList.getItems();
    productItemData = productItemData.stream().filter(i -> i.getTitle() != null).collect(Collectors.toList());
    int randomIndex;
    for (int i = 0; i < countOfProducts; i++) {
      randomIndex = (int) (Math.random() * productItemData.size());
      randomProductsList.add(productItemData.get(randomIndex));
      productItemData.remove(randomIndex);
    }
    return randomProductsList;
  }
}
