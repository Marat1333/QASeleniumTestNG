package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class CommonCustomerInfoPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "//*[@content-desc='ScreenHeader']/android.widget.TextView",
            metaName = "Загаловок")
    Element headerLbl;

    @AppFindBy(accessibilityId = "Button", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(accessibilityId = "firstName", metaName = "Поле 'Имя'")
    EditBox nameFld;

    @AppFindBy(accessibilityId = "middleName", metaName = "Поле 'Отчество'")
    EditBox middleNameFld;

    @AppFindBy(accessibilityId = "lastName", metaName = "Поле 'Фамилия'")
    EditBox lastNameFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button'][android.widget.TextView[@text='Мужской']]")
    MagMobButton maleOptionBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button'][android.widget.TextView[@text='Женский']]")
    MagMobButton femaleOptionBtn;

    @AppFindBy(accessibilityId = "phoneLabelMain_0")
    EditBox mainPhoneEditFld;


    // Verifications

    @Step("Поле с номером телефона должно содержать значение {value}")
    public CommonCustomerInfoPage shouldPhoneFieldIs(String value) {
        anAssert.isElementTextEqual(mainPhoneEditFld, value);
        return this;
    }

}
