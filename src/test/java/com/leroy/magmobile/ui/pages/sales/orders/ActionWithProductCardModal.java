package com.leroy.magmobile.ui.pages.sales.orders;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import io.qameta.allure.Step;

public abstract class ActionWithProductCardModal<T extends CartEstimatePage> extends CommonMagMobilePage {

    private Class<T> parentPage;

    public ActionWithProductCardModal(Class<T> type) {
        super();
        parentPage = type;
    }

    protected T newCartOrEstimatePage() throws Exception {
        return parentPage.getConstructor().newInstance();
    }

    @Override
    public void waitForPageIsLoaded() {
        removeProductMenuItem.waitForVisibility();
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

    // Actions

    @Step("Выберите пункт меню 'Добавить товар еще раз'")
    public AddProduct35Page<T> clickAddProductAgainMenuItem() {
        addProductAgainMenuItem.click();
        return new AddProduct35Page<>(parentPage);
    }

    @Step("Выберите пункт меню 'Изменить кол-во'")
    public EditProduct35Page<T> clickChangeQuantityMenuItem() {
        changeQuantityMenuItem.click();
        return new EditProduct35Page<>(parentPage);
    }

    @Step("Выберите пункт меню 'Подробнее о товаре'")
    public ProductDescriptionPage clickProductDetailsMenuItem() {
        detailsAboutProductMenuItem.click();
        return new ProductDescriptionPage();
    }

    @Step("Выберите пункт меню 'Удалить товар'")
    public void clickRemoveProductMenuItem() {
        removeProductMenuItem.click();
    }
}
