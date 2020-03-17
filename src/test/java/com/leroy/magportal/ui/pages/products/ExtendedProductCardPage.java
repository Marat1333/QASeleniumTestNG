package com.leroy.magportal.ui.pages.products;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.pages.CartPage;
import com.leroy.magportal.ui.pages.EstimatePage;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import com.leroy.magportal.ui.webelements.widgets.ExtendedProductCardWidget;
import com.leroy.magportal.ui.webelements.widgets.ShopCardWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ExtendedProductCardPage extends ProductCardPage {
    public ExtendedProductCardPage(TestContext context) {
        super(context);
    }

    public enum Tabs{
        SIMILAR_PRODUCTS,
        COMPLEMENT_PRODUCTS,
        PRICES_AND_STOCKS_IN_OTHER_SHOPS;
    }

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][2]")
    Element topBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][3]")
    Element gammaBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][4]")
    Element categoryBadge;

    @WebFindBy(xpath = "//span[contains(text(),'Цена')]/ancestor::div[2]/div[3]")
    PriceContainer productPriceLbl;

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

    @WebFindBy(xpath = "//button[@id='ANALOG']")
    Button similarProducts;

    @WebFindBy(xpath = "//button[@id='COMPLEMENT']")
    Button complementProducts;

    @WebFindBy(xpath = "//button[@id='SHOPS']")
    Button pricesAndStocksInOtherShops;

    @WebFindBy(xpath = "//div[contains(@class, 'BarViewProductCard__container')]", clazz = ExtendedProductCardWidget.class)
    ElementList<ExtendedProductCardWidget> productCards;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//button[contains(@class, 'paginationButton right')]")
    Button productCardsListRightPaginationBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//button[contains(@class, 'paginationButton left')]")
    Button productCardsListLeftPaginationBtn;

    @WebFindBy(xpath = "//input[@placeholder='Номер или название магазина']")
    EditBox searchForShop;

    @WebFindBy(xpath = "//div[contains(@class,'Catalog-NearestShopList__item_container')]", clazz = ShopCardWidget.class)
    ElementList<ShopCardWidget> shopsList;

    @WebFindBy(xpath = "//span[text()='в корзину']")
    Button addProductToCart;

    @WebFindBy(xpath = "//span[text()='в смету']")
    Button addProductToEstimate;

    private PriceContainer getHiddenRecommendedPrice() {
        hiddenRecommendedPrice.click();
        waitUntilContentHasChanged(getPageSource());
        initElements(new CustomLocator(By.xpath(hiddenRecommendedPrice.getXpath())));
        return hiddenRecommendedPrice;
    }

    private PriceContainer getHiddenPurchasePrice() {
        hiddenPurchasingPrice.click();
        return hiddenPurchasingPrice;
    }

    @Step("перейти во вкладку с дополнительной информацией")
    public ExtendedProductCardPage switchExtraInfoTabs(Tabs tab){
        switch(tab){
            case SIMILAR_PRODUCTS:
                similarProducts.click();
                break;
            case COMPLEMENT_PRODUCTS:
                complementProducts.click();
                break;
            case PRICES_AND_STOCKS_IN_OTHER_SHOPS:
                pricesAndStocksInOtherShops.click();
                break;
        }
        return this;
    }

    @Step("искать магазин по {value}")
    public ExtendedProductCardPage searchShop(String value){
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


}
