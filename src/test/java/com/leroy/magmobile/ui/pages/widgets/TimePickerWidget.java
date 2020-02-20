package com.leroy.magmobile.ui.pages.widgets;

import com.leroy.core.BaseContainer;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.NoSuchElementException;
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

    private void executeScroll(String script) {
        try {
            ((AndroidDriver) driver).findElementByAndroidUIAutomator(script);
        } catch (NoSuchElementException err) {

        }
    }

    private void scrolling(int diff, String uiSelector) {
        String scriptOneScrollOneDigit = "new UiScrollable(%s).setSwipeDeadZonePercentage(0.4).scroll%s(10);";
        String scriptOneScrollFiveDigitsBackward = "new UiScrollable(%s).setSwipeDeadZonePercentage(0).scrollBackward();";
        String scriptOneScrollFiveDigitsForward = "new UiScrollable(%s).setSwipeDeadZonePercentage(0.01).scrollForward(35);";
        while (diff != 0) {
            if (Math.abs(diff) >= 5) {
                if (diff > 0) {
                    executeScroll(String.format(scriptOneScrollFiveDigitsBackward, uiSelector));
                    diff -= 5;
                } else {
                    executeScroll(String.format(scriptOneScrollFiveDigitsForward, uiSelector));
                    diff += 5;
                }
            } else {
                String direction;
                if (diff > 0) {
                    direction = "Backward";
                    diff -= 1;
                } else {
                    direction = "Forward";
                    diff += 1;
                }
                executeScroll(String.format(scriptOneScrollOneDigit, uiSelector, direction));
            }
        }
    }

    public void selectTime(LocalTime needToSelectTime, LocalTime currentTime) {
        int diffHours = currentTime.getHour() - needToSelectTime.getHour();
        int diffMinutes = currentTime.getMinute() - needToSelectTime.getMinute();
        String uiSelector = "new UiSelector().className(\"android.view.View\").instance(%s)";
        String uiSelectorHours = String.format(uiSelector, "0");
        String uiSelectorMinutes = String.format(uiSelector, "1");
        scrolling(diffHours, uiSelectorHours);
        scrolling(diffMinutes, uiSelectorMinutes);
        confirmBtn.click();
    }
}
