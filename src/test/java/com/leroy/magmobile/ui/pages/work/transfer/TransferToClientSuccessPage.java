package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.magmobile.ui.pages.common.SuccessPage;
import com.leroy.magmobile.ui.pages.work.transfer.enums.TransferTaskTypes;
import io.qameta.allure.Step;

public class TransferToClientSuccessPage extends SuccessPage {

    private TransferTaskTypes type;

    public TransferToClientSuccessPage(TransferTaskTypes type) {
        super();
        this.type = type;
    }

    @Override
    protected String getExpectedMainBodyMessage() {
        if (type.equals(TransferTaskTypes.CLIENT_IN_SHOP_ROOM))
            return "Скоро привезем товары \n в отдел для клиента";
        else
            return "Скоро привезем товары \n на крупногабаритную кассу";
    }

    @Override
    protected String getExpectedSubBodyMessage() {
        return "Статус заявки можно отслеживать в списке заявок.";
    }

    @Override
    protected String getExpectedSubmitText() {
        return "ПЕРЕЙТИ В СПИСОК ЗАЯВОК";
    }

    // Action

    @Step("Нажать кнопку 'Перейти в список заявок'")
    public TransferRequestsPage clickSubmitButton() {
        getSubmitBtn().click();
        return new TransferRequestsPage();
    }
}
