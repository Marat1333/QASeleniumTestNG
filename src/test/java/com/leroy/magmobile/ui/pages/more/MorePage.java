package com.leroy.magmobile.ui.pages.more;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;

public class MorePage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "UserProfile")
    public Element userProfileArea;

    public UserProfilePage goToUserProfile() {
        userProfileArea.click();
        return new UserProfilePage();
    }

}
