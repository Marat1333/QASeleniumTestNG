package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class CalendarInputBox extends BaseWidget {

    public CalendarInputBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    private DateTimeFormatter defaultDateFormat = DateTimeFormatter.ofPattern("dd.MM.yy");

    private final static String calendarContainerXpath = ".//div[contains(@class, 'DatePicker__dayPicker')]";

    @WebFindBy(xpath = ".//input", refreshEveryTime = true)
    EditBox inputFld;

    @WebFindBy(xpath = calendarContainerXpath + "//span[contains(@class,'DatePicker__captionMonth')]")
    Element selectedMonthLabel;

    @WebFindBy(xpath = calendarContainerXpath + "//span[contains(@class,'DatePicker__captionYear')]")
    Element selectedYearLabel;

    @WebFindBy(xpath = calendarContainerXpath + "//button[contains(@class,'NavButton--prev')]")
    Button previousMonthBtn;

    @WebFindBy(xpath = calendarContainerXpath + "//button[contains(@class,'NavButton--next')]")
    Button nextMonthBtn;

    private void selectDayByLabel(String value) throws Exception {
        Element dayLbl = findChildElement("//*[text()='" + value + "']");
        dayLbl.click();
    }

    /**
     * Получить выбранное значение (дату) как текст
     * @return String
     */
    public String getSelectedText() {
        return inputFld.getText();
    }

    /**
     * Получить выбранную дату
     * @return LocalDate или null, если ничего не выбрано
     */
    public LocalDate getSelectedDate() {
        String text = inputFld.getText();
        if (text.isEmpty())
            return null;
        return LocalDate.parse(text, defaultDateFormat);
    }

    /**
     * Ввести в текстовое поле дату (не используя календарь)
     * @param date - дата
     */
    public void enterDateInField(LocalDate date) {
        inputFld.clearAndFill(date.format(defaultDateFormat));
    }

    /**
     * Выбрать дату, используя календарь виджет.
     * @param date - дата, которая будет выбрана
     */
    public void selectDate(LocalDate date) throws Exception {
        inputFld.click(tiny_timeout);
        int differenceInMonth;
        Locale locale = new Locale("ru", "RU");
        Date needToSelectDate = new Date(date.getYear() - 1900,
                date.getMonthValue() - 1, date.getDayOfMonth() + 1);
        Date calendarDate = new SimpleDateFormat("MMMM yyyy", locale)
                .parse(selectedMonthLabel.getText() + " " + selectedYearLabel.getText());

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
            } else
                break;
        }
        selectDayByLabel(String.valueOf(date.getDayOfMonth()));
    }
}
