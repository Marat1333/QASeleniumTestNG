package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.time.LocalDateTime;

public abstract class TransferOrderPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackCloseMaster", metaName = "Кнопка назад")
    protected Element backBtn;

    @AppFindBy(xpath = "//android.widget.TextView[starts-with(@text, 'Заявка №')]/preceding-sibling::android.widget.TextView[1]")
    private Element creationDateTime;

    @AppFindBy(xpath = "//android.widget.TextView[starts-with(@text, 'Заявка №')]")
    private Element orderNumberObj;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='BackCloseMaster']//following::android.view.ViewGroup[@content-desc='Button']",
            metaName = "Кнопка удалить (мусорка)")
    Element trashBtn;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Заявка')]/following-sibling::android.view.ViewGroup[2]/android.view.ViewGroup[@index='0']",
            metaName = "Иконка Шаг 1")
    Element step1Icon;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Заявка')]/following-sibling::android.view.ViewGroup[2]/android.view.ViewGroup[@index='3']",
            metaName = "Иконка Шаг 2")
    Element step2Icon;

    @Override
    protected void waitForPageIsLoaded() {
        orderNumberObj.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    protected boolean isOrderNumberVisibleAndValid() {
        String number = getOrderNumber();
        return number.matches("\\d{7}");
    }

    // Grab Data
    public String getOrderNumber() {
        return ParserUtil.strWithOnlyDigits(orderNumberObj.getText());
    }

    public LocalDateTime getCreationDateTime() {
        return DateTimeUtil.strToLocalDateTime(creationDateTime.getText(), DateTimeUtil.DD_MMM_HH_MM);
    }

    // Action

    @Step("Нажать на иконку шага 2")
    public void clickStep2Icon() {
        step2Icon.click();
    }

    @Step("Удалить заявку на отзыв и подтвердить")
    public TransferRequestsPage removeTransferTask() {
        trashBtn.click();
        new ConfirmRemovingProductModal()
                .clickConfirmButton();
        trashBtn.waitForInvisibility();
        return new TransferRequestsPage();
    }

    // Verifications

    @Step("Проверить, что номер заявки = {expectedNumber}")
    public void shouldTaskNumberIs(String expectedNumber) {
        if (expectedNumber.length() > 7)
            expectedNumber = expectedNumber.substring(expectedNumber.length() - 7);
        anAssert.isEquals(getOrderNumber(), expectedNumber, "Ожидался другой номер заявки");
    }

    // ---------- Widgets -------------------

    protected static class SelectedClientWidget extends CardWidget<MagCustomerData> {

        public SelectedClientWidget(WebDriver driver, CustomLocator locator) {
            super(driver, locator);
        }

        @AppFindBy(xpath = ".//android.widget.TextView[1]", metaName = "Имя клиента")
        Element name;

        @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, '+')]", metaName = "Номер телефона клиента")
        Element phone;

        @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, '@')]", metaName = "Email клиента")
        Element email;

        @Override
        public MagCustomerData collectDataFromPage(String pageSource) {
            MagCustomerData customerData = new MagCustomerData();
            customerData.setName(name.getText());
            customerData.setPhone(ParserUtil.standardPhoneFmt(phone.getTextIfPresent()));
            customerData.setEmail(email.getTextIfPresent());
            return customerData;
        }

        @Override
        public boolean isFullyVisible(String pageSource) {
            return false;
        }
    }

}
