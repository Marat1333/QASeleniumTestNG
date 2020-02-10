package com.leroy.magmobile.ui.pages.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.widget.SupplierCardWidget;
import io.qameta.allure.Step;

public class SuppliersSearchPage extends BaseAppPage {

    public SuppliersSearchPage(TestContext context) {
        super(context);
    }

    private String SCREEN_CONTENT_XPATH = "//android.view.ViewGroup[@content-desc='ScreenContent']";

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
        waitUntilProgressBarIsInvisible();
    }

    @Step("Найти поставщика по {value} и выбрать его")
    public void searchForSupplier(String value) {
        searchString.clearFillAndSubmit(value);
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
        E(SCREEN_CONTENT_XPATH + "//android.widget.TextView[contains(@text, '" + value + "')]")
                .click();
    }

    @Step("Подтвердить выбор")
    public FilterPage applyChosenSupplier() {
        confirmBtn.click();
        return new FilterPage(context);
    }

    public SuppliersSearchPage verifyRequiredElements() {
        softAssert.isElementVisible(searchString);
        softAssert.verifyAll();
        return this;
    }

    @Step("Поставщик с кодом/именем {value} выбран")
    public SuppliersSearchPage shouldSupplierCheckboxIsSelected(String value) {
        Element anchorElement = E(String.format(SCREEN_CONTENT_XPATH + SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_SELECTED.getPictureName());
        return this;
    }

    @Step("Проверить кол-во результатов поиска. Должно быть = {count}")
    public SuppliersSearchPage shouldCountOfSuppliersIs(int count) {
        anAssert.isEquals(supplierCards.getCount(), count,
                "Неверное кол-во поставщиков на странице");
        return this;
    }

    @Step("найденный поставщик содержит критерий поиска {text}")
    public void shouldSupplierCardsContainText(String text) {
        String[] searchWords = null;
        if (text.contains(" "))
            searchWords = text.split(" ");
        anAssert.isFalse(E("contains(не найдено)").isVisible(), "Должен быть найден хотя бы один товар");
        anAssert.isTrue(supplierCards.getCount() > 0,
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
