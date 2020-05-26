package com.leroy.magmobile.ui.pages.common.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class CalendarWidget extends BaseWidget {

    public CalendarWidget(WebDriver driver) {
        super(driver);
        initElements();
    }

    private static final String XPATH_BASE_CALENDAR_GROUP = "//android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[2]/android.view.ViewGroup[2]";

    @AppFindBy(xpath = XPATH_BASE_CALENDAR_GROUP + "/android.view.ViewGroup[1]",
            metaName = "Кнопка к предыдущему месяцу")
    private Element previousMonthBtn;

    @AppFindBy(xpath = XPATH_BASE_CALENDAR_GROUP + "/android.view.ViewGroup[2]",
            metaName = "Кнопка к следующему месяцу")
    private Element nextMonthBtn;

    @AppFindBy(xpath = XPATH_BASE_CALENDAR_GROUP + "/android.widget.TextView[1]",
            metaName = "Выбранный календарь-год")
    private Element selectedMonthYearLabel;

    @AppFindBy(text = "ОТМЕНА")
    private Element cancelBtn;

    @AppFindBy(text = "ОК")
    private Element okBtn;

    private Element findDayLabel(String day) {
        String xpath;
        if (day.equals("1"))
            xpath = XPATH_BASE_CALENDAR_GROUP + "//android.widget.TextView[@text='1']";
        else
            xpath = XPATH_BASE_CALENDAR_GROUP + String.format(
                    "//android.widget.TextView[@text='1']/following::android.widget.TextView[@text='%s']", day);
        return new Element(driver, By.xpath(xpath));
    }

    public void selectDate(LocalDate date) throws Exception {
        int differenceInMonth;
        Locale locale = new Locale("ru", "RU");
        Date needToSelectDate = new Date(date.getYear() - 1900, date.getMonthValue() - 1, date.getDayOfMonth() + 1);
        Date calendarDate = new SimpleDateFormat("MMMM yyyy", locale).parse(selectedMonthYearLabel.getText());

        if (needToSelectDate.before(calendarDate)) {
            differenceInMonth = (calendarDate.getYear() - needToSelectDate.getYear()) * 12 + calendarDate.getMonth() - needToSelectDate.getMonth();
        } else {
            differenceInMonth = (needToSelectDate.getYear() - calendarDate.getYear()) * 12 + needToSelectDate.getMonth() - calendarDate.getMonth();
        }

        for (int i = 0; i < differenceInMonth; i++) {
            if (needToSelectDate.before(calendarDate)) {
                previousMonthBtn.click();
                wait(1);
            } else if (needToSelectDate.after(calendarDate)) {
                nextMonthBtn.click();
                wait(1);
            } else
                break;
        }
        wait(1);
        findDayLabel(String.valueOf(date.getDayOfMonth())).click();
        okBtn.click();
    }
}
