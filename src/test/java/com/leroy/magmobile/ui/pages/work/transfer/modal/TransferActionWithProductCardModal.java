package com.leroy.magmobile.ui.pages.work.transfer.modal;

import com.leroy.magmobile.ui.pages.sales.orders.ActionWithProductCardModal;
import com.leroy.magmobile.ui.pages.work.transfer.TransferOrderStep1Page;
import io.qameta.allure.Step;

public class TransferActionWithProductCardModal extends ActionWithProductCardModal<TransferOrderStep1Page> {

    public TransferActionWithProductCardModal() {
        super(TransferOrderStep1Page.class);
    }

    @Step("Проверить, что экран 'Действия с товаром' отображается корректно")
    public TransferActionWithProductCardModal verifyRequiredElements() {
        softAssert.areElementsVisible(changeQuantityMenuItem,
                detailsAboutProductMenuItem, removeProductMenuItem);
        softAssert.verifyAll();
        return this;
    }
}
