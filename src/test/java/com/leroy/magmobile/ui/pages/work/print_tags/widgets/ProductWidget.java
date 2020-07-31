package com.leroy.magmobile.ui.pages.work.print_tags.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ProductWidget extends CardWidget<ProductTagData> {
    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc=\"lmCode\"]")
    Element lmCode;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc=\"barCode\"]")
    Element barCode;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::*[1]")
    Element title;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::android.view.ViewGroup[1]")
    Element anchor;

    @AppFindBy(accessibilityId = "count46")
    Element smallCountLbl;

    @AppFindBy(accessibilityId = "count610")
    Element middleCountLbl;

    @AppFindBy(accessibilityId = "count1018")
    Element bigCountLbl;

    public ProductWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public ProductTagData collectDataFromPage(String pageSource) {
        ProductTagData data = new ProductTagData();
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText()));
        if (smallCountLbl.isVisible()) {
            data.setSmallSizeCount(Integer.parseInt(ParserUtil.strWithOnlyDigits(smallCountLbl.getText())));
        } else {
            data.setSmallSizeCount(0);
        }
        if (middleCountLbl.isVisible()) {
            data.setMiddleSizeCount(Integer.parseInt(ParserUtil.strWithOnlyDigits(middleCountLbl.getText())));
        } else {
            data.setMiddleSizeCount(0);
        }
        if (bigCountLbl.isVisible()) {
            data.setBigSizeCount(Integer.parseInt(ParserUtil.strWithOnlyDigits(bigCountLbl.getText())));
        } else {
            data.setBigSizeCount(0);
        }
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible() && (smallCountLbl.isVisible() || middleCountLbl.isVisible() || bigCountLbl.isVisible());
    }
}
