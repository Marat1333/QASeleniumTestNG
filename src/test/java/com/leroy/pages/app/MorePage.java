package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class MorePage extends BaseAppPage {

    public MorePage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(accessibilityId = "UserProfile")
    public Element userProfileArea;

    public UserProfilePage goToUserProfile() {
        userProfileArea.click();
        return new UserProfilePage(driver);
    }

}
