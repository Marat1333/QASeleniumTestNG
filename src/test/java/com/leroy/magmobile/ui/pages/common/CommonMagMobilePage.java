package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;

public class CommonMagMobilePage extends BaseAppPage {

    public CommonMagMobilePage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Bad Request') or contains(@text, 'rror')]")
    Element errorAlertMessage;

    protected void shouldNotAnyErrorVisible() {
        boolean errorVisible = errorAlertMessage.isVisible();
        String description = errorVisible? "Обнаружена ошибка: " + errorAlertMessage.getText() : "";
        anAssert.isFalse(errorVisible, description);
    }

}
