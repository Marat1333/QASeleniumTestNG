package com.leroy.magmobile.ui.pages.work.transfer.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class SelectPickupPointModal extends CommonMagMobilePage {

    @Override
    public void waitForPageIsLoaded() {
        toClientInShopRoomMenuItem.waitForVisibility();
    }

    @AppFindBy(xpath = "//android.widget.TextView", metaName = "Загаловок модального окна")
    protected Element headerLbl;

    @AppFindBy(text = "Клиенту в торговый зал")
    protected Element toClientInShopRoomMenuItem;

    @AppFindBy(text = "Крупногабаритная касса")
    protected Element overSizedCheckoutMenuItem;


    // Actions

    @Step("Выберите пункт меню 'Клиенту в торговый зал'")
    public void clickToClientInShopRoomMenuItem() {
        toClientInShopRoomMenuItem.click();
    }

    @Step("Выберите пункт меню 'Крупногабаритная касса'")
    public void clickOverSizedCheckoutMenuItemMenuItem() {
        overSizedCheckoutMenuItem.click();
    }

}
