package com.leroy.pages.app.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "RMProductCard", metaName = "Область с заголовком карточки товара")
    public Element productCardHeaderArea;

    @AppFindBy(accessibilityId = "Button")
    public Element withdrawalBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button']/android.widget.TextView",
            metaName = "Текст кнопки 'Отозвать'")
    public Element withdrawalBtnLabel;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup")
    private Element mainContentArea;

    // Modal window elements
    @AppFindBy(accessibilityId = "monoPalletReserved")
    private EditBox quantityItemsFld;

    @Override
    public void waitForPageIsLoaded() {
        mainContentArea.waitForVisibility();
    }

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

    //@Step("Проверьте, что все элементы страницы '' отображаются корректно")
    public ProductCardPage verifyAllElementsVisibility() {
        softAssert.isElementVisible(productCardHeaderArea);
        // TODO
        softAssert.verifyAll();
        return this;
    }

    public ProductCardPage shouldWithdrawalButtonHasQuantity(String count) {
        anAssert.isElementTextEqual(withdrawalBtnLabel,
                String.format("ОТОЗВАТЬ %s шт.", count));
        return this;
    }

}
