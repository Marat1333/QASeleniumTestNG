package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.ShortSalesDocumentData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class SalesDocumentWidget extends CardWidget<ShortSalesDocumentData> {

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

    @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'Клиент:') or contains(@text, 'Юр. лицо:')]")
    private Element customerName;

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
            return ParserUtil.strWithOnlyDigits(sDocNumber);
    }

    public String getCustomerName(String pageSource) {
        String name = customerName.getTextIfPresent(pageSource);
        if (name == null)
            return null;
        else
            return name.replaceAll("Клиент:", "").replaceAll("Юр. лицо:", "").trim();
    }

    @Override
    public ShortSalesDocumentData collectDataFromPage(String pageSource) {
        if (pageSource == null)
            pageSource = getPageSource();
        ShortSalesDocumentData document = new ShortSalesDocumentData();
        document.setTitle(title.getText(pageSource));
        document.setDocumentTotalPrice(ParserUtil.strToDouble(price.getText(pageSource)));
        document.setNumber(getDocNumber(true, pageSource));
        document.setPin(getPinCode(true, pageSource));
        document.setDate((DateTimeUtil.strToLocalDateTime(date.getText(pageSource), "dd MMM, H:mm")));
        document.setDocumentState(documentType.getTextIfPresent(pageSource));
        document.setCustomerName(getCustomerName(pageSource));
        return document;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return image.isVisible(pageSource) && number.isVisible(pageSource) &&
                number.getText(pageSource).contains("№") && date.isVisible(pageSource);
    }

    @Override
    public void click() {
        name.click();
    }
}
