package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class NomenclatureSearchPage extends BaseAppPage {
    public NomenclatureSearchPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Номер выбранной номенклатуры")
    Element screenTitle;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[following-sibling::android.widget.TextView]/android.view.ViewGroup")
    Element nomenclatureBackBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Все отделы']/following::android.widget.TextView[1]")
    Element selectedNomenclatureLbl;

    @AppFindBy(text = "ПОКАЗАТЬ ВСЕ ТОВАРЫ")
    Element showAllGoods;

    @Step("Вернитесь на окно выбора отдела")
    public NomenclatureSearchPage returnToDepartmentChoseWindow() {
        int counter = 0;
        while (nomenclatureBackBtn.isVisible()) {
            if (counter > 3) {
                break;
            }
            nomenclatureBackBtn.click();
            counter++;
        }
        return this;
    }

    @Step("Нажмите 'Показать все товары'")
    public SearchProductPage clickShowAllProductsBtn() {
        showAllGoods.click();
        SearchProductPage searchPage = new SearchProductPage(context);
        waitForProgressBarIsVisible();
        waitForProgressBarIsInvisible();
        return searchPage;
    }

    @Override
    public void waitForPageIsLoaded() {
        showAllGoods.waitForVisibility();
    }

    // Verifications

    @Override
    public NomenclatureSearchPage verifyRequiredElements() {
        softAssert.isElementVisible(screenTitle);
        softAssert.isElementVisible(showAllGoods);
        softAssert.verifyAll();
        return this;
    }

    public NomenclatureSearchPage shouldTitleWithNomenclatureIs(String text) {
        if (text.isEmpty())
            anAssert.isElementNotVisible(screenTitle);
        else
            anAssert.isElementTextEqual(screenTitle, text);
        return this;
    }

    public NomenclatureSearchPage shouldSelectedNomenclatureIs(String text) {
        anAssert.isElementTextEqual(selectedNomenclatureLbl, text);
        return this;
    }

    public NomenclatureSearchPage verifyNomenclatureBackBtnVisibility(boolean shouldBeVisible) {
        if (shouldBeVisible)
            anAssert.isElementVisible(nomenclatureBackBtn);
        else
            anAssert.isElementNotVisible(nomenclatureBackBtn);
        return this;
    }
}
