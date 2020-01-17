package com.leroy.magmobile.ui.pages.common.modal;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobileScreen;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import io.qameta.allure.Step;

public class SortPage extends CommonMagMobileScreen {
    public SortPage(TestContext context) {
        super(context);
    }

    private final String CHECK_BOX_XPATH = "//android.widget.TextView[contains(@text,'%s')]/following-sibling::android.view.ViewGroup";
    public final static String SORT_BY_LM_ASC = "По ЛМ-коду: 1";
    public final static String SORT_BY_LM_DESC = "По ЛМ-коду: 9";
    public final static String SORT_BY_AVAILABLE_STOCK_ASC = "По возрастанию запаса";
    public final static String SORT_BY_AVAILABLE_STOCK_DESC = "По убыванию запаса";

    @AppFindBy(xpath = "//*[contains(@text,'" + SORT_BY_LM_ASC + "')]")
    Element sortByLmAsc;

    @AppFindBy(xpath = "//*[contains(@text,'" + SORT_BY_LM_DESC + "')]")
    Element sortByLmDesc;

    @Override
    public void waitForPageIsLoaded() {
        sortByLmAsc.waitForVisibility();
        sortByLmDesc.waitForVisibility();
    }

    @Step("Выбрать вид сортировки {value}")
    public SearchProductPage choseSort(String value) {
        Element neededElement;
        switch (value) {
            case SORT_BY_LM_ASC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_LM_ASC));
                neededElement.doubleClick();
                break;
            case SORT_BY_LM_DESC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_LM_DESC));
                neededElement.doubleClick();
                break;
            case SORT_BY_AVAILABLE_STOCK_ASC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_AVAILABLE_STOCK_ASC));
                neededElement.doubleClick();
                break;
            case SORT_BY_AVAILABLE_STOCK_DESC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_AVAILABLE_STOCK_DESC));
                neededElement.doubleClick();
                break;
        }
        return new SearchProductPage(context);
    }

    public SortPage verifyRequiredElements() {
        softAssert.isElementVisible(sortByLmAsc);
        softAssert.isElementVisible(sortByLmDesc);
        softAssert.verifyAll();
        return new SortPage(context);

    }
}

