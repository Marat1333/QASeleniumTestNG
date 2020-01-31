package com.leroy.magmobile.ui.pages.search;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import org.openqa.selenium.WebDriver;

public class SearchCustomerWidget extends CardWidget<CustomerData> {

    public SearchCustomerWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    private Element nameVal;
    @AppFindBy(xpath = "./android.widget.TextView[2]")
    private Element cardNumberVal;
    @AppFindBy(xpath = "./android.widget.TextView[3]")
    private Element cardTypeVal;
    @AppFindBy(xpath = "./android.widget.TextView[4]")
    private Element phoneVal;
    @AppFindBy(xpath = "./android.widget.TextView[5]")
    private Element emailVal;

    @Override
    public CustomerData collectDataFromPage(String ps) {
        if (ps == null)
            ps = getPageSource();
        CustomerData customerData = new CustomerData();
        customerData.setName(nameVal.getText(ps));
        customerData.setCardNumber(cardNumberVal.getText(ps));
        customerData.setCardType(cardTypeVal.getText(ps));
        customerData.setPhone(phoneVal.getText(ps));
        return customerData;
    }

    @Override
    public boolean isFullyVisible(String ps) {
        return nameVal.isVisible(ps) && phoneVal.isVisible(ps);
    }
}