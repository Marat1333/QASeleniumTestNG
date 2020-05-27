package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.sales.PricesAndQuantityPage;
import com.leroy.magmobile.ui.pages.sales.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class ProductDescriptionPage extends ProductCardPage {

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='lmCode']",
            metaName = "ЛМ код товара")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='barCode']",
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

    @Override
    public void waitForPageIsLoaded() {
        lmCode.waitForVisibility();
    }

    public static boolean isThisPage(TestContext context) {
        String ps = getPageSource(context.getDriver());
        Element el = new Element(context.getDriver(),
                By.xpath("//*[contains(@content-desc, 'Screen')]//android.widget.TextView"));
        return el.isVisible(ps) && el.getText(ps).equals(Basket35Page.SCREEN_TITLE);
    }

    // Actions

    @Step("Перейти на страницу с детализацией цен и запасов")
    public PricesAndQuantityPage goToPricesAndQuantityPage() {
        productPriceGammaCardBtn.click();
        return new PricesAndQuantityPage();
    }

    // Verifications

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

    @Step("Проверить, что ЛМ код товара = {text}")
    public ProductDescriptionPage shouldProductLMCodeIs(String text) {
        anAssert.isEquals(lmCode.getText().replaceAll("\\D", ""), text,
                "ЛМ код должен быть %s");
        return this;
    }

    @Step("Проверить, что бар код товара = {text}")
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
