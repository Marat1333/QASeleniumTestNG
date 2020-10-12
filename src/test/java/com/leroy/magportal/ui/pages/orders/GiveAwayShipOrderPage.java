package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.orders.ToGiveAwayProductCardData;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.pages.orders.OrderCreatedPage;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductToGiveAwayCardWidget;
import com.leroy.magportal.ui.pages.orders.widget.PickingWidget;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.data.PickingData;
import com.leroy.magportal.ui.pages.picking.widget.AssemblyProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

/**
 * Описывает вкладку "К выдаче и возврату"
 */
public class GiveAwayShipOrderPage extends OrderCreatedPage {

    @WebFindBy(xpath = "//div[contains(@class, 'lm-puz2-Order-OrderViewFooter__buttonsWrapper')]//button", metaName = "Кнопка 'Выдать'")
    Button GiveAwayBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Order-GiveAway-Card')]",
            clazz = OrderProductToGiveAwayCardWidget.class)
    CardWebWidgetList<OrderProductToGiveAwayCardWidget, ToGiveAwayProductCardData> productCards;


    //div[contains(@class, 'lm-shared-order-ProductCard__body-title')]


    // Actions

    @Step("Нажать кнопку 'Выдать'")
    public GiveAwayShipOrderPage clickGiveAwayButton() {
        GiveAwayBtn.click();
        waitForSpinnerAppearAndDisappear();
        //anAssert.isTrue(finishAssemblyBtn.isVisible(), "Кнопка Завершить не отображается");
        //anAssert.isFalse(finishAssemblyBtn.isEnabled(), "Кнопка Завершить активна");
        return this;
    }

    @Step("Изменить кол-во сборка для {index}-ого товара")
    public GiveAwayShipOrderPage editToShipQuantity(int index, int val) throws Exception {
        index--;
        productCards.get(index).editQuantity(val);
        return this;
    }

    // Verifications

    @Step("Проверить, что кол-во 'Собрано' у {index}-ого товара равно {value}")
    public GiveAwayShipOrderPage shouldProductToShipQuantityIs(int index, int value) throws Exception {
        index--;
        anAssert.isEquals(productCards.get(index).getToGiveAwayQuantity(), String.valueOf(value),
                "Неверное кол-во 'К выдаче' у " + (index + 1) + "-ого товара");
        return this;
    }
}
    

