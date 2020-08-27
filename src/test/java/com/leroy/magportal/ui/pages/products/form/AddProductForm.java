package com.leroy.magportal.ui.pages.products.form;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ExtendedSearchModal;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class AddProductForm extends MagPortalBasePage {

    @WebFindBy(text = "Добавление товара")
    Element addProductLbl;
    @WebFindBy(xpath = "//input[@name='productSearchValue']", metaName = "Поле поиска товаров")
    EditBox searchProductFld;

    // Actions

    @Step("Ввести {text} в поле для добавления товара и нажать Enter")
    public void enterTextInSearchProductField(String text) {
        searchProductFld.clear(true);
        searchProductFld.clearFillAndSubmit(text);
        waitForSpinnerAppearAndDisappear(short_timeout);
        waitForSpinnerAppearAndDisappear(1);
        if (!ExtendedSearchModal.isModalVisible()) {
            addProductLbl.click();
        }
        anAssert.isFalse(E("//div[contains(@class, 'Modal')]//*[text()='Изменения не сохранены']").isVisible(),
                "Появилось окно 'Изменения не сохранены'");
    }

    // Verifications
    @Step("Проверить, что поле поиска товаров отображается")
    public AddProductForm shouldSearchFieldIsVisible() {
        anAssert.isElementVisible(searchProductFld);
        return this;
    }

}
