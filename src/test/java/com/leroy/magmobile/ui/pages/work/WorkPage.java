package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.constants.MagMobElementTypes;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class WorkPage extends CommonMagMobilePage {

    private static final String XPATH_WITHDRAWAL_FROM_RM_AREA =
            "//android.widget.ScrollView//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Отзыв')]]";

    @AppFindBy(accessibilityId = "ScreenTitle")
    private Element titleObj;

    @AppFindBy(xpath = XPATH_WITHDRAWAL_FROM_RM_AREA, metaName = "'Отзыв с RM' область")
    private Element withdrawalFromRMArea;

    @AppFindBy(xpath = XPATH_WITHDRAWAL_FROM_RM_AREA + "/android.widget.TextView",
            metaName = "'Отзыв с RM' метка")
    private Element withdrawalFromRMLabel;

    @AppFindBy(xpath = XPATH_WITHDRAWAL_FROM_RM_AREA +
            "//android.view.ViewGroup[@content-desc='lmui-Icon']/android.view.ViewGroup",
            metaName = "'Отзыв с RM' плюсик")
    private Element withdrawalFromRMPlusIcon;

    @AppFindBy(text = "План поставок в отдел")
    private Element departmentSupplyPlanLbl;

    @AppFindBy(text = "Изъятие и списание")
    private Element withdrawalAndDisposalLbl;

    @AppFindBy(text = "Печать ценников")
    private Element priceTagPrintingLbl;

    @AppFindBy(text = "Заказ бализаж")
    private Element orderBalisageLbl;

    @AppFindBy(text = "Мини-инвентаризация")
    private Element miniInventoryLbl;

    @Override
    public void waitForPageIsLoaded() {
        titleObj.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать на иконку + рядом с Отзыв с RM")
    public StockProductsPage clickWithdrawalFromRMPlusIcon() {
        withdrawalFromRMPlusIcon.click();
        return new StockProductsPage();
    }

    // ------------------------ Verifications ------------------------//

    @Step("Проверить, что страница 'Ежедневная работа' отображается корректно")
    public WorkPage verifyRequiredElements() {
        softAssert.isElementTextEqual(titleObj, "Ежедневная работа");
        softAssert.isElementTextEqual(withdrawalFromRMLabel, "Отзыв с RM");
        softAssert.isElementImageMatches(withdrawalFromRMPlusIcon,
                MagMobElementTypes.CIRCLE_PLUS.getPictureName());
        softAssert.isElementVisible(departmentSupplyPlanLbl);
        softAssert.isElementVisible(withdrawalAndDisposalLbl);
        //softAssert.isElementVisible(priceTagPrintingLbl); - Отображается только на определенных магазинах (например, 20)
        softAssert.isElementVisible(orderBalisageLbl);
        softAssert.isElementVisible(miniInventoryLbl);
        softAssert.verifyAll();
        return this;
    }

}
