package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobGrayCheckBox;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class RuptureWidget extends CardWidget<RuptureData> {
    @AppFindBy(xpath = ".//android.widget.ImageView")
    Element productPhoto;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='lmCode']")
    Element lmCodeLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='barCode']")
    Element barCodeLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView")
    Element titleLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='Button-container']//android.view.ViewGroup[@content-desc='lmui-Icon']",
            clazz = MagMobGrayCheckBox.class)
    ElementList<MagMobGrayCheckBox> taskCheckBoxes;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='Button-container']//android.view.ViewGroup[@content-desc='lmui-Icon']/following-sibling::android.widget.TextView[1]")
    ElementList<Element> tasksLbl;

    public RuptureWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public RuptureData collectDataFromPage(String pageSource) {
        Map<String, Boolean> actionsMap = new HashMap<>();
        RuptureData data = new RuptureData();
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCodeLbl.getText(pageSource)));
        data.setBarCode(ParserUtil.strWithOnlyDigits(barCodeLbl.getText(pageSource)));
        data.setTitle(titleLbl.getText(pageSource));
        AndroidScrollView<String> actionsScrollView = new AndroidScrollView<>(driver, By.xpath(this.getXpath()));
        actionsScrollView.setSwipeDeadZonePercentage(50);
        int sizeCounter = 0;
        while (true) {
            for (int i = 0; i < taskCheckBoxes.getCount(); i++) {
                try {
                    actionsMap.put(tasksLbl.get(i).getText(pageSource), taskCheckBoxes.get(i).isChecked());
                } catch (Exception e) {
                    Log.warn(e.getMessage());
                }

            }
            if (sizeCounter == actionsMap.size()) {
                break;
            }
            sizeCounter = actionsMap.size();
        }
        data.setActions(actionsMap);
        return data;
    }

    @Override
    public RuptureData collectShortDataFromPage(String pageSource) {
        RuptureData data = new RuptureData();
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCodeLbl.getText(pageSource)));
        data.setBarCode(ParserUtil.strWithOnlyDigits(barCodeLbl.getText(pageSource)));
        data.setTitle(titleLbl.getText(pageSource));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCodeLbl.isVisible(pageSource) && titleLbl.isVisible(pageSource);
    }
}
