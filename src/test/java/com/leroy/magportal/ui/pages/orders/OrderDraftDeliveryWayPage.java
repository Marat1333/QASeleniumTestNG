package com.leroy.magportal.ui.pages.orders;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Form;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.TextArea;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.pages.orders.modal.SubmittedOrderModal;
import com.leroy.utils.ParserUtil;
import com.leroy.utils.RandomUtil;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.experimental.Accessors;

public class OrderDraftDeliveryWayPage extends OrderDraftPage {

    @Data
    @Accessors(chain = true)
    public static class PageState {
        boolean clientAdded;
    }

    @WebFindBy(xpath = "//button[descendant::span[text()='Самовывоз']]", metaName = "Кнопка-опция 'Самовывоз'")
    Button pickupBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='Доставка']]", metaName = "Кнопка-опция 'Доставка'")
    Button deliveryBtn;

    @WebFindBy(xpath = "//div[label[text()='Ближайшая дата получения']]//input", metaName = "Поле 'Ближайшая дата получения'")
    EditBox deliveryDateFld;

    @WebFindBy(xpath = "//div[label[text()='Место получения']]//input", metaName = "Поле 'Ближайшая дата получения'")
    EditBox pickupPlaceFld;

    @Form
    CustomerSearchForm customerSearchForm;

    @WebFindBy(xpath = "//input[@name='NAME_SURNAME']", metaName = "Поле 'Имя и фамилия'")
    EditBox nameSurnameFld;

    @WebFindBy(xpath = "//input[@name='PHONE']", metaName = "Поле 'Телефон'")
    EditBox phoneFld;

    @WebFindBy(xpath = "//input[@name='EMAIL']", metaName = "Поле 'email'")
    EditBox emailFld;

    @WebFindBy(xpath = "//input[@name='PIN_CODE']", metaName = "Поле PIN код")
    EditBox pinCodeFld;

    @WebFindBy(xpath = "//div[label[text()='PIN-код для оплаты']]//div[contains(@class, 'Bordered__tooltipContainer')]",
            metaName = "Ошибка-подсказка у поля PIN код")
    Element pinCodeErrorTooltip;

    @WebFindBy(id = "textAreaId", metaName = "Поле 'Комментарий'")
    TextArea commentFld;

    @WebFindBy(xpath = "//button[descendant::span[text()='Подтвердить заказ']]", metaName = "Кнопка 'Подтвердить заказ'")
    Element confirmOrderBtn;

    public CustomerSearchForm getCustomerSearchForm() {
        return customerSearchForm;
    }

    @Override
    public void waitForPageIsLoaded() {
        pickupBtn.waitForVisibility();
    }

    // Actions

    @Step("Ввести PIN код")
    public OrderDraftDeliveryWayPage enterPinCode(SalesDocWebData orderData, boolean tryToFindValidPin) {
        pinCodeFld.scrollTo();
        pinCodeFld.click();
        pinCodeFld.clear(true);
        pinCodeFld.fill(orderData.getPinCode());
        waitForSpinnerAppearAndDisappear();
        int iTryCount = 3;
        while (pinCodeErrorTooltip.isVisible() && tryToFindValidPin && iTryCount > 0) {
            Log.debug("Пробуем подобрать валидный PIN. Осталось попыток " + iTryCount);
            iTryCount--;
            orderData.setPinCode(RandomUtil.randomPinCode(
                    !SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(orderData.getDeliveryType())));
            pinCodeFld.clearFillAndSubmit(orderData.getPinCode());
        }
        if (tryToFindValidPin)
            anAssert.isFalse(pinCodeErrorTooltip.isVisible(),
                    "Не удалось подобрать валидный PIN, по-прежнему отображается ошибка");
        return this;
    }

    public OrderDraftDeliveryWayPage enterPinCode(SalesDocWebData orderData) {
        return enterPinCode(orderData, true);
    }

    public OrderDraftDeliveryWayPage enterPinCode(String pinCode) {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setPinCode(pinCode);
        return enterPinCode(salesDocWebData, false);
    }

    @Step("Нажать на кнопку 'Подтвердить заказ'")
    public SubmittedOrderModal clickConfirmOrderButton() {
        confirmOrderBtn.click();
        return new SubmittedOrderModal();
    }

    @Step("Нажать на кнопку 'Подтвердить заказ' - негативный сценарий")
    public OrderDraftDeliveryWayPage clickConfirmOrderButtonNegativePath() {
        confirmOrderBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    // Verifications

    @Step("Проверить, что страница 'Оформление заказа' отображается корректно")
    public OrderDraftDeliveryWayPage verifyRequiredElements(PageState pageState) {
        softAssert.areElementsVisible(pickupBtn, deliveryBtn, nameSurnameFld, phoneFld, emailFld,
                pinCodeFld, commentFld, confirmOrderBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поле с пин кодом = {text}")
    public OrderDraftDeliveryWayPage shouldPinCodeFieldIs(String text) {
        anAssert.isElementTextEqual(pinCodeFld, text);
        return this;
    }

    @Step("Проверить, что ошибка под полем пин код содержит текст = {text}")
    public OrderDraftDeliveryWayPage shouldPinCodeErrorTooltipIs(String text) {
        anAssert.isElementTextEqual(pinCodeErrorTooltip, text);
        return this;
    }

    @Step("Проверить, что поля 'Получатель' заполнены соответствующими данными")
    public OrderDraftDeliveryWayPage shouldReceiverIs(SimpleCustomerData customerData) {
        softAssert.isElementTextEqual(nameSurnameFld, customerData.getName());
        softAssert.isEquals(ParserUtil.standardPhoneFmt(phoneFld.getText()), customerData.getPhoneNumber(),
                "Неверный номер телефона у Получателя");
        softAssert.isEquals(emailFld.getText(), customerData.getEmail(), "Неверный email у Получателя");
        softAssert.verifyAll();
        return this;
    }

}
