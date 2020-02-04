package com.leroy.magmobile.ui.pages.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
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
    private MagMobGreenSubmitButton submitBtn;

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
    public SubmittedWithdrawalOrderPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.isElementTextEqual(headerLbl,
                "Заявка на отзыв отправлена", ps);
        softAssert.isElementTextEqual(messageLbl,
                "Статус заявки можно отслеживать в списке заявок.", ps);
        softAssert.isElementTextEqual(submitBtn,
                "ПЕРЕЙТИ В СПИСОК ЗАЯВОК", ps);
        softAssert.verifyAll();
        return this;
    }

}
