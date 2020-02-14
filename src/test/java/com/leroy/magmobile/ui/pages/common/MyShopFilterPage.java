package com.leroy.magmobile.ui.pages.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyShopFilterPage extends FilterPage {

    public MyShopFilterPage(TestContext context) {
        super(context);
    }

    public static final String TOP = "ТОП";
    public static final String TOP_EM = "Топ ЕМ";
    public static final String HAS_AVAILABLE_STOCK = "Есть теор. запас";

    @AppFindBy(text = "Есть теор. запас")
    Element hasAvailableStockLbl;

    @AppFindBy(text = "Поставщик")
    Element supplierBtn;

    Element topEm = E("contains(Топ ЕМ)");

    Element hasAvailableStock = E("contains(Есть теор. запас)");

    Element bestPrice = E("contains(Лучшая цена)");

    final String CLEAR_SUPPLIERS_FILTER_BTN_XPATH = "//android.widget.EditText[contains(@text,%s)]/ancestor::android.view.ViewGroup[2]/following-sibling::android.view.ViewGroup";

    @Override
    public void waitForPageIsLoaded() {
        hasAvailableStockLbl.waitForVisibility(short_timeout);
    }

    @Step("Нажать на \"Показать все фильтры\"")
    public MyShopFilterPage clickShowAllFiltersBtn() {
        showAllFiltersBtn.click();
        return new MyShopFilterPage(context);
    }

    @Override
    public MyShopFilterPage choseFewFilters(Object... filters) throws Exception {
        clickShowAllFiltersBtn();
        for (Object filter : filters) {
            if ((filter.getClass() == String.class && (String.valueOf(filter).contains("ТОП")))) {
                choseTopFilter(String.valueOf(filter));
            } else if (filter.getClass() == String.class && (String.valueOf(filter).matches("\\d+"))) {
                goToSuppliersSearchPage(false);
                new SuppliersSearchPage(context)
                        .searchForAndChoseSupplier(String.valueOf(filter))
                        .applyChosenSupplier();
                new MyShopFilterPage(context);
            } else {
                super.choseFewFilters(filter);
            }
        }
        return new MyShopFilterPage(context);
    }

    @Override
    @Step("Выбрать checkBox фильтр {value}")
    public MyShopFilterPage choseCheckBoxFilter(String value) throws Exception {
        switch (value) {
            case TOP_EM:
                topEm.click();
                break;
            case TOP_1000:
                top1000.click();
                break;
            case CTM:
                ctm.click();
                break;
            case HAS_AVAILABLE_STOCK:
                hasAvailableStock.click();
                break;
            case BEST_PRICE:
                bestPrice.click();
                break;
            case LIMITED_OFFER:
                limitedOffer.click();
                break;
            case AVS:
                mainScrollView.scrollDown(2);
                String pageSource = getPageSource();
                avs.click();
                if (!waitUntilContentIsChanged(pageSource)) {
                    mainScrollView.scrollDown();
                    avs.click();
                }
                break;
            default:
                throw new Exception();
        }
        return new MyShopFilterPage(context);
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

    @Step("Очистить поле с фильтром по поставщику")
    public MyShopFilterPage clearSuppliersFilter(String supplierName) {
        mainScrollView.scrollDown();
        Element clearSuppliersFilterBtn = E(String.format(CLEAR_SUPPLIERS_FILTER_BTN_XPATH, supplierName));
        clearSuppliersFilterBtn.click();
        return new MyShopFilterPage(context);
    }

    @Step("Выбрать фильтр top {top}")
    public MyShopFilterPage choseTopFilter(String top) {
        Element element = E("contains(" + top + ")");
        if (!element.isVisible()){
            mainScrollView.scrollUpToElement(element);
        }
        clickElementAndWaitUntilContentIsChanged(element);
        return new MyShopFilterPage(context);
    }

    @Step("Очистить дату AVS, нажав на крест")
    public MyShopFilterPage clearAvsDate() {
        if (!clearAvsDateBtn.isVisible()) {
            mainScrollView.scrollDown();
        }
        String pageSource = getPageSource();
        clearAvsDateBtn.click();
        if (!waitUntilContentIsChanged(pageSource)) {
            mainScrollView.scrollDown();
            clearAvsDateBtn.click();
        }
        return new MyShopFilterPage(context);
    }

    //VERIFICATIONS


    @Override
    public MyShopFilterPage verifyFewFiltersAreChosen(Object... filters) throws Exception {
        if (!bestPrice.isVisible()) {
            clickShowAllFiltersBtn();
        }
        for (Object filter : filters) {
            if ((filter.getClass() == String.class && (String.valueOf(filter).contains("ТОП")))) {
                if (!myShopBtn.isVisible()){
                    mainScrollView.scrollUpToElement(myShopBtn);
                }
                shouldFilterHasBeenChosen(String.valueOf(filter));
            } else {
                super.verifyFewFiltersAreChosen(filter);
            }
        }
        return this;
    }

    @Override
    public FilterPage verifyFewFiltersAreNotChosen(Object... filters) throws Exception {
        if (!bestPrice.isVisible()) {
            clickShowAllFiltersBtn();
        }
        for (Object filter : filters) {
            if ((filter.getClass() == String.class && (String.valueOf(filter).contains("ТОП")))) {
                if (!myShopBtn.isVisible()){
                    mainScrollView.scrollUpToElement(myShopBtn);
                }
                shouldFilterHasNotBeenChosen(String.valueOf(filter));
            } else {
                super.verifyFewFiltersAreNotChosen(filter);
            }
        }
        return this;
    }

    @Step("Проверяем, что кнопка выбора фильтра по поставщикам содержит текст {supplierName}")
    public MyShopFilterPage shouldSupplierButtonContainsText(int countOfChosenSuppliers, String supplierName) {
        if (!supplierBtn.isVisible()){
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

}
