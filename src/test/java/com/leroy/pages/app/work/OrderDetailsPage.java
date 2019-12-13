package com.leroy.pages.app.work;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.widgets.ProductCardWidget;
import org.openqa.selenium.WebDriver;

public class OrderDetailsPage extends BaseAppPage {

    public OrderDetailsPage(WebDriver driver) {
        super(driver);
    }

    // Parameters areas
    @AppFindBy(xpath = "//android.widget.TextView[@text='Способ пополнения']/following::android.widget.TextView[1]",
            metaName = "Способ пополнения")
    public Element replenishmentMethod;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Дата доставки товара']/following::android.widget.TextView[1]",
            metaName = "Дата доставки товара")
    public Element deliveryDate;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Ожидаемое время доставки товара']/following::android.widget.TextView[1]",
            metaName = "Ожидаемое время доставки товара")
    public Element deliveryTime;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Комментарий']/following::android.widget.TextView[1]",
            metaName = "Комментарий")
    public Element comment;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='ТОВАРЫ НА ОТЗЫВ']]/following-sibling::android.view.ViewGroup/android.view.ViewGroup",
            clazz = ProductCardWidget.class)
    public ElementList<ProductCardWidget> productsForWithdrawal;

    @Override
    public void waitForPageIsLoaded() {
        replenishmentMethod.waitForVisibility();
        deliveryDate.waitForVisibility();
        comment.waitForVisibility();
    }
}
