package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.widget.CalendarWidget;
import com.leroy.magmobile.ui.pages.widgets.TimePickerWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class TransferShopRoomStep2Page extends TransferOrderPage {

    @AppFindBy(text = "ПАРАМЕТРЫ ЗАЯВКИ", metaName = "Заголовок 'ПАРАМЕТРЫ ЗАЯВКИ'")
    private Element headerLbl;

    private static final String deliveryDateText = "Дата поставки товара";
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='" + deliveryDateText + "']]",
            metaName = "Область '" + deliveryDateText + "'")
    private Element deliveryDateArea;

    @AppFindBy(xpath = "//android.widget.TextView[@text='" + deliveryDateText + "']/following-sibling::android.widget.TextView[1]",
            metaName = deliveryDateText)
    private Element deliveryDateLbl;

    private static final String deliveryTimeText = "Ожидаемое время поставки товара";
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='" + deliveryTimeText + "']]",
            metaName = "Область '" + deliveryTimeText + "'")
    private Element deliveryTimeArea;
    @AppFindBy(xpath = "//android.widget.TextView[@text='" + deliveryTimeText + "']/following-sibling::android.widget.EditText[1]",
            metaName = deliveryTimeText)
    private Element deliveryTimeLbl;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле комментарий")
    private EditBox commentFld;

    @AppFindBy(accessibilityId = "Button")
    private MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        submitBtn.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать на поле даты поставки и меняем дату на {date} и подтвердить изменение")
    public TransferShopRoomStep2Page editDeliveryDate(LocalDate date) throws Exception {
        deliveryDateArea.click();
        new CalendarWidget(driver).selectDate(date);
        return this;
    }

    @Step("Изменить ожидаемое время доставки на {time} и подтвердить его")
    public TransferShopRoomStep2Page editDeliveryTime(LocalTime time, boolean useDocCreationTimeForSelectTime) throws Exception {
        String timeFromPage = deliveryTimeLbl.getTextIfPresent();
        LocalTime currentTime;
        if (useDocCreationTimeForSelectTime)
            currentTime = getCreationDateTime().toLocalTime();
        else {
            currentTime = timeFromPage == null ? LocalTime.now() :
                    LocalTime.parse(deliveryTimeLbl.getText());
            if (ZonedDateTime.now().getOffset().equals(ZoneOffset.of("+03:00")))
                currentTime = currentTime.minusHours(3);
        }
        deliveryTimeArea.click();
        new TimePickerWidget(driver).selectTime(time, currentTime);
        deliveryTimeLbl.waitForVisibility();
        return this;
    }

    @Step("Ввести комментарий и подтвердить его")
    public TransferShopRoomStep2Page editComment(String commentText) {
        commentFld.clearFillAndSubmit(commentText);
        return this;
    }

    @Step("Нажать кнопку ОТПРАВИТЬ ЗАЯВКУ")
    public TransferToShopRoomSuccessPage clickSubmitBtn() {
        submitBtn.click();
        return new TransferToShopRoomSuccessPage();
    }

    /* ------------------------- Verifications -------------------------- */

    @Step("Проверить, что экран 'Параметры заявки' отображается корректно")
    public TransferShopRoomStep2Page verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, submitBtn, deliveryDateArea,
                deliveryDateLbl, deliveryTimeArea, commentFld);
        softAssert.isTrue(isOrderNumberVisibleAndValid(),
                "Номер заявки должен быть валиден");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поле дата должно быть {date}")
    public TransferShopRoomStep2Page shouldDateFieldIs(LocalDate date) {
        String dateFormat = "d MMMM yyyy";
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

    @Step("Проверить, что поле время должно быть {expectedTime}")
    public TransferShopRoomStep2Page shouldTimeFieldIs(LocalTime expectedTime) {
        LocalTime actualTime = DateTimeUtil.strToLocalTime(deliveryTimeLbl.getText());
        long diffTime = ChronoUnit.MINUTES.between(actualTime, expectedTime);
        anAssert.isTrue(Math.abs(diffTime) <= 1,
                "Неверное время поставки товара. Актуальное: " + actualTime + " Ожидалось: " + expectedTime);
        return this;
    }

    @Step("Проверить, что поле комментарий должно быть {text}")
    public TransferShopRoomStep2Page shouldCommentFieldIs(String text) {
        anAssert.isElementTextEqual(commentFld, text);
        return this;
    }

}
