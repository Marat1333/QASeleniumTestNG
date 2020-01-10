package com.leroy.core.web_elements.android;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class AndroidScrollView extends BaseWidget {

    AndroidDriver<MobileElement> androidDriver;
    private List<String> tmpTextList;

    public AndroidScrollView(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
        androidDriver = (AndroidDriver) driver;
    }

    /**
     * Scroll down to the end and get all text labels as ArrayList
     */
    public List<String> getFullTextLabelsList() {
        scrollToEnd();
        return new ArrayList<>(tmpTextList);
    }

    private static void addNonRepeatingText(List<String> existedList, List<String> newList) {
        int iMatchCount = 0;
        for (String str : existedList) {
            if (iMatchCount >= newList.size())
                break;
            if (str.equals(newList.get(iMatchCount))) {
                iMatchCount++;
            } else {
                iMatchCount = 0;
            }
        }

        for (int i = iMatchCount; i < newList.size(); i++) {
            existedList.add(newList.get(i));
        }
    }

    /**
     * Scroll down to the specific text
     * @param findText - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @return this
     */
    public AndroidScrollView scrollDownToText(String findText, int maxScrollCount) {
        initialWebElementIfNeeded();
        tmpTextList = new ArrayList<>();
        List<String> prefTextList = null;
        int i = 0;
        while (true) {
            if (i >= maxScrollCount)
                break;
            List<WebElement> weList = this.webElement.findElements(By.xpath(".//android.widget.TextView"));
            if (weList.size() < 2)
                break;
            List<String> currentTextList = new ArrayList<>();
            for (WebElement we : weList) {
                String currentText = we.getText();
                currentTextList.add(currentText);
                if (findText != null && currentText.contains(findText))
                    break;
            }
            addNonRepeatingText(tmpTextList, currentTextList);
            WebElement endElement = weList.get(0);
            WebElement startElement = weList.get(weList.size() - 1);
            int startX = startElement.getLocation().getX() + (startElement.getSize().getWidth() / 2);
            int startY = startElement.getLocation().getY() + (startElement.getSize().getHeight() / 2);

            int endX = endElement.getLocation().getX() + (endElement.getSize().getWidth() / 2);
            int endY = endElement.getLocation().getY() + (endElement.getSize().getHeight() / 2);

            new TouchAction<>(androidDriver)
                    .press(point(startX, startY))
                    .waitAction(waitOptions(ofMillis(500)))
                    .moveTo(point(endX, endY))
                    .release().perform();
            Log.debug("<-- Scroll down #" + (i + 1) + "-->");
            if (currentTextList.equals(prefTextList))
                break;
            prefTextList = new ArrayList<>(currentTextList);
            i++;
        }
        return this;
    }

    public AndroidScrollView scrollDownToText(String findText) {
        return scrollDownToText(findText, 20);
    }

    public AndroidScrollView scrollToEnd() {
        return scrollDownToText(null, 60);
    }

    public AndroidScrollView scrollDown(int count) {
        return scrollDownToText(null, count);
    }

}
