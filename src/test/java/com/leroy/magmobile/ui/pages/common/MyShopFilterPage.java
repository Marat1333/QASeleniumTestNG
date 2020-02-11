package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class MyShopFilterPage extends FilterPage {

    public MyShopFilterPage(TestContext context) {
        super(context);
    }

    public static final String TOP = "ТОП";
    public static final String TOP_EM = "Топ ЕМ";
    public static final String HAS_AVAILABLE_STOCK = "Есть теор. запас";

    @AppFindBy(text = "ТОП 0")
    Element top0Btn;

    @AppFindBy(text = "Поставщик")
    Element supplierBtn;

    Element topEm = E("contains(Топ ЕМ)");

    Element hasAvailableStock = E("contains(Есть теор. запас)");

    final String CLEAR_SUPPLIERS_FILTER_BTN_XPATH = "//android.widget.EditText[contains(@text,%s)]/ancestor::android.view.ViewGroup[2]/following-sibling::android.view.ViewGroup";

    @Override
    public void waitForPageIsLoaded() {
        top0Btn.waitForVisibility(short_timeout);
    }

    public MyShopFilterPage scroll(String direction) {
        if (direction.equals("down")) {
            mainScrollView.scrollDown();
        } else {
            mainScrollView.scrollUp();
        }
        return new MyShopFilterPage(context);
    }

    @Override
    @Step("Выбрать checkBox фильтр {value}")
    public void choseCheckBoxFilter(String value) throws Exception {
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
                mainScrollView.scrollDown();
                avs.click();
                mainScrollView.scrollUp();
                break;
            default:
                throw new Exception();
        }
    }

    @Step("Перейти на страницу выбора поставщиков")
    public SuppliersSearchPage goToSuppliersSearchPage(boolean hideKeyboard) {
        mainScrollView.scrollDown();
        supplierBtn.click();
        if (hideKeyboard) {
            hideKeyboard();
        }
        return new SuppliersSearchPage(context);
    }

    @Step("Очистить поле с фильтром по поставщику")
    public MyShopFilterPage clearSuppliersFilter(String supplierName) throws Exception {
        mainScrollView.scrollDown();
        Element clearSuppliersFilterBtn = E(String.format(CLEAR_SUPPLIERS_FILTER_BTN_XPATH, supplierName));
        clearSuppliersFilterBtn.click();
        return new MyShopFilterPage(context);
    }

    @Step("Выбрать фильтр top {top}")
    public MyShopFilterPage choseTopFilter(String top) {
        Element element = E("contains(" + top + ")");
        clickElementAndWaitUntilContentIsChanged(element);
        return new MyShopFilterPage(context);
    }

    //VERIFICATIONS

    @Step("Проверяем, что кнопка выбора фильтра по поставщикам содержит текст {supplierName}")
    public MyShopFilterPage shouldSupplierButtonContainsText(int countOfChosenSuppliers, String supplierName) {
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
