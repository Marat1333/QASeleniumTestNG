package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public abstract class OrderCreatedPage extends OrderHeaderPage {

    @WebFindBy(xpath = "//button[@id='pickings']",
            metaName = "Вкладка Сборок")
    Button pickingsTab;

    @WebFindBy(id = "giveaway",
            metaName = "Вкладка Выдачи и возврата")
    Element shipRefundTab;

    @WebFindBy(id = "main", metaName = "Вкладка Содержания")
    Element mainTab;

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

    @Step ("Перейти на вкладку 'К ВЫдаче и Возврату'")
    public GiveAwayShipOrderPage clickGoToShipRefund()  {
        shipRefundTab.click();
        return new GiveAwayShipOrderPage();

    }


    // Verifications


    @Step("Проверить, что статус заказа - {value}")
    public void shouldOrderStatusIs(String value) {
        anAssert.isEquals(E("//div[contains(@class, 'OrderViewHeader')]//span[contains(@class, 'Status-container')]").getText(),
                value, "Неверный статус заказа");
    }
}
