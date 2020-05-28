package com.leroy.magportal.ui.pages.products;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.products.SearchProductPage.Direction;
import com.leroy.magportal.ui.pages.products.widget.ExtendedProductCardWidget;
import com.leroy.magportal.ui.webelements.searchelements.ProductPriceInfoWidget;
import com.leroy.magportal.ui.webelements.searchelements.ProductQuantityInfoWidget;
import io.qameta.allure.Step;

import java.util.List;

public class ExtendedProductCardPage extends ProductCardPage {

    public enum Tab {
        SIMILAR_PRODUCTS,
        COMPLEMENT_PRODUCTS,
        PRICES_AND_STOCKS_IN_OTHER_SHOPS
    }

    @WebFindBy(xpath = "//button[@id='ANALOG']")
    static Button similarProducts;

    @WebFindBy(xpath = "//button[@id='COMPLEMENT']")
    static Button complementProducts;

    @WebFindBy(xpath = "//div[contains(@class,'active')]//div[contains(@class, 'BarViewProductCard__container')]",
            clazz = ExtendedProductCardWidget.class, refreshEveryTime = true)
    ElementList<ExtendedProductCardWidget> productCards;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//button[contains(@class, 'paginationButton right')]")
    Button productCardsListRightPaginationBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//button[contains(@class, 'paginationButton left')]")
    Button productCardsListLeftPaginationBtn;

    @WebFindBy(xpath = "//span[text()='в корзину']/ancestor::button[1]")
    static Button addProductToCart;

    @WebFindBy(xpath = "//span[text()='в смету']/ancestor::button[1]")
    static Button addProductToEstimate;

    @WebFindBy(xpath = "//div[contains(@class,'Tabs-Title-outer')]/following-sibling::div")
    ElementList<Element> tabContainerList;

    //DATA

    @WebFindBy(xpath = "//p[contains(text(), 'Код поставщика')]/../*")
    ElementList<Element> supplierInfo;

    @WebFindBy(xpath = "//span[contains(text(),'Цена')]/ancestor::div[contains(@class,'bottom')]")
    static ProductPriceInfoWidget productPriceInfoWidget;

    @WebFindBy(xpath = "//span[contains(text(),'Доступно для продажи')]/ancestor::div[contains(@class,'bottom')]")
    static ProductQuantityInfoWidget productQuantityInfoWidget;

    //TODO remove static modifier
    @WebFindBy(xpath = "//span[contains(@class, 'Badge') and contains(text(),'Топ')]")
    static Element topBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][3]")
    Element gammaBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][4]")
    Element categoryBadge;

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
        while (true) {
            for (ExtendedProductCardWidget widget : productCards) {
                String widgetLmCode = widget.getLmCode();
                if (widgetLmCode.equals(lmCode)) {
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

    //Overloaded
    @Step("Открыть полное описание товара")
    public ExtendedProductCardPage showFullDescription() {
        showFullDescription.click();
        return this;
    }

    //Overloaded
    @Step("Показать все характеристики товара")
    public ExtendedProductCardPage showAllSpecifications() {
        showAllSpecifications.click();
        return this;
    }

    @Override
    public ExtendedProductCardPage verifyRequiredElements() {
        softAssert.areElementsVisible(topBadge, addProductToCart, addProductToEstimate, productPriceInfoWidget,
                productQuantityInfoWidget, pricesAndStocksInOtherShops);
        softAssert.verifyAll();
        shouldUrlContains("isAllGammaView=false");
        return this;
    }

    public ExtendedProductCardPage shouldAllAdditionalProductsIsVisible(List<ProductItemData> data) throws Exception {
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

    public ExtendedProductCardPage shouldNavigationBtnHasCorrectCondition(Direction direction, boolean isVisible) {
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

    }
}
