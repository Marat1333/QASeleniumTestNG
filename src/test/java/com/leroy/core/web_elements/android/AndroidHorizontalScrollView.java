package com.leroy.core.web_elements.android;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import com.leroy.magmobile.models.CardWidgetData;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AndroidHorizontalScrollView<T extends CardWidgetData> extends BaseWidget {

    private boolean experiment = false;
    private final int MAX_SCROLL_COUNT = 10;

    private AndroidDriver<MobileElement> androidDriver;
    private List<T> uniqueContent = new ArrayList<>();
    private String eachElementXpath;
    private Class<? extends BaseWidget> eachElementClass;

    public enum ScrollDirection {
        LEFT,
        RIGHT
    }

    public AndroidHorizontalScrollView(WebDriver driver, By by) {
        this(driver, by, "//android.widget.TextView", null);
    }

    public AndroidHorizontalScrollView(WebDriver driver, By by, String eachElementXpath,
                                       Class<? extends CardWidget<T>> clazz) {
        super(driver, new CustomLocator(by));
        this.eachElementXpath = eachElementXpath;
        androidDriver = (AndroidDriver) driver;
        if (clazz == null)
            eachElementClass = TextViewWidget.class;
        else
            eachElementClass = clazz;
    }

    // ----------------- PRIVATE METHODS -----------------------------//

    private MobileElement executeUIAutomatorScript(String script) {
        setImplicitWait(0);
        MobileElement mobileElement;
        try {
            mobileElement = androidDriver.findElementByAndroidUIAutomator(script);
        } catch (NoSuchElementException err) {
            mobileElement = null;
            // This error is ok.
            // if you know how to execute UIAutomator script with different way, so update this code
        }
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return mobileElement;
    }

    private void simpleHorizontalScrollByUIAutomator(ScrollDirection direction) {
        String methodScroll = direction.equals(ScrollDirection.RIGHT) ? "scrollForward()" : "scrollBackward()";
        executeUIAutomatorScript(
                "new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()." +
                        "." + methodScroll + ";");
    }

    private void simpleHorizontalScroll(ScrollDirection direction) {
        int anchorY = this.getLocation().getY();
        int rightBorder = this.getWidth() - 150;
        int leftBorder = this.getWidth() - rightBorder;

        TouchAction action = new TouchAction((AndroidDriver) driver);
        if (direction.equals(ScrollDirection.RIGHT)) {
            action.press(PointOption.point(rightBorder, anchorY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
                    .moveTo(PointOption.point(leftBorder, anchorY))
                    .release()
                    .perform();
        } else {
            action.press(PointOption.point(leftBorder, anchorY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
                    .moveTo(PointOption.point(rightBorder, anchorY))
                    .release()
                    .perform();
        }
    }

    private AndroidHorizontalScrollView<T> scrollTo(ScrollDirection direction, String text) {
        if (experiment) {
            executeUIAutomatorScript(
                    "new UiScrollable(new UiSelector().scrollable(true)).setAsHorizontalList()" +
                            ".scrollTextIntoView(\"" + text + "\");");
        } else {
            // TODO
        }
        return this;
    }

    private void scrollTo(ScrollDirection direction, Element element) {
        int i = 0;
        while (!element.isVisible() && i < MAX_SCROLL_COUNT) {
            simpleHorizontalScroll(direction);
            i++;
        }
    }

    // -------------------- PUBLIC METHODS ------------------------//

    public AndroidHorizontalScrollView<T> scrollLeft(String text) {
        return scrollTo(ScrollDirection.LEFT, text);
    }

    public AndroidHorizontalScrollView<T> scrollLeft(Element elem) {
        scrollTo(ScrollDirection.LEFT, elem);
        return this;
    }

    public AndroidHorizontalScrollView<T> scrollRight(String text) {
        return scrollTo(ScrollDirection.RIGHT, text);
    }

    public AndroidHorizontalScrollView<T> scrollRight(Element elem) {
        scrollTo(ScrollDirection.RIGHT, elem);
        return this;
    }

    public List<T> getFullDataList() {
        List<T> currentElementsDataList = new ArrayList<>();
        int prevSize;
        int currSize;
        while (true) {
            ElementList<CardWidget<T>> elementsList = this.findChildElements(eachElementXpath, eachElementClass);
            prevSize = uniqueContent.size();
            for (CardWidget<T> widget : elementsList) {
                T data = widget.collectDataFromPage();
                currentElementsDataList.add(data);
            }
            for (T data : currentElementsDataList) {
                if (!uniqueContent.contains(data)) {
                    uniqueContent.add(data);
                }
            }
            currSize = uniqueContent.size();
            currentElementsDataList.clear();
            if (currSize == prevSize) {
                break;
            }
            if (experiment)
                simpleHorizontalScroll(ScrollDirection.RIGHT);
            else
                simpleHorizontalScrollByUIAutomator(ScrollDirection.RIGHT);
        }

        return uniqueContent;
    }
}
