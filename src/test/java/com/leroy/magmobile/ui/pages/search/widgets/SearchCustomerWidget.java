package com.leroy.magmobile.ui.pages.search.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import org.openqa.selenium.WebDriver;

public class SearchCustomerWidget extends CardWidget<MagCustomerData> {

    public SearchCustomerWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    private Element nameVal;
    @AppFindBy(xpath = "./android.widget.TextView[2]")
    private Element cardNumberVal;
    @AppFindBy(xpath = "./android.widget.TextView[3]")
    private Element cardTypeVal;
    @AppFindBy(xpath = "./android.widget.TextView[contains(@text, '+7')]")
    private Element phoneVal;
    @AppFindBy(xpath = "./android.widget.TextView[contains(@text, '@')]")
    private Element emailVal;

    @Override
    public MagCustomerData collectDataFromPage(String ps) {
        if (ps == null)
            ps = getPageSource();
        MagCustomerData customerData = new MagCustomerData();
        customerData.setName(nameVal.getText(ps));
        //customerData.setCardNumber(cardNumberVal.getText(ps)); TODO
        //customerData.setCardType(cardTypeVal.getText(ps));
        customerData.setPhone(phoneVal.getText(ps));
        return customerData;
    }

    @Override
    public boolean isFullyVisible(String ps) {
        return nameVal.isVisible(ps) && phoneVal.isVisible(ps);
    }
}