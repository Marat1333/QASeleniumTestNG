package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.fieldfactory.CustomLocator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import org.openqa.selenium.WebDriver;

public class DualCalendarInputBox extends CommonCalendarInputBox {

    public DualCalendarInputBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    private void selectOneDate(LocalDate date) throws Exception {
        Locale locale = new Locale("ru", "RU");
        SimpleDateFormat frmt = new SimpleDateFormat("MMMM yyyy", locale);
        int differenceInMonth;

        Date needToSelectDate = new Date(date.getYear() - 1900,
                date.getMonthValue() - 1, date.getDayOfMonth() + 1);
        Date leftCalendarDate = frmt
                .parse(firstSelectedMonthLabel.getText() + " " + firstSelectedYearLabel.getText());

        if (needToSelectDate.before(leftCalendarDate)) {
            differenceInMonth = (leftCalendarDate.getYear() - needToSelectDate.getYear()) * 12 +
                    leftCalendarDate.getMonth() - needToSelectDate.getMonth();
        } else {
            differenceInMonth = (needToSelectDate.getYear() - leftCalendarDate.getYear()) * 12 +
                    needToSelectDate.getMonth() - leftCalendarDate.getMonth();
        }

        for (int i = 0; i < differenceInMonth; i++) {
            if (needToSelectDate.before(leftCalendarDate)) {
                previousMonthBtn.click();
            } else if ((i + 1) < differenceInMonth && needToSelectDate.after(leftCalendarDate)) {
                nextMonthBtn.click();
            } else {
                break;
            }
        }

        int calendarNumber = frmt.parse(
                firstSelectedMonthLabel.getText() + " " + firstSelectedYearLabel.getText())
                .getMonth() == needToSelectDate.getMonth() ? 1 : 2;
        selectDayByLabel(calendarNumber, String.valueOf(date.getDayOfMonth()));
    }

    /**
     * Выбрать даты "с" и "по", используя календарь виджет.
     *
     * @param fromDate - дата "С", которая будет выбрана
     * @param toDate   - дата "По", которая будет выбрана
     */
    public void selectDate(LocalDate fromDate, LocalDate toDate) throws Exception {
        open();
        selectOneDate(fromDate);
        selectOneDate(toDate);
    }

    /**
     * Получить выбранную "С" дату
     *
     * @return LocalDate или null, если ничего не выбрано
     */
    public LocalDate getSelectedFromDate() {
        return getSelectedDate(1);
    }

    /**
     * Получить выбранную "По" дату
     *
     * @return LocalDate или null, если ничего не выбрано
     */
    public LocalDate getSelectedToDate() {
        return getSelectedDate(2);
    }

}
