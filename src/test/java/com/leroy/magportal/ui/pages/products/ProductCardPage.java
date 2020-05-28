package com.leroy.magportal.ui.pages.products;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.catalog.Characteristic;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magportal.ui.models.search.NomenclaturePath;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.products.widget.ShopCardWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class ProductCardPage extends MenuPage {

    @WebFindBy(xpath = "//span[contains(text(), 'К результатам поиска')]")
    Button backToSearchResults;

    @WebFindBy(xpath = "//span[contains(text(), 'Каталог товаров')]/ancestor::div[2]/div", clazz = Button.class)
    ElementList<Button> nomenclaturePath;

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

    //DATA
    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][1]")
    Element nomenclatureBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][2]")
    Element gammaBadge;

    @WebFindBy(xpath = "//div[contains(@id,'barCodeButton')]/ancestor::div[3]/preceding-sibling::div" +
            "//span[contains(@class, 'LmCode')]/following-sibling::span")
    Element lmCodeLbl;

    @WebFindBy(xpath = "//div[@id='barCodeButton']/div[1]/span")
    Element barCodeLbl;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][3]")
    Element categoryBadge;

    @WebFindBy(xpath = "//p[contains(text(), 'Характеристики')]/ancestor::div[3]/preceding-sibling::span")
    Element productTitle;

    @WebFindBy(xpath = "//p[contains(text(), 'Характеристики')]/following-sibling::div")
    ElementList<Element> characteristics;

    @WebFindBy(xpath = "//p[contains(text(), 'Описание')]/following-sibling::div//*[contains(text(),'')]")
    ElementList<Element> description;


    @Override
    public void waitForPageIsLoaded() {
        waitForSpinnerAppearAndDisappear();
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

    public NomenclaturePath getNomenclaturePath() throws Exception {
        List<String> nomenclatureAttributes = new ArrayList<>();
        for (Button attribute : nomenclaturePath) {
            nomenclatureAttributes.add(attribute.findChildElement("./span/span").getText());
        }
        NomenclaturePath nomenclature = new NomenclaturePath();
        nomenclature.setAllDepartments(nomenclatureAttributes.get(0));
        nomenclature.setDepartmentId(nomenclatureAttributes.get(1));
        nomenclature.setSubDepartmentId(nomenclatureAttributes.get(2));
        nomenclature.setClassId(nomenclatureAttributes.get(3));
        nomenclature.setSubClassId(nomenclatureAttributes.get(4));
        return nomenclature;
    }

    @Step("Перейти на страницу поиска с примененным фильтром по номенклатуре {attribute}")
    public SearchProductPage navigateToSearchByNomenclatureAttribute(String attribute) throws Exception {
        for (Button nomenclatureLvl : nomenclaturePath) {
            if (nomenclatureLvl.findChildElement("./span/span").getText().equals(attribute)) {
                nomenclatureLvl.click();
                break;
            }
        }
        return new SearchProductPage();
    }

    /*@Step("Вернуться к результатам поиска")
    public SearchProductPage backToSearchResult() {
        backToSearchResults.click();
        return new SearchProductPage(context);
    }*/

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

    //Verifications

    @Step("Проверить наличие поискового критерия в карте товара")
    public ProductCardPage shouldProductCardContainsLmOrBarCode(String text) {
        if (text.matches("\\D+")) {
            anAssert.isElementTextContains(productTitle, text);
        } else {
            String barCode = ParserUtil.strWithOnlyDigits(barCodeLbl.getText());
            anAssert.isTrue(lmCodeLbl.getText().contains(text) ||
                            barCode.equals(text), "Карта товара не содержит критерий поиска " + text);
        }
        return this;
    }

    @Step("Проверить, что все данные по товару корректно отобразились")
    public void shouldProductCardContainsAllData(CatalogProductData data) throws Exception {
        shouldCharacteristicsIsVisible(data.getCharacteristics());
        shouldDescriptionIsVisible(data.getDescription());
    }

    @Step("Проверить, что характеристики товара отображены")
    private void shouldCharacteristicsIsVisible(List<Characteristic> characteristics) throws Exception {
        if (!showAllSpecifications.isVisible()) {
            showAllSpecifications();
        }
        anAssert.isEquals(this.characteristics.getCount(), characteristics.size(), "data and viewData size mismatch");
        Element key, value;
        for (int i = 0; i < this.characteristics.getCount(); i++) {
            key = this.characteristics.get(i).findChildElement("./div[1]/p");
            value = this.characteristics.get(i).findChildElement("./div[2]/p");
            anAssert.isElementTextEqual(key, characteristics.get(i).getName() + ":");
            anAssert.isElementTextEqual(value, characteristics.get(i).getValue());
        }
    }

    @Step("Проверить, что описание товара отображено")
    private void shouldDescriptionIsVisible(String description) {
        if (!showFullDescription.isVisible()) {
            showFullDescription();
        }
        String eachDescriptionElementText;
        for (Element each : this.description) {
            eachDescriptionElementText = each.getText();
            anAssert.isElementTextContainsIgnoringCase(description, eachDescriptionElementText,
                    "description hasn`t cantains " + eachDescriptionElementText);
        }
    }

    @Step("Проверить, что страница 'Карта товара' отображается корректно")
    public ProductCardPage verifyRequiredElements() {
        softAssert.areElementsVisible(gammaBadge, productTitle, lmCodeLbl, pricesAndStocksInOtherShops);
        softAssert.areElementsNotVisible(ExtendedProductCardPage.topBadge, ExtendedProductCardPage.addProductToCart,
                ExtendedProductCardPage.addProductToEstimate, ExtendedProductCardPage.productPriceInfoWidget,
                ExtendedProductCardPage.productQuantityInfoWidget);
        shouldUrlContains("isAllGammaView=true");
        softAssert.verifyAll();
        return this;
    }
}
