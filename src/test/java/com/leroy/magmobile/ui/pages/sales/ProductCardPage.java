package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ReviewsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SpecificationsPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

public class ProductCardPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "Tabs")
    protected Element mainArea;

    @AppFindBy(accessibilityId = "BackCloseModal")
    protected Element returnBackBtn;

    @AppFindBy(text = "ДЕЙСТВИЯ С ТОВАРОМ")
    protected MagMobGreenSubmitButton actionWithProductBtn;

    @Override
    public void waitForPageIsLoaded() {
        mainArea.waitForVisibility();
    }

    public final String DESCRIPTION = "ОПИСАНИЕ ТОВАРА";
    public final String SPECIFICATION = "ХАРАКТЕРИСТИКИ";
    public final String REVIEWS = "ОТЗЫВЫ";
    public final String SIMILAR_PRODUCTS = "АНАЛОГИЧНЫЕ ТОВАРЫ";

    /* ------------------------- ACTION STEPS -------------------------- */

    public SearchProductPage returnBack() throws Exception {
        return returnBack(SearchProductPage.class);
    }

    @Step("Перейти назад на страницу поиска товара")
    public <T extends CommonMagMobilePage> T returnBack(Class<T> page) throws Exception {
        returnBackBtn.click();
        return page.getConstructor().newInstance();
    }

    @Step("Перейти во вкладку {value}")
    public <T> T switchTab(String value) {
        Element element;
        switch (value) {
            case DESCRIPTION:
                element = E(DESCRIPTION);
                swipeLeftTo(mainArea, element);
                element.click();
                return (T) new ProductDescriptionPage();
            case SPECIFICATION:
                element = E(SPECIFICATION);
                swipeLeftTo(mainArea, element);
                element.click();
                return (T) new SpecificationsPage();
            case REVIEWS:
                element = E(REVIEWS);
                swipeRightTo(mainArea, element);
                element.click();
                return (T) new ReviewsPage();
            case SIMILAR_PRODUCTS:
                element = E(SIMILAR_PRODUCTS);
                swipeRightTo(mainArea, element);
                element.click();
                return (T) new SimilarProductsPage();
            default:
                throw new IllegalArgumentException("Unknown argument: " + value);
        }
    }

    @Step("Нажмите на кнопку Действия с товаром")
    public void clickActionWithProductButton() {
        actionWithProductBtn.click();
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
