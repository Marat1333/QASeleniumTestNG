package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.ScannerWithSearchBtnPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.DeleteSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.FinishSessionAcceptModalPage;
import io.qameta.allure.Step;

public class RupturesScannerPage extends ScannerWithSearchBtnPage {
    @AppFindBy(text = "Сканирование по одному")
    Element rupturesByOneLbl;

    @AppFindBy(text = "Массовое сканирование")
    Element rupturesBulkLbl;

    @AppFindBy(accessibilityId = "deleteBulkSessionBtn")
    Element rupturesDeleteBulkSessionBtn;

    @AppFindBy(accessibilityId = "finishBulkSessionBtn")
    Element rupturesFinishBulkSessionBtn;

    @AppFindBy(xpath = "//*[@class = 'android.widget.Toast']")
    Element rupturesToast;

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

    @Step("Завершить массовую сессию")
    public FinishSessionAcceptModalPage finishBulkSession() {
        rupturesFinishBulkSessionBtn.click();
        return new FinishSessionAcceptModalPage();
    }

    @Step("Удалить массовую сессию")
    public DeleteSessionModalPage deleteBulkSession() {
        rupturesDeleteBulkSessionBtn.click();
        return new DeleteSessionModalPage();
    }

    @Step("Перейти в список перебоев")
    public ActiveSessionPage navigateToRuptureProductList() {
        rupturesListBtn.click();
        return new ActiveSessionPage();
    }

    @Step("Проверить отображение кнопки перехода в список перебоев")
    public RupturesScannerPage shouldRupturesListNavBtnIsVisible(boolean isVisible) {
        if (isVisible) {
            anAssert.isElementVisible(rupturesListBtn);
        } else {
            anAssert.isElementNotVisible(rupturesListBtn);
        }
        return this;
    }

    @Step("Проверить, что счетчик перебоев отображает корректное значение")
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

    @Step("Проверить отображение кнопки удаления массовой сессии")
    public RupturesScannerPage shouldDeleteButtonIsVisible(boolean isVisible) {
        if (isVisible) {
            anAssert.isElementVisible(rupturesDeleteBulkSessionBtn);
        } else {
            anAssert.isElementNotVisible(rupturesDeleteBulkSessionBtn);
        }
        return this;
    }

    @Step("Проверить отображение кнопки завершения массовой сессии")
    public RupturesScannerPage shouldFinishButtonIsVisible(boolean isVisible) {
        if (isVisible) {
            anAssert.isElementVisible(rupturesFinishBulkSessionBtn);
        } else {
            anAssert.isElementNotVisible(rupturesFinishBulkSessionBtn);
        }
        return this;
    }

    @Step("Проверить тост успешного добавления")
    public RupturesScannerPage checkSuccessToast() {
        anAssert.isEquals(rupturesToast.getText(), "Успешно добавлено!", "Некорректный текст тоста");
        return this;
    }
}
