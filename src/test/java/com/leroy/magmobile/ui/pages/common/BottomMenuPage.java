package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.more.MorePage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import io.qameta.allure.Step;

public class BottomMenuPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "Route__btn_cart")
    private Element salesBtn;

    @AppFindBy(accessibilityId = "Route__btn_work")
    private Element workBtn;

    @AppFindBy(accessibilityId = "Route__btn_support")
    private Element supportBtn;

    @AppFindBy(accessibilityId = "Route__btn_more")
    private Element moreBtn;

    @Override
    protected void waitForPageIsLoaded() {
        salesBtn.waitForVisibility();
        workBtn.waitForVisibility();
        supportBtn.waitForVisibility();
        moreBtn.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Перейдите в раздел 'Продажи'")
    public MainProductAndServicesPage goToSales() {
        salesBtn.click();
        return new MainProductAndServicesPage();
    }

    @Step("Перейдите в раздел 'Работа'")
    public WorkPage goToWork() {
        workBtn.click();
        return new WorkPage();
    }

    @Step("Перейдите в раздел 'Поддержка'")
    public SupportPage goToSupport() {
        supportBtn.click();
        return new SupportPage();
    }

    @Step("Перейдите в раздел 'Еще'")
    public MorePage goToMoreSection() {
        moreBtn.click();
        return new MorePage();
    }

}
