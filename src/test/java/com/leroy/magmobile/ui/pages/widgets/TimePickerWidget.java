package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.BaseContainer;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

import java.time.LocalTime;

public class TimePickerWidget extends BaseContainer {

    public TimePickerWidget(WebDriver driver) {
        super(driver);
        initElements();
        hoursPicker.waitForVisibility();
        minutesPicker.waitForVisibility();
    }

    @AppFindBy(xpath = "//android.view.View[1]", cacheLookup = false)
    private Element hoursPicker;

    @AppFindBy(xpath = "//android.view.View[2]", cacheLookup = false)
    private Element minutesPicker;

    @AppFindBy(accessibilityId = "Button")
    private Element confirmBtn;

    public void selectTime(LocalTime needToSelectTime, LocalTime currentTime) throws Exception {
        int diffHours = currentTime.getHour() - needToSelectTime.getHour();
        int diffHoursY = diffHours > 0 ? 105 : 110;
        int diffMinutes = currentTime.getMinute() - needToSelectTime.getMinute();
        hoursPicker.dragAndDrop(0, diffHours * diffHoursY);
        do {
            minutesPicker.dragAndDrop(0, (diffMinutes > 10 ? 10 : diffMinutes) * 110);
            wait(1); // need to wait until the animation of picker finishes
            diffMinutes -= 10;
        } while (diffMinutes >= 0);
        confirmBtn.click();
    }
}
