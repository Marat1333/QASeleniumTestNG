package com.leroy.magportal.ui.pages.common;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.NewFeaturesModalWindow;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.common.modal.ShopSelectionModal;
import com.leroy.magportal.ui.pages.customers.CustomerPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class MenuPage extends BaseWebPage {

    public MenuPage() {
        super();
        driver.switchTo().defaultContent();
    }

    // User Profile

    @WebFindBy(xpath = "//div[contains(@class, 'UserCaption')]//p", metaName = "Имя пользователя")
    Element userNameLbl;


    // Left menu

    @WebFindBy(xpath = "//button[contains(@class, 'burger-btn')]", metaName = "Бургер меню кнопка")
    private Button burgerMenuBtn;

    @WebFindBy(text = "Магазин портал")
    private Element menuTitle;

    @WebFindBy(xpath = "//button[contains(@class, 'UserCardLg__supportBtn')]", metaName = "Написать в поддержку")
    Button supportButton;

    private static final String LEFT_MENU_SPECIFIC_ITEM_XPATH =
            "//div[contains(@class, 'side-menu-buttons-container')]//button[descendant::span[text()='%s']]";

    private void openMenu() {
        if (!supportButton.isVisible())
            burgerMenuBtn.click();
    }

    public <T extends BaseWebPage> T goToPage(Class<T> pageClass) throws Exception {
        openMenu();
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
        return pageClass.getConstructor().newInstance();
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

    @Step("Открываем левое меню, идет в профиль пользователя и выбераем магазин {value}")
    public MenuPage selectShopInUserProfile(String value) throws Exception {
        openMenu();
        userNameLbl.click();
        new LeftUserProfileMenuPage()
                .selectShop(value);
        return this;
    }


}
