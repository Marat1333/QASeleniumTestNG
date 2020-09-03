package com.leroy.magportal.ui.webelements.commonelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class CommonCalendarInputBox extends BaseWidget {

    public CommonCalendarInputBox(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    private DateTimeFormatter defaultDateFormat = DateTimeFormatter.ofPattern("dd.MM.yy");

    protected final String calendarContainerXpath = ".//div[contains(@class, 'DatePicker__dayPicker')]";

    @WebFindBy(xpath = ".//input", refreshEveryTime = true)
    EditBox firstInputFld;

    @WebFindBy(xpath = ".//input[2]")
    EditBox secondInputFld;

    @WebFindBy(id = "DatePicker--clearIcon", metaName = "Иконка 'крестик' для очистки")
    Element cleatBtn;

    @WebFindBy(xpath = calendarContainerXpath + "//span[contains(@class,'DatePicker__captionMonth')]")
    Element firstSelectedMonthLabel;

    @WebFindBy(xpath = calendarContainerXpath + "//span[contains(@class,'DatePicker__captionYear')]")
    Element firstSelectedYearLabel;

    @WebFindBy(xpath = calendarContainerXpath + "//div[contains(@class, 'DayPicker-Month')][2]//span[contains(@class,'DatePicker__captionMonth')]")
    Element secondSelectedMonthLabel;

    @WebFindBy(xpath = calendarContainerXpath + "//div[contains(@class, 'DayPicker-Month')][2]//span[contains(@class,'DatePicker__captionYear')]")
    Element secondSelectedYearLabel;

    @WebFindBy(xpath = calendarContainerXpath + "//button[contains(@class,'NavButton--prev')]")
    Button previousMonthBtn;

    @WebFindBy(xpath = calendarContainerXpath + "//button[contains(@class,'NavButton--next')]")
    Button nextMonthBtn;

    protected void selectDayByLabel(int pickerNumber, String value) throws Exception {
        Element dayLbl = findChildElement(
                "//div[contains(@class, 'DayPicker-Month')][" + pickerNumber + "]//*[text()='" + value + "']");
        dayLbl.click();
    }

    /**
     * Получить выбранное значение (дату) как текст
     *
     * @param pickerIndex - 1 - первый календарь виджет; 2 - второй календарь виджет
     * @return String
     */
    protected String getSelectedText(int pickerIndex) {
        return pickerIndex == 1 ? firstInputFld.getText() : secondInputFld.getText();
    }

    /**
     * Получить выбранную дату
     *
     * @param pickerIndex - 1 - первый календарь виджет; 2 - второй календарь виджет
     * @return LocalDate или null, если ничего не выбрано
     */
    protected LocalDate getSelectedDate(int pickerIndex) {
        EditBox inputBox = pickerIndex == 1 ? firstInputFld : secondInputFld;
        if (!inputBox.isVisible())
            return null;
        String text = inputBox.getText();
        if (text.isEmpty())
            return null;
        return LocalDate.parse(text, defaultDateFormat);
    }

    protected void open() {
        if (!firstSelectedMonthLabel.isVisible())
            firstInputFld.click(tiny_timeout);
    }

    /**
     * Очищает поле с датой, с помозью нажатия на иконку крестика
     */
    public void clear() {
        open();
        cleatBtn.click();
    }

    /**
     * Ввести в текстовое поле дату (не используя календарь)
     *
     * @param pickerIndex - 1 - первый календарь виджет; 2 - второй календарь виджет
     * @param date        - дата
     */
    protected void enterDateInField(LocalDate date, int pickerIndex) {
        EditBox inputFld = pickerIndex == 1 ? firstInputFld : secondInputFld;
        inputFld.clearAndFill(date.format(defaultDateFormat));
    }
}

