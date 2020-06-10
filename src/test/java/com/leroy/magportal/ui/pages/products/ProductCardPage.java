package com.leroy.magportal.ui.pages.products;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.*;
import com.leroy.magmobile.api.data.catalog.Characteristic;
import com.leroy.magportal.api.data.catalog.products.CatalogProductData;
import com.leroy.magportal.api.data.catalog.shops.NearestShopsData;
import com.leroy.magportal.ui.models.search.NomenclaturePath;
import com.leroy.magportal.ui.models.search.ShopCardData;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.products.widget.ShopCardWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ProductCardPage extends MenuPage {

    @WebFindBy(xpath = "//span[contains(text(), 'К результатам поиска')]")
    Button backToSearchResults;

    @WebFindBy(xpath = "//span[contains(text(), 'Каталог товаров')]/ancestor::div[2]/div", clazz = Button.class)
    ElementList<Button> nomenclaturePath;

    @WebFindBy(xpath = "//span[contains(text(),'ВСЕ ХАРАКТЕРИСТИКИ')]/..")
    Element showAllSpecifications;

    @WebFindBy(xpath = "//span[contains(text(),'ВСЕ ОПИСАНИЕ')]/..")
    Element showFullDescription;

    @WebFindBy(xpath = "//button[@id='SHOPS']")
    Button pricesAndStocksInOtherShops;

    @WebFindBy(xpath = "//input[@placeholder='Номер или название магазина']")
    EditBox searchForShop;

    @WebFindBy(xpath = "//div[contains(@class,'Catalog-NearestShopList__item_container')]", clazz = ShopCardWidget.class,
            refreshEveryTime = true)
    ElementList<ShopCardWidget> shopsList;

    //DATA
    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][1]")
    Element nomenclatureBadge;

    @WebFindBy(xpath = "//span[contains(text(),'Гамма')]")
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

    @WebFindBy(xpath = "//p[contains(text(), 'Описание')]/following-sibling::div//*[contains(text(),'') and not(contains(name(),'ul'))]")
    ElementList<Element> description;

    @WebFindBy(xpath = "//*[@id='barCodeButton']")
    Element barCodeListOpenerBtn;

    @WebFindBy(xpath = "//*[@id='barCodeButton']/ancestor::div[@class='lmui-popover__opener']/following-sibling::*//span[contains(text(),'')]")
    ElementList<Element> productBarCodes;

    @WebFindBy(xpath = "//div[contains(@class,'lmui-View lmui-ImageGallery__container')]/div[not(contains(@class,'Preview'))]//img", clazz = Image.class)
    ElementList<Image> productImagesGallery;

    @WebFindBy(xpath = "//div[contains(@class,'imageSlideContainer')]//img", clazz = Image.class)
    ElementList<Image> productPreviewImagesGallery;

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
        showFullDescription.scrollTo();
        showFullDescription.click();
        return this;
    }

    @Step("Показать все характеристики товара")
    public ProductCardPage showAllSpecifications() {
        showAllSpecifications.scrollTo();
        showAllSpecifications.click();
        return this;
    }

    @Step("Искать магазин по критерию {criterion}")
    public void searchShopBy(String criterion) {
        searchForShop.clearAndFill(criterion);
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
        shouldNomenclaturePathIsCorrect(data);
        shouldProductSpecificationsIsDisplayed(data);
        shouldImagesIsVisible(data.getImages());
    }

    @Step("Проверить, что на карточке товары отображены изображения")
    private void shouldImagesIsVisible(List<String> images) throws Exception {
        String imageLink;
        Image img;
        String attributeValue;
        for (int i = 0; i < images.size(); i++) {
            img = productImagesGallery.get(i);
            attributeValue = img.findChildElement("/ancestor::div[3]").getAttribute("aria-hidden");
            if (i == 0) {
                anAssert.isContainsIgnoringCase(attributeValue, "false", "Изображение не отображено");
            } else {
                anAssert.isContainsIgnoringCase(attributeValue, "true", "Изображение не отображено");
            }
            imageLink = images.get(i);
            anAssert.isEquals(imageLink, img.getLink(), "Ссылки на изображения не совпадают");
            anAssert.isEquals(imageLink, productPreviewImagesGallery.get(i).getLink(), "Ссылки на изображения не совпадают");
        }
    }

    @Step("Проверить, что признаки товара отображены")
    private void shouldProductSpecificationsIsDisplayed(CatalogProductData data) throws Exception {
        softAssert.isElementTextContains(gammaBadge, data.getGamma());
        softAssert.isEquals(productTitle.getText(), data.getTitle(), "Название отображено некорректно");
        softAssert.isEquals(lmCodeLbl.getText(), data.getLmCode(), "LmCode отображен некорректно");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(barCodeLbl.getText()), data.getBarCode(), "BarCode отображен некорректно");
        barCodeListOpenerBtn.click();
        List<String> barCodes = data.getBarCodes();
        for (int i = 0; i < productBarCodes.getCount(); i++) {
            anAssert.isEquals(ParserUtil.strWithOnlyDigits(productBarCodes.get(i).getText()), barCodes.get(i), "BarCode отображен некорректно");
        }
        barCodeListOpenerBtn.click();
        softAssert.verifyAll();
    }

    @Step("Проверить, что товарная иерархия продукта отображена")
    private void shouldNomenclaturePathIsCorrect(CatalogProductData data) throws Exception {
        NomenclaturePath path = getNomenclaturePath();
        String departmentId = path.getDepartmentId();
        String subDepartmentId = path.getSubDepartmentId();
        String classId = path.getClassId();
        String subClassId = path.getSubClassId();
        while (departmentId.startsWith("0")) {
            departmentId = departmentId.substring(1);
        }
        while (subDepartmentId.startsWith("0")) {
            subDepartmentId = subDepartmentId.substring(1);
        }
        while (classId.startsWith("0")) {
            classId = classId.substring(1);
        }
        while (subClassId.startsWith("0")) {
            subClassId = subClassId.substring(1);
        }
        softAssert.isElementTextContains(nomenclatureBadge, departmentId);
        softAssert.isContainsIgnoringCase(departmentId, data.getGroupId(), "Отделы не равны");
        softAssert.isContainsIgnoringCase(subDepartmentId, data.getDepartmentId(), "Подотделы не равны");
        softAssert.isContainsIgnoringCase(classId, data.getClassId(), "Типы не равны");
        softAssert.isContainsIgnoringCase(subClassId, data.getSubclassId(), "Подтипы не равны");
        softAssert.verifyAll();
    }

    @Step("Проверить, что характеристики товара отображены")
    private void shouldCharacteristicsIsVisible(List<Characteristic> characteristics) throws Exception {
        if (showAllSpecifications.isVisible()) {
            showAllSpecifications();
        }
        anAssert.isEquals(this.characteristics.getCount(), characteristics.size(), "data and viewData size mismatch");
        Element key, value;
        for (int i = 0; i < this.characteristics.getCount(); i++) {
            key = this.characteristics.get(i).findChildElement("./div[1]/p");
            value = this.characteristics.get(i).findChildElement("./div[2]/p");
            anAssert.isEquals(key.getText(), characteristics.get(i).getName() + ":", "Характеристика отличается");
            anAssert.isEquals(value.getText(), characteristics.get(i).getValue(), "Значение характеристики");
        }
    }

    @Step("Проверить, что описание товара отображено")
    private void shouldDescriptionIsVisible(String description) {
        description = ParserUtil.replaceSpecialSymbols(description);
        if (showFullDescription.isVisible()) {
            showFullDescription();
        }
        String eachDescriptionElementText;
        for (Element each : this.description) {
            eachDescriptionElementText = each.getText();
            anAssert.isContainsIgnoringCase(description, eachDescriptionElementText,
                    description + " hasn`t cantains " + eachDescriptionElementText);
        }
    }

    @Step("Проверить, что страница 'Карта товара' отображается корректно")
    public ProductCardPage verifyRequiredElements() {
        ExtendedProductCardPage extendedProductCardPage = new ExtendedProductCardPage();
        softAssert.areElementsVisible(gammaBadge, productTitle, lmCodeLbl, pricesAndStocksInOtherShops);
        softAssert.areElementsNotVisible(extendedProductCardPage.topBadge, extendedProductCardPage.addProductToCart,
                extendedProductCardPage.addProductToEstimate, extendedProductCardPage.productPriceInfoWidget,
                extendedProductCardPage.productQuantityInfoWidget);
        shouldUrlContains("isAllGammaView=true");
        softAssert.verifyAll();
        return this;
    }

    public void shouldNearestShopInfoIsCorrect(List<NearestShopsData> dataList) throws Exception {
        ShopCardData data;
        NearestShopsData nearestShopsData;
        TreeMap<Double, Integer> sortedByDistanceShopId = new TreeMap<>();
        for (NearestShopsData tmp : dataList) {
            sortedByDistanceShopId.put(tmp.getDistance(), tmp.getId());
        }
        List<Integer> shopIdList = new ArrayList<>(sortedByDistanceShopId.values());
        for (int i = 0; i < dataList.size(); i++) {
            data = shopsList.get(i).grabDataFromWidget();
            nearestShopsData = dataList.get(i);
            anAssert.isEquals(data.getId(), shopIdList.get(i), "Sort mismatch");
            anAssert.isEquals(data.getId(), nearestShopsData.getId(), "ID");
            anAssert.isEquals(data.getName(), nearestShopsData.getName(), "City name");
            anAssert.isEquals(data.getAddress(), nearestShopsData.getCityName() + ", " +
                    ParserUtil.replaceSpecialSymbols(nearestShopsData.getAddress()), "Address");
            anAssert.isEquals(data.getPrice(), nearestShopsData.getPrice(), "Price");
            anAssert.isEquals(data.getQuantity(), nearestShopsData.getAvailableStock(), "Stocks");
            anAssert.isEquals(data.getDistance(),
                    BigDecimal.valueOf(nearestShopsData.getDistance()).setScale(1, RoundingMode.HALF_UP).doubleValue(),
                    "Distance");
        }
    }

    public void shouldFoundShopsIsCorrect(String criterion) {
        ShopCardData data;
        for (ShopCardWidget tmp : shopsList) {
            data = tmp.grabDataFromWidget();
            if (criterion.matches("\\^.?+D+.?+")) {
                anAssert.isContainsIgnoringCase(data.getName(), criterion, "Name expected: " + criterion
                        + "actual" + data.getName());
            } else {
                anAssert.isTrue(String.valueOf(data.getId()).contains(criterion)||data.getName().contains(criterion), "ID expected: "
                        + criterion + " actual" + data.getId());
            }
        }
    }

}
