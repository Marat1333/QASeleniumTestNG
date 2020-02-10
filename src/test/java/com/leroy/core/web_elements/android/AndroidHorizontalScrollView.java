package com.leroy.core.web_elements.android;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.models.CardWidgetData;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AndroidHorizontalScrollView<T extends CardWidgetData> extends BaseWidget {

    private AndroidDriver<MobileElement> androidDriver;
    private List<T> currentElementsDataList;
    private List<T> uniqueContent = new ArrayList<>();
    private String eachElementXpath;
    private Class<? extends BaseWidget> eachElementClass;

    public enum ScrollDirection {
        LEFT,
        RIGHT
    }

    public AndroidHorizontalScrollView(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
        androidDriver = (AndroidDriver) driver;
    }

    public AndroidHorizontalScrollView(WebDriver driver, CustomLocator locator, String eachElementXpath,
                                       Class<? extends CardWidget<T>> clazz) {
        super(driver, locator);
        this.eachElementXpath = eachElementXpath;
        androidDriver = (AndroidDriver) driver;
        eachElementClass = clazz;
    }

    public void simpleHorizontalScroll(ScrollDirection direction) {
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

    public void simpleHorizontalScrollTo(ScrollDirection direction, Element element) {
        int anchorY = this.getLocation().getY();
        int rightBorder = this.getWidth() - 150;
        int leftBorder = this.getWidth() - rightBorder;

        while (!element.isVisible()) {
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
    }

    public List<T> getFullDataList() {
        currentElementsDataList = new ArrayList<>();
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
            simpleHorizontalScroll(ScrollDirection.RIGHT);
        }

        return uniqueContent;
    }
}
