package com.leroy.pages.app.more;

import com.leroy.core.TestContext;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;

public class DepartmentListPage extends BaseAppPage {

    public DepartmentListPage(TestContext context) {
        super(context);
    }

    private static final String TYPICAL_DEPARTMENT_AREA_XPATH =
            "//android.widget.ScrollView//android.view.ViewGroup";

    private Element getSpecificShopAreaById(String id) {
        return new Element(driver,
                By.xpath(TYPICAL_DEPARTMENT_AREA_XPATH + String.format(
                        "[android.widget.TextView[starts-with(@text, '%s')]]", id)));
    }

    private Element getSpecificShopAreaByName(String name) {
        return new Element(driver,
                By.xpath(TYPICAL_DEPARTMENT_AREA_XPATH + String.format(
                        "[android.widget.TextView[contains(@text, '%s')]]", name)));
    }

    public UserProfilePage selectDepartmentById(String id) {
        getSpecificShopAreaById(id).click();
        return new UserProfilePage(context);
    }
}
