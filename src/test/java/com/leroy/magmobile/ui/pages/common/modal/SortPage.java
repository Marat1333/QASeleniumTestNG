package com.leroy.magmobile.ui.pages.common.modal;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import io.qameta.allure.Step;

import java.util.Arrays;

public class SortPage extends CommonMagMobilePage {
    public SortPage(TestContext context) {
        super(context);
    }

    private final String CHECK_BOX_XPATH = "//android.widget.TextView[contains(@text,'%s')]/following-sibling::android.view.ViewGroup";
    public final static String SORT_BY_LM_ASC = "По ЛМ-коду: 1→9";
    public final static String SORT_BY_LM_DESC = "По ЛМ-коду: 9→1";
    public final static String SORT_BY_AVAILABLE_STOCK_ASC = "По возрастанию запаса";
    public final static String SORT_BY_AVAILABLE_STOCK_DESC = "По убыванию запаса";

    @AppFindBy(xpath = "//*[contains(@text,'" + SORT_BY_LM_ASC + "')]")
    Element sortByLmAscLbl;

    @AppFindBy(xpath = "//*[contains(@text,'" + SORT_BY_LM_DESC + "')]")
    Element sortByLmDescLbl;

    @AppFindBy(text = SORT_BY_AVAILABLE_STOCK_ASC)
    Element sortByStockAscLbl;

    @AppFindBy(text = SORT_BY_AVAILABLE_STOCK_DESC)
    Element sortByStockDescLbl;

    @Override
    public void waitForPageIsLoaded() {
        sortByLmAscLbl.waitForVisibility();
    }

    @Step("Выбрать вид сортировки {value}")
    public SearchProductPage selectSort(String value) {
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
        softAssert.areElementsVisible(Arrays.asList(
                sortByLmAscLbl, sortByLmDescLbl, sortByStockDescLbl, sortByStockAscLbl));
        softAssert.verifyAll();
        return new SortPage(context);

    }
}

