package com.leroy.magmobile.ui.pages.sales.orders;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public abstract class CartOrderEstimatePage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackButton")
    Element backBtn;

    // Actions

    @Step("Нажмите кнопку назад")
    public void clickBack() {
        backBtn.click();
    }
}
