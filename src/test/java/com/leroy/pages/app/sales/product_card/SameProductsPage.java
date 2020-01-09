package com.leroy.pages.app.sales.product_card;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.sales.ProductCardPage;
import com.leroy.pages.app.sales.widget.SearchProductCardWidget;
import io.qameta.allure.Step;

public class SameProductsPage extends ProductCardPage {

    public SameProductsPage(TestContext context){
        super(context);
    }

    @AppFindBy(text = "за штуку")
    Element productCardPrice;

    @AppFindBy(text = "доступно")
    Element productCardQuantity;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup[android.widget.ImageView]",
            clazz = SearchProductCardWidget.class)
    private ElementList<SearchProductCardWidget> productCards;


    @Step("Перейти в {value} карточку товара")
    public ProductDescriptionPage goToProductCard(int value) throws Exception {
        value--;
        scrollDownTo(productCards.get(value));
        anAssert.isTrue(productCards.get(value).isVisible(),"Найдена "+value+" по счету карточка товара");
        productCards.get(value).click();
        return new ProductDescriptionPage(context);
    }

    public void verifyProductCardHasAllGammaView() throws Exception{
        hideKeyboard();
        scrollDown();
        anAssert.isFalse(productCardPrice.isVisible(), "Не отображается Цена");
        anAssert.isFalse(productCardQuantity.isVisible(),"Не отображается Кол-во");
    }

}
