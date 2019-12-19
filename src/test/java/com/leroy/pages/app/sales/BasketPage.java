package com.leroy.pages.app.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;

public class BasketPage extends BaseAppPage {

    public BasketPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Корзина')]")
    protected Element screenTitle;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Корзина')]/following::android.widget.TextView[1]")
    protected Element documentType;

    public String getDocumentNumber() {
        return screenTitle.getText().replaceAll("Корзина № ", "").trim();
    }

    // ------------- Verifications ----------------------//

    public BasketPage shouldDocumentTypeIs(String text) {
        anAssert.isElementTextEqual(documentType, text);
        return this;
    }
}
