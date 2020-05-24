package com.leroy.magportal.ui.pages.common;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.NewFeaturesModalWindow;
import com.leroy.magportal.ui.pages.common.modal.ShopSelectionModal;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.customers.CustomerPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class MenuPage extends MagPortalBasePage {

    public MenuPage(Context context) {
        super(context);
    }

    // User Profile

    @WebFindBy(xpath = "//span[@id='Button']//div", metaName = "Кнопка профиль пользователя")
    Element userProfileBtn;

    @WebFindBy(xpath = "(//div[contains(@class, 'Main-AvatarDropDownItem')])[2]",
            metaName = "Поле выбора магазина")
    Element userProfileShopArea;


    // Left menu

    @WebFindBy(id = "burgerMenuButton", metaName = "Бургер меню кнопка")
    private Button burgerMenuBtn;

    @WebFindBy(text = "Магазин портал")
    private Element menuTitle;

    private static final String LEFT_MENU_SPECIFIC_ITEM_XPATH =
            "//div[contains(@class, 'lmui-View-column lmui-View-start')]//span[text()='%s']";

    public <T extends BaseWebPage> T goToPage(Class<T> pageClass) throws Exception {
        if (!menuTitle.isVisible())
            burgerMenuBtn.click();
        String expectedMenuItem;
        if (OrderHeaderPage.class == pageClass) expectedMenuItem = "Заказы";
        else if (CustomerPage.class == pageClass) expectedMenuItem = "Клиенты";
        else if (SearchProductPage.class == pageClass) expectedMenuItem = "Товары";
        else if (CartPage.class == pageClass) expectedMenuItem = "Корзины";
        else if (EstimatePage.class == pageClass) expectedMenuItem = "Сметы";
        else
            throw new IllegalArgumentException("Переход на страницу " + pageClass.getName() + " еще не реализован через класс MenuPage");
        Element menuItem = new Element(driver, new CustomLocator(
                By.xpath(String.format(LEFT_MENU_SPECIFIC_ITEM_XPATH, expectedMenuItem)),
                "Пункт меню " + expectedMenuItem));
        menuItem.waitForVisibility();
        anAssert.isTrue(menuItem.isVisible(), "Не удалось найти пункт меню " + expectedMenuItem);
        menuItem.click();
        return (T) pageClass.getConstructor(Context.class).newInstance(context);
    }

    @Step("Подождать появления окна с новыми фичами и закрыть его, если оно появится")
    public MenuPage closeNewFeaturesModalWindowIfExist() {
        NewFeaturesModalWindow modalWindow = new NewFeaturesModalWindow(driver);
        modalWindow.waitForVisibility(short_timeout);
        if (modalWindow.isVisible())
            modalWindow.clickSubmitButton();
        modalWindow.waitForInvisibility();
        return this;
    }

    @Step("Выберите магазин {value} в профиле пользователя")
    public MenuPage selectShopInUserProfile(String value) throws Exception {
        userProfileBtn.click();
        userProfileShopArea.click();
        new ShopSelectionModal(context)
                .selectShop(value)
                .clickSaveButton();
        waitForSpinnerAppearAndDisappear();
        return this;
    }


}
