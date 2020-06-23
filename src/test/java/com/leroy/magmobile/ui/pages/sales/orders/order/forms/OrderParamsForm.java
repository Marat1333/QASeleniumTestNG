package com.leroy.magmobile.ui.pages.sales.orders.order.forms;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import com.leroy.utils.RandomUtil;

import java.time.LocalDate;

public class OrderParamsForm extends BaseAppPage {

    @AppFindBy(xpath = AndroidScrollView.TYPICAL_XPATH, metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    // Выбери способ получения
    @AppFindBy(text = "Самовывоз")
    MagMobButton pickupBtn;

    @AppFindBy(text = "Доставка")
    MagMobButton deliveryBtn;

    @AppFindBy(accessibilityId = "date", metaName = "Поле 'Ближайшая дата получения'")
    EditBox pickupDateFld;

    @AppFindBy(accessibilityId = "place", metaName = "Поле 'Место получения'")
    EditBox pickupPlaceFld;

    @AppFindBy(accessibilityId = "homeDeliverDate", metaName = "Поле 'Ближайшая дата доставки'")
    EditBox deliveryDateFld;

    // Получатель
    @AppFindBy(accessibilityId = "fullname", metaName = "Поле 'Имя и Фамилия'")
    EditBox fullNameFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[descendant::android.widget.EditText[@content-desc='fullname']]/following-sibling::android.view.ViewGroup[@index='1']",
            metaName = "Иконка клиента для поиска")
    Element customerIconForSearchBtn;

    @AppFindBy(accessibilityId = "phone", metaName = "Поле 'Телефон'")
    EditBox phoneFld;

    @AppFindBy(accessibilityId = "email", metaName = "Поле 'Email'")
    EditBox emailFld;

