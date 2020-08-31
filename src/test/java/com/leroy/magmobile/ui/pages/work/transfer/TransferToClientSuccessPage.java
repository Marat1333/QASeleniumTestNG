package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.magmobile.ui.pages.common.SuccessPage;
import io.qameta.allure.Step;

public class TransferToClientSuccessPage extends SuccessPage {

    @Override
    protected String getExpectedMainBodyMessage() {
        return "Скоро привезем товары \n в отдел для клиента";
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
