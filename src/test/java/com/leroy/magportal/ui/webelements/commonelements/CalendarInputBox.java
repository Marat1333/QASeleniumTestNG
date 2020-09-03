package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.fieldfactory.CustomLocator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import org.openqa.selenium.WebDriver;

public class CalendarInputBox extends CommonCalendarInputBox {

    public CalendarInputBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    /**
     * Получить выбранное значение (дату) как текст
     *
     * @return String
     */
    public String getSelectedText() {
        return getSelectedText(1);
    }

    /**
     * Получить выбранную дату
     *
     * @return LocalDate или null, если ничего не выбрано
     */
    public LocalDate getSelectedDate() {
        return getSelectedDate(1);
    }

    /**
     * Выбрать дату, используя календарь виджет.
     *
     * @param date - дата, которая будет выбрана
     */
    public void selectDate(LocalDate date) throws Exception {
        open();
        int differenceInMonth;
        Locale locale = new Locale("ru", "RU");
        Date needToSelectDate = new Date(date.getYear() - 1900,
                date.getMonthValue() - 1, date.getDayOfMonth());
        Date calendarDate = new SimpleDateFormat("MMMM yyyy", locale)
                .parse(firstSelectedMonthLabel.getText() + " " + firstSelectedYearLabel.getText());

        if (needToSelectDate.before(calendarDate)) {
            differenceInMonth = (calendarDate.getYear() - needToSelectDate.getYear()) * 12 +
                    calendarDate.getMonth() - needToSelectDate.getMonth();
        } else {
            differenceInMonth = (needToSelectDate.getYear() - calendarDate.getYear()) * 12 +
                    needToSelectDate.getMonth() - calendarDate.getMonth();
        }

        for (int i = 0; i < differenceInMonth; i++) {
            if (needToSelectDate.before(calendarDate)) {
                previousMonthBtn.click();
            } else if (needToSelectDate.after(calendarDate)) {
                nextMonthBtn.click();
            } else {
                break;
            }
        }
        selectDayByLabel(1, String.valueOf(date.getDayOfMonth()));
    }

    /**
     * Ввести в текстовое поле дату (не используя календарь)
     *
     * @param date - дата
     */
    public void enterDateInField(LocalDate date) {
        enterDateInField(date, 1);
    }

}