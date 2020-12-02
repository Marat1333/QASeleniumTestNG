package com.leroy.magportal.ui.pages.picking.mobile;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class PickingFilterMobilePage extends MagPortalBasePage {

    @WebFindBy(xpath = "//span[contains(@class, 'PickingFiltersFields__textRelative')]",
            metaName = "Загаловок 'Фильтры'")
    Element title;

    private final static String FILTER_BTN_XPATH = "//div[contains(@class, 'PickingFiltersFields__mobile-field-block')]//button[descendant::span[contains(text(), '%s')]]";
    private final static String FILTER_SALES_SCHEME_BTN_XPATH = "//div[contains(@class, 'PickingFiltersFields__mobile-field-block')]//button[descendant::span[text()= '%s']]";

    // Фильтры по типу сборки
    Element shoppingRoomOptionBtn = E(String.format(FILTER_BTN_XPATH, "торг.зал"), "Опция 'Торг зал'");
    Element stockOptionBtn = E(String.format(FILTER_BTN_XPATH, "склад"), "Опция 'Склад'");
    Element ssOptionBtn = E(String.format(FILTER_BTN_XPATH, "CC"), "Опция 'CC'");

    // Фильтры по статусу сборки
    Element allowedForPickingOptionBtn = E(String.format(FILTER_BTN_XPATH, "Готов к сборке"), "Опция 'Готов к сборке'");
    Element pickingOptionBtn = E(String.format(FILTER_BTN_XPATH, "Сборка"), "Опция 'Сборка'");
    Element pickingPauseOptionBtn = E(String.format(FILTER_BTN_XPATH, "Сборка (пауза)"), "Опция 'Сборка (пауза)'");
    Element partiallyPickingOptionBtn = E(String.format(FILTER_BTN_XPATH, "Част. собран"), "Опция 'Част. собран'");
    Element pickedOptionBtn = E(String.format(FILTER_BTN_XPATH, "Собран"), "Опция 'Собран'");

    @WebFindBy(xpath = "//div[contains(@class, 'PickingFiltersFields__mobile-footer')]//button[1]",
            metaName = "Кнопка очистить фильтры (метла)")
    Element clearBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'mobile')]//button[contains(@class, 'PickingFiltersFields__submit-btn')]",
            metaName = "Кнопка 'Применить'")
    Element confirmBtn;

    @Override
    protected void waitForPageIsLoaded() {
        title.waitForVisibility();
    }

    private boolean isButtonActive(Element btn) {
        return btn.getAttribute("class").contains("active");
    }

    private void deselectOption(Element... elements) {
        for (Element el : elements) {
            if (isButtonActive(el))
                el.click();
        }
    }

    private void selectFilter(String value) {
        Element filterBtn = E(String.format(FILTER_BTN_XPATH, value));
        if (!isButtonActive(filterBtn))
            filterBtn.click();
    }

    @Step("Выбрать тип сборки")
    public PickingFilterMobilePage selectAssemblyType(PickingConst.AssemblyType... assemblyTypes) {
        deselectOption(shoppingRoomOptionBtn, stockOptionBtn, ssOptionBtn);
        for (PickingConst.AssemblyType assemblyType : assemblyTypes) {
            if (assemblyType.equals(PickingConst.AssemblyType.SHOPPING_ROOM) &&
                    !isButtonActive(shoppingRoomOptionBtn)) {
                shoppingRoomOptionBtn.click();
            }
            if (assemblyType.equals(PickingConst.AssemblyType.STOCK) &&
                    !isButtonActive(stockOptionBtn)) {
                stockOptionBtn.click();
            }
            if (assemblyType.equals(PickingConst.AssemblyType.SS) &&
                    !isButtonActive(ssOptionBtn)) {
                ssOptionBtn.click();
            }
        }
        return this;
    }

    @Step("Выбрать статус сборки")
    public PickingFilterMobilePage selectPickingStatus(SalesDocumentsConst.PickingStatus... pickingStatuses) {
        deselectOption(allowedForPickingOptionBtn, pickingOptionBtn, pickingPauseOptionBtn,
                partiallyPickingOptionBtn, pickedOptionBtn);
        for (SalesDocumentsConst.PickingStatus pickingStatus : pickingStatuses) {
            if (pickingStatus.equals(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING) &&
                    !isButtonActive(allowedForPickingOptionBtn)) {
                allowedForPickingOptionBtn.click();
            }
            if (pickingStatus.equals(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS) &&
                    !isButtonActive(pickingOptionBtn)) {
                pickingOptionBtn.click();
            }
            if (pickingStatus.equals(SalesDocumentsConst.PickingStatus.PAUSE_PICKING) &&
                    !isButtonActive(pickingPauseOptionBtn)) {
                pickingPauseOptionBtn.click();
            }
            if (pickingStatus.equals(SalesDocumentsConst.PickingStatus.PICKED) &&
                    !isButtonActive(pickedOptionBtn)) {
                pickedOptionBtn.click();
            }
            if (pickingStatus.equals(SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED) &&
                    !isButtonActive(partiallyPickingOptionBtn)) {
                partiallyPickingOptionBtn.click();
            }
        }
        return this;
    }

    @Step("Выбрать фильтр 'Мои'")
    public PickingFilterMobilePage selectFilterMy() {
        selectFilter("Мои");
        return this;
    }

    @Step("Выбрать отдел")
    public PickingFilterMobilePage selectDepartmentFilter(String... departments) {
        for (String department : departments) {
            selectFilter(department);
        }
        return this;
    }

    @Step("Выбрать схему продажи")
    public PickingFilterMobilePage selectSalesScheme(String... schemes) {
        for (String scheme : schemes) {
            Element filterBtn = E(String.format(FILTER_SALES_SCHEME_BTN_XPATH, scheme));
            if (!isButtonActive(filterBtn))
                filterBtn.clickJS();
        }
        return this;
    }

    @Step("Нажать кнопку очистки фильтров (метла)")
    public PickingFilterMobilePage clickClearFilters() {
        clearBtn.click();
        return this;
    }

    @Step("Нажать кнопку 'Применить'")
    public PickingDocListMobilePage clickConfirmBtn() {
        confirmBtn.click();
        waitForSpinnerAppearAndDisappear();
        return new PickingDocListMobilePage();
    }

}
