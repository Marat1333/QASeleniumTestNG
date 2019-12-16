package com.leroy.pages.app.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class WorkPage extends BaseAppPage {

    public WorkPage(TestContext context) {
        super(context);
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

    @Step("Нажать на иконку + рядом с Отзыв с RM")
    public StockProductsPage clickWithdrawalFromRMPlusIcon() {
        withdrawalFromRMPlusIcon.click();
        return new StockProductsPage(context);
    }

    @Step("Проверьте видимость всех элементов на странице 'Ежедневная работа'")
    public WorkPage verifyVisibilityOfAllElements() {
        softAssert.isElementTextEqual(titleObj, "Ежедневная работа");
        softAssert.isElementTextEqual(withdrawalFromRMLabel, "Отзыв с RM");
        softAssert.isElementVisible(withdrawalFromRMPlusIcon);
        // TODO there are more elements
        softAssert.verifyAll();
        return this;
    }

}
