package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ConfirmRemoveProductModal;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public abstract class OrderDraftPage extends OrderHeaderPage {

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__header')]//span",
            metaName = "Номер заказа")
    Element orderNumber;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__header')]//span[contains(@class, 'Status-container')]",
            metaName = "Статус заказа")
    Element orderStatus;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__header')]//div[contains(@class, 'popover__opener')]//button",
            metaName = "Кнопка удаления заказа (мусорка)")
    Button trashBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__header')]/div/div[2]/div/span[1]", metaName = "Дата создания")
    Element creationDate;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__header')]/div/div[2]/div/span[2]", metaName = "Автор документа")
    Element author;

    // Tabs

    @WebFindBy(id = "ORDER_CONTENT_TAB_ID", metaName = "Вкладка 'Состав заказа'")
    Button contentOrderTab;

    @WebFindBy(id = "DELIVERY_TYPES_TAB_ID", metaName = "Вкладка 'Способ получения'")
    Button deliveryTypeTab;

    @WebFindBy(id = "Ready", metaName = "Вкладка 'Готово'")
    Button readyTab;

    // Bottom area

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__footer')]//div[span[contains(text(), 'Вес')]]//span",
            metaName = "Текст с количеством и весом товара")
    Element countAndWeightProductLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__footer')]//div[contains(@class, 'Estimate-price')]//span",
            metaName = "Итого стоимость заказа")
    Element orderTotalPrice;

    // Grab info

    @Step("Получить номер заказа со страницы")
    public String getOrderNumber() {
        return ParserUtil.strWithOnlyDigits(orderNumber.getText());
    }

    @Step("Получить цифру кол-ва товаров в заказе на нижней панели")
    public int getProductCount() {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText().split("•");
        return ParserUtil.strToInt(actualCountProductAndWeight[0]);
    }

    @Step("Получить общий вес товаров в заказе на нижней панели")
    public Double getTotalWeight() {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText().split("•");
        double weight = ParserUtil.strToDouble(actualCountProductAndWeight[1]);
        return actualCountProductAndWeight[1].endsWith("кг") ? weight : weight * 1000;
    }


    // Actions

    @Step("Нажать кнопку удаления (мусорка) и подтвердить удаление заказа")
    public OrderDraftPage removeOrder() {
        trashBtn.click();
        new ConfirmRemoveProductModal().clickYesButton();
        trashBtn.waitForInvisibility();
        return this;
    }

    @Step("Нажать на кнопку 'Состав заказа'")
    public OrderDraftContentPage goToContentOrderTab() {
        contentOrderTab.click();
        return new OrderDraftContentPage();
    }

    @Step("Нажать на кнопку 'Способ получения'")
    public OrderDraftDeliveryWayPage goToDeliveryTypeTab() {
        deliveryTypeTab.click();
        return new OrderDraftDeliveryWayPage();
    }

    // Verifications

    @Step("Проверить, что статус заказа = {value}")
    public OrderDraftPage shouldOrderStatusIs(String value) {
        anAssert.isEquals(orderStatus.getText().toLowerCase(), value.toLowerCase(),
                "Неверный статусс заказа");
        return this;
    }

    @Step("Проверить, что не выбран никакой документ (заказ)")
    public OrderDraftPage shouldNoOneDocumentIsSelected() {
        anAssert.isFalse(orderNumber.isVisible(), "Номер документа отображается");
        return this;
    }

}
