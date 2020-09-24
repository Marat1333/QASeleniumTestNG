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
import com.leroy.magportal.ui.pages.cart_estimate.modal.ConfirmRemoveProductModal;
import com.leroy.magportal.ui.pages.orders.widget.OrderDraftProductCardWidget;
import com.leroy.magportal.ui.pages.products.form.AddProductForm;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.Colors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.leroy.constants.DefectConst.INVALID_ORDER_DRAFT_DATE;
import static com.leroy.constants.DefectConst.PAO_931;

public class OrderDraftContentPage extends OrderDraftPage {

    @Form
    @Getter
    AddProductForm addProductForm;

    @WebFindBy(xpath = "//div[contains(@class, 'CreationProductCard')]",
            clazz = OrderDraftProductCardWidget.class)
    CardWebWidgetList<OrderDraftProductCardWidget, ProductOrderCardWebData> productCards;

    // Bottom area

    @WebFindBy(xpath = "//div[contains(@class, 'OrderCreate__footer')]//button", metaName = "Кнопка 'Способ получения'")
    Button deliveryTypeBtn;

    // Grab data

    @Step("Получить информацию о заказе с вкладки 'Содержание'")
    public SalesDocWebData getOrderData() throws Exception {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setCreationDate(creationDate.getText());
        salesDocWebData.setAuthorName(author.getText());
        salesDocWebData.setNumber(ParserUtil.strWithOnlyDigits(orderNumber.getText()));
        salesDocWebData.setStatus(orderStatus.getText());
        OrderWebData orderWebData = new OrderWebData();
        orderWebData.setTotalPrice(ParserUtil.strToDouble(orderTotalPrice.getText()));
        orderWebData.setProductCount(getProductCount());
        orderWebData.setTotalWeight(getTotalWeight());
        orderWebData.setProductCardDataList(productCards.getDataList());
        salesDocWebData.setOrders(Collections.singletonList(orderWebData));
        return salesDocWebData;
    }

    // Actions

    @Step("Изменить кол-во {index}-ого товара")
    public OrderDraftContentPage editProductQuantity(int index, int value) throws Exception {
        index--;
        productCards.get(index).editQuantity(value);
        waitForSpinnerAppearAndDisappear();
        shouldModalThatChangesIsNotSavedIsNotVisible();
        return this;
    }

    @Step("Удалить {index}-ый товар из заказа")
    public OrderDraftContentPage removeProduct(int index) throws Exception {
        index--;
        productCards.get(index).clickTrashBtn();
        new ConfirmRemoveProductModal().clickYesButton();
        waitForSpinnerAppearAndDisappear();
        shouldModalThatChangesIsNotSavedIsNotVisible();
        return this;
    }

    // Verifications
    @Step("Проверить, что данные заказа соответствуют ожидаемому")
    public OrderDraftContentPage shouldOrderContentDataIs(SalesDocWebData orderData) throws Exception {
        SalesDocWebData expectedOrderData = orderData.clone();
        SalesDocWebData actualData = getOrderData();
        expectedOrderData.setPinCode(null);
        expectedOrderData.setClient(null);
        expectedOrderData.setDeliveryType(null);
        if (INVALID_ORDER_DRAFT_DATE)
            expectedOrderData.setCreationDate(null);
        expectedOrderData.getOrders().get(0).getProductCardDataList().forEach(p -> p.setBarCode(null));
        // Не понятно как проверять вес, когда он в корзине отображается суммарный, а в заказе за штуку:
        expectedOrderData.getOrders().get(0).getProductCardDataList().forEach(p -> p.setWeight(null));
        if (DefectConst.STOCK_ISSUE)
            expectedOrderData.getOrders().get(0).getProductCardDataList().forEach(p -> p.setAvailableTodayQuantity(null));
        if (PAO_931 && expectedOrderData.getOrders().get(0).getProductCardDataList().get(0).getDiscountPercent() != null)
            expectedOrderData.getOrders().forEach(p -> p.setTotalPrice(null));
        actualData.assertEqualsNotNullExpectedFields(expectedOrderData);
        return this;
    }

    // OrderCreatedContentPage содержит подобный метод
    @Step("Проверить, что в заказе имеются товары с лм кодами: {lmCodes}")
    public OrderDraftContentPage shouldProductsHave(List<String> lmCodes) throws Exception {
        SalesDocWebData salesDocWebData = getOrderData();
        if (!INVALID_ORDER_DRAFT_DATE)
            softAssert.isNotEquals(salesDocWebData.getCreationDate(), "Invalid date",
                    "Неверная дата создания");
        softAssert.isFalse(salesDocWebData.getNumber().isEmpty(), "Заказ не имеет номера");
        softAssert.isEquals(salesDocWebData.getStatus().toLowerCase(),
                SalesDocumentsConst.States.DRAFT.getUiVal().toLowerCase(), "Неверный статус заказа");
        anAssert.isTrue(salesDocWebData.getOrders() != null && salesDocWebData.getOrders().size() == 1,
                "Информация о заказе недоступна");
        OrderWebData orderWebData = salesDocWebData.getOrders().get(0);
        softAssert.isEquals(orderWebData.getProductCount(), orderWebData.getProductCardDataList().size(),
                "Неверная информация о кол-ве товаров в заказе");
        double expectedTotalWeight = 0.0;
        double expectedTotalPrice = 0.0;
        Set<String> actualLmCodes = new HashSet<>();
        for (ProductOrderCardWebData productData : orderWebData.getProductCardDataList()) {
            expectedTotalWeight += productData.getWeight() * productData.getSelectedQuantity();
            expectedTotalPrice += productData.getTotalPrice();
            actualLmCodes.add(productData.getLmCode());
        }
        softAssert.isEquals(actualLmCodes, new HashSet<>(lmCodes), "Ожидались другие товары в заказе");
        softAssert.isTrue(Math.abs(orderWebData.getTotalWeight() - expectedTotalWeight) <= 0.011,
                String.format("Неверный общий вес заказа. Actual: %s Expected: %s",
                        orderWebData.getTotalWeight(), expectedTotalWeight));
        softAssert.isEquals(orderWebData.getTotalPrice(), expectedTotalPrice, "Неверная общая стоимость заказа");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что {index} товар подсвечивается красной рамкой")
    public OrderDraftContentPage shouldProductIsDangerHighlighted(int index) throws Exception {
        index--;
        Element productCard = E(productCards.get(index).getXpath());
        softAssert.isEquals(Color.fromString(productCard.getCssValue("border-color")),
                Colors.RED.getColorValue(), "Ожидался border-color = RED у карточки товара " + (index + 1));
        softAssert.isTrue(productCard.getAttribute("class").contains("danger"),
                "Карточка товара " + (index + 1) + " должна содержать в названии класса 'danger'");
        softAssert.isFalse(deliveryTypeBtn.isEnabled(), "Кнопка 'Способ получения' должна быть неактивна");
        softAssert.verifyAll();
        return this;
    }
}
