package com.leroy.magmobile.ui.pages.work.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.StockProductsPage;
import io.qameta.allure.Step;

public class QuantityProductsForWithdrawalModalPage extends CommonMagMobilePage {

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    @AppFindBy(text = "Количество товаров на отзыв")
    Element headerLbl;

    @AppFindBy(accessibilityId = "monoPalletReserved")
    private EditBox quantityItemsFld;

    @AppFindBy(accessibilityId = "Button")
    private MagMobGreenSubmitButton withdrawalBtn;

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Ввести количество товара для отзыва - {quantity}")
    public QuantityProductsForWithdrawalModalPage enterCountOfItems(String quantity) throws Exception {
        quantityItemsFld.clearAndFill(quantity);
        return this;
    }

    @Step("Нажать кнопку ОТОЗВАТЬ (submit)")
    public StockProductsPage clickSubmitBtn() {
        withdrawalBtn.click();
        return new StockProductsPage();
    }

    // Verifications

    @Step("Проверить, что модальное окно для ввода кол-ва товаров на отзыв отображается корректно")
    public QuantityProductsForWithdrawalModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, quantityItemsFld, withdrawalBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что на кнопке 'Отозвать' отображается кол-вл {count}")
    public QuantityProductsForWithdrawalModalPage shouldWithdrawalButtonHasQuantity(String count) {
        anAssert.isElementTextEqual(withdrawalBtn,
                String.format("ОТОЗВАТЬ %s шт.", count));
        return this;
    }

    @Step("Проверить активность кнопки 'Отозвать'. Параметр - {shouldBeEnabled}")
    public QuantityProductsForWithdrawalModalPage shouldSubmitButtonActivityIs(boolean shouldBeEnabled) {
        if (shouldBeEnabled)
            anAssert.isTrue(withdrawalBtn.isEnabled(), "Кнопка не активна");
        else
            anAssert.isFalse(withdrawalBtn.isEnabled(), "Кнопка активна");
        return this;
    }

}
