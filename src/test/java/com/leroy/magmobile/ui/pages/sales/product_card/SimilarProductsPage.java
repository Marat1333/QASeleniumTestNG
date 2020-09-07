package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.CatalogSimilarProducts;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductAllGammaCardWidget;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

import java.util.List;
import java.util.stream.Collectors;

public class SimilarProductsPage extends ProductCardPage {

    private AndroidScrollView<ProductCardData> productCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.widget.TextView[@text='доступно']/..", SearchProductCardWidget.class);

    private AndroidScrollView<ProductCardData> allGammaProductCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            //TODO write more specific xpath
            ".//android.view.ViewGroup[@content-desc='lmCode']/..", SearchProductAllGammaCardWidget.class);

    // Verifications


    @Override
    public void waitForPageIsLoaded() {
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
    }

    @Step("Проверить, что фронт корректно отобразил ответ от сервера по запросу на catalog product")
    public SimilarProductsPage shouldCatalogResponseEqualsContent(CatalogSimilarProducts responseData, SearchProductPage.CardType type, Integer entityCount) throws Exception {
        List<CatalogProductData> productDataListFromResponse = responseData.getItems();
        List<ProductCardData> productCardDataListFromPage;
        switch (type) {
            case COMMON:
                productCardDataListFromPage = productCardsScrollView.getFullDataList(entityCount);
                break;
            case ALL_GAMMA:
                productCardDataListFromPage = allGammaProductCardsScrollView.getFullDataList(entityCount);
                break;
            default:
                throw new IllegalArgumentException("Incorrect CardType");
        }
        anAssert.isEquals(productCardDataListFromPage.size(), productDataListFromResponse.size(),
                "Кол-во записей на странице не соответсвует");

        //На фронте реализована сортировка карточек по availableStock, как сортировать товары у которых остаток 0?
        List<String> dataLmCodes = productDataListFromResponse.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
        List<String> frontLmCodes = productCardDataListFromPage.stream().map(ProductCardData::getLmCode).collect(Collectors.toList());

        anAssert.isTrue(dataLmCodes.containsAll(frontLmCodes), "lmCodes mismatch");
        return this;
    }

    @Step("Проверить, что карточки товаров имеют соответсвующий вид для фильтра 'Вся гамма ЛМ'")
    public SimilarProductsPage verifyProductCardsHaveAllGammaView() {
        anAssert.isFalse(E("за штуку").isVisible(), "Карточки товаров не должны содержать цену");
        anAssert.isFalse(E("доступно").isVisible(), "Карточки товаров не должны содержать доступное кол-во");
        return this;
    }

}
