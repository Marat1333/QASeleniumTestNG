package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.more.MorePage;
import com.leroy.pages.app.more.UserProfilePage;
import com.leroy.pages.app.work.WorkPage;
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

    public void goToSales() {
        salesBtn.click();
    }

    @Step("Перейдите в раздел 'Работа'")
    public WorkPage goToWork() {
        workBtn.click();
        WorkPage workPage = new WorkPage(context);
        workPage.titleObj.waitForVisibility();
        return workPage;
    }

    @Step("Перейдите в раздел 'Поддержка'")
    public void goToSupport() {
        supportBtn.click();
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
