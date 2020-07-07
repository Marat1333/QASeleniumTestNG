package com.leroy.magmobile.ui.pages.work.supply_plan.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SearchHistoryElementData;
import org.openqa.selenium.WebDriver;

public class SearchHistoryElementWidget extends CardWidget<SearchHistoryElementData> {
    public SearchHistoryElementWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.view.ViewGroup[2]/android.widget.TextView[1]")
    Element nameLbl;

    @AppFindBy(xpath = "./android.view.ViewGroup[2]/android.widget.TextView[2]")
    Element codeLbl;

    @AppFindBy(xpath = "./android.view.ViewGroup[1]")
    Element typeImage;

    @Override
    public SearchHistoryElementData collectDataFromPage(String pageSource) {
        SearchHistoryElementData data = new SearchHistoryElementData();
        data.setCode(codeLbl.getText().replaceAll("\\D+",""));
        data.setName(nameLbl.getText());
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return nameLbl.isVisible()&&codeLbl.isVisible();
    }
}

