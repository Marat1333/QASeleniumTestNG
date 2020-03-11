package com.leroy.magportal.ui.pages.products;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.webelements.MagPortalComboBox;
import com.leroy.magportal.ui.webelements.searchelements.SupplierComboBox;
import com.leroy.magportal.ui.webelements.searchelements.SupplierDropDown;
import com.leroy.magportal.ui.webelements.widgets.CalendarWidget;
import com.leroy.magportal.ui.webelements.widgets.ProductCardWidget;
import com.leroy.magportal.ui.webelements.widgets.SupplierCardWidget;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchProductPage extends MenuPage {
    public SearchProductPage(TestContext context) {
        super(context);
    }

    public enum Filters {
        HAS_AVAILABLE_STOCK("Есть теор. запас"),
        TOP_EM("Топ EM"),
        BEST_PRICE("Лучшая цена"),
        TOP_1000("Топ 1000"),
        LIMITED_OFFER("Предложение ограничено"),
        CTM("Собственная торг. марка"),
        ORDERED("Под заказ (код 48)"),
        AVS("AVS");

        private String name;

        Filters(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Spinner-active')]")
    Element loadingSpinner;

    @WebFindBy(xpath = "//input[@placeholder='ЛМ, название или штрихкод']")
    EditBox searchInput;

    @WebFindBy(xpath = "//button[@id='MyShop']")
    Button myShopFilterBtn;

    @WebFindBy(xpath = "//button[@id='AllGamma']")
    Button allGammaFilterBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]/ancestor::div[2]/div/span[1]/span")
    ElementList<Element> nomenclaturePathButtons;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]/ancestor::div[1]")
    Element allDepartmentsBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'lmui-View-row lmui-View-middle')]" +
            "//span[contains(@class, 'color-mainText') and not(contains(text(), 'Показаны'))]")
    Element currentNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]")
    Element searchByAllDepartmentsFilterBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]/ancestor::span/preceding-sibling::span")
    Element currentSearchByPhraseInNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class, 'Nomenclatures__link-text')]")
    ElementList<Element> nomenclatureElementsList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Гамма']/ancestor::div[1]")
    MagPortalComboBox gammaComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Топ пополнения']/ancestor::div[1]")
    MagPortalComboBox topComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='еще']/ancestor::button")
    Button showAllFilters;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='еще']/ancestor::button/following-sibling::div/span")
    Element filtersCounter;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='ПОКАЗАТЬ ТОВАРЫ']/ancestor::button/preceding-sibling::button")
    Button clearAllFiltersInFilterFrameBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'ПОКАЗАТЬ ТОВАРЫ')]")
    Element applyFiltersBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//label[text()='Поставщик']/ancestor::div[1]")
    SupplierComboBox supplierDropBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Дата AVS']")
    MagPortalComboBox avsDropBox;

    @WebFindBy(xpath = "//div[contains(@class, 'DatePicker__dayPicker')]")
    CalendarWidget avsDropDownCalendar;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]/ancestor::div[2]")
    MagPortalComboBox sortComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]/ancestor::div[6]/following-sibling::button[1]")
    Button extendedViewBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]/ancestor::div[6]/following-sibling::button[2]")
    Button listViewBtn;

    @WebFindBy(xpath = "//span[contains(text(), 'ПОКАЗАТЬ ЕЩЕ')]")
    Element showMoreProductsBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'BarViewProductCard__container')]", clazz = ProductCardWidget.class)
    ElementList<ProductCardWidget> productCardsList;

    public SearchProductPage choseNomenclature(String dept, String subDept, String classId, String subClass) throws Exception {
        allDepartmentsBtn.click();
        if (dept != null) {
            for (Element deptEl : nomenclatureElementsList) {
                if (deptEl.getText().contains(dept)) {
                    deptEl.click();
                    break;
                }
            }
        }
        if (dept != null && subDept != null) {
            for (Element subDeptEl : nomenclatureElementsList) {
                if (subDeptEl.getText().contains(subDept)) {
                    subDeptEl.click();
                    break;
                }
            }
        }
        if (dept != null && subDept != null && classId != null) {
            String refactoredClassId = classId;
            refactoredClassId = refactoredClassId.replaceAll("^0", "");
            for (Element classEl : nomenclatureElementsList) {
                if (classEl.getText().contains(refactoredClassId)) {
                    classEl.click();
                    break;
                }
            }
        }
        if (dept != null && subDept != null && classId != null && subClass != null) {
            String refactoredSubClass = subClass;
            refactoredSubClass = refactoredSubClass.replaceAll("^0", "");
            for (Element subClassEl : nomenclatureElementsList) {
                if (subClassEl.getText().contains(refactoredSubClass)) {
                    subClassEl.click();
                    break;
                }
            }
        }
        return this;
    }

    private SearchProductPage showAllFilters() {
        showAllFilters.click();
        return this;
    }

    public SearchProductPage choseCheckboxFilter(Filters filter, boolean applyFilters) {
        Element checkbox = E("contains(" + filter.getName() + ")");
        if (!(filter.equals(Filters.HAS_AVAILABLE_STOCK) || filter.equals(Filters.TOP_EM))) {
            showAllFilters();
        }
        checkbox.click();
        if (applyFilters) {
            applyFiltersBtn.click();
        }
        return this;
    }

    public SearchProductPage selectGammaFilter(String... gammaFilters) throws Exception {
        List<String> tmpFilters = new ArrayList<>();
        tmpFilters.addAll(java.util.Arrays.asList(gammaFilters));
        gammaComboBox.click();
        gammaComboBox.pickElementFromList(tmpFilters);
        return this;
    }

    public SearchProductPage selectTopFilter(String... topFilters) throws Exception {
        List<String> tmpFilters = new ArrayList<>();
        tmpFilters.addAll(java.util.Arrays.asList(topFilters));
        topComboBox.click();
        topComboBox.pickElementFromList(tmpFilters);
        return this;
    }

    public SearchProductPage choseAvsDate(boolean neqNull, LocalDate date) throws Exception {
        avsDropBox.click();
        if (!neqNull) {
            avsDropDownCalendar.selectDate(date);
        }
        return this;
    }

    public SearchProductPage choseSupplier(String value) {
        supplierDropBox.click();
        SupplierDropDown supplierDropDown = supplierDropBox.supplierDropDown;
        supplierDropDown.loadingSpinner.waitForInvisibility();
        supplierDropDown.searchSupplier(value);
        supplierDropDown.loadingSpinner.waitForVisibility(short_timeout);
        supplierDropDown.loadingSpinner.waitForInvisibility();

        for (SupplierCardWidget widget : supplierDropDown.getSupplierCards()) {
            if (widget.getSupplierCode().equals(value) || widget.getSupplierName().equals(value)) {
                widget.click();
            }
        }
        supplierDropBox.click();
        return this;
    }

}
