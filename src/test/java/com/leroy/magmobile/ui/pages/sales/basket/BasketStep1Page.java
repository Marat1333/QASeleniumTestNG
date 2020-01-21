package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
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
    private MagMobButton servicesMyDepartmentArea;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(text = "ТОВАР")
    private MagMobButton addProductBtn;

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
    @Step("Убедиться, что мы находимся на странице Корзина - Шаг 1, и все необходимые элементы отражаются корректно")
    public BasketStep1Page verifyRequiredElements() {
        super.verifyRequiredElements();
        softAssert.isElementVisible(productCard);
        softAssert.isElementVisible(servicesMyDepartmentArea);
        softAssert.isElementVisible(totalPriceLbl);
        softAssert.isElementVisible(totalPriceVal);
        softAssert.isElementVisible(addProductBtn);
        softAssert.isElementVisible(nextParametersBtn);
        softAssert.verifyAll();
        return this;
    }

}

