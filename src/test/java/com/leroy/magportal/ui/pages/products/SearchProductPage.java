package com.leroy.magportal.ui.pages.products;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.webelements.MagPortalComboBox;
import com.leroy.magportal.ui.webelements.widgets.CalendarWidget;

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

    @WebFindBy(xpath = "//input[@placeholder='ЛМ, название или штрихкод']")
    EditBox searchInput;

    @WebFindBy(xpath = "//button[@id='MyShop']")
    Button myShopFilterBtn;

    @WebFindBy(xpath = "//button[@id='AllGamma']")
    Button allGammaFilterBtn;

    //TODO create new Element Class хлебные крошки
    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]/ancestor::div[2]")
    Button nomenclaturePath;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'lmui-View-row lmui-View-middle')]" +
            "//span[contains(@class, 'color-mainText') and not(contains(text(), 'Показаны'))]")
    Element currentNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]")
    Button searchByAllDepartmentsFilterBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]/ancestor::span/preceding-sibling::span")
    Element currentSearchByPhraseInNomenclatureLbl;

    //TODO create new Element Class лист элементов номенклатуры
    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'Nomenclatures__link')]/ancestor::div[2]")
    Element nomenclatureList;

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

    @WebFindBy(xpath = "//div[contains(@class, \"active\")]//span[contains(text(),'ПОКАЗАТЬ ТОВАРЫ')]")
    Button applyFiltersBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//label[text()='Поставщик']/ancestor::div[1]")
    MagPortalComboBox supplierDropBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Дата AVS']")
    MagPortalComboBox avsDropBox;

    @WebFindBy(xpath = "//div[contains(@class, 'DatePicker__dayPicker')]")
    CalendarWidget avsDropDownCalendar;

    private SearchProductPage showAllFilters() {
        showAllFilters.click();
        return this;
    }

    public SearchProductPage choseCheckboxFilter(Filters filter, boolean applyFilters) {
        Element checkbox = E("contains(" + filter.getName() + ")");
        //WebElement checkbox = findElement(By.xpath("//div[contains(@class, 'active')]//span[text()='"+filter.getName()+"']/ancestor::button"));
        if (!(filter.equals(Filters.HAS_AVAILABLE_STOCK) || filter.equals(Filters.TOP_EM))) {
            showAllFilters();
        }
        checkbox.click();
        if (applyFilters) {
            applyFiltersBtn.click();
        }
        return this;
    }

    public SearchProductPage choseAvsDate(LocalDate date) throws Exception {
        avsDropBox.click();
        avsDropDownCalendar.selectDate(date);
        return this;
    }

    public SearchProductPage selectGammaFilter(String ... gammaFilters)throws Exception{
        List<String> tmpFilters = new ArrayList<>();
        tmpFilters.addAll(java.util.Arrays.asList(gammaFilters));
        gammaComboBox.click();
        gammaComboBox.pickElementFromList(tmpFilters);
        return this;
    }

}
