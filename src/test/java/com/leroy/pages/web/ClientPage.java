package com.leroy.pages.web;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.web.common.MenuPage;
import io.qameta.allure.Step;

public class ClientPage extends MenuPage {

    private static final String HEADER = "Клиенты";

    public ClientPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//h4", metaName = "Заголовок страницы")
    private Element headerLbl;

    @WebFindBy(xpath = "//span[text()='Телефон']", metaName = "Текст 'Телефон'")
    private Element phoneLbl;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Input__icon_left-xl-1 with-action')]",
            metaName = "Кнопка поиска")
    private Button searchBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Input__icon_right-xl-1 with-action')]",
            metaName = "Кнопка поиска")
    private Button scanBarCodeBtn;

    @WebFindBy(id = "inputId", metaName = "Поле для ввода телефона")
    private EditBox searchPhoneNumberFld;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Button-buttonSize-xl')]",
            metaName = "Кнопка 'Создать клиента'")
    private Element createClientBtn;

    @Override
    public void waitForPageIsLoaded() {
        createClientBtn.waitForVisibility();
    }

    // ACTIONS

    @Step("Нажмите кнопку 'Создать клиента'")
    public CreatingClientPage clickCreateClientButton() {
        createClientBtn.click();
        return new CreatingClientPage(context);
    }

    // --------------------- Verifications --------------------------//

    @Override
    public ClientPage verifyRequiredElements() {
        softAssert.isElementTextEqual(headerLbl, HEADER);
        softAssert.isElementVisible(phoneLbl);
        softAssert.isElementVisible(searchBtn);
        softAssert.isElementVisible(searchPhoneNumberFld);
        softAssert.isElementVisible(scanBarCodeBtn);
        softAssert.isElementVisible(createClientBtn);
        softAssert.verifyAll();
        return this;
    }

}
