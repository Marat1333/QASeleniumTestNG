package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.more.MorePage;
import com.leroy.magmobile.ui.pages.more.UserProfilePage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import io.qameta.allure.Step;

public class BottomMenuPage extends BaseAppPage {

    public BottomMenuPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "Route__btn_cart")
    private Element salesBtn;

    @AppFindBy(accessibilityId = "Route__btn_work")
    private Element workBtn;

    @AppFindBy(accessibilityId = "Route__btn_support")
    private Element supportBtn;

    @AppFindBy(accessibilityId = "Route__btn_more")
    private Element moreBtn;

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Перейдите в раздел 'Продажи'")
    public SalesPage goToSales() {
        salesBtn.click();
        return new SalesPage(context);
    }

    @Step("Перейдите в раздел 'Работа'")
    public WorkPage goToWork() {
        workBtn.click();
        return new WorkPage(context);
    }

    @Step("Перейдите в раздел 'Поддержка'")
    public SupportPage goToSupport() {
        supportBtn.click();
        return new SupportPage(context);
    }

    @Step("Перейдите в раздел 'Еще'")
    public MorePage goToMoreSection() {
        moreBtn.click();
        return new MorePage(context);
    }

    @Step("Установить магазин {shop} и отдел {department} для пользователя")
    public UserProfilePage setShopAndDepartmentForUser(String shop, String department) {
        return goToMoreSection().goToUserProfile().goToEditShopForm().searchForShopAndSelectById(shop)
                .goToEditDepartmentForm().selectDepartmentById(department);
    }

}
