package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.CustomerPuzWidget;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public abstract class CartEstimatePage extends MenuPage {

    public CartEstimatePage(Context context) {
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
    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl__mode-VIEW')]", metaName = "Карточка клиента")
    CustomerPuzWidget selectedCustomerCard;

    // Product area
    @WebFindBy(text = "Добавление товара")
    Element addProductLbl;
    @WebFindBy(xpath = "//input[@name='productSearchValue']", metaName = "Поле поиска товаров")
    EditBox searchProductFld;

    protected abstract CardWebWidgetList<OrderPuzWidget, OrderWebData> orders();

    // Grab information

    @Step("Получить номер документа со страницы")
    public abstract String getDocumentNumber();

    @Step("Получить статус документа со страницы")
    public abstract String getDocumentStatus();

    @Step("Получить имя создателя документа со страницы")
    public abstract String getDocumentAuthor();

    @Step("Получить информацию о документе со страницы")
    public SalesDocWebData getSalesDocData() {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setOrders(orders().getDataList());
        salesDocWebData.setNumber(getDocumentNumber());
        salesDocWebData.setStatus(getDocumentStatus());
        salesDocWebData.setAuthorName(getDocumentAuthor());
        salesDocWebData.setClient(selectedCustomerCard.collectDataFromPage());
        return salesDocWebData;
    }

    @Step("Получить информацию о добавленных в документ продуктах со страницы")
    public List<ProductOrderCardWebData> getProductDataList() {
        List<ProductOrderCardWebData> resultList = new ArrayList<>();
        for (OrderPuzWidget orderWidget : orders()) {
            resultList.addAll(orderWidget.getProductDataList());
        }
        return resultList;
    }

    // Actions

    @Step("Ввести {text} в поле для добавления товара и нажать Enter")
    public void enterTextInSearchProductField(String text) {
        searchProductFld.clearFillAndSubmit(text);
        waitForSpinnerAppearAndDisappear();
        addProductLbl.click();
    }

    @Step("Нажать на кнопку 'Добавить клиента'")
    public CartEstimatePage clickAddCustomer() {
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
    public CartEstimatePage selectCustomerByPhone(String phone) throws Exception {
        enterPhoneInSearchCustomerField(phone);
        anAssert.isTrue(customerSearchItems.waitUntilAtLeastOneElementIsPresent(short_timeout),
                "Клиент с номером +7" + phone + "нельзя выбрать");
        customerSearchItems.get(0).click();
        customerSearchItems.waitUntilElementCountEquals(0);
        return this;
    }

    // Verifications

    @Step("Проверить, что все необходимые элементы для добавления клиента доступны")
    public CartEstimatePage shouldAddingNewUserAvailable() {
        softAssert.areElementsVisible(naturalPersonBtn, legalPersonBtn, customerPhoneFld);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет телефон {val}")
    public CartEstimatePage shouldSelectedCustomerHasPhone(String val) {
        softAssert.isElementNotVisible(naturalPersonBtn);
        softAssert.isElementNotVisible(legalPersonBtn);
        softAssert.isElementNotVisible(customerPhoneFld);
        if (!val.startsWith("+7"))
            val = "+7" + val;
        softAssert.isEquals(selectedCustomerCard.getPhone(),
                val, "Ожидался другой номер телефона у выбранного клиента");
        return this;
    }

}