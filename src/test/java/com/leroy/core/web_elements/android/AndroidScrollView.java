package com.leroy.core.web_elements.android;

import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.models.CardWidgetData;
import com.leroy.pages.app.widgets.CardWidget;
import com.leroy.pages.app.widgets.TextViewWidget;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class AndroidScrollView<T extends CardWidgetData> extends BaseWidget {

    public static final String TYPICAL_XPATH = "//android.widget.ScrollView";
    private Class<? extends BaseWidget> rowWidgetClass;
    private String oneRowXpath;

    AndroidDriver<MobileElement> androidDriver;

    private List<T> tmpCardDataList;

    public AndroidScrollView(WebDriver driver, CustomLocator locator) {
        this(driver, locator, ".//android.widget.TextView", null);
    }

    public AndroidScrollView(WebDriver driver, CustomLocator locator, String oneRowXpath,
                             Class<? extends CardWidget<T>> clazz) {
        super(driver, locator);
        if (clazz == null)
            this.rowWidgetClass = TextViewWidget.class;
        else
            this.rowWidgetClass = clazz;
        this.oneRowXpath = oneRowXpath;
        androidDriver = (AndroidDriver) driver;
    }

    /**
     * Scroll down to the end and get all text labels as ArrayList
     */
    public List<T> getFullDataList() throws Exception {
        scrollDown();
        return new ArrayList<>(tmpCardDataList);
    }

    public List<String> getFullDataAsStringList() throws Exception {
        return getFullDataList().stream().map(T::toString).collect(Collectors.toList());
    }

    private void addNonRepeatingText(List<T> existedList, List<T> newList) {
        int iMatchCount = 0;
        for (T data : existedList) {
            if (iMatchCount >= newList.size())
                break;
            if (data.equals(newList.get(iMatchCount))) {
                iMatchCount++;
            } else {
                iMatchCount = 0;
            }
        }

        for (int i = iMatchCount; i < newList.size(); i++) {
            existedList.add(newList.get(i));
        }
    }

    private void simpleScroll(WebElement startElement, WebElement endElement) {
        int startX = startElement.getLocation().getX() + (startElement.getSize().getWidth() / 2);
        int startY = startElement.getLocation().getY() + (startElement.getSize().getHeight() / 2);

        int endX = endElement.getLocation().getX() + (endElement.getSize().getWidth() / 2);
        int endY = endElement.getLocation().getY() + (endElement.getSize().getHeight() / 2);

        new TouchAction<>(androidDriver)
                .press(point(startX, startY))
                .waitAction(waitOptions(ofMillis(500)))
                .moveTo(point(endX, endY))
                .release().perform();
    }

    /**
     * Scroll to the specific text
     *
     * @param findText       - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @param direction      - up or down
     * @return this
     */
    private AndroidScrollView<T> scrollToText(String findText, int maxScrollCount, String direction) throws Exception {
        initialWebElementIfNeeded();
        tmpCardDataList = new ArrayList<>();
        List<T> prevDataList = null;
        int i = 0;
        while (true) {
            if (i >= maxScrollCount)
                break;
            ElementList<CardWidget<T>> cardWidgetList = this.findChildElements(oneRowXpath, rowWidgetClass);
            int widgetCount = cardWidgetList.getCount();
            if (widgetCount < 1)
                break;
            List<T> currentVisibleDataList = new ArrayList<>();
            for (CardWidget<T> we : cardWidgetList) {
                T data = we.collectDataFromPage();
                currentVisibleDataList.add(data);
                if (findText != null && data.toString().contains(findText))
                    break;
            }
            addNonRepeatingText(tmpCardDataList, currentVisibleDataList);
            WebElement endElement;
            WebElement startElement;
            if (direction.equals("down")) {
                endElement = cardWidgetList.get(0).getWebElement();
                startElement = cardWidgetList.get(widgetCount - 1).getWebElement();
            } else {
                endElement = cardWidgetList.get(widgetCount - 1).getWebElement();
                startElement = cardWidgetList.get(0).getWebElement();
            }
            simpleScroll(startElement, endElement);
            Log.debug("<-- Scroll down #" + (i + 1) + "-->");
            if (currentVisibleDataList.equals(prevDataList))
                break;
            prevDataList = new ArrayList<>(currentVisibleDataList);
            i++;
        }
        return this;
    }

    /**
     * Scroll down to the specific text
     *
     * @param findText       - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @return this
     */
    public AndroidScrollView<T> scrollDownToText(String findText, int maxScrollCount) throws Exception {
        return scrollToText(findText, maxScrollCount, "down");
    }

    /**
     * Scroll up to the specific text
     *
     * @param findText       - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @return this
     */
    public AndroidScrollView<T> scrollUpToText(String findText, int maxScrollCount) throws Exception {
        return scrollToText(findText, maxScrollCount, "up");
    }

    public AndroidScrollView<T> scrollDownToText(String findText) throws Exception {
        return scrollDownToText(findText, 20);
    }

    public AndroidScrollView<T> scrollDown() throws Exception {
        return scrollDown(60);
    }

    public AndroidScrollView<T> scrollUp() throws Exception {
        return scrollUp(60);
    }

    public AndroidScrollView<T> scrollDown(int count) throws Exception {
        return scrollDownToText(null, count);
    }

    public AndroidScrollView<T> scrollUp(int count) throws Exception {
        return scrollUpToText(null, count);
    }

}
