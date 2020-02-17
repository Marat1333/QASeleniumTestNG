package com.leroy.magmobile.ui.pages.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidHorizontalScrollView;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import com.leroy.magmobile.ui.pages.common.widget.SupplierCardWidget;
import com.leroy.magmobile.ui.pages.widgets.CalendarWidget;
import com.leroy.models.FiltersData;
import com.leroy.models.TextViewData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.util.NoSuchElementException;

public class FilterPage extends BaseAppPage {

    private static final String SCREEN_TITLE = "Фильтры по товарам";

    public FilterPage(TestContext context) {
        super(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        screenTitleLbl.waitUntilTextIsEqualTo(SCREEN_TITLE);
    }

    public static final String GAMMA = "ГАММА";
    public static final String TOP = "ТОП";
    public static final String TOP_1000 = "Toп 1000";
    public static final String TOP_EM = "Топ ЕМ";
    public static final String CTM = "CTM";
    public static final String BEST_PRICE = "Лучшая цена";
    public static final String LIMITED_OFFER = "Предложение ограничено";
    public static final String MY_SHOP_FRAME_TYPE = "МОЙ МАГАЗИН";
    public static final String ALL_GAMMA_FRAME_TYPE = "ВСЯ ГАММА ЛМ";
    public static final String AVS = "AVS";
    public static final String COMMON_PRODUCT_TYPE = "ОБЫЧНЫЙ";
    public static final String ORDERED_PRODUCT_TYPE = "ПОД ЗАКАЗ";
    public static final String HAS_AVAILABLE_STOCK = "Есть теор. запас";

    private final String HORIZONTAL_SCROLL = "//android.widget.TextView[contains(@text,'%s')]/ancestor::android.widget.HorizontalScrollView";

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок экрана 'Фильтры по товарам'")
    Element screenTitleLbl;

    @AppFindBy(accessibilityId = "BackButton")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]//android.view.ViewGroup[@content-desc=\"Button\"]")
    Element clearAllFiltersBtn;

