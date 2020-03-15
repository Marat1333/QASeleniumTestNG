package com.leroy.magmobile.ui.pages.more;

import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.models.TextViewData;
import org.openqa.selenium.By;

public class DepartmentListPage extends CommonMagMobilePage {

    public DepartmentListPage(Context context) {
        super(context);
    }

    private static final String TYPICAL_DEPARTMENT_AREA_XPATH =
            "//android.widget.ScrollView//android.view.ViewGroup";

    private AndroidScrollView<TextViewData> scrollWithDepartments =
            E("//android.widget.ScrollView", AndroidScrollView.class);

    private Element getSpecificShopAreaById(String id) throws Exception {
        scrollWithDepartments.scrollDownToText(id);
        return new Element(driver,
                By.xpath(TYPICAL_DEPARTMENT_AREA_XPATH + String.format(
                        "[android.widget.TextView[starts-with(@text, '%s')]]", id)));
    }

    private Element getSpecificShopAreaByName(String name) {
        return new Element(driver,
                By.xpath(TYPICAL_DEPARTMENT_AREA_XPATH + String.format(
                        "[android.widget.TextView[contains(@text, '%s')]]", name)));
    }

    public UserProfilePage selectDepartmentById(String id) throws Exception {
        getSpecificShopAreaById(id).click();
        return new UserProfilePage(context);
    }
}
