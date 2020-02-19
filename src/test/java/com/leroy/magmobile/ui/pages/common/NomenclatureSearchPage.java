package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import com.leroy.models.TextViewData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;

public class NomenclatureSearchPage extends BaseAppPage {
    public NomenclatureSearchPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Номер выбранной номенклатуры")
    Element screenTitle;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[following-sibling::android.widget.TextView]/android.view.ViewGroup")
    Element nomenclatureBackBtn;

    @AppFindBy(text = "ПОКАЗАТЬ ВСЕ ТОВАРЫ")
    Element showAllGoods;

    AndroidScrollView<TextViewData> nomenclatureElementsList = new AndroidScrollView<>(driver, new CustomLocator(By.xpath("//android.widget.ScrollView/android.view.ViewGroup")), ".//android.widget.TextView", TextViewWidget.class);

    private AndroidScrollView<TextViewData> scrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR);

    @Override
    public void waitForPageIsLoaded() {
        showAllGoods.waitForVisibility();
    }

    @Step("Вернитесь на окно выбора отдела")
    public NomenclatureSearchPage returnBackNTimes(int counter) {
        String screenTitleText;
        for (int y = 0; y < counter; y++) {
            if (!nomenclatureBackBtn.isVisible()) {
                Log.error("Осуществлен переход во \"Все отделы\", перейти дальше невозможно");
                throw new NoSuchElementException("There is no back button");
            }
            screenTitleText = screenTitle.getText();
            nomenclatureBackBtn.click();
            screenTitle.waitForTextIsNotEqual(screenTitleText);
        }
        return new NomenclatureSearchPage(context);
    }

    @Step("Выбрать отдел {dept}, подотдел {subDept}, раздел {classId}, подраздел {subClass}")
    public NomenclatureSearchPage choseDepartmentId(String dept, String subDept, String classId, String subClass) throws Exception {
        if (dept != null) {
            selectElementFromArray(dept);
        }
        if (dept != null && subDept != null) {
            selectElementFromArray(subDept);
        }
        if (dept != null && subDept != null && classId != null) {
            String refactoredClassId;
            if (classId.length() == 4) {
                refactoredClassId = classId.replaceAll("^0", "");
            } else {
                throw new Exception("Wrong classId length");
            }
            selectElementFromArray(refactoredClassId);
        }
        if (dept != null && subDept != null && classId != null && subClass != null) {
            String refactoredSubClass;
            if (subClass.length() == 4) {
                refactoredSubClass = subClass.replaceAll("^0", "");
            } else {
                throw new Exception("Wrong subClassId length");
            }
            selectElementFromArray(refactoredSubClass);
        }
        return this;
    }

    private NomenclatureSearchPage selectElementFromArray(String value) throws Exception {
        int counter = 0;
        String pageSource;
        Element element;
        element = scrollView.findChildElement("//*[contains(@text, '" + value + "')]");
        if (!element.isVisible()) {
            scrollView.scrollDownToElement(element);
        }
        pageSource = getPageSource();
        element.click();
        if (waitUntilContentIsChanged(pageSource, short_timeout)) {
            counter++;
        }
        if (counter < 1) {
            throw new IllegalArgumentException("There is no " + value + " nomenclature element");
        }
        return new NomenclatureSearchPage(context);
    }

    @Step("Нажмите 'Показать все товары'")
    public SearchProductPage clickShowAllProductsBtn() {
        scrollView.scrollToBeginning();
        String pageSource = getPageSource();
        showAllGoods.click();
        waitUntilContentIsChanged(pageSource, short_timeout);
        return new SearchProductPage(context);
    }

    // Verifications

    public NomenclatureSearchPage verifyRequiredElements() {
        softAssert.isElementVisible(screenTitle);
        softAssert.isElementVisible(showAllGoods);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отображено 15 отделов")
    public NomenclatureSearchPage shouldDepartmentsCountIs15() {
        List<TextViewData> uniqueElementsArray = nomenclatureElementsList.getFullDataList();
        List<String> result = new ArrayList<>();
        String tmp;
        for (TextViewData data : uniqueElementsArray) {
            tmp = data.toString();
            tmp = tmp.substring(0, 2);
            if (tmp.matches("\\d+")) {
                result.add(data.toString());
            }
        }
        anAssert.isEquals(result.size(), 15,
                "Найдено некорректное кол-во отделов");
        return this;
    }


    @Step("Выбранная номенклатура соответствует критерию {text}")
    public NomenclatureSearchPage shouldTitleWithNomenclatureIs(String text) {
        String format = "%s - %s - %s - %s";
        String emptyText = "_ _ _";
        String expectedText;

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
            } else {
                expectedText = String.format(format, text.substring(0, 3), text.substring(3, 7), text.substring(7, 11), text.substring(11, 15));
            }
            anAssert.isElementTextEqual(screenTitle, expectedText);
            return this;

        } else {
            anAssert.isTrue(false, "Некорректная длинна номенклатуры");
            return this;
        }
    }

    @Step("Проверить, что Кнопка \"назад\" должна быть отображена - {shouldBeVisible}")
    public NomenclatureSearchPage verifyNomenclatureBackBtnVisibility(boolean shouldBeVisible) {
        if (shouldBeVisible)
            anAssert.isElementVisible(nomenclatureBackBtn);
        else
            anAssert.isElementNotVisible(nomenclatureBackBtn);
        return this;
    }
}
