package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SearchShopPage extends BaseAppPage {

    public SearchShopPage(WebDriver driver) {
        super(driver);
    }

    private static final String TYPICAL_SHOP_AREA_XPATH =
            "//android.view.ViewGroup[@content-desc='ScreenContent']/android.widget.ScrollView//android.view.ViewGroup[android.widget.TextView[@index='%s' and @text='%s']]";

    @AppFindBy(accessibilityId = "ScreenTitle-EditShop")
    private EditBox searchFld;

    @AppFindBy(xpath = "(//*[@content-desc='Button'])[2]")
    private EditBox confirmBtn;

    private Element getSpecificShopAreaById(String id) {
        return new Element(driver,
                By.xpath(String.format(TYPICAL_SHOP_AREA_XPATH, "0", id)));
    }

    private Element getSpecificShopAreaByName(String name) {
        return new Element(driver,
                By.xpath(String.format(TYPICAL_SHOP_AREA_XPATH, "1", name)));
    }

    private void fillInSearchField(String text) {
        searchFld.clearAndFill(text);
    }

    public UserProfilePage searchForShopAndSelectById(String id) {
        fillInSearchField(id);
        getSpecificShopAreaById(id).doubleClick();
        confirmBtn.click();
        return new UserProfilePage(driver);
    }

}
