package com.leroy.magportal.ui.webelements.widgets;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class CalendarWidget extends BaseWidget {

    public CalendarWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//span[contains(@class,'DatePicker__captionMonth')]")
    Element selectedMonthLabel;

    @WebFindBy(xpath = ".//span[contains(@class,'DatePicker__captionYear')]")
    Element selectedYearLabel;

    private Button getPreviousMonthBtn(){
        return new Button(driver, new CustomLocator(By.xpath(".//button[contains(@class,'NavButton--prev')]")));
    }
    
    private Button getNextMonthBtn(){
        return new Button(driver, new CustomLocator(By.xpath(".//button[contains(@class,'NavButton--next')]")));
    }

    private void selectDayByLabel(String value) throws Exception {
        Element dayLbl = new Element(driver, new CustomLocator(By.xpath(getXpath()+"//*[text()='" + value + "']")));
        dayLbl.click();
    }

    public void selectDate(LocalDate date) throws Exception {
        int differenceInMonth;
        Locale locale = new Locale("ru", "RU");
        Date needToSelectDate = new Date(date.getYear() - 1900, date.getMonthValue() - 1, date.getDayOfMonth() + 1);
        Date calendarDate = new SimpleDateFormat("MMMM yyyy", locale).parse(selectedMonthLabel.getText() + " " + selectedYearLabel.getText());

        if (needToSelectDate.before(calendarDate)) {
            differenceInMonth = (calendarDate.getYear() - needToSelectDate.getYear()) * 12 + calendarDate.getMonth() - needToSelectDate.getMonth();
        } else {
            differenceInMonth = (needToSelectDate.getYear() - calendarDate.getYear()) * 12 + needToSelectDate.getMonth() - calendarDate.getMonth();
        }

        for (int i = 0; i < differenceInMonth; i++) {
            if (needToSelectDate.before(calendarDate)) {
                getPreviousMonthBtn().click();
            } else if (needToSelectDate.after(calendarDate)) {
                getNextMonthBtn().click();
            } else
                break;
        }
        selectDayByLabel(String.valueOf(date.getDayOfMonth()));
    }
}
