package com.leroy.pages.web.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.web.CustomerPage;
import com.leroy.pages.web.OrdersPage;
import org.openqa.selenium.By;

public class MenuPage extends BaseWebPage {

    public MenuPage(TestContext context) {
        super(context);
    }

    @WebFindBy(id = "burgerMenuButton", metaName = "Бургер меню кнопка")
    private Button burgerMenuBtn;

    @WebFindBy(text = "Магазин портал")
    private Element menuTitle;

    private static final String LEFT_MENU_SPECIFIC_ITEM_XPATH =
            "//div[contains(@class, 'lmui-View-column lmui-View-start')]//span[text()='%s']";

    public <T> T goToPage(Class<? extends BaseWebPage> pageClass) throws Exception {
        if (!menuTitle.isVisible())
            burgerMenuBtn.click();
        String expectedMenuItem;
        if (OrdersPage.class == pageClass) expectedMenuItem = "Заказы";
        else if (CustomerPage.class == pageClass) expectedMenuItem = "Клиенты";
        else
            throw new IllegalArgumentException("Переход на страницу " + pageClass.getName() + " еще не реализован через класс MenuPage");
        Element menuItem = new Element(driver, new CustomLocator(
                By.xpath(String.format(LEFT_MENU_SPECIFIC_ITEM_XPATH, expectedMenuItem)),
                "Пункт меню " + expectedMenuItem));
        menuItem.waitForVisibility();
        anAssert.isTrue(menuItem.isVisible(), "Не удалось найти пункт меню " + expectedMenuItem);
        menuItem.click();
        return (T) pageClass.getConstructor(TestContext.class).newInstance(context);
    }


}
