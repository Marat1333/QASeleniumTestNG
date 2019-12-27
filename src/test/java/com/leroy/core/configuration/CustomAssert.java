package com.leroy.core.configuration;

import com.leroy.core.testrail.helpers.StepLog;
import com.leroy.core.testrail.models.StepResultModel;
import com.leroy.core.util.ImageUtil;
import com.leroy.core.web_elements.general.Element;
import org.testng.Assert;
import java.awt.*;

public class CustomAssert {

    private StepLog stepLog;

    public CustomAssert(StepLog stepLog) {
        this.stepLog = stepLog;
    }

    public void isTrue(boolean condition, String desc) {
        if (!condition) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Полностью противоположно ожидаемому");
            stepLog.assertFail(desc);
        }
        Assert.assertTrue(condition, desc);
    }

    public void isFalse(boolean condition, String desc) {
        if (condition) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Полностью противоположно ожидаемому");
            stepLog.assertFail(desc);
        }
        Assert.assertFalse(condition, desc);
    }

    public void isEquals(Object actual, Object expected, String desc) {
        String expectedResultText;
        if (desc.contains("%s"))
            expectedResultText = String.format(desc, expected.toString());
        else
            expectedResultText = desc;
        if (!actual.equals(expected)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResultText);
            curStepRes.addActualResult(actual.toString());
            stepLog.assertFail(expectedResultText);
        }
        Assert.assertEquals(actual, expected, expectedResultText);
    }

    public void isNotEquals(Object actual, Object expected, String desc) {
        String expectedResultText;
        if (desc.contains("%s"))
            expectedResultText = String.format(desc, expected.toString());
        else
            expectedResultText = desc;
        if (actual.equals(expected)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(expectedResultText);
            curStepRes.addActualResult(actual.toString());
            stepLog.assertFail(expectedResultText);
        }
        Assert.assertNotEquals(actual, expected, expectedResultText);
    }

    public void isNull(Object object, String desc) {
        if (object != null) {
            stepLog.assertFail(desc);
        }
        Assert.assertNull(object, desc);
    }

    public void isNotNull(Object object, String desc) {
        if (object == null) {
            stepLog.assertFail(desc);
        }
        Assert.assertNotNull(object, desc);
    }

    public void isElementTextEqual(Element element, String expectedText) {
        if (isElementVisible(element)) {
            String actualText = element.getText();
            String desc2 = element.getMetaName() + String.format(" должно содержать текст **%s**", expectedText);
            if (!actualText.equals(expectedText)) {
                StepResultModel curStepRes = stepLog.getCurrentStepResult();
                curStepRes.addExpectedResult(desc2);
                stepLog.assertFail(desc2);
                curStepRes.addActualResult(element.getMetaName() + String.format(" содержит текст **%s**", actualText));
            }
            Assert.assertEquals(actualText, expectedText,
                    desc2);
        }
    }

    public boolean isElementVisible(Element element) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = element.isVisible();
        String desc = element.getMetaName() + " должен отображаться";
        if (!elementVisibility) {
            stepLog.assertFail(desc);
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult(element.getMetaName() + " **не** отображается");
            Assert.fail(desc);
            return false;
        } else
            return true;
    }

    public boolean isElementNotVisible(Element element) {
        Assert.assertNotNull(element.getMetaName(), "Element meta name is NULL!");
        boolean elementVisibility = element.isVisible();
        String desc = element.getMetaName() + " не должен отображаться";
        if (elementVisibility) {
            stepLog.assertFail(desc);
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult(element.getMetaName() + " отображается");
            Assert.fail(desc);
            return true;
        } else
            return false;
    }

    public void isElementImageMatches(Element elem, String pictureName) {
        ImageUtil.CompareResult result = null;
        String desc = "Визуально элемент '"+elem.getMetaName()+"' должен соответствовать эталону";
        try {
            //comment after first use
            //ImageUtil.takeScreenShot(elem, pictureName);
            result = ImageUtil.takeScreenAndCompareWithBaseImg(elem, pictureName);
        } catch (Exception err) {
            Log.error(err.getMessage());
            Assert.fail("Couldn't take screenshot for " + elem.getMetaName());
        }
        if (!ImageUtil.CompareResult.Matched.equals(result)) {
            StepResultModel curStepRes = stepLog.getCurrentStepResult();
            curStepRes.addExpectedResult(desc);
            curStepRes.addActualResult("Визуально элемент '"+elem.getMetaName()+"' не соответствует эталону");
            stepLog.assertFail(desc);
        }
        Assert.assertEquals(result,
                ImageUtil.CompareResult.Matched,
                desc);
    }

    public void isOvalCheckBoxEnabled(Element element, Color expectedColor) throws Exception{
        Color actualColor = ImageUtil.getPointColor(element);
        Assert.assertTrue(similarTo(actualColor,expectedColor));
    }

    protected boolean similarTo(Color actualColor, Color expectedColor){
        double limit = 20;
        double distance = Math.pow(actualColor.getRed() - expectedColor.getRed(),2)
                + Math.pow(actualColor.getGreen() - expectedColor.getGreen(),2)
                + Math.pow(actualColor.getBlue() - expectedColor.getBlue(),2);
        if(distance < limit){
            return true;
        }else{
            return false;
        }
    }

}
