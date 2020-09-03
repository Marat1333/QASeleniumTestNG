package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.modal.DiscountModal;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.orders.OrderDraftDeliveryWayPage;
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

    @WebFindBy(xpath = "//div[contains(@class, 'SalesDoc-ViewFooter__action')]//button[descendant::span[contains(text(),'Оформить заказ')]]",
            metaName = "Кнопки Оформить заказ", clazz = Button.class)
    ElementList<Button> confirmButtons;

    @Override
    protected CardWebWidgetList<OrderPuzWidget, OrderWebData> orders() {
        return orders;
    }

    @Override
    public void waitForPageIsLoaded() {
        String expectedHeader = "Корзины";
        anAssert.isTrue(mainCartHeaderLbl.waitUntilTextIsEqualTo(expectedHeader),
                "Страница 'Корзины' не загрузилась'");
        waitForSpinnerDisappear();
    }

    public CartPage waitForProductsAreLoaded() {
        orders().waitUntilAtLeastOneElementIsPresent();
        return this;
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
        createCartBtn.waitForInvisibility();
        return this;
    }

    @Step("Нажать на иконку 'Сделать скидку'")
    public DiscountModal clickDiscountIcon(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        OrderPuzWidget orderWidget = orders().get(orderIdx);
        orderWidget.getProductWidget(productIdx).clickCreateDiscount();
        return new DiscountModal();
    }

    @Step("Нажать кнопку 'Оформить заказ'")
    public OrderDraftDeliveryWayPage clickConfirmButton(int index) throws Exception {
        index--;
        anAssert.isTrue(confirmButtons.getCount() > 0, "Кнопка 'Оформить заказ' не отображается");
        confirmButtons.get(index).click();
        waitForSpinnerAppearAndDisappear();
        return new OrderDraftDeliveryWayPage();
    }

    public OrderDraftDeliveryWayPage clickConfirmButton() throws Exception {
        return clickConfirmButton(1);
    }

    // Verifications

    /**
     * Проверить состояние страницы после нажатия на кнопку "+Создать корзину" То, что отображаются
     * все необходимые элементы на странице
     */
    @Step("Проверить, что страница Корзины отображается корректно после нажатия на кнопку 'Создать корзину'")
    public void verificationAfterClickCreateNewCartButton() {
        softAssert.areElementsVisible(searchProductFld);
        softAssert.isElementTextEqual(cartNumber, "№ —");
        softAssert.verifyAll();
    }

    @Step("Проверить, что страница 'Корзины' отображается корректно, когда она пустая")
    public CartPage verifyEmptyCartPage() {
        softAssert.isElementVisible(createCartBtn);
        softAssert.areElementsNotVisible(searchProductFld, cartNumber, cartAuthor);
        softAssert.isEquals(orders.getCount(), 0, "Отображаются заказы");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что на странице корзины содержатся ожидаемые данные")
    public CartPage shouldCartHasData(SalesDocWebData expectedEstimateData) throws Exception {
        shouldDocumentHasData(expectedEstimateData);
        return this;
    }

}
