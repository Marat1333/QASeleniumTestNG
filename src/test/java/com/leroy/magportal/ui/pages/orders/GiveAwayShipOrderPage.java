package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.pages.orders.OrderCreatedPage;
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

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard']",
            clazz = AssemblyProductCardWidget.class)
    CardWebWidgetList<AssemblyProductCardWidget, PickingProductCardData> productCards;


    // Actions

    /*@Step("Нажать кнопку 'Выдать'")
    public PickingContentPage clickStartAssemblyButton() {
        //GiveAwayBtn.click();
        //waitForSpinnerAppearAndDisappear();
        //anAssert.isTrue(finishAssemblyBtn.isVisible(), "Кнопка Завершить не отображается");
        //anAssert.isFalse(finishAssemblyBtn.isEnabled(), "Кнопка Завершить активна");
        //return this;
    }*/

    @Step("Изменить кол-во сборка для {index}-ого товара")
    public GiveAwayShipOrderPage editToShipQuantity(int index, int val) throws Exception {
        index--;
        productCards.get(index).editCollectQuantity(val);
        return this;
    }
}
    

