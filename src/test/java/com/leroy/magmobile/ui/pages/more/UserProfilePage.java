package com.leroy.magmobile.ui.pages.more;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;

public class UserProfilePage extends BottomMenuPage {

    private static final String TYPICAL_OPTION_XPATH = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup";

    public UserProfilePage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = TYPICAL_OPTION_XPATH + "[android.widget.TextView[@text='Магазин']]")
    private Element shopArea;

    @AppFindBy(xpath = TYPICAL_OPTION_XPATH + "[android.widget.TextView[@text='Отдел']]")
    private Element departmentArea;

    public SearchShopPage goToEditShopForm() {
        shopArea.click();
        return new SearchShopPage(context);
    }

    public DepartmentListPage goToEditDepartmentForm() {
        departmentArea.click();
        return new DepartmentListPage(context);
    }

}
