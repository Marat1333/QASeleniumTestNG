package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.TagsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.enums.Format;
import io.qameta.allure.Step;

public class SessionFormatsModalPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModal;

    @AppFindBy(containsText = "Форматы ценников")
    Element header;

    @AppFindBy(containsText = "4 × 6 см")
    Button smallSizePrintBtn;

    @AppFindBy(containsText = "6 × 10 см")
    Button middleSizePrintBtn;

    @AppFindBy(containsText = "10 × 18 см")
    Button bigSizePrintBtn;

    @AppFindBy(containsText = "все ценники")
    Button allSizesPrintBtn;

    @Override
    protected void waitForPageIsLoaded() {
        allSizesPrintBtn.waitForVisibility();
        closeModal.waitForVisibility();
        header.waitForVisibility();
    }

    @Step("Закрыть модалку")
    public TagsListPage closeModal() {
        closeModal.click();
        return new TagsListPage();
    }

    @Step("Распечатать ценники")
    public PagesQuantityModalPage printFormat(Format format) {
        switch (format) {
            case SMALL:
                smallSizePrintBtn.click();
                break;
            case MIDDLE:
                middleSizePrintBtn.click();
                break;
            case BIG:
                bigSizePrintBtn.click();
                break;
            case ALL:
                allSizesPrintBtn.click();
                break;
        }
        return new PagesQuantityModalPage();
    }

    @Step("Проверить, что отображены кнопки с форматами")
    public SessionFormatsModalPage shouldFormatBtnAreVisible(Format... formats) {
        for (Format eachFormat : formats) {
            switch (eachFormat) {
                case SMALL:
                    softAssert.isElementVisible(smallSizePrintBtn);
                    break;
                case MIDDLE:
                    softAssert.isElementVisible(middleSizePrintBtn);
                    break;
                case BIG:
                    softAssert.isElementVisible(bigSizePrintBtn);
                    break;
                case ALL:
                    break;
            }
        }
        softAssert.isElementVisible(allSizesPrintBtn);
        softAssert.verifyAll();
        return this;
    }
}
