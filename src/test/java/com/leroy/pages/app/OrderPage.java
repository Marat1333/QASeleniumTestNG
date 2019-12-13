package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.widgets.CalendarWidget;
import com.leroy.pages.app.widgets.TimePickerWidget;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.time.LocalTime;

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
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='" + deliveryDateText + "']]",
            metaName = "Область '" + deliveryDateText + "'")
    private Element deliveryDateArea;

    @AppFindBy(xpath = "//android.widget.TextView[@text='" + deliveryDateText + "']/following-sibling::android.widget.TextView[1]",
            metaName = deliveryDateText)
    public Element deliveryDateLbl;

    private static final String deliveryTimeText = "Ожидаемое время доставки товара";
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='" + deliveryTimeText + "']]",
            metaName = "Область '" + deliveryTimeText + "'")
    private Element deliveryTimeArea;
    @AppFindBy(xpath = "//android.widget.TextView[@text='" + deliveryTimeText + "']/following-sibling::android.widget.TextView[1]",
            metaName = deliveryTimeText)
    public Element deliveryTimeLbl;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле комментарий")
    public EditBox commentFld;

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

    public void editDeliveryTime(LocalTime time) throws Exception {
        LocalTime currentTime = LocalTime.parse(deliveryTimeLbl.getText());
        deliveryTimeArea.click();
        new TimePickerWidget(driver).selectTime(time, currentTime);
    }

    public SubmittedWithdrawalOrderPage clickSubmitBtn() {
        submitBtn.click();
        return new SubmittedWithdrawalOrderPage(driver);
    }

}
