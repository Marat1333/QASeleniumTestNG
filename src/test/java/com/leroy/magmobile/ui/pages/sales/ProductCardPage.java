package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ReviewsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SpecificationsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalPage;
import io.qameta.allure.Step;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "Tabs")
    protected Element mainArea;

    @AppFindBy(accessibilityId = "BackCloseModal")
    protected Element returnBackBtn;

    @AppFindBy(text = "ДЕЙСТВИЯ С ТОВАРОМ")
    protected MagMobSubmitButton actionWithProductBtn;

    @Override
    public void waitForPageIsLoaded() {
        mainArea.waitForVisibility();
    }

    public final String DESCRIPTION = "ОПИСАНИЕ ТОВАРА";
    public final String SPECIFICATION = "ХАРАКТЕРИСТИКИ";
    public final String REVIEWS = "ОТЗЫВЫ";
    public final String SIMILAR_PRODUCTS = "АНАЛОГИЧНЫЕ ТОВАРЫ";

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Перейти назад на страницу поиска товара")
    public SearchProductPage returnBack() {
        returnBackBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Перейти во вкладку {value}")
    public <T> T switchTab(String value) {
        Element element;
        switch (value) {
            case DESCRIPTION:
                element = E(DESCRIPTION);
                swipeLeftTo(mainArea, element);
                element.click();
                return (T) new ProductDescriptionPage(context);
            case SPECIFICATION:
                element = E(SPECIFICATION);
                swipeLeftTo(mainArea, element);
                element.click();
                return (T) new SpecificationsPage(context);
            case REVIEWS:
                element = E(REVIEWS);
                swipeRightTo(mainArea, element);
                element.click();
                return (T) new ReviewsPage(context);
            case SIMILAR_PRODUCTS:
                element = E(SIMILAR_PRODUCTS);
                swipeRightTo(mainArea, element);
                element.click();
                return (T) new SimilarProductsPage(context);
            default:
                throw new IllegalArgumentException("Unknown argument: " + value);
        }
    }

    @Step("Нажмите на кнопку Действия с товаром")
    public ActionWithProductModalPage clickActionWithProductButton() {
        actionWithProductBtn.click();
        return new ActionWithProductModalPage(context);
    }

    /* ------------------------- Verifications -------------------------- */

    public ProductCardPage verifyRequiredElements(boolean submitBtnShouldBeVisible) {
        softAssert.isElementVisible(mainArea);
        if (submitBtnShouldBeVisible)
            softAssert.isElementVisible(actionWithProductBtn);
        else
            softAssert.isElementNotVisible(actionWithProductBtn);
        softAssert.verifyAll();
        return this;
    }

}