package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.constants.DefectConst;
import com.leroy.core.annotations.Form;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.models.salesdoc.ShortSalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ConfirmRemoveProductModal;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ExtendedSearchModal;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ShortCartEstimateDocumentCardWidget;
import com.leroy.magportal.ui.pages.common.LeftDocumentListPage;
import com.leroy.magportal.ui.pages.common.modal.ConfirmRemoveModal;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.testng.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CartEstimatePage extends
        LeftDocumentListPage<ShortCartEstimateDocumentCardWidget, ShortSalesDocWebData> {

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
    @Form
    CustomerSearchForm customerSearchForm;

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
    public SalesDocWebData getSalesDocData() throws Exception {
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
        return customerSearchForm.getCustomerData();
    }

    @Step("Получить информацию о добавленных в документ продуктах со страницы")
    public List<ProductOrderCardWebData> getProductDataList() throws Exception {
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
        searchProductFld.clear(true);
        searchProductFld.clearFillAndSubmit(text);
        waitForSpinnerAppearAndDisappear(short_timeout);
        if (!ExtendedSearchModal.isModalVisible()) {
            if (this instanceof CartPage)
                waitForSpinnerAppearAndDisappear();
            addProductLbl.click();
        }
        waitForSpinnerDisappear();
    }

    @Step("Скопировать товар #{productIdx} из заказа #{orderIdx}")
    public CartEstimatePage copyProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        OrderPuzWidget orderWidget = orders().get(orderIdx);
        int productCountBefore = orderWidget.getProductWidgets().getCount();
        orderWidget.getProductWidget(productIdx).clickCopy();
        waitForSpinnerAppearAndDisappear();
        orderWidget.getProductWidgets().waitUntilElementCountEqualsOrAbove(productCountBefore + 1);
        return this;
    }

    @Step("Скопировать товар #{productIdx}")
    public CartEstimatePage copyProductByIndex(int productIdx) throws Exception {
        return copyProductByIndex(1, productIdx);
    }

    @Step("Установить кол-во {quantity} для товара #{productIdx} из заказа #{orderIdx}")
    public CartEstimatePage changeQuantityProductByIndex(Number quantity, int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders().get(orderIdx).getProductWidget(productIdx).editQuantity(quantity);
        if (this instanceof CartPage)
            waitForSpinnerAppearAndDisappear();
        return this;
    }

    public CartEstimatePage changeQuantityProductByIndex(Number quantity, int productIdx) throws Exception {
        return changeQuantityProductByIndex(quantity, 1, productIdx);
    }

    @Step("Нажать '+' для увеличения кол-ва товара #{productIdx} из заказа #{orderIdx}")
    public CartEstimatePage increaseQuantityProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders().get(orderIdx).getProductWidget(productIdx).clickPlusQuantity();
        if (this instanceof CartPage)
            waitForSpinnerAppearAndDisappear();
        return this;
    }

    public CartEstimatePage increaseQuantityProductByIndex(int productIdx) throws Exception {
        return increaseQuantityProductByIndex(1, productIdx);
    }

    @Step("Нажать '-' для уменьшения кол-ва товара #{productIdx} из заказа #{orderIdx}")
    public CartEstimatePage decreaseQuantityProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders().get(orderIdx).getProductWidget(productIdx).clickMinusQuantity();
        if (this instanceof CartPage)
            waitForSpinnerAppearAndDisappear();
        return this;
    }

    public CartEstimatePage decreaseQuantityProductByIndex(int productIdx) throws Exception {
        return decreaseQuantityProductByIndex(1, productIdx);
    }

    @Step("Удалить товар #{productIdx} из заказа #{orderIdx}")
    public CartEstimatePage removeProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        OrderPuzWidget orderWidget = orders().get(orderIdx);
        int productCountBefore = orderWidget.getProductWidgets().getCount();
        orderWidget.getProductWidget(productIdx).clickDelete();
        new ConfirmRemoveProductModal()
                .verifyRequiredElements()
                .clickYesButton();
        waitForSpinnerAppearAndDisappear();
        if (productCountBefore > 1)
            orderWidget.getProductWidgets().waitUntilElementCountEquals(productCountBefore - 1);
        else
            searchProductFld.waitForInvisibility();
        waitForSpinnerDisappear();
        return this;
    }

    public CartEstimatePage removeProductByIndex(int productIdx) throws Exception {
        return removeProductByIndex(1, productIdx);
    }

    public CartEstimatePage clickAddCustomer() {
        customerSearchForm.clickAddCustomer();
        return this;
    }

    public CreateCustomerForm clickOptionEditCustomer() {
        return customerSearchForm.clickOptionEditCustomer();
    }

    public CartEstimatePage clickOptionSelectAnotherCustomer() {
        customerSearchForm.clickOptionSelectAnotherCustomer();
        return this;
    }

    public CartEstimatePage clickOptionRemoveCustomer() {
        customerSearchForm.clickOptionRemoveCustomer();
        return this;
    }

    @Step("Удалить выбранного клиента")
    public CartEstimatePage removeSelectedCustomer() {
        clickOptionRemoveCustomer();
        new ConfirmRemoveModal().clickConfirmBtn();
        return this;
    }

    public CartEstimatePage selectSearchType(String value) throws Exception {
        customerSearchForm.selectSearchType(value);
        return this;
    }

    public void enterPhoneInSearchCustomerField(String value) {
        customerSearchForm.enterPhoneInSearchCustomerField(value);
    }

    public void enterCardNumberInSearchCustomerField(String value) {
        customerSearchForm.enterCardNumberInSearchCustomerField(value);
    }

    public void enterEmailInSearchCustomerField(String value) {
        customerSearchForm.enterEmailInSearchCustomerField(value);
    }

    public CartEstimatePage selectCustomerByPhone(String phone) throws Exception {
        customerSearchForm.selectCustomerByPhone(phone);
        return this;
    }

    public CartEstimatePage selectCustomerByEmail(String email) throws Exception {
        customerSearchForm.selectCustomerByEmail(email);
        return this;
    }

    public CreateCustomerForm clickCreateCustomerButton() {
        return customerSearchForm.clickCreateCustomerButton();
    }

    // Verifications

    public CartEstimatePage shouldAddingNewUserAvailable() {
        customerSearchForm.shouldAddingNewUserAvailable();
        return this;
    }

    public CartEstimatePage shouldSearchTypeOptionsAreCorrected() throws Exception {
        customerSearchForm.shouldSearchTypeOptionsAreCorrected();
        return this;
    }

    public CartEstimatePage shouldSelectedCustomerHasPhone(String val) {
        customerSearchForm.shouldSelectedCustomerHasPhone(val);
        return this;
    }

    public CartEstimatePage shouldSelectedCustomerHasEmail(String val) {
        customerSearchForm.shouldSelectedCustomerHasEmail(val);
        return this;
    }

    public CartEstimatePage shouldSelectedCustomerHasName(String val) {
        customerSearchForm.shouldSelectedCustomerHasName(val);
        return this;
    }

    public CartEstimatePage shouldSelectedCustomerHasCardNumber(String val) {
        customerSearchForm.shouldSelectedCustomerHasCardNumber(val);
        return this;
    }

    public CartEstimatePage shouldSelectedCustomerIs(SimpleCustomerData expectedCustomerData) {
        customerSearchForm.shouldSelectedCustomerIs(expectedCustomerData);
        return this;
    }

    public CartEstimatePage clickCardSearchFieldAndCheckThatDefaultValueIs(String value) {
        customerSearchForm.clickCardSearchFieldAndCheckThatDefaultValueIs(value);
        return this;
    }

    public CartEstimatePage shouldErrorTooltipCustomerIsRequiredVisible() {
        customerSearchForm.shouldErrorTooltipCustomerIsRequiredVisible();
        return this;
    }

    @Step("Проверить, что в документ добавлены товары с ЛМ кодами: {lmCodes}")
    public CartEstimatePage shouldDocumentHasProducts(List<String> lmCodes) throws Exception {
        List<ProductOrderCardWebData> productData = getProductDataList();
        List<String> actualLmCodes = productData.stream().map(
                ProductOrderCardWebData::getLmCode).collect(Collectors.toList());
        anAssert.isEquals(actualLmCodes, lmCodes, "Ожидались другие товары в смете");
        return this;
    }

    @Step("Проверить, что в документе {index}-ый товар имеет определенные характеристики (expectedProductData)")
    public CartEstimatePage shouldDocumentHasProduct(int index, ProductOrderCardWebData expectedProductData) throws Exception {
        index--;
        List<ProductOrderCardWebData> actualProductData = getProductDataList();
        if (DefectConst.STOCK_ISSUE)
            expectedProductData.setAvailableTodayQuantity(null);
        actualProductData.get(index).assertEqualsNotNullExpectedFields(expectedProductData, 0, index);
        return this;
    }

    @Step("Проверить, что документ (Смета / Корзина) в списке слева отображается")
    public void shouldDocumentIsPresent(ShortSalesDocWebData expectedDocumentData) throws Exception {
        List<ShortSalesDocWebData> documentList = documentCardList().getDataList()
                .stream().filter(d -> d.getNumber().equals(expectedDocumentData.getNumber()))
                .collect(Collectors.toList());
        anAssert.isEquals(documentList.size(), 1,
                String.format("Документ с номером %s не найден", expectedDocumentData.getNumber()));
        documentList.get(0).assertEqualsNotNullExpectedFields(expectedDocumentData);
    }

    /**
     * Проверить, что на странице сметы содержатся ожидаемые данные
     */
    protected void shouldDocumentHasData(SalesDocWebData expectedDocumentData) throws Exception {
        SalesDocWebData actualEstimateData = getSalesDocData();
        if (expectedDocumentData.getNumber() == null)
            anAssert.isFalse(getDocumentNumber().isEmpty(), "Отсутствует номер документа");
        else
            softAssert.isEquals(actualEstimateData.getNumber(), expectedDocumentData.getNumber(),
                    "Ожидался другой номер документа");
        if (expectedDocumentData.getAuthorName() != null) {
            softAssert.isEquals(actualEstimateData.getAuthorName(), expectedDocumentData.getAuthorName(),
                    "Ожидался другой автор документа");
        }
        if (expectedDocumentData.getStatus() != null) {
            softAssert.isEquals(actualEstimateData.getStatus(), expectedDocumentData.getStatus().toUpperCase(),
                    "Ожидался другой статус документа");
        }
        if (expectedDocumentData.getCreationDate() != null) {
            softAssert.isEquals(actualEstimateData.getCreationDate(), expectedDocumentData.getCreationDate(),
                    "Ожидался другая дата создания документа");
        } else {
            softAssert.isFalse(Strings.isNullOrEmpty(actualEstimateData.getCreationDate()),
                    "Дата создания документа не отображается");
        }
        if (actualEstimateData.getClient() == null || actualEstimateData.getClient().getName() == null) {
            softAssert.isTrue(expectedDocumentData.getClient() == null || expectedDocumentData.getClient().getName() == null,
                    "Информация о клиенте отсутствует");
        } else {
            softAssert.isEquals(actualEstimateData.getClient(), expectedDocumentData.getClient(),
                    "Ожидался другой клиент в документе");
        }
        anAssert.isEquals(actualEstimateData.getOrders().size(),
                expectedDocumentData.getOrders().size(),
                "Ожидалось другое кол-во заказов в документе");
        for (int i = 0; i < actualEstimateData.getOrders().size(); i++) {
            OrderWebData actualOrder = actualEstimateData.getOrders().get(i);
            OrderWebData expectedOrder = expectedDocumentData.getOrders().get(i);

            anAssert.isEquals(actualOrder.getProductCardDataList().size(),
                    expectedOrder.getProductCardDataList().size(),
                    "Ожидалось другое кол-во товаров в документе (Заказ #" + (i + 1) + ")");
            for (int j = 0; j < actualOrder.getProductCardDataList().size(); j++) {
                ProductOrderCardWebData actualProduct = actualOrder.getProductCardDataList().get(j);
                ProductOrderCardWebData expectedProduct = expectedOrder.getProductCardDataList().get(j);
                actualProduct.assertEqualsNotNullExpectedFields(expectedProduct, i, j);
            }

            softAssert.isEquals(actualOrder.getTotalPrice(), expectedOrder.getTotalPrice(),
                    "Заказ #" + (i + 1) + " Неверное сумма итого");
            if (expectedOrder.getTotalWeight() != null) {
                double totalSelectedQuantity = expectedOrder.getProductCardDataList().stream().mapToDouble(ProductOrderCardWebData::getSelectedQuantity).sum();
                softAssert.isTrue(Math.abs(actualOrder.getTotalWeight() - expectedOrder.getTotalWeight()) <= totalSelectedQuantity * 0.011,
                        "Заказ #" + (i + 1) + " Неверный итого вес");
            } else {
                double expectedTotalWeight = 0.0;
                for (ProductOrderCardWebData pr : actualOrder.getProductCardDataList()) {
                    expectedTotalWeight = ParserUtil.plus(pr.getWeight(), expectedTotalWeight, 2);
                }
                softAssert.isTrue(Math.abs(actualOrder.getTotalWeight() - expectedTotalWeight) <= 0.03,
                        "Заказ #" + (i + 1) + " Итого вес должен быть равен сумме весов всех продуктов;\n" +
                                "Actual: " + actualOrder.getTotalWeight() + "; Expected: " + expectedTotalWeight + ";");
            }
            softAssert.isEquals(actualOrder.getProductCount(), expectedOrder.getProductCount(),
                    "Заказ #" + (i + 1) + " Неверное кол-во продуктов в заказе");
        }
        softAssert.verifyAll();
    }


}