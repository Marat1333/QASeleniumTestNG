package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class CartPage extends CartEstimatePage {

    @WebFindBy(xpath = "//div[contains(@class, 'Layouts-TopTwo-topBar-container')]//span",
            metaName = "Заголовок 'Корзины'")
    Element mainCartHeaderLbl;

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать корзину']]",
            metaName = "Текст кнопки 'Создать корзину'")
    Element createCartBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Cart-CartsView__header-text')]", metaName = "Номер корзины")
    Element cartNumber;

    @WebFindBy(xpath = "//div[contains(@class, 'CartsView__header-info__row')][2]//div[1]/span",
            metaName = "Дата создания документа")
    Element creationDate;

    @WebFindBy(xpath = "//div[contains(@class, 'CartsView__header-info__row')][2]//div[2]/span",
            metaName = "Создатель документа")
    Element cartAuthor;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - 21) = 'CartsView__cart__group']",
            clazz = OrderPuzWidget.class)
    CardWebWidgetList<OrderPuzWidget, OrderWebData> orders;

    @Override
    protected CardWebWidgetList<OrderPuzWidget, OrderWebData> orders() {
        return orders;
    }

    @Override
    public void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        anAssert.isElementVisible(mainCartHeaderLbl, timeout);
    }

    // Grab info

    @Override
    public String getDocumentNumber() {
        return ParserUtil.strWithOnlyDigits(cartNumber.getText());
    }

    @Override
    public String getDocumentStatus() {
        return null;
    }

    @Override
    public String getDocumentAuthor() {
        return cartAuthor.getText();
    }

    @Override
    public String getCreationDate() {
        return creationDate.getText();
    }

    // Actions

    @Step("Нажать кнопку 'Создать корзину'")
    public CartPage clickCreateCartButton() {
        createCartBtn.click();
        return this;
    }

    // Verifications

    /**
     * Проверить состояние страницы после нажатия на кнопку "+Создать корзину"
     * То, что отображаются все необходимые элементы на странице
     */
    @Step("Проверить, что страница Корзины отображается корректно после нажатия на кнопку 'Создать корзину'")
    public void verificationAfterClickCreateNewCartButton() {
        softAssert.areElementsVisible(addCustomerBtnLbl, searchProductFld);
        softAssert.isElementTextEqual(cartNumber, "№ —");
        softAssert.verifyAll();
    }

    @Step("Проверить, что страница 'Корзины' отображается корректно, когда она пустая")
    public CartPage verifyEmptyCartPage() {
        softAssert.isElementVisible(createCartBtn);
        softAssert.areElementsNotVisible(addCustomerBtnLbl, searchProductFld, cartNumber, cartAuthor,
                customerPhoneSearchFld);
        softAssert.isEquals(orders.getCount(), 0, "Отображаются заказы");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что на странице корзины содержатся ожидаемые данные")
    public CartPage shouldCartHasData(SalesDocWebData expectedEstimateData) {
        shouldDocumentHasData(expectedEstimateData);
        return this;
    }

}
