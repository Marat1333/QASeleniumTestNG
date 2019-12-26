package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.StaleElementReferenceException;

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
            try {
                nomenclatureBackBtn.getWebElement().click();
            } catch (StaleElementReferenceException err) {
                // Nothing to do. Button disappeared, then break loop
                break;
            }
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
        String format = "%s - %s - %s - %s";
        String emptyText = "_ _ _";
        String expectedText;
        if (text.contains("_"))
            expectedText = text;
        else if (text.isEmpty())
            expectedText = String.format(format, emptyText, emptyText, emptyText, emptyText);
        else if (text.length() < 4)
            expectedText = String.format(format, text, emptyText, emptyText, emptyText);
        else if (text.length() < 7)
            expectedText = String.format(format, text.substring(0, 3), text.substring(3, 6), emptyText, emptyText);
        else if (text.length() < 10)
            expectedText = String.format(
                    format, text.substring(0, 3), text.substring(3, 6), text.substring(6, 9), emptyText);
        else
            expectedText = String.format(
                    format, text.substring(0, 3), text.substring(3, 6), text.substring(6, 9), text.substring(9));
        anAssert.isElementTextEqual(screenTitle, expectedText);
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
