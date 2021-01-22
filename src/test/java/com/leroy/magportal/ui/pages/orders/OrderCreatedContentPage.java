package com.leroy.magportal.ui.pages.orders;

import com.leroy.constants.DefectConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Form;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.pages.orders.modal.RemoveOrderModal;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductCardWidget;
import com.leroy.magportal.ui.pages.products.form.AddProductForm;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.leroy.constants.DefectConst.INVALID_ORDER_DRAFT_DATE;
import static com.leroy.constants.DefectConst.PAO_931;

public class OrderCreatedContentPage extends OrderCreatedPage {

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewHeader__orderId')]//span", metaName = "Номер заказа")
    Element orderNumber;

    @WebFindBy(xpath = "//div[contains(@class, 'Order-CancelOrderBtn')]//button", metaName = "Кнопка удаления заказа")
    Button trashBtn;

    private final static String ORDER_SUB_HEADER_XPATH = "//div[contains(@class, 'OrderViewHeader')][div[contains(@class, 'OrderViewHeader__mainInfo')]]/div[2]";

    @WebFindBy(xpath = ORDER_SUB_HEADER_XPATH + "/span[1]",
            metaName = "Дата создания")
    Element creationDate;

    @WebFindBy(xpath = ORDER_SUB_HEADER_XPATH + "/span[7]",
            metaName = "Способ получения")
    Element deliveryType;

    @Form
    CustomerSearchForm customerSearchForm;

