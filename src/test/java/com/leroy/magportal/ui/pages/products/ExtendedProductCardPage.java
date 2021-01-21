package com.leroy.magportal.ui.pages.products;

import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.product.details.ExtStocks;
import com.leroy.common_mashups.catalogs.data.product.details.StockAreas;
import com.leroy.constants.Currency;
import com.leroy.constants.Units;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierData;
import com.leroy.magportal.ui.models.search.PriceContainerData;
import com.leroy.magportal.ui.models.search.StocksData;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.products.SearchProductPage.Direction;
import com.leroy.magportal.ui.pages.products.widget.ExtendedProductCardWidget;
import com.leroy.magportal.ui.webelements.searchelements.ProductPriceInfoWidget;
import com.leroy.magportal.ui.webelements.searchelements.ProductQuantityInfoWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ExtendedProductCardPage extends ProductCardPage {

    public enum Tab {
        SIMILAR_PRODUCTS,
        COMPLEMENT_PRODUCTS,
        PRICES_AND_STOCKS_IN_OTHER_SHOPS
    }

    @WebFindBy(xpath = "//button[@id='ANALOG']")
    Button similarProducts;

    @WebFindBy(xpath = "//button[@id='COMPLEMENT']")
    Button complementProducts;

    @WebFindBy(xpath = "//div[contains(@class,'active')]//div[contains(@class, 'BarViewProductCard__container')]",
            clazz = ExtendedProductCardWidget.class, refreshEveryTime = true)
    ElementList<ExtendedProductCardWidget> productCards;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//button[contains(@class, 'paginationButton right')]")
    Button productCardsListRightPaginationBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//button[contains(@class, 'paginationButton left')]")
    Button productCardsListLeftPaginationBtn;

    @WebFindBy(xpath = "//span[text()='в корзину']/ancestor::button[1]")
    Button addProductToCart;

    @WebFindBy(xpath = "//span[text()='в смету']/ancestor::button[1]")
    Button addProductToEstimate;

    @WebFindBy(xpath = "//div[contains(@class,'Tabs-Title-outer')]/following-sibling::div")
    ElementList<Element> tabContainerList;

    //DATA

    @WebFindBy(xpath = "//p[contains(text(), 'Код поставщика')]/../*")
    ElementList<Element> supplierInfo;

    @WebFindBy(xpath = "//span[contains(text(),'Цена')]/ancestor::div[contains(@class,'bottom')]")
    ProductPriceInfoWidget productPriceInfoWidget;

    @WebFindBy(xpath = "//span[contains(text(),'Доступно для продажи')]/ancestor::div[contains(@class,'bottom')]")
    ProductQuantityInfoWidget productQuantityInfoWidget;

    @WebFindBy(xpath = "//span[contains(text(),'Топ')]")
    Element topBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][4]")
    Element categoryBadge;

    @Override
    public void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        addProductToCart.waitForVisibility();
        addProductToEstimate.waitForVisibility();
    }

    @Step("Перейти в карточку аналогичного товара {lmCode}")
    public ExtendedProductCardPage goToAdditionalProduct(String lmCode, Tab tab) throws Exception {
        if (tab.equals(Tab.SIMILAR_PRODUCTS)) {
            anAssert.isElementVisible(similarProducts);
        } else if (tab.equals(Tab.COMPLEMENT_PRODUCTS)) {
            anAssert.isElementVisible(complementProducts);
        } else {
            throw new IllegalArgumentException();
        }
        switchExtraInfoTabs(tab);
        boolean condition = false;
        int i = 0;
        while (i != 20) {
            for (ExtendedProductCardWidget widget : productCards) {
                String widgetLmCode = widget.getLmCode();
                if (widgetLmCode.equals(lmCode)) {
                    widget.scrollTo();
                    widget.click();
                    condition = true;
                    break;
                }
            }
            if (condition) {
                break;
            }
            if (!productCardsListRightPaginationBtn.isVisible()) {
                throw new AssertionError("There is no needed lmCode");
            }
            productCardsListRightPaginationBtn.click();
            i++;
        }
        return new ExtendedProductCardPage();
    }

    @Step("перейти во вкладку с дополнительной информацией")
    public ExtendedProductCardPage switchExtraInfoTabs(Tab tab) throws Exception {
        String attribute = "className";
        String condition = "active";
        switch (tab) {
            case SIMILAR_PRODUCTS:
                if (similarProducts.isVisible() && tabContainerList.get(0).getAttribute(attribute).contains(condition)) {
                    return this;
                }
                similarProducts.scrollTo();
                similarProducts.click();
                break;
            case COMPLEMENT_PRODUCTS:
                if (similarProducts.isVisible() && complementProducts.isVisible() &&
                        tabContainerList.get(1).getAttribute(attribute).contains(condition)) {
                    return this;
                }
                complementProducts.scrollTo();
                complementProducts.click();
                break;
            case PRICES_AND_STOCKS_IN_OTHER_SHOPS:
                if (similarProducts.isVisible() && complementProducts.isVisible() &&
                        tabContainerList.get(2).getAttribute(attribute).contains(condition)) {
                    return this;
                } else if ((similarProducts.isVisible() || complementProducts.isVisible()) &&
                        tabContainerList.get(1).getAttribute(attribute).contains(condition)) {
                    return this;
                } else if (!similarProducts.isVisible() && !complementProducts.isVisible()) {
                    return this;
                }
                pricesAndStocksInOtherShops.scrollTo();
                pricesAndStocksInOtherShops.click();
                break;
        }
        return this;
    }

    @Step("искать магазин по {value}")
    public ExtendedProductCardPage searchShop(String value) {
        searchForShop.clearAndFill(value);
        return this;
    }

    @Step("Добавить товар в корзину")
    public CartPage addProductToCart() {
        addProductToCart.click();
        return new CartPage();
    }

    @Step("Добавить товар в смету")
    public EstimatePage addProductToEstimate() {
        addProductToEstimate.click();
        return new EstimatePage();
    }

    @Override
    public ExtendedProductCardPage verifyRequiredElements() {
        softAssert.areElementsVisible(topBadge, addProductToCart, addProductToEstimate, productPriceInfoWidget,
                productQuantityInfoWidget, pricesAndStocksInOtherShops);
        softAssert.verifyAll();
        shouldUrlContains("isAllGammaView=false");
        return this;
    }

    @Step("Проверить, что все дополнительные товары отображен")
    public ExtendedProductCardPage shouldAllAdditionalProductsIsVisible(List<ProductData> data) throws Exception {
        if (data.size() > 4) {
            shouldNavigationBtnHasCorrectCondition(Direction.FORWARD, true);
        } else {
            shouldNavigationBtnHasCorrectCondition(Direction.FORWARD, false);
        }
        shouldNavigationBtnHasCorrectCondition(Direction.BACK, false);
        String widgetLmCode;
        int productCardIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            if (i % 4 == 0 && i > 0) {
                shouldNavigationBtnHasCorrectCondition(Direction.FORWARD, true);
                productCardsListRightPaginationBtn.click();
                shouldNavigationBtnHasCorrectCondition(Direction.BACK, true);
                productCardIndex = productCardIndex % 4;
            }
            widgetLmCode = productCards.get(productCardIndex).getLmCode();
            anAssert.isEquals(data.get(i).getLmCode(), widgetLmCode, "lmCode mismatch");
            productCardIndex++;
        }
        return this;
    }

    private ExtendedProductCardPage shouldNavigationBtnHasCorrectCondition(Direction direction, boolean isVisible) {
        if (direction.equals(Direction.FORWARD)) {
            if (isVisible) {
                anAssert.isElementVisible(productCardsListRightPaginationBtn);
            } else {
                anAssert.isElementNotVisible(productCardsListRightPaginationBtn);
            }
        } else {
            if (isVisible) {
                anAssert.isElementVisible(productCardsListLeftPaginationBtn);
            } else {
                anAssert.isElementNotVisible(productCardsListLeftPaginationBtn);
            }
        }
        return this;
    }

    @Override
    public void shouldProductCardContainsAllData(CatalogProductData data) throws Exception {
        super.shouldProductCardContainsAllData(data);
        //get prices from data and convert them to Front-end format
        Double salesPrice = data.getSalesPrice().getPrice();
        Double pricePerUnit = data.getAltPrice().getPrice();
        Double recommendedPrice = data.getRecommendedPrice().getPrice();
        Double purchasePrice = data.getPurchasePrice().getPrice();
        //get price containers from front-end
        PriceContainerData salesPriceContainerData = productPriceInfoWidget.getPriceContainerData();
        PriceContainerData pricePerUnitContainerData = productPriceInfoWidget.getPricePerUnitContainerData();
        PriceContainerData recommendedPriceContainerData = productPriceInfoWidget.getHiddenRecommendedPriceContainerData();
        PriceContainerData purchasePriceContainerData = productPriceInfoWidget.getHiddenPurchasePriceContainerData();

        softAssert.isElementTextContains(topBadge, data.getTop());
        softAssert.isEquals(salesPriceContainerData.getPrice(), salesPrice, "SalePrice mismatch");
        shouldCurrencyIsCorrect(salesPriceContainerData.getCurrency(), data.getSalesPrice().getPriceCurrency());
        shouldUnitsIsCorrect(salesPriceContainerData.getUnits(), data.getSalesPrice().getPriceUnit());
        softAssert.isEquals(pricePerUnitContainerData.getPrice(), pricePerUnit, "AltPrice mismatch");
        shouldCurrencyIsCorrect(pricePerUnitContainerData.getCurrency(), data.getAltPrice().getPriceCurrency());
        softAssert.isEquals(recommendedPriceContainerData.getPrice(), recommendedPrice, "RecommendedPrice mismatch");
        shouldCurrencyIsCorrect(recommendedPriceContainerData.getCurrency(), data.getRecommendedPrice().getPriceCurrency());
        shouldUnitsIsCorrect(recommendedPriceContainerData.getUnits(), data.getRecommendedPrice().getPriceUnit());
        softAssert.isEquals(purchasePriceContainerData.getPrice(), purchasePrice, "PurchasePrice mismatch");
        shouldCurrencyIsCorrect(purchasePriceContainerData.getCurrency(), data.getPurchasePrice().getPriceCurrency());
        shouldPriceChangeDateIsCorrect(data.getSalesPrice().getDateOfChange());
        softAssert.isEquals(productPriceInfoWidget.isMismatchPriceThanRecommendedTooltipVisible(), !recommendedPrice.equals(salesPrice),
                "SalePrice and RecommendedPrice mismatch lbl is invisible");
        softAssert.isEquals(productPriceInfoWidget.getReasonOfChange(), data.getSalesPrice().getReasonOfChange(),
                "Price reason of change mismatch");
        shouldStocksIsCorrect(data);
        shouldSupplierDataIsCorrect(data.getSupplier());
        softAssert.verifyAll();
    }

    private void shouldCurrencyIsCorrect(String priceCurrency, String currency) {
        if (priceCurrency != null) {
            switch (currency) {
                case "RUR":
                    softAssert.isEquals(Currency.RUB.getName(), priceCurrency, "Currency mismatch");
                    break;
                default:
                    throw new AssertionError("Undefined currency");
            }
        }
    }

    private void shouldUnitsIsCorrect(String priceUnit, String unit) {
        if (priceUnit != null) {
            switch (unit) {
                case "EA":
                    softAssert.isEquals(Units.EA.getRuName(), priceUnit, "Units mismatch");
                    break;
                default:
                    throw new AssertionError("Undefined unit");
            }
        }
    }

    private void shouldPriceChangeDateIsCorrect(String date) {
        Locale locale = new Locale("ru");
        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern("d MMM", locale);
        DateTimeFormatter longFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", locale);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

        LocalDateTime dataDate = LocalDateTime.parse(date, inputFormatter);
        if (DriverFactory.isGridProfile()) {
            dataDate = dataDate.plusHours(com.leroy.constants.TimeZone.UTC);
        } else {
            dataDate = dataDate.plusHours(com.leroy.constants.TimeZone.MSC);
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        calendar.setTime(new Date());
        int currentYear = calendar.get(java.util.Calendar.YEAR);

        String formattedDataDate;
        if (dataDate.getYear() == currentYear) {
            formattedDataDate = shortFormatter.format(dataDate);
        } else {
            formattedDataDate = longFormatter.format(dataDate);
        }
        String viewDate = productPriceInfoWidget.getLastPriceChangeDateLbl();
        softAssert.isContainsIgnoringCase(viewDate, formattedDataDate, "Даты не совпадают: отображается " + viewDate + " ожидаемая дата: " + formattedDataDate);
    }

    private void shouldStocksIsCorrect(CatalogProductData data) {
        StocksData stockData = productQuantityInfoWidget.getDataFromWidget();
        StockAreas stockAreas = data.getStocks();
        stockAreas.replaceNull();
        softAssert.isEquals(stockData.getAvailableForSale(), data.getAvailableStock(), "Доступный остаток");
        softAssert.isEquals(stockData.getSaleHall(), stockAreas.getLs(), "Торговый зал");
        softAssert.isEquals(stockData.getRm(), stockAreas.getRm(), "Склад RM");
        softAssert.isEquals(stockData.getEm(), stockAreas.getEm(), "Склад EM");
        softAssert.isEquals(stockData.getRd(), stockAreas.getRd(), "Склад RD");
        ExtStocks extStocks = data.getExtStocks();
        Integer unavailableStockSum = (extStocks.getWhb() == null ? 0 : extStocks.getWhb()) +
                (extStocks.getWhbp() == null ? 0 : extStocks.getWhbp()) +
                (extStocks.getCor() == null ? 0 : extStocks.getCor()) +
                (extStocks.getTsfOutbound() == null ? 0 : extStocks.getTsfOutbound()) +
                (extStocks.getRtv() == null ? 0 : extStocks.getRtv()) +
                (extStocks.getUtsp() == null ? 0 : extStocks.getUtsp()) +
                (extStocks.getTbc() == null ? 0 : extStocks.getTbc()) +
                (extStocks.getExpo() == null ? 0 : extStocks.getExpo());
        softAssert.isEquals(stockData.getUnavailableForSale(), unavailableStockSum, "Недоступный остаток");
    }

    private void shouldSupplierDataIsCorrect(CatalogSupplierData data) throws Exception {
        softAssert.isEquals(data.getSupName(), supplierInfo.get(0).getText(), "Название поставщика");
        softAssert.isEquals("Код поставщика: " + data.getSupCode(), supplierInfo.get(1).getText(), "Код поставщика");
        softAssert.isEquals(ParserUtil.replaceSpecialSymbols(data.getSupPhone()), supplierInfo.get(2).getText(), "Телефон");
        softAssert.isEquals(data.getSupContactName(), supplierInfo.get(3).getText(), "Имя представителя");
        softAssert.isEquals(data.getSupEmail(), supplierInfo.get(4).getText(), "Email");
    }
}
