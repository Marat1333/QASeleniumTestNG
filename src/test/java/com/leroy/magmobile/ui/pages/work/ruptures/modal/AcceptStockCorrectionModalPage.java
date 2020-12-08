package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.work.transfer.TransferOrderStep1Page;
import io.qameta.allure.Step;

public class AcceptStockCorrectionModalPage extends CommonMagMobilePage {
    @AppFindBy(containsText = "Переход в \"Достоверность\"")
    Element header;

    @AppFindBy(containsText = "Для создания коррекции")
    Element description;

    @AppFindBy(text = "ПРОДОЛЖИТЬ")
    Button continueBtn;

    @AppFindBy(text = "ОТМЕНА")
    Button cancel;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        description.waitForVisibility();
    }

    @Step("Нажать 'Продолжить'")
    public void clickContinueButton(){
        continueBtn.click();
    }

    public AcceptStockCorrectionModalPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, description, continueBtn, cancel);
        softAssert.verifyAll();
        return this;
    }
}
