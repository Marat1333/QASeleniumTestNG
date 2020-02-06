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

    @Override
    public void waitForPageIsLoaded() {
        top0Btn.waitForVisibility(short_timeout);
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
    public SuppliersSearchPage goToSuppliersSearchPage() throws Exception {
        mainScrollView.scrollDown();
        supplierBtn.click();
        return new SuppliersSearchPage(context);
    }

    @Step("Выбрать фильтр top")
    public void choseTopFilter() { //TODO Надо сделать данный метод с параметром, чтоб можно было выбирать ТОП 1 и другие
        clickElementAndWaitForContentIsChanged(top0Btn);
    }

}
