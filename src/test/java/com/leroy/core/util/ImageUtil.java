package com.leroy.core.util;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Element;
import io.appium.java_client.MobileElement;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;

public class ImageUtil {

    private static String PATH_SNAPSHOTS = "src\\main\\resources\\snapshots\\" + DriverFactory.BROWSER_PROFILE + "\\";

    public enum CompareResult {
        Matched, SizeMismatch, PixelMismatch
    }

    /**
     * Get rectangle for the screen area
     */
    private static Rectangle getScreenRectangle(Element element1, Element element2, Delta delta) {
        if (element2 != null) {
            Point pointElem1 = element1.getLocation();
            Point pointElem2 = element2.getLocation();
            Dimension sizeElem1 = element1.getSize();
            Dimension sizeElem2 = element2.getSize();
            return new Rectangle(pointElem1.getX() - delta.getLeft(), pointElem1.getY() - delta.getTop(),
                    (pointElem2.getY() + sizeElem2.getHeight()) - pointElem1.getY() +
                            delta.getBottom() + delta.getTop(),
                    (pointElem2.getX() + sizeElem1.getWidth()) - pointElem1.getX() +
                            delta.getRight() + delta.getLeft());
        } else
            return element1.getRectangle();
    }

    /**
     * Take screenshot
     *
     * @param driver - webdriver
     * @param rect   - The area to be taken
     * @return - file with image
     * @throws Exception -
     */
    private static File captureElementBitmap(WebDriver driver, Rectangle rect) throws Exception {
        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(screen);
        BufferedImage dest = img.getSubimage(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        ImageIO.write(dest, "png", screen);
        return screen;
    }

    /**
     * Compare the actual image with base image
     *
     * @param actualFile         - file with actual image
     * @param baseFile           - file with expected image
     * @param expectedPercentage - expected pixel match percentage
     * @return - CompareResult
     */
    private static CompareResult compareImage(String actualFile, String baseFile, Double expectedPercentage) throws Exception {
        CompareResult compareResult = CompareResult.PixelMismatch;
        Image baseImage = Toolkit.getDefaultToolkit().getImage(baseFile);
        Image actualImage = Toolkit.getDefaultToolkit().getImage(actualFile);
        try {
            PixelGrabber baseImageGrab = new PixelGrabber(baseImage, 0, 0, -1, -1, false);
            PixelGrabber actualImageGrab = new PixelGrabber(actualImage, 0, 0, -1, -1, false);
            int[] baseImageData = null;
            int[] actualImageData = null;
            if (baseImageGrab.grabPixels()) {
                baseImageData = (int[]) baseImageGrab.getPixels();
            }
            if (actualImageGrab.grabPixels()) {
                actualImageData = (int[]) actualImageGrab.getPixels();
            }
            if (baseImageGrab.getHeight() != actualImageGrab.getHeight() ||
                    baseImageGrab.getWidth() != actualImageGrab.getWidth()) {
                compareResult = CompareResult.SizeMismatch;
                Log.error("Height: " + baseImageGrab.getHeight() + "<>" +
                        actualImageGrab.getHeight());
                Log.error("Width: " + baseImageGrab.getWidth() + "<>" +
                        actualImageGrab.getWidth());
            } else {
                int pixelsCount = baseImageData.length;
                int wrongPixelsCount = 0;
                for (int i = 0; i < pixelsCount; i++) {
                    if (baseImageData[i] != actualImageData[i])
                        wrongPixelsCount++;
                }
                double actualPercent = (pixelsCount - wrongPixelsCount) / Double.valueOf(pixelsCount) * 100;
                if (actualPercent >= expectedPercentage) {
                    compareResult = CompareResult.Matched;
                }
                Log.info("Percentage pixel match: " + actualPercent + "%");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compareResult;
    }

    // -------------- Public methods -------------------

    /**
     * Method for making sample snapshots
     */
    public static void takeScreenShot(WebDriver driver, Rectangle rect, String pictureName)
            throws Exception {
        File file = captureElementBitmap(driver, rect);
        FileUtils.copyFile(file, new File(PATH_SNAPSHOTS + pictureName + ".png"));
    }

    public static void takeScreenShot(Element element, String pictureName)
            throws Exception {
        takeScreenShot(element.getDriver(), element.getRectangle(), pictureName);
    }

    private static CompareResult takeScreenAndCompareWithBaseImg(
            WebDriver driver, Rectangle rect,
            String pictureName, Double expectedPercentage) throws Exception {
        String filePath = PATH_SNAPSHOTS + pictureName + ".png";
        return compareImage(captureElementBitmap(driver, rect).getAbsolutePath(),
                filePath, expectedPercentage);
    }

    public static CompareResult takeScreenAndCompareWithBaseImg(
            Element elem, String pictureName, Double expectedPercentage) throws Exception {
        return takeScreenAndCompareWithBaseImg(
                elem.getDriver(), elem.getRectangle(), pictureName, expectedPercentage);
    }

    public static Color getColor(Element element, int xOffset, int yOffset) throws Exception {
        // Find center of Element with offset
        Point centerElemPoint = ((MobileElement)element.getWebElement()).getCenter();
        centerElemPoint.x += xOffset;
        centerElemPoint.y += yOffset;
        Rectangle pointForGettingColor = new Rectangle(centerElemPoint, new Dimension(1,1));
        // Get Image and define color of the center
        String currentScreenPath = captureElementBitmap(element.getDriver(), pointForGettingColor)
                .getAbsolutePath();
        Image actualImage = Toolkit.getDefaultToolkit().getImage(currentScreenPath);
        PixelGrabber actualImageGrab = new PixelGrabber(actualImage, 0, 0, -1, -1, false);
        int[] actualImageData = null;
        if (actualImageGrab.grabPixels()) {
            actualImageData = (int[]) actualImageGrab.getPixels();
        }
        if (actualImageData == null)
            return null;
        else
            return new Color(actualImageData[0]);
    }

    public static CompareResult takeScreenAndCompareWithBaseImg(
            Element elem, String pictureName) throws Exception {
        return takeScreenAndCompareWithBaseImg(
                elem, pictureName, 99.0);
    }

}
