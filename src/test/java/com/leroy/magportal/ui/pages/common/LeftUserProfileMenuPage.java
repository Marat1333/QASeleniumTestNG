package com.leroy.magportal.ui.pages.common;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.modal.ShopSelectionModal;

public class LeftUserProfileMenuPage extends BaseWebPage {

    @WebFindBy(xpath = "//div[contains(@class, 'uflm-MenuItem')][1]", metaName = "Пункт меню выбора магазина")
    Element shopMenuItem;

    void selectShop(String value) throws Exception {
        shopMenuItem.click();
        new ShopSelectionModal()
                .selectShop(value);
    }
}
