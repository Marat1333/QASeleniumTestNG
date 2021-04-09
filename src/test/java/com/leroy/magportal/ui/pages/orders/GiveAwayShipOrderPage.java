package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.orders.ToGiveAwayProductCardData;
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

    //Variables


    //int productCardIndex = productCards.get(index);

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

    @Step("Изменить кол-во 'К выдаче' для {index}-ого товара")
    public GiveAwayShipOrderPage editToShipQuantity(int index, double val) throws Exception {
        index--;
        productCards.get(index).editQuantity(val);
        return this;
    }

    // Verifications


    @Step("Проверить, что кол-во 'К выдаче' у {index}-ого товара равно {value}")
    public GiveAwayShipOrderPage shouldProductToShipQuantityIs(int index, double value) throws Exception {

        checkIndex(index);
        checkArray(productCards, index);
        index--;
        anAssert.isEquals(Double.parseDouble(productCards.get(index).getToGiveAwayQuantity()), value,
                "Неверное кол-во 'К выдаче' у " + (index + 1) + "-ого товара");
        return this;
    }
}
    

