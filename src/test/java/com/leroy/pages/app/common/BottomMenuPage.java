package com.leroy.pages.app.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.MorePage;
import com.leroy.pages.app.WorkPage;
import org.openqa.selenium.WebDriver;

public class BottomMenuPage extends BaseWebPage {

    public BottomMenuPage(WebDriver driver) {
        super(driver);
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

    public WorkPage goToWork() {
        workBtn.click();
        WorkPage workPage = new WorkPage(driver);
        workPage.titleObj.waitForVisibility();
        return workPage;
    }

    public void goToSupport() {
        supportBtn.click();
    }

    public MorePage goToMoreSection() {
        moreBtn.click();
        return new MorePage(driver);
    }

}
