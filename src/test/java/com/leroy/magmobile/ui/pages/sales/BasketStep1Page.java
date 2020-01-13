package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import io.qameta.allure.Step;

public class BasketStep1Page extends BasketPage {

    public BasketStep1Page(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.ImageView]")
    private SearchProductCardWidget productCard;

    @AppFindBy(text = "Услуги моего отдела")
    private MagMobSubmitButton servicesMyDepartmentArea;

    @AppFindBy(text = "ТОВАР")
    private MagMobSubmitButton addProductBtn;

    @AppFindBy(text = "ДАЛЕЕ К ПАРАМЕТРАМ")
    private MagMobSubmitButton nextParametersBtn;

    @Override
    public void waitForPageIsLoaded() {
        servicesMyDepartmentArea.waitForVisibility();
    }

    // --------- Action steps -------------------//

    @Step("Нажмите Далее к параметрам")
    public BasketStep2Page clickNextParametersButton() {
        nextParametersBtn.click();
        return new BasketStep2Page(context);
    }

    // --------- Verifications ------------------//

    @Override
    public BasketStep1Page verifyRequiredElements() {
        String titleText = screenTitle.getText();
        softAssert.isTrue(titleText.matches("Корзина № \\d{8}"),
                "Номер документа должен состоять из 8 символов");
        softAssert.isElementVisible(productCard);
        softAssert.isElementVisible(servicesMyDepartmentArea);
        softAssert.isElementVisible(addProductBtn);
        softAssert.isElementVisible(nextParametersBtn);
        softAssert.verifyAll();
        return this;
    }

}

