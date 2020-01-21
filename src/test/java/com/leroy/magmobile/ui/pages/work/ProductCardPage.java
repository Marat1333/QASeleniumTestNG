package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class ProductCardPage extends CommonMagMobilePage {

    public ProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "RMProductCard", metaName = "Область с заголовком карточки товара")
    private Element productCardHeaderArea;

    @AppFindBy(accessibilityId = "Button")
    private MagMobSubmitButton withdrawalBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup")
    private Element mainContentArea;

    // Modal window elements
    @AppFindBy(accessibilityId = "monoPalletReserved")
    private EditBox quantityItemsFld;


    @Override
    public void waitForPageIsLoaded() {
        mainContentArea.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Ввести количество товара для отзыва")
    public ProductCardPage enterCountOfItems(String quantity) {
        quantityItemsFld.clearAndFill(quantity);
        return this;
    }

    @Step("Нажать кнопку ОТОЗВАТЬ для ввода кол-ва товара")
    public ProductCardPage clickWithdrawalBtnForEnterQuantity() {
        withdrawalBtn.click();
        return this;
    }

    @Step("Нажать кнопку ОТОЗВАТЬ (submit)")
    public StockProductsPage clickSubmitBtn() {
        withdrawalBtn.click();
        return new StockProductsPage(context);
    }

    /* ------------------------- Verifications -------------------------- */

    @Step("Проверить, что экрана с карточкой товара отображается корректно")
    public ProductCardPage verifyRequiredElements() {
        softAssert.isElementVisible(productCardHeaderArea);
        softAssert.isElementVisible(withdrawalBtn);
        // TODO
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что на кнопке 'Отозвать' отображается кол-вл {count}")
    public ProductCardPage shouldWithdrawalButtonHasQuantity(String count) {
        anAssert.isElementTextEqual(withdrawalBtn,
                String.format("ОТОЗВАТЬ %s шт.", count));
        return this;
    }
}
