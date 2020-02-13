package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.magmobile.ui.pages.widgets.CalendarWidget;

import java.time.LocalDate;

public class AllGammaFilterPage extends FilterPage {
    public AllGammaFilterPage(TestContext context){
        super(context);
    }

    @Override
    public FilterPage choseAvsDate(LocalDate date) throws Exception {
        mainScrollView.scrollDown();
        String pageSource=getPageSource();
        avsDateBtn.click();
        if (!waitUntilContentIsChanged(pageSource)){
            mainScrollView.scrollDown();
            avsDateBtn.click();
        }
        CalendarWidget calendarWidget = new CalendarWidget(context.getDriver());
        calendarWidget.selectDate(date);
        return new FilterPage(context);
    }
}
