package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import io.qameta.allure.Step;

public class ActionWithProductCardModalPage extends CommonMagMobilePage {

    public ActionWithProductCardModalPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView", metaName = "Загаловок модального окна")
    protected Element headerLbl;

    @AppFindBy(text = "Изменить количество")
    protected Element changeQuantityMenuItem;

    @AppFindBy(text = "Добавить товар ещё раз")
    protected Element addProductAgainMenuItem;

    @AppFindBy(text = "Подробнее о товаре")
    protected Element detailsAboutProductMenuItem;

    @AppFindBy(text = "Удалить товар")
    protected Element removeProductMenuItem;

    @Override
    public void waitForPageIsLoaded() {
        removeProductMenuItem.waitForVisibility();
    }

    // Actions

    @Step("Выберите пункт меню 'Подробнее о товаре'")
    public ProductDescriptionPage clickProductDetailsMenuItem() {
        detailsAboutProductMenuItem.click();
        return new ProductDescriptionPage(context);
    }

    @Step("Выберите пункт меню 'Изменить кол-во'")
    public EditProduct35Page clickChangeQuantityMenuItem() {
        changeQuantityMenuItem.click();
        return new EditProduct35Page(context);
    }

    @Step("Выберите пункт меню 'Удалить товар'")
    public ConfirmRemovingProductModal clickRemoveProductMenuItem() {
        removeProductMenuItem.click();
        return new ConfirmRemovingProductModal(context);
    }

    // Verifications

    @Step("Проверить, что страница 'Действия с товаром' отображается корректно")
    public ActionWithProductCardModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, changeQuantityMenuItem, addProductAgainMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem);
        softAssert.verifyAll();
        return this;
    }

}