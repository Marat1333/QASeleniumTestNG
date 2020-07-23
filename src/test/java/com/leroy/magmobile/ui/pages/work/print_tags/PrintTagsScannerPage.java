package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.ScannerPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

public class PrintTagsScannerPage extends ScannerPage {
    @AppFindBy(containsText = "ВВЕСТИ ШТРИХ-КОД / ЛМ ВРУЧНУЮ")
    Button searchProductBtn;

    @AppFindBy(text = "СПИСОК ЦЕННИКОВ")
    Button tagsListBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\" and *[@text='СПИСОК ЦЕННИКОВ']]//android.view.ViewGroup/*")
    Element tagsCounterLbl;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        searchProductBtn.waitForVisibility();
    }

    @Step("Искать товар вручную")
    public SearchProductPage navigateToSearchProductPage(){
        searchProductBtn.click();
        return new SearchProductPage();
    }

    @Step("Перейти в список ценников")
    public TagsListPage navigateToTagsList(){
        tagsListBtn.click();
        return new TagsListPage();
    }

    @Step("Проверить отображение кнопки перехода в список ценников")
    public PrintTagsScannerPage shouldTagsListNavBtnIsVisible(boolean isVisible){
        if (isVisible){
            anAssert.isElementVisible(tagsListBtn);
        }else {
            anAssert.isElementNotVisible(tagsListBtn);
        }
        return this;
    }

    @Step("Проверить, что счетчик товаров в списке на печать отображает корректное значение")
    public PrintTagsScannerPage shouldCounterIsCorrect(int productsCount){
        anAssert.isElementTextEqual(tagsCounterLbl, String.valueOf(productsCount));
        return this;
    }

    @Override
    public void verifyRequiredElements() {
        super.verifyRequiredElements();
        softAssert.isElementVisible(searchProductBtn);
        softAssert.verifyAll();
    }
}
