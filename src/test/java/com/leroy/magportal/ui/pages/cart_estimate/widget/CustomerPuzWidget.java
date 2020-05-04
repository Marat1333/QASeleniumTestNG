package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.Converter;
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

    @WebFindBy(xpath = VIEW_CARD_XPATH + "/div/div[2]/div[2]//span", metaName = "email")
    Element email;

    public String getName() {
        return name.getText();
    }

    public String getPhone() {
        return Converter.standardPhoneFmt(phoneNumber.getText());
    }

    public String getEmail() {
        return email.getText();
    }

    @Override
    public SimpleCustomerData collectDataFromPage() {
        if (!this.isVisible())
            return null;
        SimpleCustomerData customerData = new SimpleCustomerData();
        if (email.isVisible())
            customerData.setEmail(email.getText());
        if (phoneNumber.isVisible())
            customerData.setPhoneNumber(getPhone());
        customerData.setName(name.getText());
        return customerData;
    }
}
