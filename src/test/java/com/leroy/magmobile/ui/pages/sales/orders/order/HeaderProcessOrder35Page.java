package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public abstract class HeaderProcessOrder35Page extends CommonMagMobilePage {

    @AppFindBy(text = "Оформление заказа")
    protected Element headerLbl;

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    protected Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ORDER_SCREEN_ID']//android.view.ViewGroup[@index='6']/android.view.ViewGroup[1]",
            metaName = "Иконка корзины")
    protected Element headerCartIcon;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ORDER_SCREEN_ID']//android.view.ViewGroup[@index='6']/android.view.ViewGroup[4]",
            metaName = "Иконка корзины")
    protected Element headerProcessOrderIcon;

    @Step("Нажмите на иконку корзины для перехода к ней")
    public CartProcessOrder35Page clickCartIcon() {
        headerCartIcon.click();
        return new CartProcessOrder35Page();
    }

    @Step("Нажмите на иконку корзины для перехода к ней")
    public ProcessOrder35Page clickProcessOrderIcon() {
        headerProcessOrderIcon.click();
        return new ProcessOrder35Page();
    }

}
