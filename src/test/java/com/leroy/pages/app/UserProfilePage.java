package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.common.BottomMenuPage;
import org.openqa.selenium.WebDriver;

public class UserProfilePage extends BottomMenuPage {

    private static final String TYPICAL_OPTION_XPATH = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup";

    public UserProfilePage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(xpath = TYPICAL_OPTION_XPATH + "[android.widget.TextView[@text='Магазин']]")
    private Element shopArea;

    @AppFindBy(xpath = TYPICAL_OPTION_XPATH + "[android.widget.TextView[@text='Отдел']]")
    private Element departmentArea;

    public SearchShopPage goToEditShopForm() {
        shopArea.click();
        return new SearchShopPage(driver);
    }

    public DepartmentListPage goToEditDepartmentForm() {
        departmentArea.click();
        return new DepartmentListPage(driver);
    }

}
