package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class CustomerPuzWidget extends CardWebWidget<SimpleCustomerData> {

    public CustomerPuzWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    private final String VIEW_CARD_XPATH = ".//div[contains(@class, 'CustomerControl-ViewCard')]";

    @WebFindBy(xpath = VIEW_CARD_XPATH + "//p", metaName = "Имя клиента")
    Element name;

    @WebFindBy(xpath = VIEW_CARD_XPATH + "/div/div[2]//span", metaName = "Номер телефона")
    Element phoneNumber;

    @WebFindBy(xpath = VIEW_CARD_XPATH
            + "//div[contains(@class, 'lmui-View lmui-View-mt-gap2')]//span",
            metaName = "Номер карты")
    Element cardNumber;

    @WebFindBy(xpath = VIEW_CARD_XPATH + "//span[contains(text(), '@')]", metaName = "email")
    Element email;

    public String getName() {
        return name.getText();
    }

    public String getPhone() {
        return ParserUtil.standardPhoneFmt(phoneNumber.getText());
    }

    public String getEmail() {
        return email.getText();
    }

    public String getCardNumber() {
        return ParserUtil.strWithOnlyDigits(cardNumber.getText());
    }

    @Override
    public SimpleCustomerData collectDataFromPage() {
        if (!this.isVisible()) {
            return new SimpleCustomerData();
        }
        SimpleCustomerData customerData = new SimpleCustomerData();
        if (email.isVisible()) {
            customerData.setEmail(email.getText());
        }
        if (phoneNumber.isVisible()) {
            customerData.setPhoneNumber(getPhone());
        }
        if (cardNumber.isVisible()) {
            customerData.setCardNumber(getCardNumber());
        }
        customerData.setName(name.getText());
        return customerData;
    }
}
