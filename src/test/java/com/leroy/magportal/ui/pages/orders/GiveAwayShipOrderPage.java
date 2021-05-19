package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.orders.ToGiveAwayProductCardData;
import com.leroy.magportal.ui.pages.orders.modal.ReturnDeliveryValueModal;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductToGiveAwayCardWidget;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

/**
 * Описывает вкладку "К выдаче и возврату"
 */
public class GiveAwayShipOrderPage extends OrderCreatedPage {

    @WebFindBy(xpath = "//div[contains(@class, 'lm-puz2-Order-OrderViewFooter__buttonsWrapper')]", metaName = "Кнопка 'Выдать'")
    Button giveAwayBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'lm-puz2-Order-OrderViewFooter__buttonsWrapper')]//span[contains(text(), 'Отгрузить')]/ancestor::button", metaName = "Кнопка 'Отгрузить'")
    Button shipBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Order-GiveAway-Card')]",
            clazz = OrderProductToGiveAwayCardWidget.class)
    CardWebWidgetList<OrderProductToGiveAwayCardWidget, ToGiveAwayProductCardData> productCards;

    @WebFindBy(xpath = "//button[@data-testid='aao-footer-startRefund-btn']", metaName = "Кнопка 'Начать возврат'")
    Button RefundBtn;

    @WebFindBy(xpath = "//button[@data-testid='aao-footer-cancel-btn']", metaName = "Кнопка 'Отмена'")
    Button CancelBtn;

    @WebFindBy(xpath = "//button[@data-testid='aao-footer-next-btn']", metaName = "Кнопка 'Далее'")
    Button FurtherBtn;



    // Actions

    @Step("Нажать кнопку 'Выдать'")
    public GiveAwayShipOrderPage clickGiveAwayButton() {
        giveAwayBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать кнопку 'Отгрузить'")
    public GiveAwayShipOrderPage clickShipButton() {
        shipBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать кнопку 'Начать возврат'")
    public GiveAwayShipOrderPage clickRefundBtn() {
        RefundBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать кнопку 'Далее'")
    public ReturnDeliveryValueModal clickFurtherBtn() {
        FurtherBtn.click();
        waitForSpinnerAppearAndDisappear();
        return new ReturnDeliveryValueModal();
    }

    @Step("Изменить кол-во 'К выдаче' для {index}-ого товара")
    public GiveAwayShipOrderPage editToShipQuantity(int index, double val) throws Exception {
        --index;
        productCards.get(index).editToShipmentQuantity(val);
        return this;
    }

    @Step("Изменить кол-во 'Возврат клиенту' для {index}-ого товара")
    public GiveAwayShipOrderPage editToRefundQuantity(int index, double val) throws Exception {
        --index;
        productCards.get(index).editToRefundQuantity(val);
        return this;
    }

    // Verifications

    @Step("Проверить, что кол-во 'К выдаче' у {index}-ого товара равно {value}")
    public GiveAwayShipOrderPage shouldProductToShipQuantityIs(int index, double value) throws Exception {
        anAssert.isEquals(Double.parseDouble(productCards.get(--index).getToGiveAwayQuantity()), value,
                "Неверное кол-во 'К выдаче' у " + (index + 1) + "-ого товара");
        return this;
    }

    @Step("Проверить, что кол-во 'Возврат клиенту' у {index}-ого товара равно {value}")
    public GiveAwayShipOrderPage shouldToRefundQuantity(int index, double value) throws Exception {
        anAssert.isEquals(Double.parseDouble(productCards.get(--index).getToRefundQuantity()), value,
                "Неверное кол-во 'К выдаче' у " + (index + 1) + "-ого товара");
        return this;
    }
}
    

