package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ProductOrderCardPuzWidget;
import com.leroy.magportal.ui.pages.common.MenuPage;
import io.qameta.allure.Step;

public abstract class CreateCartEstimatePage extends MenuPage {

    public CreateCartEstimatePage(TestContext context) {
        super(context);
    }

    // Customer area
    @WebFindBy(xpath = "//button[descendant::span[text()='Добавить клиента']]",
            metaName = "Текст на кнопке 'Добавить клиента'")
    Element addCustomerBtnLbl;

    @WebFindBy(xpath = "//button[descendant::span[text()='Физ. лица и профи']]",
            metaName = "Кнопка-опция 'Физ. лица и профи'")
    Element naturalPersonBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='Юр. лица']]",
            metaName = "Кнопка-опция 'Юр. лица'")
    Element legalPersonBtn;

    @WebFindBy(xpath = "//input[@name='phone']", metaName = "Поле для ввода телефона клиента")
    EditBox customerPhoneFld;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - 19) = 'SearchResultListItem']")
    ElementList<Element> customerSearchItems;

    // When Customer is selected
    private final String CUSTOMER_VIEW_CARD_XPATH = "//div[contains(@class, 'CustomerControl-ViewCard')]";

    @WebFindBy(xpath = CUSTOMER_VIEW_CARD_XPATH + "//a/p",
            metaName = "Имя выбранного клиента")
    Element selectedCustomerName;

    @WebFindBy(xpath = CUSTOMER_VIEW_CARD_XPATH + "//a/following::span[1]",
            metaName = "Телефон выбранного клиента")
    Element selectedCustomerPhone;

    // Product area
    @WebFindBy(text = "Добавление товара")
    Element addProductLbl;
    @WebFindBy(xpath = "//input[@name='productSearchValue']", metaName = "Поле поиска товаров")
    EditBox searchProductFld;

    public abstract ElementList<ProductOrderCardPuzWidget> products();

    // Actions

    @Step("Ввести {text} в поле для добавления товара и нажать Enter")
    public void enterTextInSearchProductField(String text) {
        int productCountBefore = products().getCount();
        searchProductFld.clearFillAndSubmit(text);
        products().waitUntilElementCountEquals(productCountBefore + 1); // временное решение, возможно, стоит изменить wait
        addProductLbl.click();
    }

    @Step("Нажать на кнопку 'Добавить клиента'")
    public CreateCartEstimatePage clickAddCustomer() {
        addCustomerBtnLbl.click();
        legalPersonBtn.waitForVisibility();
        return this;
    }

    @Step("Вводим номер телефона {phone} для поиска клиента")
    public void enterPhoneInSearchCustomerField(String phone) {
        customerPhoneFld.clear();
        customerPhoneFld.click();
        customerPhoneFld.fill(phone);
        customerPhoneFld.submit();
    }

    @Step("Выбираем клиента по номеру телефона {phone}")
    public CreateCartEstimatePage selectCustomerByPhone(String phone) throws Exception {
        enterPhoneInSearchCustomerField(phone);
        customerSearchItems.get(0).click();
        customerSearchItems.waitUntilElementCountEquals(0);
        return this;
    }

    // Verifications

    @Step("Проверить, что все необходимые элементы для добавления клиента доступны")
    public CreateCartEstimatePage shouldAddingNewUserAvailable() {
        softAssert.areElementsVisible(naturalPersonBtn, legalPersonBtn, customerPhoneFld);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет телефон {val}")
    public CreateCartEstimatePage shouldSelectedCustomerHasPhone(String val) {
        if (!val.startsWith("+7"))
            val = "+7" + val;
        anAssert.isEquals(selectedCustomerPhone.getText().replaceAll(" |-", ""),
                val, "Ожидался другой номер телефона у выбранного клиента");
        return this;
    }

}