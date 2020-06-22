package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
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

    public enum Tabs{
        DESCRIPTION("ОПИСАНИЕ ТОВАРА"),
        SPECIFICATION("ХАРАКТЕРИСТИКИ"),
        REVIEWS("ОТЗЫВЫ"),
        SIMILAR_PRODUCTS("АНАЛОГИЧНЫЕ ТОВАРЫ");

        private String name;

        Tabs(String name){
            this.name=name;
        }

        public String getName() {
            return name;
        }
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    public SearchProductPage returnBack() throws Exception {
        return returnBack(SearchProductPage.class);
    }

    @Step("Перейти назад на страницу поиска товара")
    public <T extends CommonMagMobilePage> T returnBack(Class<T> page) throws Exception {
        returnBackBtn.click();
        return page.getConstructor().newInstance();
    }

    @Step("Перейти во вкладку")
    public <T> T switchTab(Tabs tabs) {
        Element element;
        switch (tabs) {
            case DESCRIPTION:
                element = E(Tabs.DESCRIPTION.getName());
                swipeLeftTo(mainArea, element);
                element.click();
                return (T) new ProductDescriptionPage();
            case SPECIFICATION:
                element = E(Tabs.SPECIFICATION.getName());
                swipeLeftTo(mainArea, element);
                element.click();
                return (T) new SpecificationsPage();
            case REVIEWS:
                element = E(Tabs.REVIEWS.getName());
                swipeRightTo(mainArea, element);
                element.click();
                return (T) new ReviewsPage();
            case SIMILAR_PRODUCTS:
                element = E(Tabs.SIMILAR_PRODUCTS.getName());
                swipeRightTo(mainArea, element);
                element.click();
                return (T) new SimilarProductsPage();
            default:
                throw new IllegalArgumentException("Unknown argument");
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
