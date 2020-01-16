package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobileScreen;
import io.qameta.allure.Step;

public abstract class BasketPage extends CommonMagMobileScreen {

    public static class Constants {
        // Типы документа
        public static final String DRAFT_DOCUMENT_TYPE = "Черновик";
    }

    public BasketPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[1]/android.view.ViewGroup",
            metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[2]/android.view.ViewGroup",
            metaName = "Кнопка удалить")
    Element trashBtn;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Корзина')]")
    protected Element screenTitle;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Корзина')]/following::android.widget.TextView[1]")
    protected Element documentType;

    public String getDocumentNumber() {
        return screenTitle.getText().replaceAll("Корзина № ", "").trim();
    }

    // ------------- Verifications ----------------------//

    public BasketPage verifyRequiredElements() {
        shouldNotAnyErrorVisible();
        String titleText = screenTitle.getText();
        softAssert.isTrue(titleText.matches("Корзина № \\d{8}"),
                "Номер документа должен состоять из 8 символов");
        softAssert.isElementVisible(backBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что тип документа - {text}")
    public BasketPage shouldDocumentTypeIs(String text) {
        anAssert.isElementTextEqual(documentType, text);
        return this;
    }
}