    @Form
    @Getter
    AddProductForm addProductForm;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard' or contains(@class, 'CreationProductCard')]",
            clazz = OrderProductCardWidget.class)
    CardWebWidgetList<OrderProductCardWidget, ProductOrderCardWebData> productCards;

    // View Footer

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__buttonsWrapper')]//button[descendant::span[descendant::*[name()='svg']]]",
            metaName = "Кнопка редактирования заказа")
    Button editBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__buttonsWrapper')]//button[descendant::span[text()='Сохранить']]",
            metaName = "Кнопка Сохранить")
    Button saveBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__buttonsWrapper')]//button[descendant::span[text()='Отмена']]",
            metaName = "Кнопка Отмена")
    Button cancelBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__labeledText')][1]/span[2]", metaName = "Вес заказа")
    Element weight;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__labeledText')][2]/span[2]", metaName = "Габариты заказа")
    Element maxSize;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__labeledText')][3]/span[2]", metaName = "Габариты заказа")
    Element departments;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__totalPrice')]/span[2]", metaName = "Итого стоиммость заказа")
    Element totalPrice;

    // Grab data
    @Step("Получить информацию о заказе с вкладки 'Содержание'")
    public SalesDocWebData getOrderData() throws Exception {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setNumber(ParserUtil.strWithOnlyDigits(orderNumber.getText()));
        salesDocWebData.setCreationDate(creationDate.getText());
        salesDocWebData.setStatus(orderStatus.getText());
        //salesDocWebData.setClient(customerSearchForm.getCustomerData());
        salesDocWebData.setDeliveryType(deliveryType.getText().toLowerCase().equals("самовывоз") ?
                SalesDocumentsConst.GiveAwayPoints.PICKUP : SalesDocumentsConst.GiveAwayPoints.DELIVERY);
        OrderWebData orderWebData = new OrderWebData();
        orderWebData.setTotalPrice(ParserUtil.strToDouble(totalPrice.getText()));
        orderWebData.setTotalWeight(ParserUtil.strToDouble(weight.getText()));
        orderWebData.setProductCardDataList(productCards.getDataList());
        salesDocWebData.setOrders(Collections.singletonList(orderWebData));
        return salesDocWebData;
    }

    // Actions

    @Step("Нажать кнопку редактирования заказа")
    public OrderCreatedContentPage clickEditOrderButton() {
        editBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать кнопку удаления заказа")
    public RemoveOrderModal clickRemoveOrderButton() {
        trashBtn.click();
        return new RemoveOrderModal();
    }

    @Step("Нажать кнопку Сохранить")
    public OrderCreatedContentPage clickSaveOrderButton(boolean expectThatModalWindowAppear) {
        saveBtn.click();
        if (!expectThatModalWindowAppear) {
            saveBtn.waitForInvisibility();
            waitForSpinnerAppearAndDisappear();
        }
        shouldModalThatChangesIsNotSavedIsNotVisible();
        return this;
    }

    @Step("Изменить количество 'заказано' для {index}-ого товара")
    public OrderCreatedContentPage editSelectedQuantity(int index, int value) throws Exception {
        index--;
        productCards.get(index).editQuantity(value);
        shouldModalThatChangesIsNotSavedIsNotVisible();
        return this;
    }

    // Verifications

    @Step("Проверить, что данные заказа соответствуют ожидаемому")
    public OrderCreatedContentPage shouldOrderContentDataIs(SalesDocWebData orderData) throws Exception {
        SalesDocWebData expectedOrderData = orderData.clone();
        SalesDocWebData actualData = getOrderData();
        expectedOrderData.setAuthorName(null);
        expectedOrderData.setPinCode(null);
        expectedOrderData.setDeliveryDate(null);
        expectedOrderData.setComment(null);
        expectedOrderData.setRecipient(null);
        expectedOrderData.setClient(null);
        expectedOrderData.setStatus(null); // Сложно предугадать какой будет статус в определенный момент на "живой" системе
        if (INVALID_ORDER_DRAFT_DATE)
            expectedOrderData.setCreationDate(null); // TODO Надо приводить к LocalDate и проверять
        actualData.getOrders().get(0).setProductCount(actualData.getOrders().get(0).getProductCardDataList().size());
        if (PAO_931 && expectedOrderData.getOrders().get(0).getProductCardDataList().get(0).getDiscountPercent() != null)
            expectedOrderData.getOrders().forEach(p -> p.setTotalPrice(null));
        actualData.assertEqualsNotNullExpectedFields(expectedOrderData);
        return this;
    }

    // OrderDraftContentPage содержит подобный метод
    @Step("Проверить, что в заказе имеются товары с лм кодами: {lmCodes}")
    public OrderCreatedContentPage shouldProductsHave(List<String> lmCodes, boolean shouldBeRecalculatedTotalInfo)
            throws Exception {
        SalesDocWebData salesDocWebData = getOrderData();
        if (!INVALID_ORDER_DRAFT_DATE)
            softAssert.isNotEquals(salesDocWebData.getCreationDate(), "Invalid date",
                    "Неверная дата создания");
        softAssert.isFalse(salesDocWebData.getNumber().isEmpty(), "Заказ не имеет номера");
        if (!DefectConst.ISSUE_WITH_ORDER_STATUS) {
            softAssert.isEquals(salesDocWebData.getStatus().toLowerCase(),
                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal().toLowerCase(),
                    "Неверный статус заказа");
        }
        anAssert.isTrue(salesDocWebData.getOrders() != null && salesDocWebData.getOrders().size() == 1,
                "Информация о заказе недоступна");
        OrderWebData orderWebData = salesDocWebData.getOrders().get(0);
        double expectedTotalWeight = 0.0;
        double expectedTotalPrice = 0.0;
        Set<String> actualLmCodes = new HashSet<>();
        for (ProductOrderCardWebData productData : orderWebData.getProductCardDataList()) {
            expectedTotalWeight += productData.getWeight() * productData.getSelectedQuantity();
            expectedTotalPrice += productData.getTotalPrice();
            actualLmCodes.add(productData.getLmCode());
        }
        softAssert.isEquals(actualLmCodes, new HashSet<>(lmCodes), "Ожидались другие товары в заказе");
        if (shouldBeRecalculatedTotalInfo) {
            softAssert.isTrue(Math.abs(orderWebData.getTotalWeight() - expectedTotalWeight) <= 0.011,
                    String.format("Неверный общий вес заказа. Actual: %s Expected: %s",
                            orderWebData.getTotalWeight(), expectedTotalWeight));
            softAssert.isEquals(orderWebData.getTotalPrice(), expectedTotalPrice, "Неверная общая стоимость заказа");
        }
        softAssert.verifyAll();
        return this;
    }

    public OrderCreatedContentPage shouldProductsHave(List<String> lmCodes) throws Exception {
        return shouldProductsHave(lmCodes, true);
    }

    @Step("Проверить количество 'заказано' для {index}-ого товара")
    public OrderCreatedContentPage shouldSelectedProductQuantityIs(int index, int value) throws Exception {
        index--;
        anAssert.isEquals(productCards.get(index).getOrderedQuantity(), String.valueOf(value),
                "Неверное количество 'заказано' у " + (index + 1) + " товара");
        return this;
    }

    @Step("Проверить видимость кнопки 'редактирование'")
    public OrderCreatedContentPage checkEditButtonVisibility(boolean shouldBeVisible) {
        anAssert.isEquals(editBtn.isVisible(), shouldBeVisible,
                "Кнопка редактирования " + (shouldBeVisible ? "не" : "") + " видна");
        return this;
    }



    @Step("Проверить, что заказ содержит {productCount} продуктов")
    public OrderCreatedContentPage shouldOrderProductCountIs (int productCount) {
        anAssert.isEquals(productCards.getCount(), productCount,"Неверное количество товаров");
        return this;

    }

}
