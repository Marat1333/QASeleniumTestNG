package com.leroy.magportal.ui.pages.common.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.webelements.commonelements.PuzSelectControl;
import io.qameta.allure.Step;

public class ShopSelectionModal extends MagPortalBasePage {

    @WebFindBy(id = "regions")
    PuzSelectControl regionsComboBox;

    @WebFindBy(id = "shops")
    PuzSelectControl shopComboBox;

    @WebFindBy(xpath = "//button[descendant::span[text()='Сохранить']]", metaName = "Кнопка Сохранить")
    Button saveBtn;

    @Step("Выбираем магазин {value} в выпадающем списке")
    public ShopSelectionModal selectShop(String value) throws Exception {
        shopComboBox.selectOption(value);
        return this;
    }

    @Step("Выбираем регион {value} в выпадающем списке")
    public ShopSelectionModal selectRegion(String value) throws Exception {
        regionsComboBox.selectOption(value);
        return this;
    }

    @Step("Нажимаем сохранить")
    public void clickSaveButton() throws Exception {
        saveBtn.click();
    }


}
