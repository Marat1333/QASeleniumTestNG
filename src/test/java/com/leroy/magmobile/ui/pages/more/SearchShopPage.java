package com.leroy.magmobile.ui.pages.more;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SearchShopPage extends BaseAppPage {

    public SearchShopPage(TestContext context) {
        super(context);
    }

    private static final String TYPICAL_SHOP_AREA_XPATH =
            "//android.view.ViewGroup[@content-desc='ScreenContent']/android.widget.ScrollView//android.view.ViewGroup/android.widget.TextView[@index='%s']";

    @AppFindBy(accessibilityId = "ScreenTitle-EditShop")
    private EditBox searchFld;

    @AppFindBy(xpath = "(//*[@content-desc='Button'])[2]")
    private Element confirmBtn;

    private Element getSpecificShopAreaById(String id) {
        return new Element(driver,
                By.xpath(String.format(TYPICAL_SHOP_AREA_XPATH + "[@text='%s']", "0", id)));
    }

    private Element getSpecificShopAreaByName(String name) {
        return new Element(driver,
                By.xpath(String.format(TYPICAL_SHOP_AREA_XPATH + "[@text='%s']", "1", name)));
    }

    @Step("Найдите и выберете магазин с номером {id}")
    public UserProfilePage searchForShopAndSelectById(String id) {
        searchFld.clearFillAndSubmit(id);
        E(String.format(TYPICAL_SHOP_AREA_XPATH, "0")).waitUntilTextIsEqualTo(id);
        getSpecificShopAreaById(id).doubleClick();
        confirmBtn.click();
        return new UserProfilePage(context);
    }

}
