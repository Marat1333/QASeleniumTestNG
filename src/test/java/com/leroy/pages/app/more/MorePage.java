package com.leroy.pages.app.more;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;

public class MorePage extends BaseAppPage {

    public MorePage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "UserProfile")
    public Element userProfileArea;

    public UserProfilePage goToUserProfile() {
        userProfileArea.click();
        return new UserProfilePage(context);
    }

}
