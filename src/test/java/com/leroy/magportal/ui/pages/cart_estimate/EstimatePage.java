package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ProductOrderCardPuzWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.Converter;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class EstimatePage extends CartEstimatePage {

    public EstimatePage(Context context) {
        super(context);
    }

    public enum PageState {
        EMPTY, // Пустая страница, видна кнопка "Создать смету"
        CREATING_EMPTY, // Нажали кнопку "Создать смету", но ничего не добавили и не выбрали
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimateHeader')]//span[contains(text(), 'меты')]",
            metaName = "Загаловок 'Сметы'")
    Element headerLbl;

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать смету']]",
            metaName = "Текст кнопки 'Создать смету'")
    Element createEstimateBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__header-text')]", metaName = "Номер сметы")
    Element estimateNumber;

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__header')]//span[contains(@class, 'status-label')]",
            metaName = "Статус сметы")
    Element estimateStatus;

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__header-info__row')][2]//div[2]/span",
            metaName = "Создатель документа")
    Element estimateAuthor;

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__cart')]",
            clazz = OrderPuzWidget.class)
    CardWebWidgetList<OrderPuzWidget, OrderWebData> orders;

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать']]", metaName = "Кнопка 'Создать'")
    Element createBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='Добавить доставку']]",
            metaName = "Кнопка 'Добавить доставку'")
    Element addDeliveryBtn;

    @Override
    protected CardWebWidgetList<OrderPuzWidget, OrderWebData> orders() {
        return orders;
    }

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isElementVisible(headerLbl, timeout);
    }

    // Grab information from page

    @Override
    public String getDocumentNumber() {
        return Converter.strToStrWithoutDigits(estimateNumber.getText());
    }

    @Override
    public String getDocumentStatus() {
        return estimateStatus.getText();
    }

    @Override
    public String getDocumentAuthor() {
        return estimateAuthor.getText();
    }

    // Actions

    @Step("Нажать кнопку 'Создать смету'")
    public EstimatePage clickCreateEstimateButton() {
        createEstimateBtn.click();
        searchProductFld.waitForVisibility();
        return this;
    }

    @Step("Нажимаем кнопку 'Создать'")
    public SubmittedEstimateModal clickCreateButton() {
        createBtn.click();
        return new SubmittedEstimateModal(context);
    }

    @Step("Скопировать товар #{productIdx} из заказа #{orderIdx}")
    public EstimatePage copyProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders.get(orderIdx).getProductWidget(productIdx).clickCopy();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    // Verifications

    @Step("Проверить, что страница 'Создания сметы' отображается корректно")
    public EstimatePage verifyRequiredElements(PageState pageState) {
        if (pageState.equals(PageState.EMPTY)) {
            softAssert.areElementsVisible(createEstimateBtn);
        } else if (pageState.equals(PageState.CREATING_EMPTY)) {
            softAssert.areElementsVisible(addCustomerBtnLbl, searchProductFld, createBtn, addDeliveryBtn);
            softAssert.isEquals(getDocumentNumber(), "", "Смета имеет номер");
        } else {
            throw new IllegalArgumentException("Неизвестное состояние страницы сметы");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в Смете добавлены товары с ЛМ кодами: {lmCodes}")
    public EstimatePage shouldEstimateHasProducts(List<String> lmCodes) throws Exception {
        List<String> actualLmCodes = new ArrayList<>();
        for (OrderPuzWidget orderWidget : orders()) {
            for (ProductOrderCardPuzWidget productWidget : orderWidget.getProductWidgets())
                actualLmCodes.add(productWidget.getLmCode());
        }
        anAssert.isEquals(actualLmCodes, lmCodes, "Ожидались другие товары в смете");
        return this;
    }

    @Step("Проверить, что на странице сметы содержатся ожидаемые данные")
    public EstimatePage shouldEstimateHasData(SalesDocWebData expectedEstimateData) {
        SalesDocWebData actualEstimateData = getSalesDocData();
        if (expectedEstimateData.getNumber() == null)
            anAssert.isFalse(getDocumentNumber().isEmpty(), "Отсутствует номер сметы");
        else
            softAssert.isEquals(actualEstimateData.getNumber(), expectedEstimateData.getNumber(),
                    "Ожидался другой номер документа");
        softAssert.isEquals(actualEstimateData.getAuthorName(), expectedEstimateData.getAuthorName(),
                "Ожидался другой автор документа");
        softAssert.isEquals(actualEstimateData.getStatus(), expectedEstimateData.getStatus().toUpperCase(),
                "Ожидался другой статус документа");
        anAssert.isEquals(actualEstimateData.getOrders().size(),
                expectedEstimateData.getOrders().size(),
                "Ожидалось другое кол-во заказов в смете");
        for (int i = 0; i < actualEstimateData.getOrders().size(); i++) {
            OrderWebData actualOrder = actualEstimateData.getOrders().get(i);
            OrderWebData expectedOrder = expectedEstimateData.getOrders().get(i);

            anAssert.isEquals(actualOrder.getProductCardDataList().size(),
                    expectedOrder.getProductCardDataList().size(),
                    "Ожидалось другое кол-во товаров в смете (Заказ #" + (i + 1) + ")");
            for (int j = 0; j < actualOrder.getProductCardDataList().size(); j++) {
                ProductOrderCardWebData actualProduct = actualOrder.getProductCardDataList().get(j);
                ProductOrderCardWebData expectedProduct = expectedOrder.getProductCardDataList().get(j);
                if (expectedProduct.getSelectedQuantity() != null)
                    softAssert.isEquals(actualProduct.getSelectedQuantity(), expectedProduct.getSelectedQuantity(),
                            "Заказ #" + (i + 1) + " Товар #" + (j + 1) + " - ожидался другое кол-во");
                if (expectedProduct.getTotalPrice() != null)
                    softAssert.isEquals(actualProduct.getTotalPrice(), expectedProduct.getTotalPrice(),
                            "Заказ #" + (i + 1) + " Товар #" + (j + 1) + " - ожидалась другая стоимость");
                if (expectedProduct.getAvailableTodayQuantity() != null)
                    softAssert.isEquals(actualProduct.getAvailableTodayQuantity(), expectedProduct.getAvailableTodayQuantity(),
                            "Заказ #" + (i + 1) + " Товар #" + (j + 1) + " - ожидалось другое доступное кол-во");
                else
                    softAssert.isTrue(actualProduct.getAvailableTodayQuantity() >= 0,
                            "Заказ #" + (i + 1) + " Товар #" + (j + 1) + " - ожидалось, что доступное кол-во >= 0");
                if (expectedProduct.getWeight() != null)
                    softAssert.isEquals(actualProduct.getWeight(), expectedProduct.getWeight(),
                            "Заказ #" + (i + 1) + " Товар #" + (j + 1) + " - ожидался другой вес");
                else
                    softAssert.isTrue(actualProduct.getWeight() > 0,
                            "Заказ #" + (i + 1) + " Товар #" + (j + 1) + " - ожидался вес > 0");
            }

            softAssert.isEquals(actualOrder.getTotalPrice(), expectedOrder.getTotalPrice(),
                    "Заказ #" + (i + 1) + " Неверное сумма итого");
            if (expectedOrder.getTotalWeight() != null)
                softAssert.isEquals(actualOrder.getTotalWeight(), expectedOrder.getTotalWeight(),
                        "Заказ #" + (i + 1) + " Неверный итого вес");
            else {
                softAssert.isTrue(actualOrder.getTotalWeight() > 0,
                        "Заказ #" + (i + 1) + " Ожидался итого вес > 0");
                softAssert.isEquals(actualOrder.getTotalWeight(),
                        actualOrder.getProductCardDataList().stream()
                                .mapToDouble(ProductOrderCardWebData::getWeight).sum(),
                        "Заказ #" + (i + 1) + " Итого вес должен быть равен сумме весов всех продуктов");
            }
            softAssert.isEquals(actualOrder.getProductCount(), expectedOrder.getProductCount(),
                    "Заказ #" + (i + 1) + " Неверный итого вес");
        }
        softAssert.verifyAll();
        return this;
    }
}
