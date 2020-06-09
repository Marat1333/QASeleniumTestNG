package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocument35Page;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import com.leroy.utils.RandomUtil;
import io.qameta.allure.Step;

public class ProcessOrder35Page extends CommonMagMobilePage {

    @AppFindBy(text = "Оформление заказа")
    Element headerLbl;

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    Element backBtn;

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

    // Ваш заказ

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(followingTextAfter = "Итого: ")
    Element totalPriceVal;

    @AppFindBy(text = "ПОДТВЕРДИТЬ ЗАКАЗ")
    MagMobGreenSubmitButton submitButton;

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isTrue(waitForAnyOneOfElementsIsVisible(headerLbl, phoneFld, submitButton),
                "Страница оформления заказа не загрузилась");
    }

    // ACTION STEPS

    @Step("Выбираем способ получения {type}")
    public ProcessOrder35Page selectDeliveryType(OrderDetailsData.DeliveryType type) {
        if (OrderDetailsData.DeliveryType.DELIVERY.equals(type))
            deliveryBtn.click();
        else
            pickupBtn.click();
        return this;
    }

    @Step("Нажмите на иконку клиента (для перехода к экрану поиска клиента)")
    public SearchCustomerPage clickCustomerIconToSearch() {
        if (!customerIconForSearchBtn.isVisible())
            mainScrollView.scrollDownToElement(customerIconForSearchBtn);
        customerIconForSearchBtn.click();
        return new SearchCustomerPage();
    }

    @Step("Нажмите на кнопку Подтвердить заказ")
    public SubmittedSalesDocument35Page clickSubmitButton() throws Exception {
        mainScrollView.scrollDownToElement(submitButton);
        submitButton.click();
        return new SubmittedSalesDocument35Page();
    }

    private void enterPhone(String value) {
        if (value.startsWith("7"))
            value = "7" + value;
        phoneFld.clearFillAndSubmit(value);
    }

    @Step("Ввести PIN код")
    public ProcessOrder35Page enterPinCode(OrderDetailsData data, boolean tryToFindValidPin) {
        if (!pinCodeFld.isVisible())
            mainScrollView.scrollDownToElement(pinCodeFld);
        pinCodeFld.clearFillAndSubmit(data.getPinCode());
        int iTryCount = 3;
        while (pinErrorTooltip.isVisible() && tryToFindValidPin && iTryCount > 0) {
            Log.debug("Пробуем подобрать валидный PIN. Осталось попыток " + iTryCount);
            iTryCount--;
            data.setPinCode(RandomUtil.randomPinCode(
                    !OrderDetailsData.DeliveryType.DELIVERY.equals(data.getDeliveryType())));
            pinCodeFld.clearFillAndSubmit(data.getPinCode());
        }
        if (tryToFindValidPin)
            anAssert.isFalse(pinErrorTooltip.isVisible(),
                    "Не удалось подобрать валидный PIN, по-прежнему отображается ошибка");
        return this;
    }

    @Step("Заполнить поля формы 'Оформление заказа'")
    public ProcessOrder35Page fillInFormFields(OrderDetailsData data) throws Exception {
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

    // VERIFICATIONS
    @Step("Проверить, что страница 'Оформление заказа' отображается корректно")
    public ProcessOrder35Page verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, backBtn, pickupBtn, deliveryBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поля формы заполнены соответствующим образом")
    public ProcessOrder35Page shouldFormFieldsAre(OrderDetailsData data) {
        // Способ получения
        if (data.getDeliveryDate() != null) {
            if (!deliveryDateFld.isVisible())
                mainScrollView.scrollToBeginning();
            if (OrderDetailsData.DeliveryType.DELIVERY.equals(data.getDeliveryType())) {
                softAssert.isEquals(DateTimeUtil.strToLocalDate(deliveryDateFld.getText(), "dd MMM"),
                        data.getDeliveryDate(), "Неверная ближайшая дата доставки");
            } else {
                softAssert.isEquals(DateTimeUtil.strToLocalDate(pickupDateFld.getText(), "dd MMM"),
                        data.getDeliveryDate(), "Неверная ближайшая дата получения");
            }
        }
        // Получатель
        if (data.getCustomer() != null) {
            if (data.getCustomer().getName() != null) {
                if (!fullNameFld.isVisible())
                    mainScrollView.scrollDownToElement(fullNameFld);
                softAssert.isElementTextEqual(fullNameFld, data.getCustomer().getName());
            }
            if (data.getCustomer().getPhone() != null) {
                mainScrollView.scrollDownToElement(phoneFld);
                softAssert.isEquals(ParserUtil.standardPhoneFmt(phoneFld.getText()), data.getCustomer().getPhone(),
                        "Неверный телефон номера у получателя");
            }
            if (data.getCustomer().getEmail() != null) {
                mainScrollView.scrollDownToElement(emailFld);
                softAssert.isElementTextEqual(emailFld, data.getCustomer().getEmail());
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
}
