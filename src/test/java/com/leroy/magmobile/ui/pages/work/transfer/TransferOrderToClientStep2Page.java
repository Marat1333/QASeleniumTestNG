package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.work.transfer.enums.TransferTaskTypes;
import com.leroy.magmobile.ui.pages.work.transfer.modal.SelectPickupPointModal;
import io.qameta.allure.Step;

/**
 * Шаг 2 оформления заявки на отзыв товара клиенту в торговый зал
 */
public class TransferOrderToClientStep2Page extends TransferOrderPage {

    @AppFindBy(xpath = AndroidScrollView.TYPICAL_XPATH, metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Место выдачи']]/android.widget.TextView[2]",
            metaName = "Поле 'Место выдачи'")
    Element pickupPointFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Клиент']]", metaName = "Поле 'Клиент'")
    Element clientFld;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле 'Комментарий'")
    EditBox commentFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[@text='Место выдачи']]]/following-sibling::android.view.ViewGroup",
            metaName = "Карточка выбранного клиента")
    SelectedClientWidget selectedClientWidget;

    @AppFindBy(text = "ОФОРМИТЬ ПРОДАЖУ")
    MagMobGreenSubmitButton submitButton;

    // Actions

    @Step("Нажать кнопку 'Назад'")
    public TransferOrderStep1Page clickBackButton() {
        backBtn.click();
        return new TransferOrderStep1Page();
    }

    @Step("Выбрать место выдачи")
    public TransferOrderToClientStep2Page selectPickupPoint(TransferTaskTypes option) {
        pickupPointFld.click();
        SelectPickupPointModal modal = new SelectPickupPointModal();
        switch (option) {
            case CLIENT_IN_SHOP_ROOM:
                modal.clickToClientInShopRoomMenuItem();
                break;
            case OVER_SIZED_CHECKOUT:
                modal.clickOverSizedCheckoutMenuItemMenuItem();
                break;
        }
        return new TransferOrderToClientStep2Page();
    }

    @Step("Нажать на поле 'Клиент'")
    public SearchCustomerPage clickClientField() {
        clientFld.click();
        return new SearchCustomerPage();
    }

    @Step("Ввести {text} в поле 'Комментарий'")
    public TransferOrderToClientStep2Page enterTextInCommentField(String text) {
        mainScrollView.scrollToEnd();
        commentFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Нажать кнопку 'Оформить продажу'")
    public TransferToClientSuccessPage clickSubmitButton() {
        TransferTaskTypes taskType = pickupPointFld.getText().toLowerCase()
                .contains(TransferTaskTypes.CLIENT_IN_SHOP_ROOM.getValue().toLowerCase()) ?
                TransferTaskTypes.CLIENT_IN_SHOP_ROOM : TransferTaskTypes.OVER_SIZED_CHECKOUT;
        submitButton.click();
        return new TransferToClientSuccessPage(taskType);
    }

    // Verifications

    @Step("Проверить, что страница 'Шаг 2 оформления заявки на отызв для клиента в торг. зал' отображается корректно")
    public TransferOrderToClientStep2Page verifyRequiredElements() {
        softAssert.areElementsVisible(pickupPointFld, clientFld);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что место выдачи = {option}")
    public TransferOrderToClientStep2Page shouldPickupPointIs(TransferTaskTypes option) {
        anAssert.isEquals(pickupPointFld.getText(), option.getValue(), "Ожидалось другое место выдачи");
        return this;
    }

    @Step("Проверить, что выбранный клиент соответствует ожидаемому")
    public TransferOrderToClientStep2Page shouldSelectedCustomerIs(MagCustomerData customerData) {
        MagCustomerData expectedData = customerData.clone();
        selectedClientWidget.collectDataFromPage().assertEqualsNotNullExpectedFields(expectedData);
        return this;
    }

    @Step("Проверить, что комментарий = {value}")
    public TransferOrderToClientStep2Page shouldCommentFieldIs(String value) {
        anAssert.isEquals(commentFld.getText(), value, "Ожидался другой комментарий");
        return this;
    }

}
