package com.leroy.magmobile.ui.pages.common;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.widget.SupplierCardWidget;
import com.leroy.models.SupplierCardData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class SuppliersSearchPage extends BaseAppPage {

    public SuppliersSearchPage(TestContext context) {
        super(context);
    }

    private String SCREEN_CONTENT_XPATH = "//android.view.ViewGroup[@content-desc='ScreenContent']";

    @AppFindBy(xpath = "//android.widget.TextView[2]/ancestor::android.view.ViewGroup[1]",
            clazz = SupplierCardWidget.class)
    private ElementList<SupplierCardWidget> supplierCards;

    private AndroidScrollView<SupplierCardData> supplierCardScrollView = new AndroidScrollView<>(driver, new CustomLocator(By.xpath(AndroidScrollView.TYPICAL_XPATH)),
            "./descendant::android.view.ViewGroup[5]/android.view.ViewGroup",SupplierCardWidget.class);

    @AppFindBy(accessibilityId = "ScreenTitle-SuppliesSearch")
    EditBox searchString;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button-container\"]/android.view.ViewGroup")
    Element confirmBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView/descendant::android.view.ViewGroup[3]/android.widget.TextView")
    Element departmentLbl;

    private String supplierName;
    private String supplierCode;

    @Override
    public void waitForPageIsLoaded() {
        searchString.waitForVisibility();
        waitForProgressBarIsInvisible();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupllierCode() {
        return supplierCode;
    }

    @Step("Найти поставщика по {value} и выбрать его")
    public void searchSupplier(String value)throws Exception{
        String pageSource=getPageSource();
        searchString.clearFillAndSubmit(value);
        waitForContentHasChanged(pageSource,short_timeout);
        searchString.clear();
        hideKeyboard();
        pageSource=getPageSource();
        List<SupplierCardData> supplierCardData = supplierCardScrollView.getFullDataList();
        if (value.matches("\\d+")) {
            Element supplierByCode = E("contains("+value+")");
            supplierByCode.click();
            supplierName=supplierCardData.get(supplierCardData.size()-1).getSupplierName();
            supplierCode=supplierCardData.get(supplierCardData.size()-1).getSupplierCode();
        }else {
            Element supplierByName = E("contains("+value+")");
            supplierByName.click();
            supplierName=supplierCardData.get(supplierCardData.size()-1).getSupplierName();
            supplierCode=supplierCardData.get(supplierCardData.size()-1).getSupplierCode();
        }
        waitForContentHasChanged(pageSource,short_timeout);
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

    @Step("Проверить, что сортировка по отделам происходит корректно и первый отдел = отделу пользователя")
    public SuppliersSearchPage shouldSuppliersSortedByDepartmentId(){
        anAssert.isTrue(departmentLbl.getText().contains(EnvConstants.BASIC_USER_DEPARTMENT_ID), "Первый отображаемый отдел не соответствует отделу пользователя, следовательно поставщики не отсортированы по отделам");
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
