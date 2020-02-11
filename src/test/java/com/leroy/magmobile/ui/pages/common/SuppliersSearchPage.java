package com.leroy.magmobile.ui.pages.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidHorizontalScrollView;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.widget.SupplierCardWidget;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import com.leroy.models.SupplierCardData;
import com.leroy.models.TextViewData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class SuppliersSearchPage extends BaseAppPage {

    public SuppliersSearchPage(TestContext context) {
        super(context);
    }

    private String SCREEN_CONTENT_XPATH = "//android.view.ViewGroup[@content-desc='ScreenContent']";

    @AppFindBy(xpath = "//android.widget.TextView[2]/ancestor::android.view.ViewGroup[1]",
            clazz = SupplierCardWidget.class)
    private ElementList<SupplierCardWidget> supplierCards;

    private AndroidScrollView<SupplierCardData> supplierCardScrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR,
            "./descendant::android.view.ViewGroup[7]/android.view.ViewGroup", SupplierCardWidget.class);

    AndroidHorizontalScrollView<TextViewData> suppliersOvalElements = new AndroidHorizontalScrollView<>(driver,
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
    public MyShopFilterPage clickBackBtn() {
        backBtn.click();
        return new MyShopFilterPage(context);
    }

    @Step("Отменить выбор поставщика нажатием на крест в овальной области с именем поставщика")
    public SuppliersSearchPage cancelChosenSuppler() throws Exception {
        Element chosenSupplierCancelBtn = E(OVAL_BTN_DELETE_SUPPLIER_BTN_XPATH);
        chosenSupplierCancelBtn.click();
        return new SuppliersSearchPage(context);
    }

    @Step("Найти поставщика по {value} и выбрать его")
    public SuppliersSearchPage searchAndConfirmSupplier(String value) {
        searchString.clearFillAndSubmit(value);
        searchString.clear();
        hideKeyboard();
        Element supplier = E("contains(" + value + ")");
        supplier.click();
        return new SuppliersSearchPage(context);
    }

    @Step("Подтвердить выбор")
    public FilterPage applyChosenSupplier() {
        confirmBtn.click();
        return new FilterPage(context);
    }

    //VERIFICATIONS

    public SuppliersSearchPage verifyRequiredElements() {
        softAssert.isElementVisible(searchString);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в овальной области отображено имя выбранного поставщика")
    public SuppliersSearchPage shouldNameOfChosenIsDisplayedInOvalElement(String supplierName) {
        List<TextViewData> namesOfSuppliers = suppliersOvalElements.getFullDataList();
        for (TextViewData data : namesOfSuppliers) {
            anAssert.isTrue(supplierName.contains(data.getText()), "В овальной области не отображено имя выбранного поставщика");
        }
        return new SuppliersSearchPage(context);
    }

    @Step("Поставщик с кодом/именем {value} выбран")
    public SuppliersSearchPage shouldSupplierCheckboxIsSelected(String value, boolean isSelected) {
        Element anchorElement = E(String.format(SCREEN_CONTENT_XPATH + SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value));
        if (isSelected) {
            anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_SELECTED.getPictureName());
        } else {
            anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_NOT_SELECTED.getPictureName());
        }
        return this;
    }

    @Step("Проверить кол-во результатов поиска. Должно быть = {count}")
    public SuppliersSearchPage shouldCountOfSuppliersIs(int count) {
        anAssert.isEquals(supplierCards.getCount(), count,
                "Неверное кол-во поставщиков на странице");
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
