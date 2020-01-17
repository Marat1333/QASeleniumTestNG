package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobileScreen;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import io.qameta.allure.Step;

public class ActionWithProductModalScreen extends CommonMagMobileScreen {

    public ActionWithProductModalScreen(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"Button\"])[1]/android.view.ViewGroup",
            metaName = "Кнопка для закрытия модального окна")
    Element closeBtn;

    @AppFindBy(text = "Действия с товаром")
    Element headerLbl;

    @AppFindBy(text = "Добавить в документ продажи")
    Element addIntoSalesDocumentBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Добавить в документ продажи']/following-sibling::android.view.ViewGroup/android.widget.TextView")
    Element addIntoSalesDocumentCountLbl;

    @AppFindBy(text = "Добавить в заявку на отзыв с RM")
    Element addIntoOrderForWithdrawalFromRMBtn;

    @AppFindBy(text = "Уведомить клиента о наличии")
    Element notifyClientBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    // ---------- ACTION STEPS --------------------------//

    @Step("Нажмите кнопку 'Добавить в документ продажи'")
    // Может быть несколько вариантов событий:
    // 1) Когда попадаем на AddIntoSalesDocumentModalScreen
    // 2) Когда попадаем сразу на AddProductPage
    // 3) Видим модальное окно с уведомлением
    public void clickAddIntoSalesDocumentButton() {
        addIntoSalesDocumentBtn.click();
    }

    @Step("Нажать кнопку 'Добавить в документ продажи' и начать создание нового Документа продажи")
    public AddProductPage startToCreateSalesDocument() {
        boolean draftsPresent = addIntoSalesDocumentCountLbl.isVisible();
        clickAddIntoSalesDocumentButton();
        if (draftsPresent)
            return new AddIntoSalesDocumentModalScreen(context).clickCreateSalesDocumentBtn();
        else
            return new AddProductPage(context);
    }

    // Verifications

    @Step("Проверить, что модальное окно 'Действия с товаром' отобразилось со всеми необходимыми товарами")
    public ActionWithProductModalScreen verifyRequiredElements(boolean isAvsProduct) {
        shouldNotAnyErrorVisible();
        softAssert.isElementVisible(closeBtn);
        softAssert.isElementVisible(headerLbl);
        softAssert.isElementVisible(addIntoSalesDocumentBtn);
        softAssert.isElementVisible(addIntoOrderForWithdrawalFromRMBtn);
        if (isAvsProduct)
            softAssert.isElementNotVisible(notifyClientBtn);
        else
            softAssert.isElementVisible(notifyClientBtn);
        softAssert.verifyAll();
        return this;
    }

}
