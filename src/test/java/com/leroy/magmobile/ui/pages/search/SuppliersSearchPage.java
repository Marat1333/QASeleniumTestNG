package com.leroy.magmobile.ui.pages.search;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.android.AndroidHorizontalScrollView;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobGreenCheckBox;
import com.leroy.magmobile.ui.models.SupplierCardData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.search.widgets.SupplierCardWidget;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class SuppliersSearchPage extends CommonMagMobilePage {

    private String SCREEN_CONTENT_XPATH = "//android.view.ViewGroup[@content-desc='ScreenContent']";

    @AppFindBy(xpath = "//android.widget.TextView[2]/ancestor::android.view.ViewGroup[1]",
            clazz = SupplierCardWidget.class)
    private ElementList<SupplierCardWidget> supplierCards;

    private AndroidScrollView<SupplierCardData> supplierCardScrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR,
            "./descendant::android.view.ViewGroup[7]/android.view.ViewGroup", SupplierCardWidget.class);

    AndroidHorizontalScrollView<String> suppliersOvalElements = new AndroidHorizontalScrollView<>(driver,
            By.xpath("//android.widget.HorizontalScrollView"),
            "./descendant::android.view.ViewGroup[2]/android.widget.TextView",
            TextViewWidget.class);

    @AppFindBy(accessibilityId = "ScreenTitle-SuppliesSearch")
    EditBox searchString;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button-container\"]/android.view.ViewGroup")
    Element confirmBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView/descendant::android.view.ViewGroup[3]/android.widget.TextView")
    Element departmentLbl;

    @AppFindBy(accessibilityId = "BackButton", metaName = "Кнопка назад")
    private Element backBtn;

    private final String OVAL_BTN_DELETE_SUPPLIER_BTN_XPATH = "//android.widget.HorizontalScrollView/descendant::android.view.ViewGroup[3]";

    @Override
    public void waitForPageIsLoaded() {
        searchString.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    @Step("Перейти назад")
    public FilterPage clickBackBtn() {
        backBtn.click();
        return new FilterPage();
    }

    @Step("Отменить выбор поставщика нажатием на крест в овальной области с именем поставщика")
    public SuppliersSearchPage cancelChosenSuppler() throws Exception {
        Element chosenSupplierCancelBtn = E(OVAL_BTN_DELETE_SUPPLIER_BTN_XPATH);
        chosenSupplierCancelBtn.click();
        return new SuppliersSearchPage();
    }

    @Step("Найти поставщика по {value} и выбрать его")
    public SuppliersSearchPage searchForAndChoseSupplier(String value) {
        searchString.clearFillAndSubmit(value);
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
        E(SCREEN_CONTENT_XPATH + "//android.widget.TextView[contains(@text, '" + value + "')]")
                .click();
        confirmBtn.waitForVisibility();
        return this;
    }

    @Step("Подтвердить выбор")
    public FilterPage applyChosenSupplier() {
        confirmBtn.click();
        return new FilterPage();
    }

    //VERIFICATIONS

    public SuppliersSearchPage verifyRequiredElements() {
        softAssert.isElementVisible(searchString);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в овальной области отображено имя выбранного поставщика")
    public SuppliersSearchPage shouldNameOfChosenIsDisplayedInOvalElement(String supplierName) throws Exception {
        List<String> namesOfSuppliers = suppliersOvalElements.getFullDataList();
        anAssert.isFalse(namesOfSuppliers.isEmpty(), "Не найдено выбранных поставщиков");
        for (String text : namesOfSuppliers) {
            anAssert.isTrue(supplierName.contains(text),
                    "В овальной области не отображено имя выбранного поставщика");
        }
        return new SuppliersSearchPage();
    }

    @Step("Поставщик с кодом/именем {value} выбран")
    public SuppliersSearchPage shouldSupplierCheckboxIsSelected(String value, boolean isSelected) throws Exception {
        MagMobGreenCheckBox anchorElement = new MagMobGreenCheckBox(driver, new CustomLocator(By.xpath(String.format(SCREEN_CONTENT_XPATH + SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value))));
        if (isSelected) {
            anAssert.isTrue(anchorElement.isChecked(), "Фильтр '" + value + "' должен быть выбран");
        } else {
            anAssert.isTrue(!anchorElement.isChecked(), "Фильтр '" + value + "' не должен быть выбран");
        }
        return this;
    }

    @Step("Проверить кол-во результатов поиска. Должно быть = {count}")
    public SuppliersSearchPage shouldCountOfSuppliersIs(int count) {
        anAssert.isEquals(supplierCards.getCount(), count,
                "Неверное кол-во поставщиков на странице");
        return this;
    }

    @Step("Проверить кол-во результатов поиска. Должно быть > {count}")
    public SuppliersSearchPage shouldCountOfSuppliersIsMoreThan(int count) {
        anAssert.isTrue(supplierCards.getCount() > count,
                "Кол-во виджетов поставщиков меньше указанного");
        return this;
    }

    @Step("Проверить, что сортировка по отделам происходит корректно и первый отдел = отделу пользователя")
    public SuppliersSearchPage shouldSuppliersSortedByDepartmentId(String deptId) {
        anAssert.isTrue(departmentLbl.getText().contains(deptId), "Первый отображаемый отдел не соответствует отделу пользователя, следовательно поставщики не отсортированы по отделам");
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
