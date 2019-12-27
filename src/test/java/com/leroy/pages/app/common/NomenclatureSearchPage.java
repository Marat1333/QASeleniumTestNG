package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[1]]", metaName = "Окно выбора department")
    ElementList<Element> firstLevelNomenclatureElementsList;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[1]]", metaName = "Окно выбора последующих элементов")
    ElementList<Element> secondLevelNomenclatureElementsList;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[preceding-sibling::android.view.ViewGroup]")
    ElementList<Element> thirdLevelNomenclatureElementsList;

    private final String eachElementOfNomenclatureXpath = "./android.view.ViewGroup/android.widget.TextView";

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

    @Step("Показать товары по выбранной номенклатуре")
    public SearchProductPage viewAllProducts(){
        showAllGoods.click();
        return new SearchProductPage(context);
    }

    @Step("Выбрать отдел {dept}, подотдел {subDept}, раздел {classId}, подраздел {subClass}")
    public void choseDepartmentId(Integer dept, Integer subDept, Integer classId, Integer subClass) throws Exception{
        returnToDepartmentChoseWindow();
        try {
            if (dept != null) {
                selectElementFromArray(dept, firstLevelNomenclatureElementsList);
            }
            if (dept != null && subDept != null) {
                selectElementFromArray(subDept, secondLevelNomenclatureElementsList);
            }
            if (dept != null && subDept != null && classId != null) {
                selectElementFromArray(classId, secondLevelNomenclatureElementsList);
            }
            if (dept != null && subDept != null && classId != null && subClass != null) {
                selectElementFromArray(subClass, secondLevelNomenclatureElementsList);
            }
        }
        catch (NoSuchElementException e)
        {
            e.printStackTrace();
        }
    }

    private void selectElementFromArray(Integer value, ElementList<Element> someArray)throws Exception{
        int counter=0;
        for (Element element : someArray){
            String tmp = element.findChildElement(eachElementOfNomenclatureXpath).getText().replaceAll("^0+","");
            tmp = tmp.replaceAll("\\D+", "");
            if (tmp.length()>4){
                tmp=tmp.substring(0,4);
            }
            if (tmp.equals(String.valueOf(value))){
                element.findChildElement(eachElementOfNomenclatureXpath).click();
                counter++;
                break;
            }
        }
        if (counter<1){
            scrollDown();
            selectElementFromArray(value, thirdLevelNomenclatureElementsList);
        }
        counter=0;
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
