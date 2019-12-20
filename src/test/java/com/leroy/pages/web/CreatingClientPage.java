package com.leroy.pages.web;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.web.common.MenuPage;

public class CreatingClientPage extends MenuPage {

    static final String HEADER = "Создание клиента";

    public CreatingClientPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//h4", metaName = "Основной заголовок страницы")
    Element headerLbl;

    @WebFindBy(xpath = "//h5", metaName = "Подзаголовок страницы")
    Element subHeaderLbl;

    @WebFindBy(text = "Введенные данные нельзя отредактировать или удалить после сохранения.",
            metaName = "Подсказка уведомление")
    Element helpInfoLbl;

    @WebFindBy(id = "male")
    Button maleOptionBtn;

    @WebFindBy(id = "female")
    Button femaleOptionBtn;

    @WebFindBy(xpath = "//input[@name='firstName']")
    EditBox nameFld;

    @WebFindBy(text = "Контакты")
    Element contactsLbl;

    @WebFindBy(xpath = "(//input[@id='inputId'])[2]")
    EditBox phoneFld;

    @WebFindBy(xpath = "//button[contains(@id, 'personal')]")
    Button personalPhoneOptionBtn;

    @WebFindBy(xpath = "//button[contains(@id, 'work')]")
    Button workPhoneOptionBtn;

    @WebFindBy(text = "Показать все поля")
    Element showAllFieldsLbl;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Button-shadow')]")
    Button createBtn;

    // VERIFICATIONS

    @Override
    public CreatingClientPage verifyRequiredElements() {
        softAssert.isElementTextEqual(headerLbl, HEADER);
        softAssert.isElementTextEqual(subHeaderLbl, HEADER);
        softAssert.isElementVisible(helpInfoLbl);
        softAssert.isElementVisible(maleOptionBtn);
        softAssert.isElementVisible(femaleOptionBtn);
        softAssert.isElementVisible(nameFld);
        softAssert.isElementVisible(contactsLbl);
        softAssert.isElementVisible(phoneFld);
        softAssert.isElementVisible(personalPhoneOptionBtn);
        softAssert.isElementVisible(workPhoneOptionBtn);
        softAssert.isElementVisible(showAllFieldsLbl);
        softAssert.isElementVisible(createBtn);
        softAssert.verifyAll();
        return this;
    }
}
