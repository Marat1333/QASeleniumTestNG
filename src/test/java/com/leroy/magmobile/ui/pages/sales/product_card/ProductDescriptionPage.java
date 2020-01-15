package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.sales.PricesAndQuantityPage;
import com.leroy.magmobile.ui.pages.sales.ProductCardPage;
import io.qameta.allure.Step;

public class ProductDescriptionPage extends ProductCardPage {

    public ProductDescriptionPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.TextView[1]",
            metaName = "ЛМ код товара")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.TextView[2]",
            metaName = "Бар код товара")
    Element barCode;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[1]",
            metaName = "Название товара")
    Element productName;

    @AppFindBy(text = "История продаж")
    MagMobButton salesHistoryBtn;

    @AppFindBy(text = "Цена")
    MagMobButton productPriceBtn;

    @AppFindBy(text = "Цены в магазинах")
    MagMobButton productPriceGammaCardBtn;

    @Step("Перейти на страницу с детализацией цен и запасов")
    public PricesAndQuantityPage goToPricesAndQuantityPage() {
        productPriceGammaCardBtn.click();
        return new PricesAndQuantityPage(context);
    }

    @Override
    public ProductDescriptionPage verifyRequiredElements(boolean submitBtnShouldBeVisible) {
        super.verifyRequiredElements(submitBtnShouldBeVisible);
        softAssert.isElementVisible(lmCode);
        softAssert.verifyAll();
        return this;
    }

    public ProductCardPage verifyRequiredContext(String searchContext) {
        if (searchContext.matches("^.*?\\D+$")) {
            anAssert.isEquals(productName.getText(), searchContext, searchContext);
        }
        if (searchContext.length() > 8) {
            String barCode = this.barCode.getText().replaceAll(" ", "");
            anAssert.isEquals(barCode, searchContext, searchContext);
        } else {
            String lmCode = this.lmCode.getText().replaceAll("^\\D+", "");
            anAssert.isEquals(lmCode, searchContext, searchContext);
        }


        return this;
    }

    public ProductDescriptionPage shouldProductLMCodeIs(String text) {
        anAssert.isEquals(lmCode.getText().replaceAll("\\D", ""), text,
                "ЛМ код должен быть %s");
        return this;
    }

    public ProductDescriptionPage shouldProductBarCodeIs(String text) {
        anAssert.isEquals(barCode.getText().replaceAll("\\D", ""), text,
                "Бар код должен быть %s");
        return this;
    }

    public void verifyCardHasGammaView() {
        softAssert.isFalse(actionWithProductBtn.isVisible(), "Кнопка \"Действия с товаром\" отсутствует в карточке товара ЛМ");
        softAssert.isFalse(salesHistoryBtn.isVisible(), "Кнопка \"История продаж\" отсутствует в карточке товара ЛМ");
    }
}
