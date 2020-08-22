package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public abstract class TransferOrderPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "//android.widget.TextView[starts-with(@text, 'Заявка №')]")
    private Element orderNumberObj;

    @Override
    protected void waitForPageIsLoaded() {
        orderNumberObj.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    protected boolean isOrderNumberVisibleAndValid() {
        String number = getOrderNumber();
        return number.matches("IP\\.\\d{7}");
    }

    // Grab Data
    public String getOrderNumber() {
        return ParserUtil.strWithOnlyDigits(orderNumberObj.getText());
    }

    // Verifications

    @Step("Проверить, что номер заявки = {number}")
    public void shouldTaskNumberIs(String expectedNumber) {
        if (expectedNumber.length() > 7)
            expectedNumber = expectedNumber.substring(expectedNumber.length() - 7);
        anAssert.isEquals(getOrderNumber(), expectedNumber, "Ожидался другой номер заявки");
    }

}
