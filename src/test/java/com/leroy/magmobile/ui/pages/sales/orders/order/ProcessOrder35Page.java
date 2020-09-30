package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.Form;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocument35Page;
import com.leroy.magmobile.ui.pages.sales.orders.order.forms.OrderParamsForm;
import io.qameta.allure.Step;

/**
 * Оформление заказа. Шаг 2 - параметры заказа (Клиента, способ получения, pin код и т.д.)
 */
public class ProcessOrder35Page extends HeaderProcessOrder35Page {

    @AppFindBy(xpath = AndroidScrollView.TYPICAL_XPATH, metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @Form
    OrderParamsForm orderParamsForm;

    @AppFindBy(text = "ПОДТВЕРДИТЬ ЗАКАЗ")
    MagMobGreenSubmitButton submitButton;

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isTrue(orderParamsForm.waitUntilFormIsVisible(),
                "Страница оформления заказа не загрузилась");
    }

    // ACTION STEPS

    @Step("Выбираем способ получения {type}")
    public ProcessOrder35Page selectDeliveryType(SalesDocumentsConst.GiveAwayPoints type) {
        orderParamsForm.selectDeliveryType(type);
        return this;
    }

    @Step("Нажмите на иконку клиента (для перехода к экрану поиска клиента)")
    public SearchCustomerPage clickCustomerIconToSearch() throws Exception {
        return orderParamsForm.clickCustomerIconToSearch();
    }

    @Step("Нажмите на кнопку Подтвердить заказ")
    public SubmittedSalesDocument35Page clickSubmitButton() throws Exception {
        mainScrollView.scrollDownToElement(submitButton);
        submitButton.click();
        return new SubmittedSalesDocument35Page();
    }

    @Step("Ввести PIN код")
    public ProcessOrder35Page enterPinCode(OrderDetailsData data, boolean tryToFindValidPin) throws Exception {
        orderParamsForm.enterPinCode(data, tryToFindValidPin);
        return this;
    }

    @Step("Введите {value} в поле телефон")
    public void enterPhone(String value) {
        orderParamsForm.enterPhone(value);
    }

    @Step("Заполнить поля 'Получатель'")
    public ProcessOrder35Page enterCustomerInfo(MagCustomerData data) throws Exception {
        orderParamsForm.enterCustomer(data);
        return this;
    }

    @Step("Заполнить поля формы 'Оформление заказа'")
    public ProcessOrder35Page fillInFormFields(OrderDetailsData data) throws Exception {
        orderParamsForm.fillInFormFields(data);
        return this;
    }

    // VERIFICATIONS

    @Step("Проверить, что поля формы заполнены соответствующим образом")
    public ProcessOrder35Page shouldFormFieldsAre(OrderDetailsData data) throws Exception {
        orderParamsForm.shouldFormFieldsAre(data);
        return this;
    }

    @Step("Проверить, что уведомление о том, что пин код уже используется, отображается")
    public ProcessOrder35Page shouldErrorPinAlreadyExistVisible() {
        orderParamsForm.shouldErrorPinAlreadyExistVisible();
        return this;
    }

    @Step("Проверить, что уведомление о том, что пин код не должен начинаться с 9, отображается")
    public ProcessOrder35Page shouldErrorPinVisibleShouldNotStartWith9() {
        orderParamsForm.shouldErrorPinVisibleShouldNotStartWith9();
        return this;
    }

    @Step("Проверить, что уведомление о том, что пин код должен начинаться с 9, отображается")
    public ProcessOrder35Page shouldErrorPinVisibleShouldStartWith9() {
        orderParamsForm.shouldErrorPinVisibleShouldStartWith9();
        return this;
    }

}
