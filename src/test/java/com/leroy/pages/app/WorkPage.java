package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class WorkPage extends BaseAppPage {

    public static final String TITLE = "Ежедневная работа";

    public WorkPage(WebDriver driver) {
        super(driver);
    }

    private static final String XPATH_WITHDRAWAL_FROM_RM_AREA =
            "//android.widget.ScrollView//android.view.ViewGroup[android.widget.TextView[@text='Отзыв с RM']]";

    @AppFindBy(accessibilityId = "ScreenTitle")
    public Element titleObj;

    @AppFindBy(xpath = XPATH_WITHDRAWAL_FROM_RM_AREA, metaName = "'Отзыв с RM' область")
    private Element withdrawalFromRMArea;

    @AppFindBy(xpath = XPATH_WITHDRAWAL_FROM_RM_AREA + "/android.widget.TextView",
            metaName = "'Отзыв с RM' метка")
    public Element withdrawalFromRMLabel;

    @AppFindBy(xpath = XPATH_WITHDRAWAL_FROM_RM_AREA +
            "//android.view.ViewGroup[@content-desc='lmui-Icon']/android.view.ViewGroup",
            metaName = "'Отзыв с RM' плюсик")
    public Element withdrawalFromRMPlusIcon;

    public StockProductsPage clickWithdrawalFromRMPlusIcon() {
        withdrawalFromRMPlusIcon.click();
        return new StockProductsPage(driver);
    }

}
