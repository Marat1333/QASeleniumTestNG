package com.leroy.core.web_elements.android;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import com.leroy.models.CardWidgetData;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.appium.java_client.touch.WaitOptions.waitOptions;
import static io.appium.java_client.touch.offset.PointOption.point;
import static java.time.Duration.ofMillis;

public class AndroidScrollView<T extends CardWidgetData> extends BaseWidget {

    private final int MAX_SCROLL_COUNT = 10;

    @AppFindBy(xpath = "//android.widget.ProgressBar", cacheLookup = false, metaName = "Progress bar")
    private Element progressBar;

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
     * Scroll down to the end and get all data as ArrayList
     */
    public List<T> getFullDataList(int maxEntityCount) throws Exception {
        scrollToText(null, null, maxEntityCount, "down");
        return new ArrayList<>(tmpCardDataList);
    }

    public List<T> getFullDataList() throws Exception {
        return getFullDataList(30);
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

    private void simpleScroll(String direction) {
        //Try to change bottomY k
        Point _location = getLocation();
        Dimension _size = getSize();
        int x = _location.getX();
        int bottomY = _location.getY() + _size.getHeight() - (int) Math.round(_size.getHeight() * 0.4);
        int topY = _location.getY() + (int) Math.round(_size.getHeight() * 0.05);

        boolean isDirectionDown = direction.equals("down");
        new TouchAction<>(androidDriver)
                .press(point(x, isDirectionDown ? bottomY : topY))
                .waitAction(waitOptions(ofMillis(1000)))
                .moveTo(point(x, isDirectionDown ? topY : bottomY))
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
    private AndroidScrollView<T> scrollToText(String findText, Integer maxScrollCount, Integer maxEntityCount, String direction) throws Exception {
        initialWebElementIfNeeded();
        tmpCardDataList = new ArrayList<>();
        List<T> prevDataList = null;
        int i = 0;
        while (true) {
            if (maxScrollCount != null && i >= maxScrollCount)
                break;
            if (maxEntityCount != null && tmpCardDataList.size() >= maxEntityCount) {
                while (tmpCardDataList.size() > maxEntityCount) {
                    tmpCardDataList.remove(tmpCardDataList.size()-1);
                }
                break;
            }
            ElementList<CardWidget<T>> cardWidgetList = this.findChildElements(oneRowXpath, rowWidgetClass);
            List<T> currentVisibleDataList = new ArrayList<>();
            for (CardWidget<T> widget : cardWidgetList) {
                if (widget.isFullyVisible()) {
                    T data = widget.collectDataFromPage();
                    currentVisibleDataList.add(data);
                    if (findText != null && data.toString().contains(findText))
                        break;
                }
            }
            if (currentVisibleDataList.size() == 0) {
                Log.warn("Couldn't find elements during scroll");
                break;
            }
            addNonRepeatingText(tmpCardDataList, currentVisibleDataList);
            simpleScroll(direction);
            progressBar.waitForInvisibility();
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
        return scrollToText(findText, maxScrollCount, null,"down");
    }

    /**
     * Scroll up to the specific text
     *
     * @param findText       - text which should be found
     * @param maxScrollCount - limit of scroll count
     * @return this
     */
    public AndroidScrollView<T> scrollUpToText(String findText, int maxScrollCount) throws Exception {
        return scrollToText(findText, maxScrollCount, null, "up");
    }

    public AndroidScrollView<T> scrollDownToText(String findText) throws Exception {
        return scrollDownToText(findText, MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollDown() throws Exception {
        return scrollDown(MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollUp() throws Exception {
        return scrollUp(MAX_SCROLL_COUNT);
    }

    public AndroidScrollView<T> scrollDown(int count) throws Exception {
        if (count == 1)
            simpleScroll("down");
        return scrollDownToText(null, count);
    }

    public AndroidScrollView<T> scrollUp(int count) throws Exception {
        if (count == 1)
            simpleScroll("up");
        return scrollUpToText(null, count);
    }

}
