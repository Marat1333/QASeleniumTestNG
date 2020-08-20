package com.leroy.magportal.ui.pages.products;

import com.leroy.constants.EnvConstants;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magportal.ui.models.search.FiltersData;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.products.widget.ExtendedProductCardTableViewWidget;
import com.leroy.magportal.ui.pages.products.widget.ExtendedProductCardWidget;
import com.leroy.magportal.ui.pages.products.widget.ProductCardTableViewWidget;
import com.leroy.magportal.ui.pages.products.widget.ProductCardWidget;
import com.leroy.magportal.ui.webelements.commonelements.*;
import com.leroy.magportal.ui.webelements.searchelements.SupplierComboBox;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.util.*;

public class SearchProductPage extends MagPortalBasePage {

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
        CTM("СТМ"),
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

    @WebFindBy(xpath = "//button[@id='MyShop']/ancestor::div[4]/following-sibling::div[1]")
    Element myShopContainer;

    @WebFindBy(xpath = "//button[@id='AllGamma']")
    Button allGammaFilterBtn;

    @WebFindBy(xpath = "//button[@id='MyShop']/ancestor::div[4]/following-sibling::div[2]")
    Element allGammaContainer;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]" +
            "/ancestor::div[2]/div/span[1]/span")
    ElementList<Element> nomenclaturePathButtons;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()=\"Каталог товаров\"]/ancestor::div[1]")
    Element allDepartmentsBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'lmui-View-row lmui-View-middle')]" +
            "//span[contains(@class, 'color-mainText') and not(contains(text(), 'Показаны'))]", refreshEveryTime = true)
    Element currentNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]")
    Element searchByAllDepartmentsFilterBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'результаты поиска по')]" +
            "/ancestor::span/preceding-sibling::span")
    Element currentSearchByPhraseInNomenclatureLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class, 'Nomenclatures__link-text')]")
    ElementList<Element> nomenclatureElementsList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='еще']/ancestor::button",
            refreshEveryTime = true)
    Button showAllFilters;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='еще']/ancestor::button[1]/following-sibling::" +
            "div[contains(@class, 'counter')]/span", refreshEveryTime = true)
    Element filtersCounter;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[text()='ПОКАЗАТЬ ТОВАРЫ']" +
            "/ancestor::button/preceding-sibling::button", refreshEveryTime = true)
    Button clearAllFiltersInFilterFrameBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'ПОКАЗАТЬ ТОВАРЫ')]/ancestor::button",
            refreshEveryTime = true)
    Element applyFiltersBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'Select__container') and descendant::label[text()='Поставщик']]")
    SupplierComboBox supplierComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//*[contains(text(),'AVS')]")
    PuzCheckBox avsCheckBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//*[contains(text(),'Есть теор. запас')]/ancestor::button[1]")
    Button hasAvailableStockButton;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//*[contains(text(),'Топ EM')]/ancestor::button[1]")
    Button topEmButton;

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container') and descendant::span[contains(text(),'Сортировать')]]")
    PuzComboBox sortComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]" +
            "/ancestor::div[6]/following-sibling::button[1]", refreshEveryTime = true)
    Button extendedViewBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(@class,'singleValue')]" +
            "/ancestor::div[6]/following-sibling::button[2]", refreshEveryTime = true)
    Button listViewBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(), 'ПОКАЗАТЬ ЕЩЕ')]")
    Element showMoreProductsBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, " +
            "'BarViewProductCard__container')]", clazz = ProductCardWidget.class, refreshEveryTime = true)
    ElementList<ProductCardWidget> productCardsList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'BarViewProductCard__container')]" +
            "//p/following-sibling::div/ancestor::div[1]", clazz = ExtendedProductCardWidget.class, refreshEveryTime = true)
    ElementList<ExtendedProductCardWidget> extendedProductCardList;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'TableView__row')]", clazz = ProductCardTableViewWidget.class,
            refreshEveryTime = true)
    ElementList<ProductCardTableViewWidget> productCardListTableView;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'TableView__row')]/div[5]/ancestor::div[1]",
            clazz = ExtendedProductCardTableViewWidget.class, refreshEveryTime = true)
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

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'DatePicker__container')][descendant::input[@placeholder='Дата AVS']]",
            refreshEveryTime = true)
    CalendarInputBox avsCalendarInputBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'Select__container')][descendant::input[@placeholder='Гамма']]",
            refreshEveryTime = true)
    PuzMultiSelectComboBox gammaComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//div[contains(@class, 'Select__container')][descendant::input[@placeholder='Топ пополнения']]",
            refreshEveryTime = true)
    PuzMultiSelectComboBox topComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//form/div[2]", refreshEveryTime = true)
    Element additionalFiltersArea;

    @WebFindBy(xpath = "//div[contains(@class, 'active')]//span[contains(text(),'ПОКАЗАТЬ ТОВАРЫ')]/ancestor::button/preceding-sibling::div")
    Button downloadExcelBtn;

    @Override
    public void waitForPageIsLoaded() {
        searchInput.waitForVisibility();
        applyFiltersBtn.waitForVisibility();
        waitForSpinnerDisappear();
    }

    @Override
    public boolean navigateForward() throws InterruptedException {
        boolean result = super.navigateForward();
        waitForSpinnerAppearAndDisappear();
        return result;
    }

    public enum Direction {
        FORWARD,
        BACK
    }

    @Step("Используя браузерную навигацию, перейти в заданном направлении {n} раз")
    public void navigateNTimes(Direction direction, int n) throws Exception {
        if (direction.equals(Direction.FORWARD)) {
            for (int i = 0; i < n; i++) {
                navigateForward();
            }
        } else {
            for (int i = 0; i < n; i++) {
                navigateBack();
            }
        }
    }

    @Override
    public boolean navigateBack() throws InterruptedException {
        boolean result = super.navigateBack();
        waitForSpinnerAppearAndDisappear();
        return result;
    }

    private String getCurrentNomenclatureName() {
        return currentNomenclatureLbl.getText();
    }

    @Step("Ввести в поисковую строку значение без инициализации поиска")
    public SearchProductPage enterStringInSearchInput(String value) {
        searchInput.fill(value);
        return this;
    }

    @Step("Выгрузить поисковую выдачу в excel")
    public SearchProductPage downloadExcelSearchResultOutput() {
        downloadExcelBtn.click();
        return this;
    }

    @Step("Перейти в карточку товара по {lmCode}")
    public <T> T searchProductCardByLmCode(String lmCode, FilterFrame frame) throws Exception {
        if (lmCode.length() != 8) {
            throw new IllegalArgumentException("Wrong lmCode length");
        }
        switchFiltersFrame(frame);
        return searchByPhrase(lmCode);
    }

    @Step("Перейти в карточку продукта {lmCode}")
    public <T> T navigateToProductCart(String lmCode, FilterFrame frame, ViewMode viewMode) throws Exception {
        Set<String> windows = driver.getWindowHandles();
        switch (frame) {
            case MY_SHOP:
                switch (viewMode) {
                    case EXTENDED:
                        for (ExtendedProductCardWidget widget : extendedProductCardList) {
                            if (widget.getLmCode().equals(lmCode)) {
                                widget.click();
                            }
                        }
                        break;
                    case LIST:
                        for (ExtendedProductCardTableViewWidget widget : extendedProductCardListTableView) {
                            if (widget.getLmCode().equals(lmCode)) {
                                widget.click();
                            }
                        }
                        break;
                }
                break;
            case ALL_GAMMA_LM:
                switch (viewMode) {
                    case EXTENDED:
                        for (ProductCardWidget widget : productCardsList) {
                            if (widget.getLmCode().equals(lmCode)) {
                                widget.click();
                            }
                        }
                        break;
                    case LIST:
                        for (ProductCardTableViewWidget widget : productCardListTableView) {
                            if (widget.getLmCode().equals(lmCode)) {
                                widget.click();
                            }
                        }
                        break;
                }
                break;
        }
        this.switchToNewWindow(windows);
        return frame.equals(FilterFrame.MY_SHOP) ? (T) new ExtendedProductCardPage() : (T) new ProductCardPage();
    }

    @Step("Перейти по адресу, содержащему фильтры")
    public SearchProductPage navigateToWithFilters(Map<String, String> params) throws Exception {
        StringBuilder paramsBuilder = new StringBuilder();
        paramsBuilder.append(EnvConstants.URL_MAG_PORTAL).append("/catalogproducts?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsBuilder.append(entry.getKey() + entry.getValue()).append("&");
        }
        navigateTo(paramsBuilder.deleteCharAt(paramsBuilder.length() - 1).toString());
        return new SearchProductPage();
    }

    @Step("Наполнить историю поиска")
    public List<String> createSearchHistory(int notesQuantity) throws Exception {
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
    public <T> T searchByPhrase(String value) throws Exception {
        Set<String> windows = driver.getWindowHandles();
        searchInput.clearFillAndSubmit(value);
        waitForSpinnerAppearAndDisappear();
        if (value.matches("\\d{8}") || value.matches("\\d{13}")) {
            boolean isMyShop = myShopContainer.getAttribute("className").contains("active");
            anAssert.isTrue(driver.getWindowHandles().size() > windows.size(),
                    "Должно было открыться новое окно с карточкой товара");
            this.switchToNewWindow(windows);
            return isMyShop ? (T) new ExtendedProductCardPage() : (T) new ProductCardPage();
        }
        return (T) this;
    }

    @Step("Ввести в поисковую строку {value} без поиска")
    public SearchProductPage fillSearchInput(String value) throws Exception {
        searchInput.clearAndFill(value);
        return this;
    }

    @Step("Очистить поисковую строку нажатием на крест")
    public SearchProductPage clearSearchInputByClearBtn() {
        clearSearchInput.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Выбрать группу фильтров")
    public SearchProductPage switchFiltersFrame(FilterFrame frame) {
        String attributeValue;
        String attributeName = "className";
        String condition = "active";
        if (frame.equals(FilterFrame.MY_SHOP)) {
            attributeValue = myShopContainer.getAttribute(attributeName);
            if (attributeValue.contains(condition)) {
                return this;
            }
            myShopFilterBtn.click();
            myShopContainer.waitUntilAttributeIsEqual(attributeName, attributeValue);
        } else {
            attributeValue = allGammaContainer.getAttribute(attributeName);
            if (attributeValue.contains(condition)) {
                return this;
            }
            allGammaFilterBtn.click();
            allGammaContainer.waitUntilAttributeIsEqual(attributeName, attributeValue);
        }
        return this;
    }

    @Step("Выбрать режим отображения карточек товара")
    public SearchProductPage switchViewMode(ViewMode mode) {
        if (mode.equals(ViewMode.EXTENDED)) {
            extendedViewBtn.click();
        } else {
            listViewBtn.click();
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
        nomenclaturePathButtons.waitUntilAtLeastOneElementIsPresent();
        for (Element element : nomenclaturePathButtons) {
            if (element.getText().contains(value)) {
                element.click();
                break;
            }
        }
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать на кнопку 'ЕЩЕ' для просмотра всех фильтров")
    public SearchProductPage showAllFilters() {
        if (additionalFiltersArea.isVisible()) {
            showAllFilters.click();
            additionalFiltersArea.waitForInvisibility();
        } else {
            showAllFilters.click();
            additionalFiltersArea.waitForVisibility();
        }
        return this;
    }

    @Step("Выбрать чек-бокс и применить фильтры - {applyFilters}")
    public SearchProductPage choseCheckboxFilter(boolean applyFilters, Filters... filters) {
        for (Filters filter : filters) {
            if (!(filter.equals(Filters.HAS_AVAILABLE_STOCK) ||
                    filter.equals(Filters.TOP_EM)) && (!supplierComboBox.isVisible())) {
                showAllFilters();
            }
            Element checkbox;
            if (filter.equals(Filters.BEST_PRICE) || filter.equals(Filters.LIMITED_OFFER)) {
                checkbox = E("//div[contains(@class, 'active')]//*[contains(text(),'" + filter.getName() + "')]/ancestor::button");
            } else {
                checkbox = E("//div[contains(@class, 'active')]//*[contains(text(),'" + filter.getName() + "')]");
            }
            checkbox.click();
            if (applyFilters) {
                applyFilters();
            }
        }
        return this;
    }

    @Step("Ввести дату AVS вручную")
    public SearchProductPage enterAvsDateManually(LocalDate date) {
        avsCalendarInputBox.enterDateInField(date);
        return this;
    }

    @Step("Выбрать фильтры Гамма")
    public SearchProductPage choseGammaFilter(String... gammaFilters) throws Exception {
        gammaComboBox.selectOptions(gammaFilters);
        return this;
    }

    @Step("Выбрать фильтры Топ")
    public SearchProductPage choseTopFilter(String... topFilters) throws Exception {
        topComboBox.selectOptions(topFilters);
        return this;
    }

    @Step("выбрать дату AVS {date}")
    public SearchProductPage choseAvsDate(LocalDate date) throws Exception {
        if (!avsCalendarInputBox.isVisible()) {
            choseCheckboxFilter(false, Filters.AVS);
        }
        avsCalendarInputBox.selectDate(date);
        return this;
    }

    @Step("Выбрать фильтр по поставщику")
    public SearchProductPage choseSupplier(boolean closeAfter, String... values) {
        if (!supplierComboBox.isVisible()) {
            showAllFilters.click();
        }

        for (String value : values) {
            supplierComboBox.searchSupplierAndSelect(value);
        }
        if (closeAfter) {
            supplierComboBox.close();
        }
        return this;
    }

    @Step("Выбрать несколько фильтров")
    public SearchProductPage choseSeveralFilters(FiltersData data, boolean applyFilters) throws Exception {
        if (data.getSuppliers().length > 0) {
            choseSupplier(true, data.getSuppliers());
        }
        if (data.getGammaFilters().length > 0) {
            choseGammaFilter(data.getGammaFilters());
        }
        if (data.getTopFilters().length > 0) {
            choseTopFilter(data.getTopFilters());
        }
        if (data.getCheckBoxes().length > 0) {
            choseCheckboxFilter(false, data.getCheckBoxes());
        }
        if (data.getAvsDate() != null) {
            choseAvsDate(data.getAvsDate());
        }
        if (applyFilters) {
            applyFilters();
        }
        return this;
    }

    @Step("Удалить выбранного поставщиков {supplierName} по нажатию на кнопку крестик в овальной области с именем поставщика")
    public SearchProductPage deleteChosenSuppliers(boolean closeAfter, String supplierName) {
        supplierComboBox.deleteChosenSuppliers(supplierName);
        if (closeAfter) {
            supplierComboBox.close();
        }
        return this;
    }

    @Step("Удалить всех выбранных поставщиков по нажатию на кнопку \"Очистить\"")
    public SearchProductPage deleteAllChosenSuppliers() {
        supplierComboBox.deleteAllChosenSuppliers();
        return this;
    }

    @Step("Очистить все фильтры")
    public SearchProductPage clearAllFilters() {
        clearAllFiltersInFilterFrameBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Применить выбранные фильтры")
    public SearchProductPage applyFilters() {
        applyFiltersBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Выбрать тип сортировки {sortType}")
    public SearchProductPage choseSortType(SortType sortType) throws Exception {
        sortComboBox.selectOption(sortType.getName());
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
        waitForSpinnerAppearAndDisappear();
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
                anAssert.isTrue(dataList.get(i).getLmCode().equals(extendedProductCardList.get(i).getLmCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + extendedProductCardList.get(i).getLmCode());
            }
        } else if (frame.equals(FilterFrame.MY_SHOP) && mode.equals(ViewMode.LIST)) {
            anAssert.isTrue(dataList.size() == extendedProductCardListTableView.getCount(),
                    "Кол-во артикулов отличается: отображено - " + extendedProductCardListTableView.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(extendedProductCardListTableView.get(i).getLmCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + extendedProductCardListTableView.get(i).getLmCode());
            }
        } else if (frame.equals(FilterFrame.ALL_GAMMA_LM) && mode.equals(ViewMode.EXTENDED)) {
            anAssert.isTrue(dataList.size() == productCardsList.getCount(),
                    "Кол-во артикулов отличается: отображено - " + productCardsList.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(productCardsList.get(i).getLmCode()),
                        "У артикулов не совпадают лм или баркод: ответ мэшапера - " + dataList.get(i).getLmCode() +
                                " отображено - " + productCardsList.get(i).getLmCode());
            }
        } else {
            anAssert.isTrue(dataList.size() == productCardListTableView.getCount(),
                    "Кол-во артикулов отличается: отображено - " + productCardListTableView.getCount() + ", получено - "
                            + dataList.size());
            for (int i = 0; i < dataList.size(); i++) {
                anAssert.isTrue(dataList.get(i).getLmCode().equals(productCardListTableView.get(i).getLmCode()),
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
                anAssert.isContainsIgnoringCase(extendedProductCardList.get(i).getTitle(), searchCriterion,
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
    public SearchProductPage shouldBreadCrumbsContainsNomenclatureName(boolean contains, String... nomenclatureElementName) {
        int condition = 0;
        for (Element tmp : nomenclaturePathButtons) {
            for (String nomenclatureAttribute : nomenclatureElementName) {
                if (tmp.getText().contains(nomenclatureAttribute)) {
                    condition++;
                    break;
                }
            }
        }
        if (contains) {
            anAssert.isTrue(condition == nomenclatureElementName.length, "не соответсвует кол-ву переданных элементов");
        } else {
            anAssert.isTrue(condition == 0, "Уровень товарной иерархии содержится в элементе UI");
        }
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
    public SearchProductPage shouldSupplierComboBoxContainsCorrectText(boolean isEmpty, String... name) {
        String elemText;
        if (isEmpty) {
            anAssert.isTrue(supplierComboBox.getSelectedOptionText().isEmpty(),
                    "Поле с выбором поставщика не пустое");
        } else if (name.length == 1) {
            elemText = supplierComboBox.getSelectedOptionText();
            anAssert.isContainsIgnoringCase(elemText, name[0],
                    "Не отображено имя поставщика " + name[0]);
        } else {
            elemText = supplierComboBox.getSelectedOptionText();
            anAssert.isEquals(elemText, "Поставщик (" + name.length + ")",
                    "Отображаемый текст не соответствует паттерну");
        }
        return this;
    }

    @Step("Проверить состояние чек-бокса выбранного поставщика")
    public SearchProductPage shouldChosenSupplierCheckboxHasCorrectCondition(boolean isChecked, String supplier) throws Exception {
        supplierComboBox.searchSupplier(supplier);
        Boolean val = supplierComboBox.isSupplierSelected(supplier);
        anAssert.isNotNull(val, "Чек-бокс для поставщика " + supplier + " не найден",
                "Чек-бокс должен быть");
        if (isChecked) {
            anAssert.isTrue(val, "Чек-бокс в состоянии disabled");
        } else {
            anAssert.isFalse(val, "Чек-бокс в состоянии enabled");
        }
        return this;
    }

    @Step("Проверить, что чек-бокс переведен в корректное состояние")
    public SearchProductPage shouldCheckboxFilterHasCorrectCondition(boolean isEnabled, Filters... filters) throws Exception {
        for (Filters filter : filters) {
            String xpath = "//div[contains(@class, 'active')]//*[contains(text(),'" +
                    filter.getName() + "')]/ancestor::button";
            if (!(filter.equals(Filters.LIMITED_OFFER) || filter.equals(Filters.BEST_PRICE))) {
                PuzCheckBox checkbox = E(xpath, PuzCheckBox.class);
                if (!checkbox.isVisible()) {
                    showAllFilters();
                }
                if (isEnabled) {
                    anAssert.isTrue(checkbox.isChecked(), "Чекбокс в состоянии disabled");
                } else {
                    anAssert.isTrue(!checkbox.isChecked(), "Чекбокс в состоянии enabled");
                }
            } else {
                PuzRadioButton puzRadioButton = E(xpath, PuzRadioButton.class);
                if (!puzRadioButton.isVisible()) {
                    showAllFilters();
                }
                if (isEnabled) {
                    anAssert.isTrue(puzRadioButton.isSelected(), "Чекбокс в состоянии disabled");
                } else {
                    anAssert.isTrue(!puzRadioButton.isSelected(), "Чекбокс в состоянии enabled");
                }
            }
        }
        return this;
    }

    @Step("Проверить, что в комбобоксе 'Дата AVS' отображается дата: {expectedDate}")
    public SearchProductPage shouldAvsContainerContainsCorrectText(
            boolean isEmpty, LocalDate expectedDate) {
        if (isEmpty && !avsCalendarInputBox.isVisible()) {
            return this;
        }
        if (isEmpty) {
            String visibleText = avsCalendarInputBox.getSelectedText();
            anAssert.isTrue(visibleText.isEmpty(),
                    "Дата не должна быть отображена, однако отображается - " + visibleText);
        } else {
            LocalDate actualDate = avsCalendarInputBox.getSelectedDate();
            anAssert.isEquals(actualDate, expectedDate,
                    "Ожидалась другая avs Дата в соответстующем фильтре");

        }
        return this;
    }

    @Step("Проверить, что комбо-бокс фильтра гамма содержит корректный текст")
    public SearchProductPage shouldGammaDropBoxContainsCorrectText(boolean isEmpty, String... gammaFilters) throws Exception {
        if (gammaFilters.length > 0 && !isEmpty) {
            String visibleText = "";
            try {
                visibleText = gammaComboBox.findChildElement(".//span[contains(@class, 'sing')]").getText();
            } catch (NoSuchElementException e) {
                throw new AssertionError("Комбо-бокс не содержит текста");
            }
            if (gammaFilters.length == 1) {
                anAssert.isEquals(visibleText, gammaFilters[0], "Текст не соответствует паттерну \"Гамма Х\"");
            } else {
                anAssert.isEquals(visibleText, "Гамма (" + gammaFilters.length + ")", "Текст не соответсвует " +
                        "паттерну \"Гамма (i)\"");
            }
        } else {
            String emptyText = gammaComboBox.findChildElement(".//input").getAttribute("defaultValue");
            anAssert.isEquals(emptyText, "", "Комбо-бокс содержит текст");
        }
        return this;
    }

    @Step("Проверить, что комбо-бокс фильтра гамма содержит корректный текст")
    public SearchProductPage shouldTopDropBoxContainsCorrectText(boolean isEmpty, String... topFilters) throws Exception {
        if (topFilters.length > 0 && !isEmpty) {
            String visibleText = "";
            try {
                visibleText = topComboBox.findChildElement(".//span[contains(@class, 'sing')]").getText();
            } catch (NoSuchElementException e) {
                throw new AssertionError("Комбо-бокс не содержит текста");
            }
            if (topFilters.length == 1) {
                anAssert.isEquals(visibleText, topFilters[0], "Текст не соответствует паттерну \"Топ Х\"");
            } else {
                anAssert.isEquals(visibleText, "Топ (" + topFilters.length + ")", "Текст не соответсвует " +
                        "паттерну \"Топ (i)\"");
            }
        } else {
            String emptyText = topComboBox.findChildElement(".//input").getAttribute("defaultValue");
            anAssert.isEquals(emptyText, "", "Комбо-бокс содержит текст");
        }
        return this;
    }

    @Step("Проверить, что фильтры выбраны")
    public SearchProductPage checkFiltersChosen(FiltersData data) throws Exception {
        if (data.getGammaFilters().length > 0) {
            shouldGammaDropBoxContainsCorrectText(false, data.getGammaFilters());
        }
        if (data.getTopFilters().length > 0) {
            shouldTopDropBoxContainsCorrectText(false, data.getTopFilters());
        }
        if (data.getCheckBoxes().length > 0) {
            shouldCheckboxFilterHasCorrectCondition(true, data.getCheckBoxes());
        }
        if (data.getSuppliers().length > 0) {
            shouldSupplierComboBoxContainsCorrectText(false, data.getSuppliers());
        }
        if (data.getAvsDate() != null) {
            shouldAvsContainerContainsCorrectText(false, data.getAvsDate());
        }
        return this;
    }

    @Step("Проверить, что фильтры не выбраны")
    public SearchProductPage checkFiltersNotChosen(FiltersData data) throws Exception {
        if (!supplierComboBox.isVisible()) {
            showAllFilters();
        }
        if (data.getGammaFilters().length > 0) {
            shouldGammaDropBoxContainsCorrectText(true, data.getGammaFilters());
        }
        if (data.getTopFilters().length > 0) {
            shouldTopDropBoxContainsCorrectText(true, data.getTopFilters());
        }
        if (data.getCheckBoxes().length > 0) {
            shouldCheckboxFilterHasCorrectCondition(false, data.getCheckBoxes());
        }
        if (data.getSuppliers().length > 0) {
            shouldSupplierComboBoxContainsCorrectText(true, data.getSuppliers());
        }
        if (data.getAvsDate() != null) {
            shouldAvsContainerContainsCorrectText(true, data.getAvsDate());
        }
        return this;
    }

    @Step("Проверить, что отображается текст с текущим поисковым критерием {searchCriterion}")
    public SearchProductPage shouldSearchCriterionIs(boolean isVisible, String searchCriterion) {
        if (isVisible) {
            anAssert.isEquals(getCurrentNomenclatureName(), "Результаты поиска «" + searchCriterion + "»",
                    "Поисковой критерий не отрисован на странице");
        } else {
            anAssert.isNotEquals(getCurrentNomenclatureName(), "Результаты поиска «" + searchCriterion + "»",
                    "Поисковой критерий отрисован на странице");
        }
        return this;
    }

    @Step("Проверить, что запрос на поиск уходит {n} раз")
    public SearchProductPage shouldRequestHasBeenInitializedNTimes(int n, boolean bySearchInput) {
        int spinnerAppearCounter = 0;
        for (int i = 0; i < n; i++) {
            if (bySearchInput) {
                searchInput.submit();
            } else {
                applyFiltersBtn.click();
            }
            if (loadingSpinner.isVisible()) {
                spinnerAppearCounter++;
                loadingSpinner.waitForInvisibility();
            }
        }
        anAssert.isEquals(spinnerAppearCounter, n, "Запрос не был отправлен " + n + " раз");
        return this;
    }

    @Step("Проверить, что поисковая строка содержит {value}")
    public SearchProductPage shouldSearchInputContainsText(String value) {
        anAssert.isElementTextEqual(searchInput, value);
        return this;
    }

    @Step("Проверить, что комбо-бокс сортировки содержит {value}")
    public SearchProductPage shouldSortComboBoxContainsText(String value) {
        String actualText = sortComboBox.getSelectedOptionText();
        anAssert.isContainsIgnoringCase(actualText, value,
                actualText + " не содержит " + value);
        return this;
    }

    @Step("Проверить, что группа фильтров выбрана")
    public SearchProductPage shouldFilterGroupHasBeenChosen(FilterFrame frame) {
        String attributeName = "className";
        String condition = "active";
        if (frame.equals(FilterFrame.MY_SHOP)) {
            anAssert.isContainsIgnoringCase(myShopContainer.getAttribute(attributeName), condition,
                    "группа фильтров \"Мой магазин\" не выбрана");
        } else {
            anAssert.isContainsIgnoringCase(allGammaContainer.getAttribute(attributeName), condition,
                    "группа фильтров \"Вся гамма ЛМ\" не выбрана");
        }
        return this;
    }

    @Step("Проверить, что счетчик фильтров равен {count}")
    public SearchProductPage shouldFilterCounterHasCorrectCondition(int count) {
        if (count == 0) {
            anAssert.isElementNotVisible(filtersCounter);
        } else if (count > 0) {
            anAssert.isElementTextContains(filtersCounter, String.valueOf(count));
        } else {
            throw new IllegalArgumentException("count should be more than 0");
        }
        return this;
    }

    @Step("Проверить, что комбобокс \"Дата AVS\" не отображается")
    public SearchProductPage shouldAvsDateComboBoxHasCorrectCondition() throws Exception {
        if (avsCheckBox.isChecked()) {
            anAssert.isElementVisible(avsCalendarInputBox);
        } else {
            anAssert.isElementNotVisible(avsCalendarInputBox);
        }
        return this;
    }

    @Step("Проверить, что недоступны фильтры: топ пополнения, топ ЕМ, есть теор. запас, поставщик")
    public SearchProductPage shouldAllGammaFiltersHasCorrectCondition() {
        if (!supplierComboBox.isVisible()) {
            showAllFilters();
        }
        softAssert.isFalse(topComboBox.isEnabled(), "top comboBox is enabled");
        softAssert.isFalse(supplierComboBox.isEnabled(), "supplier comboBox is enabled");
        softAssert.isFalse(hasAvailableStockButton.isEnabled(), "hasAvailableStock is enabled");
        softAssert.isFalse(topEmButton.isEnabled(), "top EM is enabled");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что кнопка очистки фильтров имеет корректное состояние")
    public SearchProductPage shouldCleatAllFiltersButtonHasCorrectCondition(boolean isEnabled) {
        if (isEnabled) {
            anAssert.isTrue(clearAllFiltersInFilterFrameBtn.isEnabled(), "clear all filters btn is disabled");
        } else {
            anAssert.isFalse(clearAllFiltersInFilterFrameBtn.isEnabled(), "clear all filters btn is enabled");
        }
        return this;
    }
}