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
import java.io.IOException;
import java.util.stream.IntStream;

public class ImageUtil {

    private static String PATH_SNAPSHOTS = "src/main/resources/snapshots/" + DriverFactory.BROWSER_PROFILE + "/";

    private static String getScreenPath(WebDriver driver, String pictureName) {
        Dimension dimension = driver.manage().window().getSize();
        String sDimension = dimension.getHeight() + "x" + dimension.getWidth();
        return PATH_SNAPSHOTS + sDimension + "/" + pictureName + ".png";
    }

    public enum CompareResult {
        Matched, SizeMismatch, PixelMismatch, ElementNotFound;
    }

    /**
     * Take screenshot
     *
     * @param driver - webdriver
     * @param rect   - The area to be taken
     * @return - file with image
     * @throws Exception -
     */
    public static File captureRectangleBitmap(WebDriver driver, Rectangle rect) throws IOException {
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
    private static CompareResult compareImageOldWay(String actualFile, String baseFile, Double expectedPercentage) {
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

    /**
     * New Method for comparing Images. It can compare images with different size.
     *
     * @param actualFile         - file with actual image
     * @param baseFile           - file with expected image
     * @param expectedPercentage - expected pixel match percentage
     * @return - CompareResult
     */
    public static CompareResult compareImage(String actualFile, String baseFile, Double expectedPercentage)
            throws Exception {
        BufferedImage actualImage = ImageIO.read(new File(actualFile));
        BufferedImage baseImage = ImageIO.read(new File(baseFile));
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
        double avgBaseImg = IntStream.of(baseImageData).filter(i -> Math.abs(i) != 1).average().getAsDouble();
        double avgActualImg = IntStream.of(actualImageData).filter(i -> Math.abs(i) != 1).average().getAsDouble();
        long diff = Math.round(Math.abs(avgActualImg - avgBaseImg));
        double actualPercentage = (1 - diff / 16777215.0) * 100; // 16777215 = #FFFFFF
        Log.info("Difference color is " + diff + ". Percentage pixel match: " + actualPercentage + "%");
        if (actualPercentage >= expectedPercentage)
            return CompareResult.Matched;
        return CompareResult.PixelMismatch;
    }

    // Another one method for comparing images (need to check)
    private static double getDifferencePercentForImagesWithTheSameDimension(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * width * height;

        return 100.0 * diff / maxDiff;
    }

    private static int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = rgb1 & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = rgb2 & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    // -------------- Public methods -------------------

    /**
     * Method for making sample snapshots
     */
    public static void takeScreenShot(WebDriver driver, Rectangle rect, String pictureName)
            throws Exception {
        File file = captureRectangleBitmap(driver, rect);
        FileUtils.copyFile(file, new File(getScreenPath(driver, pictureName)));
    }

    public static void takeScreenShot(Element element, String pictureName)
            throws Exception {
        takeScreenShot(element.getDriver(), element.getRectangle(), pictureName);
    }

    private static CompareResult takeScreenAndCompareWithBaseImg(
            WebDriver driver, Rectangle rect,
            String pictureName, Double expectedPercentage) throws Exception {
        String filePath = getScreenPath(driver, pictureName);
        String screenshotPath = captureRectangleBitmap(driver, rect).getAbsolutePath();
        Log.debug("Picture: " + pictureName + " Path: " + screenshotPath);
        return compareImage(screenshotPath,
                filePath, expectedPercentage);
    }

    public static CompareResult takeScreenAndCompareWithBaseImg(
            Element elem, String pictureName, Double expectedPercentage) throws Exception {
        return takeScreenAndCompareWithBaseImg(
                elem.getDriver(), elem.getRectangle(), pictureName, expectedPercentage);
    }

    public static Color getColor(Element element, int xOffset, int yOffset) throws Exception {
        // Find center of Element with offset
        Point centerElemPoint = ((MobileElement) element.getWebElement()).getCenter();
        centerElemPoint.x += xOffset;
        centerElemPoint.y += yOffset;
        Rectangle pointForGettingColor = new Rectangle(centerElemPoint, new Dimension(1, 1));
        // Get Image and define color of the center
        String currentScreenPath = captureRectangleBitmap(element.getDriver(), pointForGettingColor)
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
