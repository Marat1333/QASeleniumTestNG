package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class SuccessPrintingPage extends CommonMagMobilePage {
    @AppFindBy(text = "Ценники отправлены на печать")
    Element header;

    @AppFindBy(text = "ЗАКРЫТЬ")
    Button closeBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        closeBtn.waitForVisibility();
    }

    @Step("Закрыть страницу")
    public void closePage() {
        closeBtn.click();
        closeBtn.waitForInvisibility();
    }

    @Step("Проверить, что отображается имя принтера")
    public SuccessPrintingPage shouldPrinterNameIsVisible(String printerName) {
        anAssert.isElementVisible(E(String.format("contains(%s)", printerName)));
        return this;
    }

    public void verifyRequiredElements(){
        softAssert.areElementsVisible(header, closeBtn);
        softAssert.verifyAll();
    }
}
