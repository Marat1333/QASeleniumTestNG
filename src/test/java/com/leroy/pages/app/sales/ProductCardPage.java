package com.leroy.pages.app.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.elements.MagMobButton;
import com.leroy.pages.app.common.SearchProductPage;
import com.leroy.pages.app.work.StockProductsPage;
import io.qameta.allure.Step;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "Tabs")
    Element productTabs;

    @AppFindBy(accessibilityId = "BackCloseModal")
    Element returnBackBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.TextView[1]",
            metaName = "ЛМ код товара")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[3]/android.widget.TextView[2]",
            metaName = "Бар код товара")
    Element barCode;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[4]/android.widget.TextView[1]",
            metaName = "Название товара")
    Element productName;

    @AppFindBy(text = "ДЕЙСТВИЯ С ТОВАРОМ")
    MagMobButton actionWithProductBtn;

    @Override
    public void waitForPageIsLoaded() {
        productTabs.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */


    @Step("Перейти назад на страницу поиска товара")
    public SearchProductPage returnBack() {
        returnBackBtn.click();
        return new SearchProductPage(context);
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

    public ProductCardPage shouldProductLMCodeIs(String text) {
        anAssert.isEquals(lmCode.getText().replaceAll("\\D", ""), text,
                "ЛМ код должен быть %s");
        return this;
    }

}
