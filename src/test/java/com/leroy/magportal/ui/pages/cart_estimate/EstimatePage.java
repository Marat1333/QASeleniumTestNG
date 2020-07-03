package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.constants.EnvConstants;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ConfirmRemoveProductModal;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.common.modal.ConfirmRemoveModal;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.support.Colors;
import org.testng.util.Strings;

public class EstimatePage extends CartEstimatePage {

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

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__header-info__row')][2]//div[1]/span",
            metaName = "Дата создания документа")
    Element creationDate;

    @WebFindBy(xpath = "//div[contains(@class, 'Estimate-EstimatesView__header-info__row')][2]//div[2]/span",
            metaName = "Создатель документа")
    Element estimateAuthor;

    @WebFindBy(xpath = "//div[contains(@class, 'EstimatesView__header-buttons')]//div[@class = 'lmui-popover'][last()]//button",
            metaName = "Кнопка корзина (удалить)")
    Button trashBtn;

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
        super.waitForPageIsLoaded();
        anAssert.isElementVisible(headerLbl, timeout);
    }

    // Follow URLs

    @Step("Открыть страницу со сметой №{id} (прямой переход по URL)")
    public EstimatePage openPageWithEstimate(String id) {
        driver.get(EnvConstants.URL_MAG_PORTAL + "/estimates/view/" + id);
        return this;
    }

    // Grab information from page

    @Override
    public String getDocumentNumber() {
        return ParserUtil.strWithOnlyDigits(estimateNumber.getText());
    }

    @Override
    public String getDocumentStatus() {
        return estimateStatus.getText();
    }

    @Override
    public String getDocumentAuthor() {
        return estimateAuthor.getText();
    }

    @Override
    public String getCreationDate() {
        return creationDate.getText();
    }

    // Actions

    @Step("Нажать кнопку 'Создать смету'")
    public EstimatePage clickCreateEstimateButton() {
        createEstimateBtn.click();
        searchProductFld.waitForVisibility();
        createBtn.waitForVisibility();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Step("Нажимаем кнопку 'Создать'")
    public <T extends MagPortalBasePage> T clickCreateButton() {
        createBtn.click();
        if (selectedCustomerCard.isVisible())
            return (T) new SubmittedEstimateModal();
        else
            return (T) this;
    }

    @Step("Удалить смету")
    public EstimatePage removeEstimate() {
        trashBtn.click();
        new ConfirmRemoveModal().clickConfirmBtn();
        trashBtn.waitForInvisibility();
        waitForSpinnerDisappear();
        return this;
    }

    @Step("Установить кол-во {quantity} для товара #{productIdx} из заказа #{orderIdx}")
    public EstimatePage changeQuantityProductByIndex(Number quantity, int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders.get(orderIdx).getProductWidget(productIdx).editQuantity(quantity);
        return this;
    }

    @Step("Установить кол-во {quantity} для товара #{productIdx}")
    public EstimatePage changeQuantityProductByIndex(Number quantity, int productIdx) throws Exception {
        return changeQuantityProductByIndex(quantity, 1, productIdx);
    }

    @Step("Нажать '+' для увеличения кол-ва товара #{productIdx} из заказа #{orderIdx}")
    public EstimatePage increaseQuantityProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders.get(orderIdx).getProductWidget(productIdx).clickPlusQuantity();
        return this;
    }

    @Step("Нажать '+' для увеличения кол-ва товара #{productIdx}")
    public EstimatePage increaseQuantityProductByIndex(int productIdx) throws Exception {
        return increaseQuantityProductByIndex(1, productIdx);
    }

    @Step("Нажать '-' для уменьшения кол-ва товара #{productIdx} из заказа #{orderIdx}")
    public EstimatePage decreaseQuantityProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        orders.get(orderIdx).getProductWidget(productIdx).clickMinusQuantity();
        return this;
    }

    @Step("Нажать '-' для уменьшения кол-ва товара #{productIdx}")
    public EstimatePage decreaseQuantityProductByIndex(int productIdx) throws Exception {
        return decreaseQuantityProductByIndex(1, productIdx);
    }

    @Step("Скопировать товар #{productIdx} из заказа #{orderIdx}")
    public EstimatePage copyProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        OrderPuzWidget orderWidget = orders.get(orderIdx);
        int productCountBefore = orderWidget.getProductWidgets().getCount();
        orderWidget.getProductWidget(productIdx).clickCopy();
        waitForSpinnerAppearAndDisappear();
        orderWidget.getProductWidgets().waitUntilElementCountEqualsOrAbove(productCountBefore + 1);
        return this;
    }

    @Step("Скопировать товар #{productIdx}")
    public EstimatePage copyProductByIndex(int productIdx) throws Exception {
        return copyProductByIndex(1, productIdx);
    }

    @Step("Удалить товар #{productIdx} из заказа #{orderIdx}")
    public EstimatePage removeProductByIndex(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        OrderPuzWidget orderWidget = orders.get(orderIdx);
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
        return this;
    }

    @Step("Удалить товар #{productIdx}")
    public EstimatePage removeProductByIndex(int productIdx) throws Exception {
        return removeProductByIndex(1, productIdx);
    }

    // Verifications

    @Step("Проверить, что страница 'Создания сметы' отображается корректно")
    public EstimatePage verifyRequiredElements(PageState pageState) {
        if (pageState.equals(PageState.EMPTY)) {
            softAssert.isElementVisible(createEstimateBtn);
            softAssert.isElementNotVisible(addCustomerBtnLbl);
            softAssert.isElementNotVisible(searchProductFld);
            softAssert.isElementNotVisible(createBtn);
            softAssert.isElementNotVisible(addDeliveryBtn);
        } else if (pageState.equals(PageState.CREATING_EMPTY)) {
            softAssert.areElementsVisible(addCustomerBtnLbl, searchProductFld, createBtn, addDeliveryBtn);
            softAssert.isEquals(getDocumentNumber(), "", "Смета имеет номер");
        } else {
            throw new IllegalArgumentException("Неизвестное состояние страницы сметы");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что у товара #{productIdx} из заказа #{orderIdx} доступное кол-во выделено красным")
    public EstimatePage shouldProductAvailableStockLabelIsRed(int orderIdx, int productIdx) throws Exception {
        productIdx--;
        orderIdx--;
        anAssert.isEquals(orders.get(orderIdx).getProductWidget(productIdx).getColorOfAvailableStockLbl(),
                Colors.RED.getColorValue(), "Цвет у товара #" + (productIdx + 1) + " должен быть красный");
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
        if (expectedEstimateData.getCreationDate() != null) {
            softAssert.isEquals(actualEstimateData.getCreationDate(), expectedEstimateData.getCreationDate(),
                    "Ожидался другая дата создания документа");
        } else {
            softAssert.isFalse(Strings.isNullOrEmpty(actualEstimateData.getCreationDate()),
                    "Дата создания документа не отображается");
        }
        if (actualEstimateData.getClient() == null) {
            softAssert.isTrue(expectedEstimateData.getClient() == null,
                    "Информация о клиенте отсутствует");
        } else {
            softAssert.isEquals(actualEstimateData.getClient(), expectedEstimateData.getClient(),
                    "Ожидался другой клиент в документе");
        }
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
                double expectedTotalWeight = 0.0;
                for (ProductOrderCardWebData pr : actualOrder.getProductCardDataList()) {
                    expectedTotalWeight = ParserUtil.plus(pr.getWeight(), expectedTotalWeight, 2);
                }
                softAssert.isEquals(actualOrder.getTotalWeight(),
                        expectedTotalWeight,
                        "Заказ #" + (i + 1) + " Итого вес должен быть равен сумме весов всех продуктов");
            }
            softAssert.isEquals(actualOrder.getProductCount(), expectedOrder.getProductCount(),
                    "Заказ #" + (i + 1) + " Неверное кол-во продуктов в заказе");
        }
        softAssert.verifyAll();
        return this;
    }
}
