package com.leroy.magmobile.ui.pages.search.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobRadioButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SortPage extends CommonMagMobilePage {

    private final String CHECK_BOX_XPATH = "//android.widget.TextView[contains(@text,'%s')]/following-sibling::android.view.ViewGroup";
    public final static String SORT_BY_LM_ASC = "По ЛМ-коду: 1→9";
    public final static String SORT_BY_LM_DESC = "По ЛМ-коду: 9→1";
    public final static String SORT_BY_ALPHABET_ASC = "По названию: А→Я";
    public final static String SORT_BY_ALPHABET_DESC = "По названию: Я→А";
    public final static String DEFAULT_SORT = "По умолчанию";

    @AppFindBy(xpath = "//*[contains(@text,'" + SORT_BY_LM_ASC + "')]")
    Element sortByLmAscLbl;

    @AppFindBy(xpath = "//*[contains(@text,'" + SORT_BY_LM_DESC + "')]")
    Element sortByLmDescLbl;

    @AppFindBy(text = SORT_BY_ALPHABET_ASC)
    Element sortByAlphabetAscLbl;

    @AppFindBy(text = SORT_BY_ALPHABET_DESC)
    Element sortByAlphabetDescLbl;

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
                neededElement.click();
                break;
            case SORT_BY_LM_DESC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_LM_DESC));
                neededElement.click();
                break;
            case SORT_BY_ALPHABET_ASC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_ALPHABET_ASC));
                neededElement.click();
                break;
            case SORT_BY_ALPHABET_DESC:
                neededElement = E(String.format(CHECK_BOX_XPATH, SORT_BY_ALPHABET_DESC));
                neededElement.click();
                break;
        }
        return new SearchProductPage();
    }

    public SortPage verifyRequiredElements() {
        softAssert.areElementsVisible(sortByLmAscLbl, sortByLmDescLbl,
                sortByAlphabetDescLbl, sortByAlphabetAscLbl);
        softAssert.verifyAll();
        return new SortPage();

    }

    @Step("Проверить, что выбран тип сортировки {sortType}")
    public SortPage shouldSortIsChosen(String sortType) throws Exception {
        MagMobRadioButton neededElement;
        switch (sortType) {
            case SORT_BY_LM_ASC:
                neededElement = new MagMobRadioButton(driver, new CustomLocator(By.xpath(String.format(CHECK_BOX_XPATH, SORT_BY_LM_ASC))));
                break;
            case SORT_BY_LM_DESC:
                neededElement = new MagMobRadioButton(driver, new CustomLocator(By.xpath(String.format(CHECK_BOX_XPATH, SORT_BY_LM_DESC))));
                break;
            case SORT_BY_ALPHABET_ASC:
                neededElement = new MagMobRadioButton(driver, new CustomLocator(By.xpath(String.format(CHECK_BOX_XPATH, SORT_BY_ALPHABET_ASC))));
                break;
            case SORT_BY_ALPHABET_DESC:
                neededElement = new MagMobRadioButton(driver, new CustomLocator(By.xpath(String.format(CHECK_BOX_XPATH, SORT_BY_ALPHABET_DESC))));
                break;
            case DEFAULT_SORT:
                neededElement = new MagMobRadioButton(driver, new CustomLocator(By.xpath(String.format(CHECK_BOX_XPATH, DEFAULT_SORT))));
                break;
            default:
                throw new IllegalArgumentException("Not existed sort type");
        }
        anAssert.isTrue(neededElement.isChecked(), "Элемент должен быть выбран");
        return this;
    }
}

