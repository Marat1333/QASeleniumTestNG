package com.leroy.magportal.ui.pages.products;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.products.widget.ShopCardWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class ProductCardPage extends MenuPage {
    public ProductCardPage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//span[contains(text(), 'К результатам поиска')]")
    Button backToSearchResults;

    @WebFindBy(xpath = "//span[contains(text(), 'Каталог товаров')]/ancestor::div[2]/div", clazz = Button.class)
    ElementList<Button> nomenclaturePath;

    @WebFindBy(xpath = "//div[contains(@id,'barCodeButton')]/ancestor::div[3]/preceding-sibling::div" +
            "//span[contains(@class, 'LmCode')]/following-sibling::span")
    Element lmCodeLbl;

    @WebFindBy(xpath = "//div[@id='barCodeButton']/div[1]/span")
    Element barCodeLbl;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][1]")
    Element nomenclatureBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][2]")
    Element gammaBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][3]")
    Element categoryBadge;

    @WebFindBy(xpath = "//p[contains(text(), 'Характеристики')]/ancestor::div[3]/preceding-sibling::span")
    Element productTitle;

    @WebFindBy(xpath = "//span[contains(text(),'ВСЕ ХАРАКТЕРИСТИКИ')]")
    Element showAllSpecifications;

    @WebFindBy(xpath = "//span[contains(text(),'ВСЕ ОПИСАНИЕ')]")
    Element showFullDescription;

    @WebFindBy(xpath = "//button[@id='SHOPS']")
    Button pricesAndStocksInOtherShops;

    @WebFindBy(xpath = "//input[@placeholder='Номер или название магазина']")
    EditBox searchForShop;

    @WebFindBy(xpath = "//div[contains(@class,'Catalog-NearestShopList__item_container')]", clazz = ShopCardWidget.class)
    ElementList<ShopCardWidget> shopsList;

    @Override
    public void waitForPageIsLoaded() {
        pricesAndStocksInOtherShops.waitForVisibility();
    }

    @Override
    public boolean navigateBack() throws InterruptedException {
        boolean result = super.navigateBack();
        waitForPageIsLoaded();
        return result;
    }

    @Override
    public boolean navigateForward() throws InterruptedException {
        boolean result = super.navigateForward();
        waitForPageIsLoaded();
        return result;
    }

    /*@Step("Вернуться к результатам поиска")
    public SearchProductPage backToSearchResult() {
        backToSearchResults.click();
        return new SearchProductPage(context);
    }

    @Step("Перейти на страницу с результатами поиска по элементу номенклатуры {nomenclatureElementName}")
    public SearchProductPage backToNomenclatureElement(String nomenclatureElementName) throws Exception {
        Button targetElement = null;
        for (Button each : nomenclaturePath) {
            try {
                targetElement = (Button) each.findChildElement("/descendant::span[2][contains(text(),'" + nomenclatureElementName + "')]/ancestor::div[1]");
                if (targetElement.isPresent()) {
                    break;
                }
            } catch (NoSuchElementException e) {
                Log.warn("Try to find element");
            }
        }
        targetElement.click();
        return new SearchProductPage(context);
    }

    @Step("Открыть полное описание товара")
    public ProductCardPage showFullDescription() {
        showFullDescription.click();
        return this;
    }

    @Step("Показать все характеристики товара")
    public ProductCardPage showAllSpecifications() {
        showAllSpecifications.click();
        return this;
    }
    */

    //Verifications

    @Step("Проверить наличие поискового критерия в карте товара")
    public ProductCardPage shouldProductCardContainsLmOrBarCode(String text) {
        if (text.matches("\\D+")) {
            anAssert.isElementTextContains(productTitle, text);
        } else {
            String barCode = ParserUtil.strWithOnlyDigits(barCodeLbl.getText());
            anAssert.isTrue(lmCodeLbl.getText().contains(text) ||
                            barCode.equals(text),
                    "Карта товара не содержит критерий поиска " + text);
        }
        return this;
    }

    @Step("Проверить, что страница 'Карта товара' отображается корректно")
    public ProductCardPage verifyRequiredElements() {
        softAssert.areElementsVisible(gammaBadge, productTitle, lmCodeLbl, pricesAndStocksInOtherShops);
        softAssert.areElementsNotVisible(ExtendedProductCardPage.topBadge, ExtendedProductCardPage.addProductToCart,
                ExtendedProductCardPage.addProductToEstimate, ExtendedProductCardPage.availableForSaleLbl,
                ExtendedProductCardPage.productPriceLbl);
        shouldUrlContains("isAllGammaView=true");
        softAssert.verifyAll();
        return this;
    }
}
