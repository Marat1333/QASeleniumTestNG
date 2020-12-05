package com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class StockCorrectionAddProductWebPage extends BaseWebPage {

    @WebFindBy(xpath = "//div[text()='Числится в зале']/following-sibling::div")
    EditBox currentCountInput;

    @WebFindBy(xpath = "//div[text()='Я посчитал']/following-sibling::form/div/input")
    EditBox newCountInput;

    @WebFindBy(containsText = "в карточку")
    Element inCardBtn;

    @Override
    protected void waitForPageIsLoaded() {
        currentCountInput.waitForVisibility();
    }

    @Step("Проверить что добавлен корректный LmCode")
    public void checkLmCode(String lmCode) {
        anAssert.isElementVisible(E("//*[contains(text(), '" + lmCode + "')]", "lmCode"));
    }

    @Step("Ввести в поле \"я посчитал\" значение \"числится в зале\" +1 или 1 если \"числится в зале\" меньше нуля")
    public void enterNewCount() {
        int oldCount = ParserUtil.strToInt(currentCountInput.getText());
        int newCount = oldCount >= 0 ? oldCount + 1 : 1;
        newCountInput.fill(newCount);
    }

    @Step("Нажать \"+ в карточку\"")
    public StockCorrectionCardWebPage clickInCardBtn() {
        inCardBtn.clickJS();
        return new StockCorrectionCardWebPage();
    }
}