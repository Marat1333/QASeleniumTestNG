package com.leroy.pages.app.work;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class SubmittedWithdrawalOrderPage extends BaseAppPage {

    public SubmittedWithdrawalOrderPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[1]")
    public Element headerLbl;

    @AppFindBy(xpath = "//android.widget.TextView[2]")
    public Element messageLbl;

    @AppFindBy(accessibilityId = "Button")
    public Element submitBtn;
    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button']/android.widget.TextView")
    public Element submitBtnLbl;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitUntilTextIsEqualTo("Заявка на отзыв отправлена");
    }

    @Step("Нажать кнопку ПЕРЕЙТИ В СПИСОК ЗАЯВОК")
    public OrdersListPage clickSubmitBtn() {
        submitBtn.click();
        return new OrdersListPage(context);
    }

    public SubmittedWithdrawalOrderPage verifyVisibilityOfAllElements() {
        softAssert.isElementTextEqual(headerLbl,
                "Заявка на отзыв отправлена");
        softAssert.isElementTextEqual(messageLbl,
                "Статус заявки можно отслеживать в списке заявок.");
        softAssert.isElementTextEqual(submitBtnLbl,
                "ПЕРЕЙТИ В СПИСОК ЗАЯВОК");
        softAssert.verifyAll();
        return this;
    }

}
