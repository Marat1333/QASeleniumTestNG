package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.models.salesdoc.ShortSalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.CustomerPuzWidget;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ShortCartEstimateDocumentCardWidget;
import com.leroy.magportal.ui.pages.common.LeftDocumentListPage;
import com.leroy.magportal.ui.pages.common.modal.ConfirmRemoveModal;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CartEstimatePage extends
        LeftDocumentListPage<ShortCartEstimateDocumentCardWidget, ShortSalesDocWebData> {

    public CartEstimatePage(Context context) {
        super(context);
    }

    public static class SearchType {
        public static final String PHONE = "Телефон";
        public static final String CARD = "Карта клиента";
        public static final String EMAIL = "Email";
    }

    // Top

    @WebFindBy(xpath = "//input[@name='docId']", metaName = "Поле поиска документа")
    EditBox searchDocumentFld;

    // Left menu with document list
    @WebFindBy(xpath = "//div[contains(@class, 'Refresh-banner')]//button",
            metaName = "Кнопка Обновить список документов")
    private Button refreshDocumentListBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Documents-ListItemCard__content')]",
            clazz = ShortCartEstimateDocumentCardWidget.class)
    private CardWebWidgetList<ShortCartEstimateDocumentCardWidget, ShortSalesDocWebData> documentCardList;

    @Override
    protected Button refreshDocumentListBtn() {
        return refreshDocumentListBtn;
    }

    @Override
    protected CardWebWidgetList<ShortCartEstimateDocumentCardWidget, ShortSalesDocWebData> documentCardList() {
        return documentCardList;
    }

    // Customer area
    @WebFindBy(xpath = "//button[descendant::span[text()='Добавить клиента']]",
            metaName = "Текст на кнопке 'Добавить клиента'")
    Element addCustomerBtnLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl__error-tooltip')]//span",
            metaName = "Тултип с ошибкой")
    Element errorTooltipLbl;

    @WebFindBy(xpath = "//button[descendant::span[text()='Физ. лица и профи']]",
            metaName = "Кнопка-опция 'Физ. лица и профи'")
    Element naturalPersonBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='Юр. лица']]",
            metaName = "Кнопка-опция 'Юр. лица'")
    Element legalPersonBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Common-Filter__select')]", metaName = "Контрол выбора типа поиска")
    PuzComboBox searchTypeComboBox;

    @WebFindBy(xpath = "//input[@name='phone']", metaName = "Поле для ввода телефона клиента")
    EditBox customerPhoneSearchFld;
    @WebFindBy(xpath = "//input[@name='card']", metaName = "Поле для ввода номера карты клиента")
    EditBox customerCardSearchFld;
    @WebFindBy(xpath = "//input[@name='email']", metaName = "Поле для ввода email клиента")
    EditBox customerEmailSearchFld;
    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl-SearchMode__menu')]//button[descendant::span[text()='Создать клиента']]",
            metaName = "Кнопка 'Создать клиента'")
    Element createCustomerBtn;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - 19) = 'SearchResultListItem']")
    ElementList<Element> customerSearchItems;

    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl-ViewCard__action-btn')]",
            metaName = "Кнопка '...' для раскрытия меню с действиями над клиентом")
    Element customerActionBtn;

    @WebFindBy(id = "editCustomerButton", metaName = "Опция 'Редактировать данные клиента'")
    Element editCustomerOptionBtn;
    @WebFindBy(id = "searchCustomerButton", metaName = "Опция 'Выбрать другого клиента'")
    Element searchCustomerOptionBtn;
    @WebFindBy(id = "clearCustomerButton", metaName = "Опция 'Удалить клиента'")
    Element clearCustomerOptionBtn;

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

    @Step("Получить дату создания документа со страницы")
    public abstract String getCreationDate();

    @Step("Получить информацию о документе со страницы")
    public SalesDocWebData getSalesDocData() {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setOrders(orders().getDataList());
        salesDocWebData.setNumber(getDocumentNumber());
        salesDocWebData.setCreationDate(getCreationDate());
        salesDocWebData.setStatus(getDocumentStatus());
        salesDocWebData.setAuthorName(getDocumentAuthor());
        salesDocWebData.setClient(getCustomerData());
        return salesDocWebData;
    }

    @Step("Получить информацию о клиенте со страницы")
    public SimpleCustomerData getCustomerData() {
        return selectedCustomerCard.collectDataFromPage();
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
    public CartEstimatePage enterTextInSearchDocumentField(String text) {
        searchDocumentFld.click();
        searchDocumentFld.clearFillAndSubmit(text);
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Ввести {text} в поле для добавления товара и нажать Enter")
    public void enterTextInSearchProductField(String text) {
        searchProductFld.clearFillAndSubmit(text);
        waitForSpinnerAppearAndDisappear();
        waitForSpinnerDisappear();
        addProductLbl.click();
    }

    @Step("Нажать на кнопку 'Добавить клиента'")
    public CartEstimatePage clickAddCustomer() {
        addCustomerBtnLbl.click();
        legalPersonBtn.waitForVisibility();
        return this;
    }

    @Step("Действия с клиентом: Редактировать данные клиента")
    public CreateCustomerForm clickOptionEditCustomer() {
        customerActionBtn.click();
        editCustomerOptionBtn.click();
        return new CreateCustomerForm(context);
    }

    @Step("Действия с клиентом: Выбрать другого клиента")
    public CartEstimatePage clickOptionSelectAnotherCustomer() {
        customerActionBtn.click();
        searchCustomerOptionBtn.click();
        return this;
    }

    @Step("Действия с клиентом: Удалить клиента")
    public CartEstimatePage clickOptionRemoveCustomer() {
        customerActionBtn.click();
        clearCustomerOptionBtn.click();
        return this;
    }

    @Step("Удалить выбранного клиента")
    public CartEstimatePage removeSelectedCustomer() {
        clickOptionRemoveCustomer();
        new ConfirmRemoveModal(context).clickConfirmBtn();
        return this;
    }

    @Step("Выбираем '{value}' тип поиска клиента")
    public CartEstimatePage selectSearchType(String value) throws Exception {
        searchTypeComboBox.selectOption(value);
        return this;
    }

    private void enterTextInSearchCustomerField(EditBox inputBox, String value) {
        if (value.startsWith("+7"))
            value = value.substring(2);
        inputBox.clear();
        inputBox.click();
        inputBox.fill(value);
        inputBox.submit();
        waitForSpinnerAppearAndDisappear();
    }

    @Step("Вводим номер телефона {value} для поиска клиента")
    public void enterPhoneInSearchCustomerField(String value) {
        enterTextInSearchCustomerField(customerPhoneSearchFld, value);
    }

    @Step("Вводим номер карты {value} для поиска клиента")
    public void enterCardNumberInSearchCustomerField(String value) {
        enterTextInSearchCustomerField(customerCardSearchFld, value);
    }

    @Step("Вводим email {value} для поиска клиента")
    public void enterEmailInSearchCustomerField(String value) {
        enterTextInSearchCustomerField(customerEmailSearchFld, value);
    }

    @Step("Выбираем клиента по номеру телефона {phone}")
    public CartEstimatePage selectCustomerByPhone(String phone) throws Exception {
        if (phone.startsWith("+7"))
            phone = phone.substring(2);
        enterPhoneInSearchCustomerField(phone);
        int foundCustomerAccount = customerSearchItems.getCount();
        anAssert.isTrue(foundCustomerAccount > 0 || !customerPhoneSearchFld.isVisible(),
                "Клиент с номером +7" + phone + " не удалось выбрать");
        if (foundCustomerAccount > 0) {
            customerSearchItems.get(0).click();
            customerSearchItems.waitUntilElementCountEquals(0);
        }
        return this;
    }

    @Step("Выбираем клиента по email {email}")
    public CartEstimatePage selectCustomerByEmail(String email) throws Exception {
        enterEmailInSearchCustomerField(email);
        anAssert.isFalse(customerPhoneSearchFld.isVisible(),
                "Клиент с email " + email + " не удалось выбрать");
        return this;
    }

    @Step("Нажать кнопку 'Создать клиента' в всплывающем окне")
    public CreateCustomerForm clickCreateCustomerButton() {
        anAssert.isTrue(createCustomerBtn.isVisible(tiny_timeout),
                createCustomerBtn.getMetaName() + " не отображается");
        createCustomerBtn.click();
        return new CreateCustomerForm(context);
    }

    // Verifications

    @Step("Проверить, что все необходимые элементы для добавления клиента доступны")
    public CartEstimatePage shouldAddingNewUserAvailable() {
        softAssert.areElementsVisible(naturalPersonBtn, legalPersonBtn, customerPhoneSearchFld);
        softAssert.isEquals(customerPhoneSearchFld.getText(), "+7 (___) ___-__-__",
                "Поле для заполнения телефона должно быть пустым");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в выпдающем списке корректные опции")
    public CartEstimatePage shouldSearchTypeOptionsAreCorrected() throws Exception {
        anAssert.isEquals(searchTypeComboBox.getOptionList(),
                Arrays.asList(SearchType.PHONE, SearchType.CARD, SearchType.EMAIL),
                "Ожидались другие опции выбора типа поиска");
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет телефон {val}")
    public CartEstimatePage shouldSelectedCustomerHasPhone(String val) {
        if (!val.startsWith("+7"))
            val = "+7" + val;
        softAssert.isEquals(selectedCustomerCard.getPhone(),
                val, "Ожидался другой номер телефона у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет email {val}")
    public CartEstimatePage shouldSelectedCustomerHasEmail(String val) {
        softAssert.isEquals(selectedCustomerCard.getEmail(),
                val, "Ожидался другой email у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет имя {val}")
    public CartEstimatePage shouldSelectedCustomerHasName(String val) {
        softAssert.isEquals(selectedCustomerCard.getName(),
                val, "Ожидалось другое имя у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет номер карты {val}")
    public CartEstimatePage shouldSelectedCustomerHasCardNumber(String val) {
        softAssert.isEquals(selectedCustomerCard.getCardNumber(),
                val, "Ожидался другой номер телефона у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет следующие данные: {expectedCustomerData}")
    public CartEstimatePage shouldSelectedCustomerIs(SimpleCustomerData expectedCustomerData) {
        softAssert.isElementNotVisible(naturalPersonBtn);
        softAssert.isElementNotVisible(legalPersonBtn);
        softAssert.isElementNotVisible(customerPhoneSearchFld);
        shouldSelectedCustomerHasPhone(expectedCustomerData.getPhoneNumber());
        if (expectedCustomerData.getEmail() != null)
            shouldSelectedCustomerHasEmail(expectedCustomerData.getEmail());
        shouldSelectedCustomerHasName(expectedCustomerData.getName());
        softAssert.verifyAll();
        return this;
    }

    @Step("Кликнуть и проверить, что поле 'Карта' и содержит значение: {value}")
    public CartEstimatePage clickCardSearchFieldAndCheckThatDefaultValueIs(String value) {
        customerCardSearchFld.click();
        anAssert.isEquals(customerCardSearchFld.getText().replaceAll("_", "").trim(),
                value.replaceAll(" ", ""),
                "Ожидалось другое значение в поле поиска по Карте");
        return this;
    }

    @Step("Проверить, что тултип ошибки 'Необходимо добавить клиента' отображается")
    public CartEstimatePage shouldErrorTooltipCustomerIsRequiredVisible() {
        anAssert.isElementTextEqual(errorTooltipLbl, "Необходимо добавить клиента");
        return this;
    }

}