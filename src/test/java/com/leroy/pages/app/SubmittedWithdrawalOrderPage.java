package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.work.OrdersListPage;
import org.openqa.selenium.WebDriver;

public class SubmittedWithdrawalOrderPage extends BaseAppPage {

    public SubmittedWithdrawalOrderPage(WebDriver driver) {
        super(driver);
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

    public OrdersListPage clickSubmitBtn() {
        submitBtn.click();
        return new OrdersListPage(driver);
    }

}
