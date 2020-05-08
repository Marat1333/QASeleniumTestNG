package com.leroy.magportal.ui.pages.orders.widget;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.constants.OrderConst;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import org.openqa.selenium.WebDriver;

public class ShortOrderDocumentCardWidget extends CardWebWidget<ShortOrderDocWebData> {

    private final String pickupSvgPath = "m5.3396 2.0594 0.1073 0.045957 2.3711 1.186 4.6331 13.898c0.17625 0.072748 0.33844 0.168 0.48953 0.27899l0.14749 0.116 4.5961-1.533c0.52801-0.176 1.09 0.109 1.264 0.63199 0.1625 0.48749-0.06991 1.0103-0.52269 1.2218l-0.10832 0.043127-4.3281 1.442 0.011 0.11c0 1.381-1.119 2.4999-2.5001 2.4999-1.381 0-2.5-1.119-2.5-2.4999 0-0.91217 0.49396-1.7022 1.2231-2.1377l0.1489-0.0823-4.1891-12.571-1.63-0.81498c-0.49401-0.24699-0.69402-0.84698-0.44701-1.342 0.23029-0.4587 0.76324-0.66305 1.2337-0.49295zm6.1604 16.441c-0.24601 0-0.46401 0.098998-0.63801 0.247-0.216 0.183-0.36201 0.44699-0.36201 0.75298 0 0.55099 0.44801 0.99998 1 0.99998 0.40101 0 0.74002-0.24 0.90102-0.57999 0.060001-0.129 0.099002-0.26899 0.099002-0.41999 0-0.55199-0.44801-0.99998-1-0.99998zm7.4051-11.925 0.043072 0.10833 2 5.9999c0.1625 0.48749-0.06991 1.0103-0.52269 1.2218l-0.10832 0.043127-6.0001 2c-0.106 0.034999-0.212 0.051999-0.31701 0.051999-0.37711 0-0.72992-0.21465-0.89877-0.56302l-0.049252-0.12097-2-5.9999c-0.1625-0.48656 0.06991-1.0102 0.52349-1.2218l0.10852-0.043141 6.0001-2c0.4903-0.1625 1.0099 0.070769 1.221 0.52366zm-1.538 1.6893-4.1021 1.368 1.368 4.1029 4.1031-1.368-1.369-4.1029z";
    private final String cardSvgPath = "m20 4h-16c-1.11 0-1.99 0.89-1.99 2l-0.01 12c0 1.11 0.89 2 2 2h16c1.11 0 2-0.89 2-2v-12c0-1.11-0.89-2-2-2zm-1 14h-14c-0.55 0-1-0.45-1-1v-5h16v5c0 0.55-0.45 1-1 1zm1-10h-16v-2h16v2z";
    private final String carSvgPath = "m20 15h-0.78c-0.55-0.609-1.337-1-2.22-1-0.475 0-0.918 0.121-1.316 0.317l-1.064-5.317h1.762l1.723 3.447c0.097 0.194 0.254 0.351 0.448 0.448l1.447 0.723v1.382zm-3 3c-0.552 0-1-0.448-1-1s0.448-1 1-1 1 0.448 1 1-0.448 1-1 1zm-8.78-3c-0.55-0.609-1.337-1-2.22-1-0.771 0-1.468 0.301-2 0.78v-7.78h8.181l1.599 8h-5.56zm-2.22 3c-0.552 0-1-0.448-1-1s0.448-1 1-1 1 0.448 1 1-0.448 1-1 1zm15.447-5.895l-1.702-0.85-1.85-3.702c-0.17-0.339-0.516-0.553-0.895-0.553h-2.78l-0.24-1.196c-0.093-0.467-0.503-0.804-0.98-0.804h-10c-0.553 0-1 0.447-1 1v10c0 0.553 0.447 1 1 1 0 1.654 1.346 3 3 3s3-1.346 3-3h5c0 1.654 1.346 3 3 3s3-1.346 3-3h1c0.553 0 1-0.447 1-1v-3c0-0.379-0.214-0.725-0.553-0.895z";
    private final String b2bSvgPath = "m14 6v-2h-4v2h4zm-10 2v11h16v-11h-16zm16-2c1.11 0 2 0.89 2 2v11c0 1.11-0.89 2-2 2h-16c-1.11 0-2-0.89-2-2l0.01-11c0-1.11 0.88-2 1.99-2h4v-2c0-1.11 0.89-2 2-2h4c1.11 0 2 0.89 2 2v2h4z";

    public ShortOrderDocumentCardWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//span[contains(@class, 'OrderListItem__firstLine-orderId')]")
    Element number;

    @WebFindBy(xpath = ".//span[contains(@class, 'Status-container')]")
    Element status;

    @WebFindBy(xpath = ".//div[contains(@class, 'popover__opener')]")
    ElementList<Element> icons;

    public String getNumber() {
        return number.getText();
    }

    public String getStatus() {
        return status.getText();
    }

    public String getDeliveryType() {
        for (Element icon : icons) {
            try {
                String iconPath = icon.findChildElement("//*[name()='path']").getAttribute("d");
                switch (iconPath) {
                    case pickupSvgPath:
                        return OrderConst.DeliveryType.PICKUP;
                    case carSvgPath:
                        return OrderConst.DeliveryType.DELIVERY_TK;
                }
            } catch (Exception err) {
                Log.error(err.getMessage());
            }
        }
        return null;
    }

    @Override
    public ShortOrderDocWebData collectDataFromPage() {
        ShortOrderDocWebData salesDocData = new ShortOrderDocWebData();
        salesDocData.setNumber(getNumber());
        salesDocData.setStatus(getStatus());
        salesDocData.setDeliveryType(getDeliveryType());
        return salesDocData;
    }

}
