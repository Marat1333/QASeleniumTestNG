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

    @AppFindBy(text = "Уже используется, введи другой код", metaName = "Подсказка о том, что данный PIN уже используется")
    Element pinErrorTooltip;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле 'Комментарий'")
    EditBox commentFld;

    public boolean waitUntilFormIsVisible() {
        return waitForAnyOneOfElementsIsVisible(pickupBtn, phoneFld, commentFld);
    }

    // ------- Grab info --------- //

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
        if (SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(type))
            deliveryBtn.click();
        else
            pickupBtn.click();
        return this;
    }

    public SearchCustomerPage clickCustomerIconToSearch() {
        if (!customerIconForSearchBtn.isVisible())
            mainScrollView.scrollDownToElement(customerIconForSearchBtn);
        customerIconForSearchBtn.click();
        return new SearchCustomerPage();
    }

    private void enterPhone(String value) {
        if (value.startsWith("7"))
            value = "7" + value;
        phoneFld.clearFillAndSubmit(value);
    }

    public OrderParamsForm enterPinCode(OrderDetailsData data, boolean tryToFindValidPin) {
        if (!pinCodeFld.isVisible())
            mainScrollView.scrollDownToElement(pinCodeFld);
        pinCodeFld.clearFillAndSubmit(data.getPinCode());
        int iTryCount = 3;
        while (pinErrorTooltip.isVisible() && tryToFindValidPin && iTryCount > 0) {
            Log.debug("Пробуем подобрать валидный PIN. Осталось попыток " + iTryCount);
            iTryCount--;
            data.setPinCode(RandomUtil.randomPinCode(
                    !SalesDocumentsConst.GiveAwayPoints.DELIVERY.equals(data.getDeliveryType())));
            pinCodeFld.clearFillAndSubmit(data.getPinCode());
        }
        if (tryToFindValidPin)
            anAssert.isFalse(pinErrorTooltip.isVisible(),
                    "Не удалось подобрать валидный PIN, по-прежнему отображается ошибка");
        return this;
    }

    public OrderParamsForm fillInFormFields(OrderDetailsData data) throws Exception {
        if (data.getCustomer() != null) {
            MagCustomerData customerData = data.getCustomer();
            fullNameFld.clearFillAndSubmit(customerData.getName());
            mainScrollView.scrollDownToElement(phoneFld);
            enterPhone(customerData.getPhone());
            mainScrollView.scrollDownToElement(emailFld);
            emailFld.clearFillAndSubmit(customerData.getEmail());
        }
        enterPinCode(data, true);
        mainScrollView.scrollDownToElement(commentFld);
        commentFld.clearFillAndSubmit(data.getComment());
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
        }
        // Получатель
        if (data.getCustomer() != null || data.getOrgAccount() != null) {
            MagCustomerData expectedCustomer = data.getCustomer() != null ? data.getCustomer() :
                    data.getOrgAccount().getChargePerson();
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

    public OrderParamsForm shouldErrorPinCodeTooltipVisible() {
        anAssert.isTrue(pinErrorTooltip.isVisible(),
                "Сообщение о том, что пин код уже используется, не отображается");
        return this;
    }
}
