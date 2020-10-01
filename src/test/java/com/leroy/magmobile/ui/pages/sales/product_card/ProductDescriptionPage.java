package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.constants.DefectConst;
import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductList;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.ProductPricesQuantitySupplyPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.StocksPage;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductAllGammaCardWidget;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDescriptionPage extends ProductCardPage {

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='lmCode']",
            metaName = "ЛМ код товара")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='barCode']",
            metaName = "Бар код товара")
    Element barCode;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[1]",
            metaName = "Название товара")
    Element productName;

    @AppFindBy(containsText = "Гамма")
    Element gammaLbl;

    @AppFindBy(containsText = "Топ")
    Element topLbl;

    @AppFindBy(xpath = "//android.widget.ScrollView/*/*[2]//*[contains(@text,' — ')]")
    Element departmentLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text,'отзыв')]")
    Button reviewNavigationBtn;

    @AppFindBy(text = "История продаж")
    MagMobButton salesHistoryBtn;

    @AppFindBy(text = "Цена")
    MagMobButton productPriceBtn;

    @AppFindBy(text = "Цены в магазинах")
    MagMobButton productPriceGammaCardBtn;

    @AppFindBy(containsText = "₽/")
    Element priceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Цена']/following-sibling::android.widget.TextView[2]")
    Element dateOfPriceChangeLbl;

    @AppFindBy(accessibilityId = "presenceValue")
    Element availableStockLbl;

    @AppFindBy(accessibilityId = "priceUnit")
    Element availableStockUnitLbl;

    @AppFindBy(text = "Комплементарных товаров не найдено")
    Element complementaryProductsNotFoundLbl;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    private AndroidScrollView<ProductCardData> productCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.widget.ScrollView//android.view.ViewGroup[@content-desc='lmCode']/..", SearchProductCardWidget.class);

    private AndroidScrollView<ProductCardData> allGammaProductCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.widget.ScrollView//android.view.ViewGroup[@content-desc='lmCode']/..", SearchProductAllGammaCardWidget.class);

    @Override
    public void waitForPageIsLoaded() {
        lmCode.waitForVisibility();
    }

    // Actions

    @Step("Перейти на страницу с детализацией цен и запасов")
    public ProductPricesQuantitySupplyPage goToPricesAndQuantityPage() throws Exception {
        mainScrollView.scrollDownToText("Доступно для продажи");
        if (!actionWithProductBtn.isVisible()) {
            productPriceGammaCardBtn.click();
        } else {
            productPriceBtn.click();
        }
        return new ProductPricesQuantitySupplyPage();
    }

    @Step("Перейти на страницу с информацией о стоках")
    public StocksPage goToStocksPage() throws Exception {
        if (!availableStockLbl.isVisible()) {
            mainScrollView.scrollDownToElement(availableStockLbl);
        }
        availableStockLbl.click();
        return new StocksPage();
    }

    @Step("Перейти на страницу с историей продаж")
    public SalesHistoryPage goToSalesHistoryPage() throws Exception {
        if (!salesHistoryBtn.isVisible()) {
            mainScrollView.scrollDownToElement(salesHistoryBtn);
        }
        salesHistoryBtn.click();
        return new SalesHistoryPage();
    }

    @Step("Перейти на страницу отзывов")
    public ReviewsPage goToReviewsPage() {
        reviewNavigationBtn.click();
        return new ReviewsPage();
    }

    // Verifications

    @Override
    public ProductDescriptionPage verifyRequiredElements(boolean submitBtnShouldBeVisible) {
        super.verifyRequiredElements(submitBtnShouldBeVisible);
        softAssert.isElementVisible(lmCode);
        softAssert.verifyAll();
        return this;
    }

    public ProductCardPage verifyRequiredContext(String searchContext) {
        if (searchContext.matches("^.*?\\D+$")) {
            anAssert.isEquals(productName.getText(), searchContext, searchContext);
        }
        if (searchContext.length() > 8) {
            String barCode = this.barCode.getText().replaceAll(" ", "");
            anAssert.isEquals(barCode, searchContext, searchContext);
        } else {
            String lmCode = this.lmCode.getText().replaceAll("^\\D+", "");
            anAssert.isEquals(lmCode, searchContext, searchContext);
        }
        return this;
    }

    @Step("Проверить, что комплементарные товары корректно отображены")
    public ProductDescriptionPage shouldComplementaryProductsAreCorrect(List<CatalogProductData> apiDataList,
                                                                        SearchProductPage.CardType type) throws Exception {
        if (apiDataList.size() == 0) {
            mainScrollView.scrollToEnd();
            waitUntilProgressBarIsInvisible();
            anAssert.isElementVisible(complementaryProductsNotFoundLbl);
            return this;
        }
        Element anchor = E("//android.widget.ScrollView//android.widget.ScrollView");
        if (!anchor.isVisible()) {
            mainScrollView.scrollDownToElement(anchor);
        }
        waitUntilProgressBarIsInvisible();

        List<ProductCardData> productCardDataListFromPage = new ArrayList<>();
        if (type.equals(SearchProductPage.CardType.COMMON)) {
            productCardDataListFromPage = productCardsScrollView.getFullDataList();
        } else if (type.equals(SearchProductPage.CardType.ALL_GAMMA)) {
            productCardDataListFromPage = allGammaProductCardsScrollView.getFullDataList();
        }
        if (!DefectConst.LFRONT_3675) {
            for (int i = 0; i < apiDataList.size(); i++) {
                ProductCardData uiData = productCardDataListFromPage.get(i);
                ProductItemData apiData = apiDataList.get(i);
                softAssert.isEquals(uiData.getLmCode(), apiData.getLmCode(), "lmCode");
                if (type.equals(SearchProductPage.CardType.COMMON)) {
                    softAssert.isEquals(uiData.getAvailableQuantity(), apiData.getAvailableStock(), "available quantity");
                }
            }
        } else {
            //null available stock back-end issue
            List<String> uiLmCodes = productCardDataListFromPage.stream().map(ProductCardData::getLmCode).collect(Collectors.toList());
            List<String> apiLmCodes = apiDataList.stream().map(CatalogProductData::getLmCode).collect(Collectors.toList());
            softAssert.isTrue(apiLmCodes.containsAll(uiLmCodes), "products mismatch");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что ЛМ код товара = {text}")
    public ProductDescriptionPage shouldProductLMCodeIs(String text) {
        anAssert.isEquals(lmCode.getText().replaceAll("\\D", ""), text,
                "ЛМ код должен быть %s");
        return this;
    }

    @Step("Проверить, что бар код товара = {text}")
    public ProductDescriptionPage shouldProductBarCodeIs(String text) {
        anAssert.isEquals(barCode.getText().replaceAll("\\D", ""), text,
                "Бар код должен быть %s");
        return this;
    }

    @Step("Проверить, что кол-во отзывов соответствует данным")
    public ProductDescriptionPage shouldReviewCountIsCorrect(CatalogReviewsOfProductList data) {
        if (data.getTotalCount() == 0) {
            anAssert.isElementTextContains(reviewNavigationBtn, "Твой отзыв будет первым");
        } else {
            anAssert.isElementTextContains(reviewNavigationBtn, String.valueOf(data.getTotalCount()));
        }
        return this;
    }

    @Step("Проверить отображенные данные")
    public ProductDescriptionPage shouldDataIsCorrect(CatalogProductData data) {
        String uiDateFormat = "d.MM.yy";
        String apiDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String ps = getPageSource();
        softAssert.isElementTextContains(gammaLbl, data.getGamma(), ps);
        softAssert.isElementTextContains(topLbl, data.getTop(), ps);
        softAssert.isElementTextContains(departmentLbl, data.getDepartmentId(), ps);
        shouldProductLMCodeIs(data.getLmCode());
        shouldProductBarCodeIs(data.getBarCode());
        softAssert.isElementTextEqual(productName, data.getTitle(), ps);
        mainScrollView.scrollToEnd();
        ps = getPageSource();
        softAssert.isElementTextContains(priceLbl, ParserUtil.prettyDoubleFmt(data.getPrice()), ps);
        unitComparison(priceLbl, data.getPriceUnit());
        String priceChangeDate = dateOfPriceChangeLbl.getText(ps).replaceAll("c ", "");
        softAssert.isEquals(DateTimeUtil.strToLocalDate(priceChangeDate, uiDateFormat),
                DateTimeUtil.strToLocalDate(data.getSalesPrice().getDateOfChange(), apiDateFormat), "date of price change");
        softAssert.isElementTextContains(availableStockLbl, ParserUtil.prettyDoubleFmt(data.getAvailableStock()), ps);
        unitComparison(availableStockUnitLbl, data.getPriceUnit());
        softAssert.verifyAll();
        return this;
    }

    public void verifyCardHasGammaView() {
        softAssert.isFalse(actionWithProductBtn.isVisible(), "Кнопка \"Действия с товаром\" отсутствует в карточке товара ЛМ");
        softAssert.isFalse(salesHistoryBtn.isVisible(), "Кнопка \"История продаж\" отсутствует в карточке товара ЛМ");
        softAssert.isFalse(topLbl.isVisible(), "Лейбл ТОП не должен быть виден");
        softAssert.isFalse(dateOfPriceChangeLbl.isVisible(), "Дата изменения цены не должна быть видна");
        softAssert.isFalse(priceLbl.isVisible(), "Цена не должна быть видна");
        softAssert.verifyAll();
    }

    private void unitComparison(Element element, String unit) {
        switch (unit) {
            case "NIU":
                softAssert.isElementTextContains(element, "шт.");
        }
    }
}
