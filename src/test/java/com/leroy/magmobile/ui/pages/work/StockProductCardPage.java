package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.modal.QuantityProductsForWithdrawalModalPage;
import io.qameta.allure.Step;

public class StockProductCardPage extends CommonMagMobilePage {

    public StockProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "RMProductCard", metaName = "Область с заголовком карточки товара")
    private Element productCardHeaderArea;

    @AppFindBy(accessibilityId = "Button")
    private MagMobSubmitButton withdrawalBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup")
    private Element mainContentArea;

    @Override
    public void waitForPageIsLoaded() {
        mainContentArea.waitForVisibility();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать кнопку ОТОЗВАТЬ для ввода кол-ва товара")
    public QuantityProductsForWithdrawalModalPage clickWithdrawalBtnForEnterQuantity() {
        withdrawalBtn.click();
        return new QuantityProductsForWithdrawalModalPage(context);
    }

    /* ------------------------- Verifications -------------------------- */

    @Step("Проверить, что экрана с карточкой товара отображается корректно")
    public StockProductCardPage verifyRequiredElements() {
        softAssert.areElementsVisible(productCardHeaderArea, withdrawalBtn);
        // TODO
        softAssert.verifyAll();
        return this;
    }

}
