package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.constants.MagMobElementTypes;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.SessionsListPage;
import com.leroy.magmobile.ui.pages.work.ruptures.RupturesScannerPage;
import com.leroy.magmobile.ui.pages.work.ruptures.SessionListPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.SuppliesListPage;
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

    @AppFindBy(text = "Управление перебоями")
    private Element rupturesManageLbl;

    @AppFindBy(xpath = "//*[@text='Управление перебоями']/following-sibling::*/android.widget.TextView")
    private Element rupturesActiveSessionCounterLbl;

    @AppFindBy(xpath = "//*[@text='Управление перебоями']/following-sibling::*[2]")
    private Button createRuptureSessionBtn;

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
        if (rupturesManageLbl.isVisible()){
            rupturesActiveSessionCounterLbl.waitForVisibility();
        }
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать на иконку + рядом с Отзыв с RM")
    public StockProductsPage clickWithdrawalFromRMPlusIcon() {
        withdrawalFromRMPlusIcon.click();
        return new StockProductsPage();
    }

    @Step("Перейти в Управление перебоями")
    public SessionListPage goToRuptures(){
        rupturesManageLbl.click();
        return new SessionListPage();
    }

    @Step("Нажать на кнопку создания сессии перебоев")
    public RupturesScannerPage createRupturesSession(){
        createRuptureSessionBtn.click();
        return new RupturesScannerPage();
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
        softAssert.isElementVisible(rupturesManageLbl);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отсутствует раздел \"Управление перебоями\"")
    public WorkPage shouldRupturesNavigationBtnHasCorrectCondition(boolean isVisible){
        if (isVisible) {
            anAssert.isElementVisible(rupturesManageLbl);
        }else {
            anAssert.isElementNotVisible(rupturesManageLbl);
        }
        return this;
    }

    @Step("Проверить, что счетчик активных сессий руптюр отображает корректное значение")
    public WorkPage shouldRupturesSessionCounterIsCorrect(int sessionCounter){
        anAssert.isElementTextEqual(rupturesActiveSessionCounterLbl, String.valueOf(sessionCounter));
        return this;
    }

}
