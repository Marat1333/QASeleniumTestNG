package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.widgets.CalendarWidget;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.Calendar;

public class OrderPage extends BaseAppPage {

    public OrderPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(text = "ПАРАМЕТРЫ ЗАЯВКИ", metaName = "Заголовок 'ПАРАМЕТРЫ ЗАЯВКИ'")
    public Element headerObj;

    @AppFindBy(xpath = "//android.widget.TextView[starts-with(@text, 'Заявка №')]")
    private Element orderNumberObj;

    // Parameters areas
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Способ пополнения']]",
            metaName = "Область 'Способ пополнения'")
    private Element replenishmentMethodArea;

    private static final String deliveryDateText = "Дата доставки товара";
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='"+deliveryDateText+"']]",
            metaName = "Область '"+deliveryDateText+"'")
    private Element deliveryDateArea;

    @AppFindBy(xpath = "//android.widget.TextView[@text='"+deliveryDateText+"']/following-sibling::android.widget.TextView[1]",
            metaName = deliveryDateText)
    public Element deliveryDateObj;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Ожидаемое время доставки товара']]",
            metaName = "Область 'Ожидаемое время доставки товара'")
    private Element estimatedDeliveryTimeArea;

    @AppFindBy(accessibilityId = "Button")
    private Element submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerObj.waitForVisibility();
        submitBtn.waitForVisibility(timeout);
    }

    public String getOrderNumber() {
        return orderNumberObj.getText().replaceAll("Заявка № ", "").trim();
    }

    public boolean isOrderNumberVisibleAndValid() {
        String number = getOrderNumber();
        return number.matches("IP\\.\\d{7}");
    }

    public void editDeliveryDate(LocalDate date) throws Exception {
        deliveryDateArea.click();
        new CalendarWidget(driver).selectDate(date);
    }

}
