package com.leroy.magmobile.ui.pages.sales.orders.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public abstract class BasketPage extends CommonMagMobilePage {

    public static class Constants {
        // Типы документа
        public static final String DRAFT_DOCUMENT_TYPE = "Черновик";
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
        String ps = getPageSource();
        String titleText = screenTitle.getText(ps);
        softAssert.isTrue(titleText.matches("Корзина № \\d{8}"),
                "Номер документа должен состоять из 8 символов");
        softAssert.isElementVisible(backBtn, ps);
        softAssert.isElementVisible(trashBtn, ps);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что тип документа - {text}")
    public BasketPage shouldDocumentTypeIs(String text) {
        anAssert.isElementTextEqual(documentType, text);
        return this;
    }
}
