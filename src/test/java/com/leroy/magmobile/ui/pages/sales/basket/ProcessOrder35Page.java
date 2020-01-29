package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocument35Page;
import com.leroy.models.OrderDetailsData;
import com.leroy.models.TextViewData;
import io.qameta.allure.Step;

public class ProcessOrder35Page extends CommonMagMobilePage {

    public ProcessOrder35Page(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "Оформление заказа")
    Element headerLbl;

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = AndroidScrollView.TYPICAL_XPATH, metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<TextViewData> mainScrollView;

    // Выбери способ получения
    @AppFindBy(text = "Самовывоз")
    MagMobButton pickupBtn;

    @AppFindBy(text = "Доставка")
    MagMobButton deliveryBtn;

    // Получатель
    @AppFindBy(accessibilityId = "fullname", metaName = "Поле 'Имя и Фамилия'")
    EditBox fullNameFld;

    @AppFindBy(accessibilityId = "phone", metaName = "Поле 'Телефон'")
    EditBox phoneFld;

    @AppFindBy(accessibilityId = "email", metaName = "Поле 'Email'")
    EditBox emailFld;

    // Параметры документа
    @AppFindBy(accessibilityId = "pincode", metaName = "Поле 'PIN-код'")
    EditBox pinCodeFld;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле 'Комментарий'")
    EditBox commentFld;

    // Ваш заказ

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(followingTextAfter = "Итого: ")
    Element totalPriceVal;

    @AppFindBy(text = "ПОДТВЕРДИТЬ ЗАКАЗ")
    MagMobSubmitButton submitButton;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    // ACTION STEPS

    @Step("Нажмите на кнопку Подтвердить заказ")
    public SubmittedSalesDocument35Page clickSubmitButton() throws Exception {
        mainScrollView.scrollDownToElement(submitButton);
        submitButton.click();
        return new SubmittedSalesDocument35Page(context);
    }

    @Step("Заполнить поля формы 'Оформление заказа'")
    public ProcessOrder35Page fillInFormFields(OrderDetailsData data) throws Exception {
        fullNameFld.clearFillAndSubmit(data.getFullName());
        mainScrollView.scrollDownToElement(phoneFld);
        phoneFld.clearFillAndSubmit(data.getPhone(false));
        mainScrollView.scrollDownToElement(emailFld);
        emailFld.clearFillAndSubmit(data.getEmail());
        mainScrollView.scrollDownToElement(pinCodeFld);
        pinCodeFld.clearFillAndSubmit(data.getPinCode());
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
    public ProcessOrder35Page shouldFormFieldsAre(OrderDetailsData data) throws Exception {
        if (!fullNameFld.isVisible())
            mainScrollView.scrollUpToElement(fullNameFld);
        if (data.getFullName() != null)
            softAssert.isElementTextEqual(fullNameFld, data.getFullName());
        if (data.getPhone(false) != null) {
            mainScrollView.scrollDownToElement(phoneFld);
            softAssert.isElementTextEqual(phoneFld, data.getPhone(true));
        }
        if (data.getEmail() != null) {
            mainScrollView.scrollDownToElement(emailFld);
            softAssert.isElementTextEqual(emailFld, data.getEmail());
        }
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
