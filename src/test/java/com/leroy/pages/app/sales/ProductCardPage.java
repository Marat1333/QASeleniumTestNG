package com.leroy.pages.app.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.elements.MagMobButton;
import com.leroy.pages.app.common.SearchProductPage;
import com.leroy.pages.app.sales.product_card.ProductDescriptionPage;
import com.leroy.pages.app.sales.product_card.ReviewsPage;
import com.leroy.pages.app.sales.product_card.SameProductsPage;
import com.leroy.pages.app.sales.product_card.SpecificationsPage;
import com.leroy.pages.app.work.StockProductsPage;
import io.qameta.allure.Step;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "Tabs")
    public Element productTabs;

    @AppFindBy(accessibilityId = "BackCloseModal")
    public Element returnBackBtn;

    @AppFindBy(text = "ДЕЙСТВИЯ С ТОВАРОМ")
    public MagMobButton actionWithProductBtn;

    @Override
    public void waitForPageIsLoaded() {
        productTabs.waitForVisibility();
    }

    public final String DESCRIPTION = "ОПИСАНИЕ ТОВАРА";
    public final String SPECIFICATION = "ХАРАКТЕРИСТИКИ";
    public final String REVIEWS = "ОТЗЫВЫ";
    public final String SAME_PRODUCTS = "АНАЛОГИЧНЫЕ ТОВАРЫ";

    /* ------------------------- ACTION STEPS -------------------------- */


    @Step("Перейти назад на страницу поиска товара")
    public SearchProductPage returnBack() {
        returnBackBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Перейти во вкладку {value}")
    public <T>T goToNeededPage(String value){
        Element element;
        switch (value){
            case DESCRIPTION:
                element = E("contains("+DESCRIPTION+")");
                element.click();
                return (T) new ProductDescriptionPage(context);
            case SPECIFICATION:
                element = E("contains("+SPECIFICATION+")");
                element.click();
                return (T) new SpecificationsPage(context);
            case REVIEWS:
                element = E("contains("+REVIEWS+")");
                swipeRightTo(productTabs,element);
                element.click();
                return (T) new ReviewsPage(context);
            case SAME_PRODUCTS:
                element = E("contains("+SAME_PRODUCTS+")");
                swipeRightTo(productTabs,element);
                element.click();
                return (T) new SameProductsPage(context);
            default:
                element = E("contains("+DESCRIPTION+")");
                element.click();
                return (T) new ProductDescriptionPage(context);
        }
    }

    /* ------------------------- Verifications -------------------------- */

    @Override
    public ProductCardPage verifyRequiredElements() {
        softAssert.isElementVisible(productTabs);
        softAssert.isElementVisible(actionWithProductBtn);
        // TODO
        softAssert.verifyAll();

        return this;
    }

}
