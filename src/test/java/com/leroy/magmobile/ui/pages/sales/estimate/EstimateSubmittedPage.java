package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import io.qameta.allure.Step;

public class EstimateSubmittedPage extends CommonMagMobilePage {

    public EstimateSubmittedPage(TestContext context) {
        super(context);
    }

    @AppFindBy(containsText = "Смета создана")
    Element headerLbl;

    @AppFindBy(text = "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ")
    Element submitBtn;

    // ACTIONS

    @Step("Нажмите кнопку 'Перейти в список документов'")
    public SalesDocumentsPage clickSubmitButton() {
        submitBtn.click();
        return new SalesDocumentsPage(context);
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Смета создана...' отображается корректно")
    public EstimateSubmittedPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, submitBtn);
        softAssert.verifyAll();
        return this;
    }

}
