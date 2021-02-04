package com.leroy.magportal.ui.pages.common.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class ShopSelectionModal extends BaseWebPage {

    private static final String MODAL_WINDOW_XPATH = "//div[@aria-labelledby='ShopsModal_label']";

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//input[@name='shopInput']", metaName = "Поле поиска магазина")
    EditBox searchField;

    @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//div[contains(@class, 'ShopsList__item')]", metaName = "Первый магазин в списке")
    Element firstShop;

    @Step("Поиск и выбор магазина {value}")
    public void selectShop(String value){
        searchField.clearAndFill(value);
        firstShop.click();
    }

}
