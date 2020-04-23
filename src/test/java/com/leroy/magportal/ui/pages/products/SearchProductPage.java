package com.leroy.magportal.ui.pages.products;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.webelements.MagPortalComboBox;
import com.leroy.magportal.ui.webelements.searchelements.SupplierComboBox;
import com.leroy.magportal.ui.webelements.searchelements.SupplierDropDown;
import com.leroy.magportal.ui.webelements.widgets.*;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchProductPage extends MenuPage {
    public SearchProductPage(TestContext context) {
        super(context);
    }

    public enum FilterFrame {
        MY_SHOP,
        ALL_GAMMA_LM;
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

    public enum SortType {
        DEFAULT("По умолчанию"),
        LM_CODE_DESC("По ЛМ-коду: 9 → 1"),
        LM_CODE_ASC("По ЛМ-коду: 1 → 9"),
        NAME_DESC("По названию (Я → А)"),
        NAME_ASC("По названию (А → Я)");

        private String name;

        SortType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum ViewMode {
        EXTENDED,
        LIST;
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Spinner-active')]")
    Element loadingSpinner;

    @WebFindBy(xpath = "//input[@placeholder='ЛМ, название или штрихкод']")
    EditBox searchInput;

    @WebFindBy(xpath = "//input[@placeholder='ЛМ, название или штрихкод']/following-sibling::button")
    Button clearSearchInput;

    @WebFindBy(xpath = "//div[contains(@class, 'history')]//div[contains(@class, 'optionText')]//span[3]")
    ElementList<Element> searchHistoryElements;

    @WebFindBy(xpath = "//div[contains(@class, 'history')]//div[contains(@class, 'optionText')]//span[2]")
    ElementList<Element> searchHistoryMatchesElements;

    @WebFindBy(xpath = "//button[@id='MyShop']")
    Button myShopFilterBtn;

    @WebFindBy(xpath = "//button[@id='AllGamma']")
    Button allGammaFilterBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]" +
            "/ancestor::div[2]/div/span[1]/span")
    ElementList<Element> nomenclaturePathButtons;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]/ancestor::div[1]")
    Element allDepartmentsBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'lmui-View-row lmui-View-middle')]" +
            "//span[contains(@class, 'color-mainText') and not(contains(text(), 'Показаны'))]")
    Element currentNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]")
    Element searchByAllDepartmentsFilterBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]" +
            "/ancestor::span/preceding-sibling::span")
    Element currentSearchByPhraseInNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class, 'Nomenclatures__link-text')]")
    ElementList<Element> nomenclatureElementsList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Гамма']/ancestor::div[1]")
    MagPortalComboBox gammaComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Топ пополнения']/ancestor::div[1]")
    MagPortalComboBox topComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='еще']/ancestor::button")
    Button showAllFilters;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='еще']/ancestor::button" +
            "/following-sibling::div/span")
    Element filtersCounter;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='ПОКАЗАТЬ ТОВАРЫ']" +
            "/ancestor::button/preceding-sibling::button")
    Button clearAllFiltersInFilterFrameBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'ПОКАЗАТЬ ТОВАРЫ')]/ancestor::button")
    Element applyFiltersBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//label[text()='Поставщик']/ancestor::div[1]")
    SupplierComboBox supplierDropBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//input[@placeholder='Дата AVS']")
    MagPortalComboBox avsDropBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'DatePicker__dayPicker')]")
    CalendarWidget avsDropDownCalendar;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]/ancestor::div[2]")
    MagPortalComboBox sortComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]" +
            "/ancestor::div[6]/following-sibling::button[1]")
    Button extendedViewBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]" +
            "/ancestor::div[6]/following-sibling::button[2]")
    Button listViewBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(), 'ПОКАЗАТЬ ЕЩЕ')]")
    Element showMoreProductsBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, " +
            "'BarViewProductCard__container')]", clazz = ProductCardWidget.class)
    ElementList<ProductCardWidget> productCardsList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'BarViewProductCard__container')]" +
            "//p/following-sibling::div/ancestor::div[1]", clazz = ExtendedProductCardWidget.class)
    ElementList<ExtendedProductCardWidget> extendedProductCardList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'TableView__row')]", clazz = ProductCardTableViewWidget.class)
    ElementList<ProductCardTableViewWidget> productCardListTableView;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'TableView__row')]/div[5]/ancestor::div[1]", clazz = ExtendedProductCardTableViewWidget.class)
    ElementList<ExtendedProductCardTableViewWidget> extendedProductCardListTableView;

    @WebFindBy(text = "Произошла ошибка")
    Element errorOccurMsg;

    @WebFindBy(xpath = "//span[contains(text(),'СБРОСИТЬ')]/ancestor::button")
    Button clearAllFiltersInProductFrame;

    @WebFindBy(text = "Ничего не найдено")
    Element notFoundMsgLbl;

    @WebFindBy(xpath = "//p[contains(text(),'не дал результатов')]")
    Element notFoundMsgDescriptionLbl;

    @WebFindBy(text = "Больше ничего не найдено.")
    Element noMoreResultsLbl;

    @Override
    public void waitForPageIsLoaded() {
        searchInput.waitForVisibility();
        applyFiltersBtn.waitForVisibility();

    }

    public String getCurrentNomenclatureName() {
        return currentNomenclatureLbl.getText();
    }

    @Step("Ввести в поисковую строку значение без инициализации поиска")
    public SearchProductPage enterStringInSearchInput(String value) {
        searchInput.fill(value);
        return this;
    }

    @Step("Наполнить историю поиска")
    public List<String> createSearchHistory(int notesQuantity) {
        List<String> searchHistoryList = new ArrayList<>();
        String searchContext = "q";
        for (int i = 0; i < notesQuantity; i++) {
            searchByPhrase(searchContext);
            searchHistoryList.add(searchContext);
            searchContext = searchContext + "q";
            clearSearchInputByClearBtn();
        }
        Collections.sort(searchHistoryList, Collections.reverseOrder());
        return searchHistoryList;
    }

    @Step("Ввести в поисковую строку {value} и осуществить поиск")
    public SearchProductPage searchByPhrase(String value) {
        searchInput.clearFillAndSubmit(value);
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Очистить поисковую строку нажатием на крест")
    public SearchProductPage clearSearchInputByClearBtn() {
        clearSearchInput.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Выбрать группу фильтров {frame}")
    public SearchProductPage switchFiltersFrame(FilterFrame frame) {
        if (frame.equals(FilterFrame.MY_SHOP)) {
            myShopFilterBtn.click();
        } else {
            allGammaFilterBtn.click();
        }
        return this;
    }

    @Step("Выбрать номенклатуру {dept} {subDept} {classId} {subClass}")
    public SearchProductPage choseNomenclature(String dept, String subDept, String classId, String subClass) {
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
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Перейти по хлебным крошкам в {value}")
    public SearchProductPage navigateToPreviousNomenclatureElement(String value) {
        waitForPageIsLoaded();
        for (Element element : nomenclaturePathButtons) {
            if (element.getText().contains(value)) {
                element.click();
                break;
            }
        }
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать на кнопку \"ЕЩЕ\" для просмотра всех фильтров")
    private SearchProductPage showAllFilters() {
        showAllFilters.click();
        return this;
    }

    @Step("Выбрать чек-бокс и применить фильтры - {applyFilters}")
    public SearchProductPage choseCheckboxFilter(Filters filter, boolean applyFilters) {
        if (!(filter.equals(Filters.HAS_AVAILABLE_STOCK) || filter.equals(Filters.TOP_EM))) {
            showAllFilters();
        }
        Element checkbox = E("contains(" + filter.getName() + ")");
        checkbox.click();
        if (applyFilters) {
            applyFilters();
            waitForSpinnerAppearAndDisappear();
        }
        return this;
    }

    // Может не массив String'ов? А один класс FiltersData как в magportal
    /*@Step("Выбрать фильтры Гамма {gammaFilters.toString}")
    public SearchProductPage selectGammaFilter(String... gammaFilters) throws Exception {
        gammaComboBox.click();
        gammaComboBox.selectOptions(Arrays.asList(gammaFilters));
        return this;
    }

    @Step("Выбрать фильтры Топ {topFilters.toString}")
    public SearchProductPage selectTopFilter(String... topFilters) throws Exception {
        List<String> tmpFilters = new ArrayList<>();
        tmpFilters.addAll(java.util.Arrays.asList(topFilters));
        topComboBox.click();
        topComboBox.selectOptions(tmpFilters);
        return this;
    }

    @Step("выбрать дату AVS {date}")
    public SearchProductPage choseAvsDate(boolean neqNull, LocalDate date) throws Exception {
        avsDropBox.click();
        if (!neqNull) {
            avsDropDownCalendar.selectDate(date);
        }
        return this;
    }*/

    @Step("Выбрать фильтр по поставщику {value}")
    public SearchProductPage choseSupplier(String value) throws Exception {
        if (!supplierDropBox.isVisible()) {
            showAllFilters.click();
        }
        SupplierDropDown supplierDropDown = supplierDropBox.supplierDropDown;
        if (!supplierDropDown.isVisible()) {
            supplierDropBox.click();
        }
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

    @Step("Удалить выбранного поставщиков {supplierName} по нажатию на кнопку крестик в овальной области с именем поставщика")
    public SearchProductPage deleteChosenSuppliers(String supplierName) {
        SupplierDropDown supplierDropDown = supplierDropBox.supplierDropDown;
        if (!supplierDropDown.isVisible()) {
            supplierDropBox.click();
            waitForSpinnerAppearAndDisappear();
        }
        for (ChosenSupplierWidget widget : supplierDropDown.getChosenSuppliers()) {
            if (widget.getChosenSupplierName().equals(supplierName)) {
                widget.deleteChosenSupplier();
                break;
            }
        }
        supplierDropBox.click();
        return this;
    }

    @Step("Удалить всех выбранных поставщиков по нажатию на кнопку \"Очистить\"")
    public SearchProductPage deleteAllChosenSuppliers() {
        SupplierDropDown supplierDropDown = supplierDropBox.supplierDropDown;
        if (!supplierDropDown.isVisible()) {
            supplierDropBox.click();
            waitForSpinnerAppearAndDisappear();
        }
        supplierDropDown.deleteAllChosenSuppliers();
        return this;
    }

    @Step("Очистить все фильтры")
    public SearchProductPage clearAllFilters() {
        clearAllFiltersInFilterFrameBtn.click();
        return this;
    }

    @Step("Применить выбранные фильтры")
    public SearchProductPage applyFilters() {
        applyFiltersBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Выбрать тип сортировки")
    public SearchProductPage choseSortType(SortType sortType) {
        sortComboBox.click();
        Element sortBtn = E("//span[contains(text(),'" + sortType.getName() + "')]/ancestor::button");
        sortBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Выбрать вариант отображения товаров")
    public SearchProductPage choseViewMode(ViewMode mode) {
        if (mode.equals(ViewMode.EXTENDED)) {
            extendedViewBtn.click();
        } else {
            listViewBtn.click();
        }
        return this;
    }

    @Step("Очистить фильтры по нажатию на клавишу \"СБРОСИТЬ ФИЛЬТРЫ\"")
    public SearchProductPage clearAllFiltersInProductFrame() {
        clearAllFiltersInProductFrame.click();
        return this;
    }

    @Step("Нажать на кнопку \"Показать еще\"")
    public SearchProductPage showMoreResults() {
        showMoreProductsBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    //VERIFICATIONS

    @Step("Проверить, что отобразилось сообщение \"Ничего не найдено\" " +
            "с кнопкой \"Сбросить фильтры\" - {isClearFiltersVisible} и содержит поисковой запрос {value}")
    public SearchProductPage shouldNotFoundMsgIsDisplayed(boolean isClearFiltersVisible, String value) {
        if (isClearFiltersVisible) {
            softAssert.isElementVisible(clearAllFiltersInProductFrame);
        } else {
            softAssert.isElementTextContains(notFoundMsgDescriptionLbl, value);
        }
        softAssert.isElementVisible(notFoundMsgLbl);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что кол-во отображенных результатов соответствует кол-ву артикулов из ответа мэшапера")
    public SearchProductPage shouldResponseEntityEqualsToViewEntity(
            ProductItemDataList responseData, FilterFrame frame, ViewMode mode) throws Exception {
        List<ProductItemData> dataList = responseData.getItems();
        if (frame.equals(FilterFrame.MY_SHOP) && mode.equals(ViewMode.EXTENDED)) {
            anAssert.isTrue(dataList.size() == extendedProductCardList.getCount(),
                    "Кол-во артикулов отличается: отображено - " + extendedProductCardList.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(extendedProductCardList.get(i).getLmCode()) &&
                                dataList.get(i).getBarCode().equals(extendedProductCardList.get(i).getBarCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + extendedProductCardList.get(i).getLmCode());
            }
        } else if (frame.equals(FilterFrame.MY_SHOP) && mode.equals(ViewMode.LIST)) {
            anAssert.isTrue(dataList.size() == extendedProductCardListTableView.getCount(),
                    "Кол-во артикулов отличается: отображено - " + extendedProductCardListTableView.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(extendedProductCardListTableView.get(i).getLmCode()) &&
                                dataList.get(i).getBarCode().equals(extendedProductCardListTableView.get(i).getBarCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + extendedProductCardListTableView.get(i).getLmCode());
            }
        } else if (frame.equals(FilterFrame.ALL_GAMMA_LM) && mode.equals(ViewMode.EXTENDED)) {
            anAssert.isTrue(dataList.size() == productCardsList.getCount(),
                    "Кол-во артикулов отличается: отображено - " + productCardsList.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(productCardsList.get(i).getLmCode()) &&
                                dataList.get(i).getBarCode().equals(productCardsList.get(i).getBarCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + productCardsList.get(i).getLmCode());
            }
        } else {
            anAssert.isTrue(dataList.size() == productCardListTableView.getCount(),
                    "Кол-во артикулов отличается: отображено - " + productCardListTableView.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(productCardListTableView.get(i).getLmCode()) &&
                                dataList.get(i).getBarCode().equals(productCardListTableView.get(i).getBarCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + productCardListTableView.get(i).getLmCode());
            }
        }
        return this;
    }

    @Step("Проверить, что кнопка \"показать еще\" отображается")
    public SearchProductPage shouldShowMoreBtnBeVisible() {
        softAssert.isElementVisible(showMoreProductsBtn);
        softAssert.isElementNotVisible(noMoreResultsLbl);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что лейбл \"Больше ничего не найдено\" отображается")
    public SearchProductPage shouldNoMoreResultsBeVisible() {
        softAssert.isElementVisible(noMoreResultsLbl);
        softAssert.isElementNotVisible(showMoreProductsBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверяем наличие поискового критерия {searchCriterion} в карточке товара")
    public SearchProductPage shouldProductCardContainsText(String searchCriterion) throws Exception {
        anAssert.isElementNotVisible(notFoundMsgLbl);
        if (searchCriterion.matches("\\D+") || searchCriterion.length() < 4) {
            for (int i = 0; i < extendedProductCardList.getCount(); i++) {
                anAssert.isTextContainsIgnoringCase(extendedProductCardList.get(i).getTitle(), searchCriterion,
                        extendedProductCardList.get(i).getTitle() + " не содержит " + searchCriterion);
            }
        } else {
            for (int i = 0; i < extendedProductCardList.getCount(); i++) {
                anAssert.isTrue((extendedProductCardList.get(i).getLmCode().contains(searchCriterion) ||
                                extendedProductCardList.get(i).getBarCode().contains(searchCriterion)),
                        extendedProductCardList.get(i).toString() + " не содержит " + searchCriterion);
            }
        }
        return this;
    }

    @Step("Проверить, что поисковая строка пуста")
    public SearchProductPage shouldSearchInputBeEmpty() {
        anAssert.isElementTextEqual(searchInput, "");
        return this;
    }

    @Step("Проверить, что текущий элемент номенклатуры отобразился")
    public SearchProductPage shouldCurrentNomenclatureElementNameIsDisplayed(String nomenclatureName) {
        anAssert.isElementTextContains(currentNomenclatureLbl, nomenclatureName);
        return this;
    }

    @Step("Проверить, что хлебные крошки содержат предыдущий роидтельскую номенклатуру")
    public SearchProductPage shouldBreadCrumbsContainsPreviousNomenclatureName(String nomenclatureParentName) {
        int condition = 0;
        for (Element tmp : nomenclaturePathButtons) {
            if (tmp.getText().contains(nomenclatureParentName)) {
                condition++;
            }
        }
        anAssert.isTrue(condition == 1, nomenclatureParentName + " либо отсутствует, либо встречается более 1 раза");
        return this;
    }

    @Step("Проверить, что товары отсортированы")
    public SearchProductPage shouldProductsAreSorted(SortType order) throws Exception {
        List<String> sortedNameList = new ArrayList<>();
        List<Integer> sortedLmCodesList = new ArrayList<>();
        for (ExtendedProductCardWidget tmp : extendedProductCardList) {
            sortedLmCodesList.add(Integer.parseInt(tmp.getLmCode()));
            sortedNameList.add(tmp.getTitle());
        }
        switch (order) {
            case LM_CODE_DESC:
                Collections.sort(sortedLmCodesList, Collections.reverseOrder());
                for (int i = 0; i < extendedProductCardList.getCount(); i++) {
                    anAssert.isEquals(extendedProductCardList.get(i).getLmCode(), String.valueOf(sortedLmCodesList.get(i)), "Wrong sorting order for visible content");
                }
                return this;
            case LM_CODE_ASC:
                Collections.sort(sortedLmCodesList);
                for (int i = 0; i < extendedProductCardList.getCount(); i++) {
                    anAssert.isEquals(extendedProductCardList.get(i).getLmCode(), String.valueOf(sortedLmCodesList.get(i)), "Wrong sorting order for visible content");
                }
                return this;
            case NAME_DESC:
                Collections.sort(sortedNameList, Collections.reverseOrder());
                for (int i = 0; i < extendedProductCardList.getCount(); i++) {
                    anAssert.isEquals(extendedProductCardList.get(i).getTitle(), sortedNameList.get(i), "Wrong sorting order for visible content");
                }
                return this;
            case NAME_ASC:
                Collections.sort(sortedNameList);
                for (int i = 0; i < extendedProductCardList.getCount(); i++) {
                    anAssert.isEquals(extendedProductCardList.get(i).getTitle(), sortedNameList.get(i), "Wrong sorting order for visible content");
                }
                return this;
            default:
                throw new Exception("Wrong sorting order");
        }
    }

    @Step("Проверить, что история поиска содержит элементы массива")
    public SearchProductPage shouldSearchHistoryContainsEachElement(List<String> searchRequests) throws Exception {
        anAssert.isTrue(searchHistoryElements.getCount() > 0, "История поиска не содержит записей");
        String elemText;
        for (int i = 0; i < searchHistoryElements.getCount(); i++) {
            elemText = searchHistoryElements.get(i).getText();
            anAssert.isEquals(elemText, searchRequests.get(i), "Элементы истории поиска отличаются: отображенный - " +
                    elemText + " ожидаемый - " + searchRequests.get(i));
        }
        return this;
    }

    @Step("Проверить, что история поиска содержит элементы, совпадающая с введенным критерием поиска")
    public SearchProductPage shouldSearchHistoryElementsContainsSearchCriterion(String criterion) {
        anAssert.isTrue(searchHistoryMatchesElements.getCount() > 0, "История поиска не содержит записей");
        for (Element tmp : searchHistoryMatchesElements) {
            anAssert.isEquals(tmp.getText(), criterion, "Элемент истории поиска не содержит введенную фразу");
        }
        return this;
    }

    @Step("Проверить, что поле с выбором поставщика содержит корректный текст")
    public SearchProductPage shouldSupplierComboBoxContainsCorrectText(String... name) {
        String elemText;
        if (name == null) {
            anAssert.isElementNotVisible(supplierDropBox.chosenSupplierName);
        } else if (name.length == 1) {
            elemText = supplierDropBox.chosenSupplierName.getText();
            anAssert.isTextContainsIgnoringCase(elemText, name[0], "Не отображено имя поставщика " + name[0]);
        } else {
            elemText = supplierDropBox.chosenSupplierName.getText();
            anAssert.isTextContainsIgnoringCase(elemText, "Поставщик (" + name.length + ")", "Отображаемый текст не соответствует паттерну");
        }
        return this;
    }

}
