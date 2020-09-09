package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.constants.MagMobElementTypes;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.SessionsListPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.SuppliesListPage;
import com.leroy.magmobile.ui.pages.work.transfer.TransferOrderStep1Page;
import com.leroy.magmobile.ui.pages.work.transfer.TransferRequestsPage;
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
        departmentSupplyPlanLbl.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать на иконку + рядом с Отзыв с RM")
    public <T> T clickWithdrawalFromRMPlusIcon() {
        withdrawalFromRMPlusIcon.click();
        if (ContextProvider.getContext().isNewShopFunctionality())
            return (T) new TransferOrderStep1Page();
        else
            return (T) new StockProductsPage();
    }

    @Step("Перейти в 'Отзыв товаров со склада'")
    public TransferRequestsPage goToTransferProductFromStock() {
        withdrawalFromRMArea.click();
        return new TransferRequestsPage();
    }

    @Step("Перейти в План поставок")
    public SuppliesListPage goToShipmentListPage() {
        departmentSupplyPlanLbl.click();
        return new SuppliesListPage();
    }

    @Step("Перейти в Печать ценников")
    public SessionsListPage goToSessionsListPage() {
        priceTagPrintingLbl.click();
        return new SessionsListPage();
    }


    // ------------------------ Verifications ------------------------//

    @Step("Проверить, что страница 'Ежедневная работа' отображается корректно")
    public WorkPage verifyRequiredElements() {
        softAssert.isElementTextEqual(titleObj, "Ежедневная работа");
        if (ContextProvider.getContext().isNewShopFunctionality())
            softAssert.isElementTextEqual(withdrawalFromRMLabel, "Отзыв товаров со склада");
        else
            softAssert.isElementTextEqual(withdrawalFromRMLabel, "Отзыв с RM");
        softAssert.isElementImageMatches(withdrawalFromRMPlusIcon,
                MagMobElementTypes.CIRCLE_PLUS.getPictureName());
        softAssert.isElementVisible(departmentSupplyPlanLbl);
        softAssert.isElementVisible(withdrawalAndDisposalLbl);
        softAssert.isElementVisible(priceTagPrintingLbl);
        softAssert.isElementVisible(orderBalisageLbl);
        softAssert.isElementVisible(miniInventoryLbl);
        softAssert.verifyAll();
        return this;
    }

}