    // Параметры документа
    @AppFindBy(accessibilityId = "pincode", metaName = "Поле 'PIN-код'")
    EditBox pinCodeFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.EditText[@content-desc='pincode']]/following-sibling::android.widget.TextView",
            metaName = "Подсказка/ошибка, указывающая на проблемы ввода PIN кода")
    Element pinErrorTooltip;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле 'Комментарий'")
    EditBox commentFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.EditText[@content-desc='comment']]/../following-sibling::android.view.ViewGroup",
            metaName = "Иконка рядом с полем Комментарий")
    Element commentIconBtn;

    public boolean waitUntilFormIsVisible() {
        return waitForAnyOneOfElementsIsVisible(pickupBtn, phoneFld, commentFld);
    }

    private boolean isPinErrorAnyTextVisible() {
        return E("//android.view.ViewGroup[android.widget.EditText[@content-desc='pincode']]/../following-sibling::android.view.ViewGroup[@index='2']")
                .isVisible();
    }

    // ------- Grab info --------- //

    public LocalDate getDeliveryDate() {
        mainScrollView.scrollToBeginning();
        String ps = getPageSource();
        boolean isPickup = !deliveryDateFld.isVisible(ps);
        String strDeliveryDate = isPickup ? pickupDateFld.getText(ps) : deliveryDateFld.getText(ps);
        return DateTimeUtil.strToLocalDate(strDeliveryDate, "dd MMM");
    }

    public OrderDetailsData getOrderDetailData() {
        mainScrollView.scrollToBeginning();
        String ps = getPageSource();
        OrderDetailsData orderDetailsData = new OrderDetailsData();
        boolean isPickup = !deliveryDateFld.isVisible(ps);
        orderDetailsData.setDeliveryType(isPickup ?
                SalesDocumentsConst.GiveAwayPoints.PICKUP : SalesDocumentsConst.GiveAwayPoints.DELIVERY);
        String deliveryDate = isPickup ? pickupDateFld.getText(ps) : deliveryDateFld.getText(ps);
        orderDetailsData.setDeliveryDate(DateTimeUtil.strToLocalDate(deliveryDate, "dd MMM"));

        MagCustomerData customerData = new MagCustomerData();
        if (!fullNameFld.isVisible(ps))
            mainScrollView.scrollDownToElement(fullNameFld);
        customerData.setName(fullNameFld.getText());
        mainScrollView.scrollDownToElement(phoneFld);
        customerData.setPhone(ParserUtil.standardPhoneFmt(phoneFld.getText()));
        mainScrollView.scrollDownToElement(emailFld);
        customerData.setEmail(emailFld.getText());

        orderDetailsData.setCustomer(customerData);
        mainScrollView.scrollDownToElement(pinCodeFld);
        orderDetailsData.setPinCode(pinCodeFld.getText());
        mainScrollView.scrollDownToElement(commentFld);
        orderDetailsData.setComment(commentFld.getText());
        return orderDetailsData;
    }

    // ACTION STEPS

    public OrderParamsForm selectDeliveryType(SalesDocumentsConst.GiveAwayPoints type) {
        if (!deliveryBtn.isVisible())
            mainScrollView.scrollToBeginning();
        if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(type)) {
            deliveryBtn.click();
            deliveryDateFld.waitForVisibility();
        } else {
            pickupBtn.click();
            pickupDateFld.waitForVisibility();
        }
        return this;
    }

    public SearchCustomerPage clickCustomerIconToSearch() {
        if (!customerIconForSearchBtn.isVisible()) {
            mainScrollView.scrollToBeginning();
            mainScrollView.scrollDownToElement(customerIconForSearchBtn);
        }
        customerIconForSearchBtn.click();
        return new SearchCustomerPage();
    }

    public void enterPhone(String value) {
        if (value.startsWith("7"))
            value = "7" + value;
        phoneFld.clearFillAndSubmit(value);
    }

    public OrderParamsForm enterPinCode(OrderDetailsData data, boolean tryToFindValidPin) {
        if (!pinCodeFld.isVisible())
            mainScrollView.scrollDownToElement(pinCodeFld);
        pinCodeFld.clearFillAndSubmit(data.getPinCode());
        int iTryCount = 3;
        while (isPinErrorAnyTextVisible() && tryToFindValidPin && iTryCount > 0) {
            Log.debug("Пробуем подобрать валидный PIN. Осталось попыток " + iTryCount);
            iTryCount--;
            data.setPinCode(RandomUtil.randomPinCode(
                    !SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(data.getDeliveryType())));
            pinCodeFld.clearFillAndSubmit(data.getPinCode());
        }
        if (tryToFindValidPin)
            anAssert.isFalse(isPinErrorAnyTextVisible(),
                    "Не удалось подобрать валидный PIN, по-прежнему отображается ошибка");
        return this;
    }

    public void enterComment(String value) {
        mainScrollView.scrollDownToElement(commentFld);
        commentFld.clearFillAndSubmit(value);
        commentIconBtn.click();
    }

    public void enterCustomer(MagCustomerData customerData) {
        if (!fullNameFld.isVisible()) {
            mainScrollView.scrollToBeginning();
            mainScrollView.scrollDownToElement(fullNameFld);
        }
        fullNameFld.clearFillAndSubmit(customerData.getName());
        mainScrollView.scrollDownToElement(phoneFld);
        enterPhone(customerData.getPhone());
        mainScrollView.scrollDownToElement(emailFld);
        emailFld.clearFillAndSubmit(customerData.getEmail());
    }

    public OrderParamsForm fillInFormFields(OrderDetailsData data) throws Exception {
        if (data.getCustomer() != null) {
            MagCustomerData customerData = data.getCustomer();
            enterCustomer(customerData);
        }
        enterPinCode(data, true);
        enterComment(data.getComment());
        return this;
    }

    // Verifications

    public OrderParamsForm shouldFormFieldsAre(OrderDetailsData data) {
        // Способ получения
        if (!deliveryDateFld.isVisible())
            mainScrollView.scrollToBeginning();
        if (data.getDeliveryDate() != null) {
            if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(data.getDeliveryType())) {
                softAssert.isEquals(DateTimeUtil.strToLocalDate(deliveryDateFld.getText(), "dd MMM"),
                        data.getDeliveryDate(), "Неверная ближайшая дата доставки");
            } else {
                softAssert.isEquals(DateTimeUtil.strToLocalDate(pickupDateFld.getText(), "dd MMM"),
                        data.getDeliveryDate(), "Неверная ближайшая дата получения");
            }
        } else if (data.getDeliveryType() != null) {
            if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(data.getDeliveryType())) {
                softAssert.isElementVisible(deliveryDateFld);
            } else {
                softAssert.isElementVisible(pickupDateFld);
            }
        }

        // Получатель
        if (data.getCustomer() != null || data.getOrgAccount() != null) {
            MagCustomerData expectedCustomer;
            if (data.getCustomer() != null)
                expectedCustomer = data.getCustomer();
            else if (data.getOrgAccount().getChargePerson() != null)
                expectedCustomer = data.getOrgAccount().getChargePerson();
            else {
                expectedCustomer = new MagCustomerData();
                expectedCustomer.setName(data.getOrgAccount().getOrgName());
                expectedCustomer.setPhone(data.getOrgAccount().getOrgPhone());
                expectedCustomer.setEmail("Email (необязательно)");
            }
            if (expectedCustomer.getName() != null) {
                if (!fullNameFld.isVisible())
                    mainScrollView.scrollDownToElement(fullNameFld);
                softAssert.isElementTextEqual(fullNameFld, expectedCustomer.getName());
            }
            if (expectedCustomer.getPhone() != null) {
                mainScrollView.scrollDownToElement(phoneFld);
                softAssert.isEquals(ParserUtil.standardPhoneFmt(phoneFld.getText()), expectedCustomer.getPhone(),
                        "Неверный телефон номера у получателя");
            }
            if (expectedCustomer.getEmail() != null) {
                mainScrollView.scrollDownToElement(emailFld);
                softAssert.isElementTextEqual(emailFld, expectedCustomer.getEmail());
            }
        }

        // Параметры документа
        if (data.getPinCode() != null) {
            mainScrollView.scrollDownToElement(pinCodeFld);
            softAssert.isElementTextEqual(pinCodeFld, data.getPinCode());
        }
        if (data.getComment() != null) {
            mainScrollView.scrollDownToElement(commentFld);
            softAssert.isElementTextEqual(commentFld, data.getComment());
        }
        softAssert.verifyAll();
        return this;
    }

    public OrderParamsForm shouldErrorPinAlreadyExistVisible() {
        anAssert.isEquals(pinErrorTooltip.getText(), "Уже используется, введи другой код",
                "Сообщение о том, что пин код уже используется, не отображается");
        return this;
    }

    public OrderParamsForm shouldErrorPinVisibleShouldNotStartWith9() {
        anAssert.isEquals(pinErrorTooltip.getText(), "Не должен начинаться с 9",
                "Должно быть видно сообщение о некорректном вводе PIN кода");
        return this;
    }

    public OrderParamsForm shouldErrorPinVisibleShouldStartWith9() {
        anAssert.isEquals(pinErrorTooltip.getText(), "Должен начинаться с 9",
                "Должно быть видно сообщение о некорректном вводе PIN кода");
        return this;
    }
}
