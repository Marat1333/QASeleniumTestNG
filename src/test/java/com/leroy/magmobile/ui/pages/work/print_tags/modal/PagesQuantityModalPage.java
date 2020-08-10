package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.TagsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.enums.Format;
import io.qameta.allure.Step;

public class PagesQuantityModalPage extends CommonMagMobilePage {
    @AppFindBy(containsText = "в принтер вставлена бумага")
    Element hintText;

    @AppFindBy(xpath = "//*[contains(@text,'Формат')]/following-sibling::android.widget.TextView[1]")
    Element currentFormatLbl;

    @AppFindBy(xpath = "//*[contains(@text,'Кол-во листов')]/following-sibling::android.widget.TextView[1]")
    Element currentPagesQuantityLbl;

    @AppFindBy(text = "ОТМЕНА")
    Button cancelPrintBtn;

    @AppFindBy(text = "ПРОДОЛЖИТЬ")
    Button continuePrintBtn;

    @Override
    protected void waitForPageIsLoaded() {
        hintText.waitForVisibility();
        currentFormatLbl.waitForVisibility();
        currentPagesQuantityLbl.waitForVisibility();
    }

    @Step("Продолжить печать")
    public void continuePrinting() {
        continuePrintBtn.click();
        continuePrintBtn.waitForInvisibility();
    }

    @Step("Отменить печать")
    public TagsListPage cancelPrinting() {
        cancelPrintBtn.click();
        return new TagsListPage();
    }

    @Step("Проверить кол-во страниц и формат")
    public PagesQuantityModalPage shouldPagesQuantityAndFormatAreCorrect(Format format, int quantity) {
        switch (format) {
            case SMALL:
                softAssert.isElementTextEqual(currentFormatLbl, "4 × 6 см");
                break;
            case MIDDLE:
                softAssert.isElementTextEqual(currentFormatLbl, "6 × 10 см");
                break;
            case BIG:
                softAssert.isElementTextEqual(currentFormatLbl, "10 × 18 см");
                break;
            case ALL:
                break;
        }
        softAssert.isElementTextEqual(currentPagesQuantityLbl, quantity + " шт.");
        softAssert.verifyAll();
        return this;
    }
}
