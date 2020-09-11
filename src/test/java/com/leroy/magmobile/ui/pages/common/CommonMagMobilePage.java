package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;

public class CommonMagMobilePage extends BaseAppPage {

    public CommonMagMobilePage() {
        super(false);
        //shouldNotAnyErrorVisible();
        waitForPageIsLoaded();
        //shouldNotAnyErrorVisible();
    }

    @AppFindBy(accessibilityId = "ErrorNotification")
    Element errorAlertMessage;

    protected void shouldNotAnyErrorVisible() {
        boolean errorVisible = errorAlertMessage.isVisible();
        String description = errorVisible ? "Обнаружена ошибка: " +
                errorAlertMessage.getText() : "";
        anAssert.isFalse(errorVisible, description);
    }

}
