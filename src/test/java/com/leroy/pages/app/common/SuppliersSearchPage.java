package com.leroy.pages.app.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.common.widget.SupplierCardWidget;
import io.qameta.allure.Step;

public class SuppliersSearchPage extends BaseAppPage {

    public SuppliersSearchPage (TestContext context){
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[2]/ancestor::android.view.ViewGroup[1]",
            clazz = SupplierCardWidget.class)
    private ElementList<SupplierCardWidget> supplierCards;

    @AppFindBy(accessibilityId = "ScreenTitle-SuppliesSearch")
    EditBox searchString;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button-container\"]/android.view.ViewGroup")
    Element confirmBtn;

    @Override
    public void waitForPageIsLoaded() {
        searchString.waitForVisibility();
    }

    @Step("Найти поставщика по {value} и выбрать его")
    public void searchSupplier(String value){
        searchString.clearFillAndSubmit(value);
        searchString.clear();
        hideKeyboard();

        if (value.matches("\\d+")) {
            Element supplierByCode = E("contains("+value+")");
            supplierByCode.click();
        }else {
            Element supplierByName = E("contains("+value+")");
            supplierByName.click();
        }
    }

    @Step("Подтвердить выбор")
    public FilterPage applyChosenSupplier(){
        confirmBtn.click();
        return new FilterPage(context);
    }

    @Override
    public SuppliersSearchPage verifyRequiredElements(){
        softAssert.isElementVisible(searchString);
        softAssert.verifyAll();
        return this;
    }

    public SuppliersSearchPage verifyElementIsSelected(String value){
        Element anchorElement = E(String.format(SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH,value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_SELECTED.getPictureName());
        return this;
    }

    public SuppliersSearchPage shouldCountOfProductsOnPageMoreThan(int count) {
        anAssert.isTrue(supplierCards.getCount() > count,
                "Кол-во товаров на экране должно быть больше " + count);
        return this;
    }

    public void shouldProductCardsContainText(String text) {
        String[] searchWords = null;
        if (text.contains(" "))
            searchWords = text.split(" ");
        anAssert.isFalse(E("contains(не найдено)").isVisible(), "Должен быть найден хотя бы один товар");
        anAssert.isTrue(supplierCards.getCount() > 1,
                "Ничего не найдено для " + text);
        for (SupplierCardWidget card : supplierCards) {
            if (searchWords != null) {
                for (String each : searchWords) {
                    anAssert.isTrue(card.getName().toLowerCase().contains(each.toLowerCase()),
                            String.format("Товар с кодом %s не содержит текст %s", card.getNumber(), text));
                }
            } else {
                anAssert.isTrue(card.getName().contains(text) || card.getNumber().contains(text),
                        String.format("Товар с кодом %s не содержит текст %s", card.getNumber(), text));
            }
        }
    }
}
