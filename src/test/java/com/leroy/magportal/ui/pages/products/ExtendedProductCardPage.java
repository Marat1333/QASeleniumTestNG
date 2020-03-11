package com.leroy.magportal.ui.pages.products;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import com.leroy.magportal.ui.webelements.widgets.ExtendedProductCardWidget;
import com.leroy.magportal.ui.webelements.widgets.ShopCardWidget;

public class ExtendedProductCardPage extends ProductCardPage {
    public ExtendedProductCardPage(TestContext context) {
        super(context);
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

    @WebFindBy(xpath = "//div[contains(@class, 'BarViewProductCard__container')]")
    ElementList<ExtendedProductCardWidget> productCards;

    @WebFindBy(xpath = "//input[@placeholder='Номер или название магазина']")
    EditBox searchForShop;

    @WebFindBy(xpath = "//div[contains(@class,'Catalog-NearestShopList__item_container')]")
    ElementList<ShopCardWidget> shopsList;

    @WebFindBy(xpath = "//span[text()='в корзину']")
    Button addProductToCart;

    @WebFindBy(xpath = "//span[text()='в смету']")
    Button addProductToEstimate;

}
