package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.ScannerWithSearchBtnPage;
import io.qameta.allure.Step;

public class RupturesScannerPage extends ScannerWithSearchBtnPage {
    @AppFindBy(text = "Сканирование по одному")
    Element rupturesByOneLbl;

    @AppFindBy(text = "Массовое сканирование")
    Element rupturesBulkLbl;

    @AppFindBy(text = "СПИСОК ПЕРЕБОЕВ")
    Button rupturesListBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup/android.widget.TextView[contains(lower-case(@text), 'сканирование')]/following-sibling::android.view.ViewGroup/android.widget.TextView")
    Element rupturesCounterLbl;

    public int getCounterValue() {
        return Integer.parseInt(rupturesCounterLbl.getTextIfPresent());
    }

    @Override
    public void closeScanner() {
        //этот сканер шустрее отрабатывает и с первого раза закрывается
        closeScannerBtn.click();
    }

    @Step("Перейти в список перебоев")
    public ActiveSessionPage navigateToRuptureProductList() {
        rupturesListBtn.click();
        return new ActiveSessionPage();
    }

    @Step("Проверить отображение кнопки перехода в список ценников")
    public RupturesScannerPage shouldRupturesListNavBtnIsVisible(boolean isVisible) {
        if (isVisible) {
            anAssert.isElementVisible(rupturesListBtn);
        } else {
            anAssert.isElementNotVisible(rupturesListBtn);
        }
        return this;
    }

    @Step("Проверить, что счетчик товаров в списке на печать отображает корректное значение")
    public RupturesScannerPage shouldCounterIsCorrect(int productsCount) {
        anAssert.isElementTextEqual(rupturesCounterLbl, String.valueOf(productsCount));
        return this;
    }

    @Step("Проверить отображение лейбла 'Сканирование по одному'")
    public RupturesScannerPage shouldRupturesByOneLblIsVisible() {
        anAssert.isElementVisible(rupturesByOneLbl);
        return this;
    }

    @Step("Проверить отображение лейбла 'Массовое сканирование'")
    public RupturesScannerPage shouldRupturesBulkLblIsVisible() {
        anAssert.isElementVisible(rupturesBulkLbl);
        return this;
    }

}
