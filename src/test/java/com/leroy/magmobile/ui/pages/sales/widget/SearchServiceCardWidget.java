package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.models.search.ServiceCardData;
import org.openqa.selenium.WebDriver;

public class SearchServiceCardWidget extends CardWidget<ServiceCardData> {
    public SearchServiceCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc=\"lmCode\"]")
    private Element lmCodeObj;

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    private Element nameObj;

    @AppFindBy(xpath = "./android.view.ViewGroup[3]")
    private Element addToSalesListBtn;

    public String getLmCode(boolean onlyDigits, String pageSource) {
        if (onlyDigits)
            return lmCodeObj.getText(pageSource).replaceAll("\\D", "");
        else
            return lmCodeObj.getText(pageSource);
    }

    public String getLmCode(boolean onlyDigits) {
        return getLmCode(onlyDigits, null);
    }

    public String getName(String pageSource) {
        return nameObj.getText(pageSource);
    }

    public String getName() {
        return getName(null);
    }

    @Override
    public ServiceCardData collectDataFromPage(String pageSource) {
        String ps = pageSource == null ? driver.getPageSource() : pageSource;
        ServiceCardData serviceCardData = new ServiceCardData();
        serviceCardData.setLmCode(getLmCode(true, ps));
        serviceCardData.setName(getName(ps));
        return serviceCardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCodeObj.isVisible() && addToSalesListBtn.isVisible();
    }
}
