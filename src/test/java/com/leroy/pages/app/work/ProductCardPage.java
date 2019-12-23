package com.leroy.pages.app.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.pages.BasePage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.elements.MagMobButton;
import com.leroy.pages.app.common.SearchProductPage;
import io.qameta.allure.Step;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "RMProductCard", metaName = "Область с заголовком карточки товара")
    private Element productCardHeaderArea;

    @AppFindBy(accessibilityId = "Button")
    private MagMobButton withdrawalBtn;

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

    public ProductCardPage verifyRequiredElements() {
        softAssert.isElementVisible(productCardHeaderArea);
        // TODO
        softAssert.verifyAll();
        return this;
    }

    public ProductCardPage shouldWithdrawalButtonHasQuantity(String count) {
        anAssert.isElementTextEqual(withdrawalBtn,
                String.format("ОТОЗВАТЬ %s шт.", count));
        return this;
    }
}
