package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import io.qameta.allure.Step;

public class SubmittedWithdrawalOrderPage extends BaseAppPage {

    public SubmittedWithdrawalOrderPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[1]")
    private Element headerLbl;

    @AppFindBy(xpath = "//android.widget.TextView[2]")
    private Element messageLbl;

    @AppFindBy(accessibilityId = "Button")
    private MagMobSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitUntilTextIsEqualTo("Заявка на отзыв отправлена");
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажать кнопку ПЕРЕЙТИ В СПИСОК ЗАЯВОК")
    public OrdersListPage clickSubmitBtn() {
        submitBtn.click();
        return new OrdersListPage(context);
    }

    /* ------------------------- Verifications  -------------------------- */

    @Step("Проверить, что страница с уведомлением успешно созданный заявки отображается корректно")
    public SubmittedWithdrawalOrderPage verifyVisibilityOfAllElements() {
        softAssert.isElementTextEqual(headerLbl,
                "Заявка на отзыв отправлена");
        softAssert.isElementTextEqual(messageLbl,
                "Статус заявки можно отслеживать в списке заявок.");
        softAssert.isElementTextEqual(submitBtn,
                "ПЕРЕЙТИ В СПИСОК ЗАЯВОК");
        softAssert.verifyAll();
        return this;
    }

}
