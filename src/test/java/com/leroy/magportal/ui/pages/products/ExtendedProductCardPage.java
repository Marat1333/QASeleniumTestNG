package com.leroy.magportal.ui.pages.products;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.products.widget.ExtendedProductCardWidget;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import io.qameta.allure.Step;

public class ExtendedProductCardPage extends ProductCardPage {
    public ExtendedProductCardPage(Context context) {
        super(context);
        waitForPageIsLoaded();
    }

    public enum Tab {
        SIMILAR_PRODUCTS,
        COMPLEMENT_PRODUCTS,
        PRICES_AND_STOCKS_IN_OTHER_SHOPS
    }

    @WebFindBy(xpath = "//span[contains(@class, 'Badge') and contains(text(),'Топ')]")
    static Element topBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][3]")
    Element gammaBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][4]")
    Element categoryBadge;

    @WebFindBy(xpath = "//span[contains(text(),'Цена')]/ancestor::div[2]/div[3]")
    static PriceContainer productPriceLbl;

    @WebFindBy(xpath = "//span[contains(text(),'Цена')]/ancestor::div[1]/following-sibling::div[1]/span")
    PriceContainer lastPriceChangeDateLbl;

    @WebFindBy(xpath = "//span[contains(text(),'Цена')]/ancestor::div[2]/following-sibling::div[2]" +
            "/div[contains(@class, 'textAlign')]")
    PriceContainer pricePerUnit;

    //сначала кликнуть по нему
    @WebFindBy(xpath = "//span[contains(text(),'Рекомендованная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]" +
            "//div[contains(@class,'ProductCard')][1]")
    PriceContainer hiddenRecommendedPrice;

    //сначала кликнуть по нему
    @WebFindBy(xpath = "//span[contains(text(),'Закупочная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]" +
            "//div[contains(@class,'ProductCard')][1]")
    PriceContainer hiddenPurchasingPrice;

    @WebFindBy(xpath = "//span[contains(text(),'Доступно для продажи')]/../following-sibling::*/span")
    static Element availableForSaleLbl;

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

    /*private PriceContainer getHiddenRecommendedPrice() {
        hiddenRecommendedPrice.click();
        initElements(new CustomLocator(By.xpath(hiddenRecommendedPrice.getXpath())));
        return hiddenRecommendedPrice;
    }

    private PriceContainer getHiddenPurchasePrice() {
        hiddenPurchasingPrice.click();
        return hiddenPurchasingPrice;
    }*/

    @Step("Перейти в карточку аналогичного товара {lmCode}")
    public ExtendedProductCardPage goToAdditionalProduct(String lmCode, Tab tab) throws Exception{
        if (tab.equals(Tab.SIMILAR_PRODUCTS)) {
            anAssert.isElementVisible(similarProducts);
        } else if (tab.equals(Tab.COMPLEMENT_PRODUCTS)) {
            anAssert.isElementVisible(complementProducts);
        } else{
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
        return new ExtendedProductCardPage(context);
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
                }else if ((similarProducts.isVisible() || complementProducts.isVisible()) &&
                        tabContainerList.get(1).getAttribute(attribute).contains(condition)){
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
        return new CartPage(context);
    }

    @Step("Добавить товар в смету")
    public EstimatePage addProductToEstimate() {
        addProductToEstimate.click();
        return new EstimatePage(context);
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
        softAssert.areElementsVisible(topBadge, addProductToCart, addProductToEstimate, productPriceLbl,
                availableForSaleLbl, pricesAndStocksInOtherShops);
        softAssert.verifyAll();
        shouldUrlContains("isAllGammaView=false");
        return this;
    }
}
