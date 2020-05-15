package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import io.qameta.allure.Step;

public class ActionWithProductModalPage extends CommonActionWithProductModalPage {

    public ActionWithProductModalPage(Context context) {
        super(context);
    }

    @AppFindBy(text = "Добавить в документ продажи")
    Element addIntoSalesDocumentBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Добавить в документ продажи']/following-sibling::android.view.ViewGroup/android.widget.TextView")
    Element addIntoSalesDocumentCountLbl;

    @AppFindBy(text = "Добавить в заявку на отзыв с RM")
    Element addIntoOrderForWithdrawalFromRMBtn;

    // ---------- ACTION STEPS --------------------------//

    @Step("Нажмите кнопку 'Добавить в документ продажи'")
    // Может быть несколько вариантов событий:
    // 1) Когда попадаем на AddIntoSalesDocumentModalScreen
    // 2) Когда попадаем сразу на AddProductPage
    // 3) Видим модальное окно с уведомлением
    public void clickAddIntoSalesDocumentButton() {
        addIntoSalesDocumentBtn.click();
    }

    @Step("Нажмите на кнопку 'Добавить в заявку на Отзыв с RM'")
    // Может быть несколько вариантов событий:
    // 1)
    // 2)
    public void clickAddIntoWithdrawalOrderFromRMButton() {
        addIntoOrderForWithdrawalFromRMBtn.click();
    }

    @Step("Нажать кнопку 'Добавить в документ продажи' и начать создание нового Документа продажи")
    public AddProductPage startToCreateSalesDocument() {
        boolean draftsPresent = addIntoSalesDocumentCountLbl.isVisible();
        clickAddIntoSalesDocumentButton();
        if (draftsPresent)
            return new AddIntoSalesDocumentModalPage(context)
                    .clickCreateSalesDocumentBtn();
        else
            return new AddProductPage(context);
    }

    // VERIFICATIONS

    @Override
    @Step("Проверить, что модальное окно 'Действия с товаром' отобразилось со всеми необходимыми товарами")
    public ActionWithProductModalPage verifyRequiredElements(boolean isAvsProduct) {
        String ps = getPageSource();
        softAssert.isElementVisible(closeBtn, ps);
        softAssert.isElementVisible(headerLbl, ps);
        softAssert.isElementVisible(addIntoSalesDocumentBtn, ps);
        softAssert.isElementVisible(addIntoOrderForWithdrawalFromRMBtn, ps);
        if (isAvsProduct)
            softAssert.isElementNotVisible(notifyClientBtn, ps);
        else
            softAssert.isElementVisible(notifyClientBtn, ps);
        softAssert.verifyAll();
        return this;
    }

}
