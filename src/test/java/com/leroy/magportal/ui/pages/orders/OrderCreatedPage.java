package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public abstract class OrderCreatedPage extends OrderHeaderPage {

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewHeader__mainInfo')]//div[contains(@class, 'Order-OrderStatus')]//span",
            metaName = "Статус заказа")
    protected Element orderStatus;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewHeader__mainInfo')]/following-sibling::div/span[3]",
            metaName = "Статус оплаты заказа")
    protected Element paymentStatus;

    @WebFindBy(id = "info", metaName = "Вкладка 'Информация'")
    Button infoTab;

    // Actions

    @Step("Перейти на вкладку 'Информация'")
    public OrderCreatedInfoPage clickInfoTab() {
        infoTab.click();
        return new OrderCreatedInfoPage();
    }


    @WebFindBy(xpath = "//button[@id='pickings']",
            metaName = "Вкладка Сборок")
    Button pickingsTab;

    @WebFindBy(id = "giveaway",
            metaName = "Вкладка Выдачи и возврата")
    Element shipRefundTab;

    @WebFindBy(id = "main", metaName = "Вкладка Содержания")
    Element mainTab;

    @WebFindBy(xpath = "//div[contains(@class, 'lmui-View lmui-Tabs-Title-ActiveBar lmui-Tabs-Title-ActiveBar-md')]",
            metaName = "Вкладка Контроля")
    Element ControlTab;

    @Override
    public void waitForPageIsLoaded() {
        mainTab.waitForVisibility();
        waitForSpinnerDisappear();

    }

    // Actions

    @Step ("Перейти на вкладку 'Сборки'")
    public AssemblyOrderPage clickGoToPickings()  {
        pickingsTab.click();
        return new AssemblyOrderPage();

    }

    @Step ("Перейти на вкладку 'Контроль'")
    public ControlOrderPage clickGoToControlTab()  {
        ControlTab.click();
        return new ControlOrderPage();

    }

    @Step ("Перейти на вкладку 'К ВЫдаче и Возврату'")
    public GiveAwayShipOrderPage clickGoToShipRefund()  {
        shipRefundTab.click();
        return new GiveAwayShipOrderPage();

    }

    // Verifications

    @Step("Проверить, что статус заказа - {value}")
    public void shouldOrderStatusIs(String value) {
        anAssert.isEquals(orderStatus.getText().toLowerCase(), value.toLowerCase(), "Неверный статус заказа");
    }

    @Step("Проверить, что статус оплаты заказа - {value}")
    public void shouldOrderPaymentStatusIs(String value) {
        anAssert.isEquals(paymentStatus.getText().toLowerCase(), value.toLowerCase(),
                "Неверный статус оплаты заказа");
    }
}
