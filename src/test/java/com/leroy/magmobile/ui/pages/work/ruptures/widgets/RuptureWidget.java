package com.leroy.magmobile.ui.pages.work.ruptures.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import org.openqa.selenium.WebDriver;

public class RuptureWidget extends CardWidget<RuptureData> {
    @AppFindBy(xpath = ".//android.widget.ImageView")
    Element productPhoto;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='lmCode']")
    Element lmCodeLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='barCode']")
    Element barCodeLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView")
    Element titleLbl;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='Button-container']", clazz = MagMobCheckBox.class)
    ElementList<MagMobCheckBox> taskCheckBoxes;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='Button-container']/following-sibling::android.widget.TextView[1]")
    ElementList<Element> tasksLbl;

    public RuptureWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public RuptureData collectDataFromPage(String pageSource) {
        return null;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return false;
    }
}
