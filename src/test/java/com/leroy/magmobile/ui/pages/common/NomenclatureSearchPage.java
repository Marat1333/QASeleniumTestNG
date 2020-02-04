package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import io.qameta.allure.Step;
import org.openqa.selenium.NoSuchElementException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

    private final String eachElementOfNomenclatureXpath = "./android.view.ViewGroup/android.widget.TextView";

    @Step("Вернитесь на окно выбора отдела")
    public NomenclatureSearchPage returnBackNTimes(int counter) throws Exception {
        String pageSource = getPageSource();
        for (int y = 0; y < counter; y++) {
            if (!nomenclatureBackBtn.isVisible()) {
                Log.error("Осуществлен переход во \"Все отделы\", перейти дальше невозможно");
                throw new NoSuchElementException("There is no back button");
            }
            nomenclatureBackBtn.click();
            if (!waitForContentHasChanged(pageSource, short_timeout)) {
                nomenclatureBackBtn.click();
                waitForContentHasChanged(pageSource, short_timeout);
            }
        }
        return new NomenclatureSearchPage(context);
    }

    @Step("Выбрать отдел {dept}, подотдел {subDept}, раздел {classId}, подраздел {subClass}")
    public NomenclatureSearchPage choseDepartmentId(String dept, String subDept, String classId, String subClass) throws Exception {
        if (dept != null) {
            selectElementFromArray(dept, firstLevelNomenclatureElementsList);
        }
        if (dept != null && subDept != null) {
            selectElementFromArray(subDept, secondLevelNomenclatureElementsList);
        }
        if (dept != null && subDept != null && classId != null) {
            String refactoredClassId = "";
            if (classId.length() == 4) {
                refactoredClassId = classId.replaceAll("^0", "");
            } else {
                throw new Exception("Wrong classId length");
            }
            selectElementFromArray(refactoredClassId, secondLevelNomenclatureElementsList);
        }
        if (dept != null && subDept != null && classId != null && subClass != null) {
            String refactoredSubClass = "";
            if (subClass.length() == 4) {
                refactoredSubClass = subClass.replaceAll("^0", "");
            } else {
                throw new Exception("Wrong subClassId length");
            }
            selectElementFromArray(refactoredSubClass, secondLevelNomenclatureElementsList);
        }
        return new NomenclatureSearchPage(context);
    }

    private NomenclatureSearchPage selectElementFromArray(String value, ElementList<Element> someArray) throws Exception {
        String pageSource = getPageSource();
        int counter = 0;
        for (Element element : someArray) {
            Element tmpEl = element.findChildElement(eachElementOfNomenclatureXpath);
            String tmp = tmpEl.getText();
            tmp = tmp.replaceAll("\\D+", "");
            if (tmp.length() > 4) {
                tmp = tmp.substring(0, 4);
            }
            if (tmp.equals(value)) {
                tmpEl.click();
                waitForContentHasChanged(pageSource, short_timeout);
                counter++;
                break;
            }
        }
        if (counter < 1) {
            scrollDown();
            selectElementFromArray(value, secondLevelNomenclatureElementsList);
        }
        return new NomenclatureSearchPage(context);
    }

    @Step("Нажмите 'Показать все товары'")
    public SearchProductPage clickShowAllProductsBtn() {
        scrollUpTo(showAllGoods);
        showAllGoods.click();
        SearchProductPage searchPage = new SearchProductPage(context);
        waitForProgressBarIsVisible();
        waitForProgressBarIsInvisible();
        return searchPage;
    }

    @Override
    public void waitForPageIsLoaded() {
        //showAllGoods.waitForVisibility();
        screenTitle.waitForVisibility();
    }

    // Verifications

    public NomenclatureSearchPage verifyRequiredElements() {
        softAssert.isElementVisible(screenTitle);
        softAssert.isElementVisible(showAllGoods);
        softAssert.verifyAll();
        return this;
    }

    @Step("отображено 15 отделов")
    public NomenclatureSearchPage shouldDepartmentsCountIs15() throws Exception {
        Set<String> uniqueElementsArray = new HashSet<>();
        for (Element element : firstLevelNomenclatureElementsList) {
            uniqueElementsArray.add(element.findChildElement(eachElementOfNomenclatureXpath).getText());
        }
        scrollDown();
        for (Element element : secondLevelNomenclatureElementsList) {
            if (!uniqueElementsArray.contains(element)) {
                uniqueElementsArray.add(element.findChildElement(eachElementOfNomenclatureXpath).getText());
            }
        }
        System.out.println(uniqueElementsArray.size());
        anAssert.isTrue(uniqueElementsArray.size() == 15, "Найдено некорректное кол-во отделов");
        return this;
    }


    @Step("Выбранная номенклатура соответствует критерию {text}")
    public NomenclatureSearchPage shouldTitleWithNomenclatureIs(String text) {
        String format = "%s - %s - %s - %s";
        String emptyText = "_ _ _";
        String expectedText = "";

        if (text.length() % 3 == 0 && text.length() != 15 && !text.isEmpty()) {
            if (text.contains("_"))
                expectedText = text;
            else if (text.length() == 3)
                expectedText = String.format(format, text, emptyText, emptyText, emptyText);
            else if (text.length() == 6)
                expectedText = String.format(format, text.substring(0, 3), text.substring(3, 6), emptyText, emptyText);
            else if (text.length() == 9)
                expectedText = String.format(
                        format, text.substring(0, 3), text.substring(3, 6), text.substring(6, 9), emptyText);
            else
                expectedText = String.format(
                        format, text.substring(0, 3), text.substring(3, 6), text.substring(6, 9), text.substring(9));
            anAssert.isElementTextEqual(screenTitle, expectedText);
            return this;
        } else if (text.isEmpty()) {
            expectedText = String.format(format, emptyText, emptyText, emptyText, emptyText);
            anAssert.isElementTextEqual(screenTitle, expectedText);
            return this;

        } else if (text.length() == 15 || text.length() == 11 || text.length() == 7) {
            if (text.length() == 7) {
                expectedText = String.format(format, text.substring(0, 3), text.substring(3, 7), emptyText, emptyText);
            } else if (text.length() == 11) {
                expectedText = String.format(format, text.substring(0, 3), text.substring(3, 7), text.substring(7, 11), emptyText);
            } else if (text.length() == 15) {
                expectedText = String.format(format, text.substring(0, 3), text.substring(3, 7), text.substring(7, 11), text.substring(11, 15));
            }
            anAssert.isElementTextEqual(screenTitle, expectedText);
            return this;

        } else {
            anAssert.isTrue(false,"Некорректная длинна номенклатуры");
            return this;
        }
    }

    @Step("Кнопка \"назад\" должна быть отображена - {shouldBeVisible}")
    public NomenclatureSearchPage verifyNomenclatureBackBtnVisibility(boolean shouldBeVisible) {
        if (shouldBeVisible)
            anAssert.isElementVisible(nomenclatureBackBtn);
        else
            anAssert.isElementNotVisible(nomenclatureBackBtn);
        return this;
    }
}
