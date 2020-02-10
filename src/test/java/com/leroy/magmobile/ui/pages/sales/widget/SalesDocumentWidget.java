package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.models.SalesDocumentData;
import org.openqa.selenium.WebDriver;

public class SalesDocumentWidget extends CardWidget<SalesDocumentData> {

    public SalesDocumentWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='lmui-Icon']")
    private Element image;

    @AppFindBy(xpath = ".//android.widget.TextView[1]")
    private Element title;

    @AppFindBy(xpath = ".//android.widget.TextView[2]")
    private Element price;

    @AppFindBy(xpath = ".//android.widget.TextView[3]")
    private Element number;

    @AppFindBy(xpath = ".//android.view.ViewGroup/android.widget.TextView[@index='4' or @index='5']") // TODO
    private Element name;

    @AppFindBy(xpath = ".//android.widget.TextView[4 and starts-with(@text, 'PIN')]")
    private Element pin;

    @AppFindBy(xpath = ".//android.view.ViewGroup[count(android.widget.TextView) > 1][2]/android.widget.TextView[1]")
    private Element date;

    @AppFindBy(xpath = ".//android.view.ViewGroup[1]/android.widget.TextView")
    private Element documentType;

    public String getPinCode(boolean onlyDigits, String pageSource) {
        String sPinCode = pin.getTextIfPresent(pageSource);
        if (!onlyDigits)
            return sPinCode;
        if (sPinCode == null)
            return null;
        else
            return sPinCode.replaceAll("PIN ", "");
    }

    public String getDocNumber(boolean onlyDigits, String pageSource) {
        String sDocNumber = number.getTextIfPresent(pageSource);
        if (!onlyDigits)
            return sDocNumber;
        if (sDocNumber == null)
            return null;
        else
            return sDocNumber.replaceAll("₽|\\s", "");
    }

    @Override
    public SalesDocumentData collectDataFromPage(String pageSource) {
        if (pageSource == null)
            pageSource = getPageSource();
        SalesDocumentData document = new SalesDocumentData();
        document.setTitle(title.getText(pageSource));
        document.setPrice(price.getText(pageSource).replaceAll("₽|\\s", ""));
        document.setNumber(getDocNumber(true, pageSource));
        document.setPin(getPinCode(true, pageSource));
        document.setDate(date.getText(pageSource));
        document.setDocumentState(documentType.getTextIfPresent(pageSource));
        return document;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return image.isVisible(pageSource) && date.isVisible(pageSource);
    }

    @Override
    public void click() {
        name.click();
    }
}
