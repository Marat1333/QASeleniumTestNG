package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.ScannerWithSearchBtnPage;
import io.qameta.allure.Step;

public class RupturesScannerPage extends ScannerWithSearchBtnPage {
    @AppFindBy(text = "СПИСОК ПЕРЕБОЕВ")
    Button rupturesListBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\" and *[@text='СПИСОК ПЕРЕБОЕВ']]//android.view.ViewGroup/*")
    Element rupturesCounterLbl;

    @Override
    public void closeScanner() {
        //этот сканер шустрее отрабатывает и с первого раза закрывается
        closeScannerBtn.click();
    }

    @Step("Перейти в список перебоя")
    public SessionProductsListPage navigateToRuptureProductList() {
        rupturesListBtn.click();
        return new SessionProductsListPage();
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
}
