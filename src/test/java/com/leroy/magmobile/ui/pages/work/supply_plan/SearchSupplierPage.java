package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SearchHistoryElementData;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.SearchHistoryElementWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.SupplierSearchResultWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class SearchSupplierPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "Ты пока ничего не искал(а)")
    Element emptySearchHistoryLbl;

    @AppFindBy(accessibilityId = "ScreenTitle-SuppliesSearch")
    EditBox searchInput;

    @AppFindBy(accessibilityId = "Button")
    Button clearSearchInput;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenContent\"]/android.view.ViewGroup[2]//android.widget.TextView")
    Button selectDepartmentBtn;

    AndroidScrollView<String> mainScrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR);

    AndroidScrollView<SearchHistoryElementData> searchHistory = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup",
            SearchHistoryElementWidget.class);

    @AppFindBy(containsText = " СКЛАД")
    Element warehouseAnchorLbl;

    AndroidScrollView<String> warehouseSearchResultList = new AndroidScrollView<>(driver,
            By.xpath(AndroidScrollView.TYPICAL_XPATH + "//*[contains(@text,' - ')]/.."));

    @AppFindBy(containsText = " МАГАЗИН")
    Element shopsAnchorLbl;

    AndroidScrollView<String> shopsSearchResultList = new AndroidScrollView<>(driver,
            By.xpath(AndroidScrollView.TYPICAL_XPATH + "//*[contains(@text,' — ')]/.."));

    @AppFindBy(containsText = " ПОСТАВЩИК")
    Element suppliersAnchorLbl;

    AndroidScrollView<SupplierData> suppliersSearchResultList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//*[contains(@text,'Код:')]/..", SupplierSearchResultWidget.class);

    @AppFindBy(text = "Ничего не найдено")
    Element notFoundMsg;

    @Override
    protected void waitForPageIsLoaded() {
        backBtn.waitForVisibility();
        searchInput.waitForVisibility();
    }

    @Step("Ввести {value} в поисковую строку")
    public SearchSupplierPage searchForSupplier(String value) {
        searchInput.clearAndFill(value);
        hideKeyboard();
        waitUntilProgressBarIsInvisible();
        return new SearchSupplierPage();
    }

    @Step("Выбрать поставщика по коду")
    public SupplierWeekSuppliesPage goToSupplierWeekSuppliesPage(String id) {
        E(String.format("//android.widget.ScrollView//*[contains(@text,'%s')]", id)).click();
        return new SupplierWeekSuppliesPage();
    }

    @Step("Создать историю поиска")
    public SearchSupplierPage createSearchHistory(List<String> searchRequests) {
        for (String eachReq : searchRequests) {
            searchForSupplier(eachReq);
            SupplierWeekSuppliesPage supplierWeekSuppliesPage = goToSupplierWeekSuppliesPage(eachReq);
            supplierWeekSuppliesPage.clearTitle();
        }
        return new SearchSupplierPage();
    }

    @Step("Перейти по элементу из истории поиска")
    public SupplierWeekSuppliesPage goToSupplierWeekPageBySearchHistory(String id) {
        E(String.format("//android.widget.ScrollView//*[contains(@text,'Код: %s')]", id)).click();
        return new SupplierWeekSuppliesPage();
    }

    @Step("проверить результат поиска")
    public SearchSupplierPage shouldDataIsCorrect(List<SupplierData> dataList) throws Exception {
        int warehousesCount = 0;
        int shopsCount = 0;
        int suppliersCount = 0;

        for (SupplierData eachData : dataList) {
            String type = eachData.getType();
            switch (type) {
                case "WH":
                    warehousesCount++;
                    break;
                case "ST":
                    shopsCount++;
                    break;
                case "SUPP":
                    suppliersCount++;
                    break;
                default:
                    throw new IllegalArgumentException("Wrong type");
            }
        }
        SupplierData data;

        if (warehousesCount > 0) {
            mainScrollView.scrollDownToElement(warehouseAnchorLbl);
            softAssert.isElementTextContains(warehouseAnchorLbl, String.valueOf(warehousesCount));
            if (warehouseSearchResultList.isVisible()) {
                List<String> uiWarehouseData = warehouseSearchResultList.getFullDataList(warehousesCount);
                for (String eachUiData : uiWarehouseData) {
                    data = dataList.get(0);
                    softAssert.isEquals(data.getSupplierId() + " - " + data.getName(), eachUiData, "warehouse");
                    dataList.remove(0);
                }
            } else {
                throw new AssertionError("warehouses not found");
            }
        }

        mainScrollView.scrollToBeginning();
        if (shopsCount > 0) {
            mainScrollView.scrollDownToElement(shopsAnchorLbl);
            softAssert.isElementTextContains(shopsAnchorLbl, String.valueOf(shopsCount));
            if (shopsSearchResultList.isVisible()) {
                List<String> uiShopsData = shopsSearchResultList.getFullDataList(shopsCount);
                for (String eachUiData : uiShopsData) {
                    data = dataList.get(0);
                    softAssert.isEquals(data.getSupplierId() + " — " + data.getName(), eachUiData, "shop");
                    dataList.remove(0);
                }
            } else {
                throw new AssertionError("shops not found");
            }
        }

        mainScrollView.scrollToBeginning();
        if (suppliersCount > 0) {
            mainScrollView.scrollDownToElement(suppliersAnchorLbl);
            softAssert.isElementTextContains(suppliersAnchorLbl, String.valueOf(suppliersCount));
            if (suppliersSearchResultList.isVisible()) {
                List<SupplierData> uiSuppliersData = suppliersSearchResultList.getFullDataList(suppliersCount);
                for (SupplierData eachUiData : uiSuppliersData) {
                    data = dataList.get(0);
                    softAssert.isEquals(data.getName(), eachUiData.getName(), "supplier name");
                    softAssert.isEquals("Код: " + data.getSupplierId(), eachUiData.getSupplierId(), "supplier code");
                    dataList.remove(0);
                }
            } else {
                throw new AssertionError("suppliers not found");
            }
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отображено сообщение: \"Ничего не найдено\"")
    public SearchSupplierPage shouldNotFoundMsgIsDisplayed() {
        anAssert.isElementVisible(notFoundMsg);
        return this;
    }

    @Step("Проверить, что отображена надпись:\"Ты пока ничего не искал\"")
    public SearchSupplierPage shouldFirstSearchMsgBeVisible() {
        anAssert.isElementVisible(emptySearchHistoryLbl);
        return this;
    }

    @Step("Проверить историю поиска")
    public SearchSupplierPage shouldSearchHistoryIsCorrect(List<String> searchHistory) throws Exception {
        List<SearchHistoryElementData> searchHistoryData = this.searchHistory.getFullDataList();
        for (int i = 0; i < searchHistory.size(); i++) {
            softAssert.isEquals(searchHistory.get(i), searchHistoryData.get(searchHistoryData.size() - 1 - i).getCode(), "id");
        }
        softAssert.verifyAll();
        return this;
    }

    public SearchSupplierPage verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, searchInput);
        softAssert.verifyAll();
        return this;
    }

}
