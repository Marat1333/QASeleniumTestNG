package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.widgets.CalendarWidget;
import com.leroy.magmobile.ui.pages.widgets.TimePickerWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OrderPage extends CommonMagMobilePage {

    public OrderPage(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "ПАРАМЕТРЫ ЗАЯВКИ", metaName = "Заголовок 'ПАРАМЕТРЫ ЗАЯВКИ'")
    private Element headerObj;

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
    private Element deliveryDateLbl;

    private static final String deliveryTimeText = "Ожидаемое время доставки товара";
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='" + deliveryTimeText + "']]",
            metaName = "Область '" + deliveryTimeText + "'")
    private Element deliveryTimeArea;
    @AppFindBy(xpath = "//android.widget.TextView[@text='" + deliveryTimeText + "']/following-sibling::android.widget.TextView[1]",
            metaName = deliveryTimeText)
    private Element deliveryTimeLbl;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле комментарий")
    private EditBox commentFld;

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

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать на поле даты поставки и меняем дату на {date} и подтвердить изменение")
    public OrderPage editDeliveryDate(LocalDate date) throws Exception {
        deliveryDateArea.click();
        new CalendarWidget(driver).selectDate(date);
        return this;
    }

    @Step("Изменить ожидаемое время доставки на {time} и подтвердить его")
    public OrderPage editDeliveryTime(LocalTime time) throws Exception {
        LocalTime currentTime = LocalTime.parse(deliveryTimeLbl.getText());
        deliveryTimeArea.click();
        new TimePickerWidget(driver).selectTime(time, currentTime);
        return this;
    }

    @Step("Ввести комментарий и подтвердить его")
    public OrderPage editComment(String commentText) {
        commentFld.clearFillAndSubmit(commentText);
        return this;
    }

    @Step("Нажать кнопку ОТПРАВИТЬ ЗАЯВКУ")
    public SubmittedWithdrawalOrderPage clickSubmitBtn() {
        submitBtn.click();
        return new SubmittedWithdrawalOrderPage(context);
    }

    /* ------------------------- Verifications -------------------------- */

    @Step("Проверить, что экран 'Параметры заявки' отображается корректно")
    public OrderPage verifyVisibilityOfAllElements() {
        softAssert.isElementVisible(headerObj);
        softAssert.isTrue(isOrderNumberVisibleAndValid(),
                "Номер заявки должен быть валиден");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поле дата должно быть {date}")
    public OrderPage shouldDateFieldIs(LocalDate date) {
        String dateFormat = "dd-го MMM";
        String _deliveryDate = deliveryDateLbl.getText();
        LocalDate dateFromPage = DateTimeUtil.strToLocalDate(_deliveryDate,
                dateFormat);
        softAssert.isNotNull(dateFromPage, _deliveryDate,
                "Выбранная дата должна иметь формат '" + dateFormat + "'");
        softAssert.isEquals(dateFromPage.getDayOfMonth(), date.getDayOfMonth(),
                "Выбранный день должен быть равен %s");
        softAssert.isEquals(dateFromPage.getMonth(), date.getMonth(),
                "Выбранный месяц должен быть равен %s");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поле время должно быть {time}")
    public OrderPage shouldTimeFieldIs(LocalTime time) {
        anAssert.isElementTextEqual(deliveryTimeLbl,
                time.format(DateTimeFormatter.ofPattern("HH:mm")));
        return this;
    }

    @Step("Проверить, что поле комментарий должно быть {text}")
    public OrderPage shouldCommentFieldIs(String text) {
        anAssert.isElementTextEqual(commentFld, text);
        return this;
    }

}