    @AppFindBy(xpath = AndroidScrollView.TYPICAL_XPATH, metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<TextViewData> mainScrollView;

    AndroidHorizontalScrollView<TextViewData> gammaFilterScrollView = new AndroidHorizontalScrollView<>(driver,
            By.xpath("//android.widget.ScrollView//android.view.ViewGroup[3]/android.widget.HorizontalScrollView"));

    @AppFindBy(text = "МОЙ МАГАЗИН")
    MagMobCheckBox myShopBtn;

    @AppFindBy(text = "ВСЯ ГАММА ЛМ")
    Element gammaLmBtn;

    @AppFindBy(text = "Топ пополнения")
    Element topReplenishmentLabel;

    @AppFindBy(text = "ПОКАЗАТЬ ВСЕ ФИЛЬТРЫ")
    Element showAllFiltersBtn;

    // Чек-бокс фильтры:
    @AppFindBy(text = "Есть теор. запас")
    Element hasAvailableStock;

    @AppFindBy(text = "Топ ЕМ")
    Element topEm;

    @AppFindBy(text = "Лучшая цена")
    Element bestPrice;

    @AppFindBy(text = "Toп 1000")
    Element top1000;

    @AppFindBy(text = "Предложение ограничено")
    Element limitedOffer;

    @AppFindBy(text = "CTM")
    Element ctm;

    ///////////////////////////

    @AppFindBy(text = "Поставщик")
    Element supplierBtn;

    final String CLEAR_SUPPLIERS_FILTER_BTN_XPATH = "//android.widget.EditText[contains(@text,%s)]/ancestor::android.view.ViewGroup[2]/following-sibling::android.view.ViewGroup";

    // Тип продукта:
    @AppFindBy(text = "ПОД ЗАКАЗ")
    Element orderedProductBtn;

    @AppFindBy(text = "ОБЫЧНЫЙ")
    Element commonProductBtn;

    // AVS
    @AppFindBy(text = "AVS")
    Element avs;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Дата AVS']/following-sibling::android.view.ViewGroup")
    Element avsDateIcon;

    //@AppFindBy(xpath = "//android.widget.TextView[@text='Дата AVS']/following-sibling::android.view.ViewGroup/android.view.ViewGroup")
    //Element clearAvsDateBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Дата AVS']/following-sibling::android.widget.TextView")
    Element chosenAvsDate;

    @AppFindBy(text = "Дата AVS")
    Element avsDateBtn;

    @AppFindBy(text = "ПОКАЗАТЬ ТОВАРЫ")
    Element showGoodsBtn;

    // ------------------- ACTIONS --------------------------//

    @Step("Нажать на 'Показать все фильтры'")
    public FilterPage clickShowAllFiltersBtn() {
        showAllFiltersBtn.click();
        showAllFiltersBtn.waitForInvisibility();
        return this;
    }

    @Step("Выбрать фильтры: {filters}")
    public FilterPage choseFilters(FiltersData filters) throws Exception {
        if (filters.getFilterFrame() != null)
            switchFiltersFrame(filters.getFilterFrame());
        if (showAllFiltersBtn.isVisible()) {
            clickShowAllFiltersBtn();
        }
        if (filters.getFilterFrame() != null) switchFiltersFrame(filters.getFilterFrame());
        if (filters.getGamma() != null)
            for (String gamma : filters.getGamma())
                choseGammaFilter(gamma);
        if (filters.getTop() != null)
            for (String top : filters.getTop())
                choseTopFilter(top);
        if (filters.getSupplier() != null) selectSupplier(filters.getSupplier()[0]);
        if (filters.getProductType() != null) choseProductType(filters.getProductType());
        choseCheckBoxFilters(filters);
        if (filters.getDateAvs() != null) choseAvsDate(filters.getDateAvs());
        // TO BE CONTINUED
        return this;
    }

    @Step("Перейти на страницу выбора поставщиков")
    public SuppliersSearchPage goToSuppliersSearchPage(boolean hideKeyboard) {
        mainScrollView.scrollDownToElement(supplierBtn);
        supplierBtn.click();
        if (hideKeyboard) {
            hideKeyboard();
        }
        return new SuppliersSearchPage(context);
    }

    @Step("Выбрать поставщика: {val}")
    public FilterPage selectSupplier(String val) {
        return goToSuppliersSearchPage(false)
                .searchForAndChoseSupplier(val)
                .applyChosenSupplier();
    }

    @Step("Очистить поле с фильтром по поставщику")
    public FilterPage clearSuppliersFilter(String supplierName) {
        mainScrollView.scrollDown();
        Element clearSuppliersFilterBtn = E(String.format(CLEAR_SUPPLIERS_FILTER_BTN_XPATH, supplierName));
        clearSuppliersFilterBtn.click();
        return this;
    }

    @Step("Выбрать фильтр top {top}")
    public FilterPage choseTopFilter(String top) {
        Element element = E("contains(" + top + ")");
        if (!element.isVisible()) {
            mainScrollView.scrollUpToElement(element);
        }
        clickElementAndWaitUntilContentIsChanged(element);
        return this;
    }

    @Step("Очистить дату AVS, нажав на крест")
    public FilterPage clearAvsDate() {
        if (!avsDateIcon.isVisible()) {
            mainScrollView.scrollToEnd();
        }
        avsDateIcon.click();
        return this;
    }

    @Step("Проскроллить фильтры до {neededElement}")
    public void scrollHorizontalWidget(String neededScroll, String neededElement) {
        Element goalElement = E("contains(" + neededElement + ")");
        Element anchorElement = null;
        try {
            anchorElement = E(String.format(HORIZONTAL_SCROLL, neededScroll));
        } catch (NoSuchElementException e) {
            Log.error("Выбрана несуществующая горизонтальная область скролла. Необходимо выбрать из: ГАММА, ТОП, ОБЫЧНЫЙ (тип продукта)");
        }
        swipeRightTo(anchorElement, goalElement);
    }

    @Step("Вернуться на страницу поиска товаров и услуг")
    public SearchProductPage returnBack() {
        backBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Выбрать фрейм фильтров {value}")
    public FilterPage switchFiltersFrame(String value) {
        if (!gammaLmBtn.isVisible())
            mainScrollView.scrollToBeginning();
        if (value.equals(ALL_GAMMA_FRAME_TYPE)) {
            gammaLmBtn.click();
            topReplenishmentLabel.waitForInvisibility();
        } else {
            myShopBtn.click();
            topReplenishmentLabel.waitForVisibility();
        }
        return this;
    }

    @Step("Очистить все фильтры")
    public FilterPage clearAllFilters() {
        clearAllFiltersBtn.click();
        clearAllFiltersBtn.waitForInvisibility();
        return this;
    }

    @Step("Выбрать чек-бокс фильтры")
    public FilterPage choseCheckBoxFilters(FiltersData filtersData) throws Exception {
        if (filtersData.isTopEM())
            choseCheckBoxFilter(TOP_EM);
        if (filtersData.isHasAvailableStock())
            choseCheckBoxFilter(HAS_AVAILABLE_STOCK);
        if (filtersData.isTop1000())
            choseCheckBoxFilter(TOP_1000);
        if (filtersData.isCtm())
            choseCheckBoxFilter(CTM);
        if (filtersData.isBestPrice())
            choseCheckBoxFilter(BEST_PRICE);
        if (filtersData.isLimitedOffer())
            choseCheckBoxFilter(LIMITED_OFFER);
        if (filtersData.isAvs())
            choseCheckBoxFilter(AVS);
        return this;
    }

    @Step("Выбрать checkBox фильтр {value}")
    public FilterPage choseCheckBoxFilter(String value) throws Exception {
        switch (value) {
            case TOP_EM:
                topEm.click();
                break;
            case HAS_AVAILABLE_STOCK:
                hasAvailableStock.click();
                break;
            case TOP_1000:
                top1000.click();
                break;
            case CTM:
                ctm.click();
                break;
            case BEST_PRICE:
                bestPrice.click();
                break;
            case LIMITED_OFFER:
                limitedOffer.click();
                break;
            case AVS:
                mainScrollView.scrollToEnd();
                avs.click();
                break;
            default:
                throw new IllegalArgumentException("Checkbox filter with name " + value + " does`nt exist");
        }
        return this;
    }

    @Step("Выбрать фильтр {gamma}")
    public void choseGammaFilter(String gamma) {
        gamma = gamma.toUpperCase();
        if (!gammaFilterScrollView.isVisible()) {
            mainScrollView.scrollUpToElement(myShopBtn);
        }
        try {
            Element element = E("contains(" + gamma + ")");
            gammaFilterScrollView.scrollRight(element);
            element.click();
        } catch (NoSuchElementException e) {
            Log.error("Выбранная Гамма не найдена");
        }
    }

    @Step("Выбрать тип продукта {type}")
    public void choseProductType(String type) {
        mainScrollView.scrollDown();
        if (type.equals(COMMON_PRODUCT_TYPE)) {
            commonProductBtn.click();
        } else {
            orderedProductBtn.click();
        }
    }

    @Step("Выбрать дату avs")
    public FilterPage choseAvsDate(LocalDate date) throws Exception {
        mainScrollView.scrollToEnd();
        avsDateBtn.click();
        CalendarWidget calendarWidget = new CalendarWidget(context.getDriver());
        calendarWidget.selectDate(date);
        return this;
    }

    @Step("Показать товары по выбранным фильтрам")
    public SearchProductPage applyChosenFilters() {
        mainScrollView.scrollDownToElement(showGoodsBtn);
        showGoodsBtn.click();
        waitUntilProgressBarIsVisible();
        SearchProductPage page = new SearchProductPage(context);
        hideKeyboard();
        return page;
    }

    // -------------- Verifications ------------------------ //

    private FilterPage checkFilters(FiltersData filtersData, boolean shouldBeChecked) throws Exception {
        if (!gammaLmBtn.isVisible())
            mainScrollView.scrollToBeginning();
        if (filtersData.getFilterFrame() != null)
            if (shouldBeChecked)
                shouldFilterHasBeenChosen(filtersData.getFilterFrame());
        if (filtersData.getGamma() != null)
            for (String gamma : filtersData.getGamma())
                if (shouldBeChecked)
                    shouldFilterHasBeenChosen(gamma);
                else
                    shouldFilterHasNotBeenChosen(gamma);
        if (filtersData.getFilterFrame().equals(MY_SHOP_FRAME_TYPE) &&
                filtersData.getTop() != null)
            for (String top : filtersData.getTop())
                if (shouldBeChecked)
                    shouldFilterHasBeenChosen(top);
                else
                    shouldFilterHasNotBeenChosen(top);
        // Check-boxes:
        if (filtersData.getFilterFrame().equals(MY_SHOP_FRAME_TYPE)) {
            if (filtersData.isHasAvailableStock())
                if (shouldBeChecked) shouldElementHasBeenSelected(HAS_AVAILABLE_STOCK);
                else shouldElementHasNotBeenSelected(HAS_AVAILABLE_STOCK);
            if (filtersData.isTopEM())
                if (shouldBeChecked) shouldElementHasBeenSelected(TOP_EM);
                else shouldElementHasNotBeenSelected(TOP_EM);
        }
        mainScrollView.scrollToEnd();
        if (filtersData.isTop1000())
            if (shouldBeChecked) shouldElementHasBeenSelected(TOP_1000);
            else shouldElementHasNotBeenSelected(TOP_1000);
        if (filtersData.isCtm())
            if (shouldBeChecked) shouldElementHasBeenSelected(CTM);
            else shouldElementHasNotBeenSelected(CTM);
        if (filtersData.isBestPrice())
            if (shouldBeChecked) shouldElementHasBeenSelected(BEST_PRICE);
            else shouldElementHasNotBeenSelected(BEST_PRICE);
        if (filtersData.isLimitedOffer())
            if (shouldBeChecked) shouldElementHasBeenSelected(LIMITED_OFFER);
            else shouldElementHasNotBeenSelected(LIMITED_OFFER);
        if (filtersData.isAvs())
            if (shouldBeChecked) shouldElementHasBeenSelected(AVS);
            else shouldElementHasNotBeenSelected(AVS);
        // End check-boxes verifications
        // Тип продукта
        if (filtersData.getProductType() != null)
            if (shouldBeChecked)
                shouldFilterHasBeenChosen(filtersData.getProductType());
            else
                shouldFilterHasNotBeenChosen(filtersData.getProductType());
        // AVS дата
        if (filtersData.getDateAvs() != null)
            if (shouldBeChecked)
                shouldAvsDateIsCorrect(filtersData.getDateAvs());
            else
                shouldAvsDateIsCorrect(null);

        return this;
    }

    @Step("Проверить, что фильтры выбраны: {filtersData}")
    public FilterPage shouldFiltersAreSelected(FiltersData filtersData) throws Exception {
        return checkFilters(filtersData, true);
    }

    @Step("Проверить, что фильтры НЕ выбраны: {filtersData}")
    public FilterPage shouldFiltersAreNotSelected(FiltersData filtersData) throws Exception {
        return checkFilters(filtersData, false);
    }

    @Step("Проверяем, что кнопка выбора фильтра по поставщикам содержит текст {supplierName}")
    public FilterPage shouldSupplierButtonContainsText(int countOfChosenSuppliers, String supplierName) {
        if (!supplierBtn.isVisible()) {
            mainScrollView.scrollDownToElement(supplierBtn);
        }
        Element element;
        if (countOfChosenSuppliers == 1) {
            element = E("contains(" + supplierName + ")");
        } else if (countOfChosenSuppliers > 1) {
            element = E("contains(Выбрано " + countOfChosenSuppliers + ")");
        } else {
            element = supplierBtn;
        }
        anAssert.isElementVisible(element);
        return this;
    }

    @Step("Проверить, что отображенная дата соответствует выбранной")
    public FilterPage shouldAvsDateIsCorrect(LocalDate date) {
        String pageSource = getPageSource();
        if (date == null) {
            anAssert.isElementNotVisible(chosenAvsDate, pageSource);
        } else {
            String dateAsString = date.getDayOfMonth() + ".";
            String month = date.getMonthValue() > 9 ? String.valueOf(date.getMonthValue()) : "0" + date.getMonthValue();
            dateAsString = dateAsString + month + "." + String.valueOf(date.getYear()).substring(2);
            anAssert.isElementTextEqual(chosenAvsDate, dateAsString, pageSource);
        }
        return this;
    }

    @Step("Проверить, что кнопка очистки даты AVS отображается")
    public FilterPage shouldClearAvsDateBtnIsVisible() {
        anAssert.isElementImageMatches(avsDateIcon, MagMobElementTypes.CROSS_FILTER_PAGE.getPictureName());
        return this;
    }

    @Step("Проверить, что кнопка добавления даты AVS отображается")
    public FilterPage shouldAddAvsDateBtnIsVisible() {
        anAssert.isElementImageMatches(avsDateIcon, MagMobElementTypes.PLUS_FILTER_PAGE.getPictureName());
        return this;
    }

    @Step("Проверить, что выбран чек-бокс {value}")
    public FilterPage shouldElementHasBeenSelected(String value) {
        Element anchorElement = E(String.format(SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value),
                String.format("Чек-бокс %s", value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_SELECTED_FILTER_PAGE.getPictureName());
        return this;
    }

    @Step("Проверить, что чек-бокс {value} не выбран")
    public FilterPage shouldElementHasNotBeenSelected(String value) {
        Element anchorElement = E(String.format(SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value),
                String.format("Чек-бокс %s", value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_NOT_SELECTED_FILTER_PAGE.getPictureName());
        return this;
    }

    @Step("Проверить, что выбрана Радиогруппа {value}")
    public FilterPage shouldFilterHasBeenChosen(String value) throws Exception {
        MagMobCheckBox element = new MagMobCheckBox(driver, new CustomLocator(By.xpath("//*[contains(@text, '" + value + "')]")));
        anAssert.isTrue(element.isChecked(), "Фильтр '" + value + "' должен быть выбран");
        return this;
    }

    @Step("Проверить, что Радиогруппа {value} НЕ выбрана")
    public FilterPage shouldFilterHasNotBeenChosen(String value) throws Exception {
        MagMobCheckBox element = new MagMobCheckBox(driver, new CustomLocator(By.xpath("//*[contains(@text, '" + value + "')]")));
        anAssert.isFalse(element.isChecked(), "Фильтр '" + value + "' не должен быть выбран");
        return this;
    }

    @Step("Проверить, что кнопка \"Метла\" должна быть видна - {isVisible}")
    public FilterPage shouldClearAllFiltersBeOnPage(boolean isVisible) {
        String pageSource = getPageSource();
        if (isVisible) {
            anAssert.isElementVisible(clearAllFiltersBtn, pageSource);
        } else {
            anAssert.isElementNotVisible(clearAllFiltersBtn, pageSource);
        }
        return this;
    }

}
