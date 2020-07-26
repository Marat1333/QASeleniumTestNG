package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.modal.AddDeliveryModal;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SendEstimateToEmailModal;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import com.leroy.magportal.ui.pages.cart_estimate.widget.OrderPuzWidget;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.common.modal.ConfirmRemoveModal;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.support.Colors;

import java.util.Set;

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

    @WebFindBy(xpath = "//div[contains(@class, 'EstimatesView__header-buttons')]//div[@class = 'lmui-popover'][1]//button",
            metaName = "Кнопка отправить на почту")
    Button sendEmailBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'EstimatesView__header-buttons')]//div[@class = 'lmui-popover'][2]//button",
            metaName = "Кнопка 'На печать'")
    Button printBtn;

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

    @WebFindBy(xpath = "//div[contains(@class, 'SalesDoc-ViewFooter__action')]//button",
            metaName = "Кнопка 'Преобразовать в корзину'")
    Element transformToCartBtn;

    @Override
    protected CardWebWidgetList<OrderPuzWidget, OrderWebData> orders() {
        return orders;
    }

    @Override
    public void waitForPageIsLoaded() {
        anAssert.isElementVisible(headerLbl, timeout);
        String expectedHeader = "Сметы";
        anAssert.isTrue(headerLbl.waitUntilTextContains(expectedHeader),
                "Страница 'Сметы' не загрузилась'");
        waitForSpinnerDisappear();
    }

    @Step("Ждем, когда данные в ранее созданной смете загрузятся")
    public EstimatePage waitUntilEstimateDataIsLoaded() {
        estimateNumber.waitForVisibility();
        waitForSpinnerDisappear();
        return this;
    }

    // Follow URLs

    @Step("Открыть страницу со сметой №{id} (прямой переход по URL)")
    public EstimatePage openPageWithEstimate(String id) {
        driver.get(EnvConstants.URL_MAG_PORTAL + "/orders/estimates");
        EstimatePage estimatePage = new EstimatePage();
        estimatePage.clickDocumentInLeftMenu(id);
        estimatePage.waitUntilEstimateDataIsLoaded();
        return estimatePage;
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
        addCustomerBtnLbl.waitForVisibility();
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

    @Step("Нажать 'Отправить на почту'")
    public SendEstimateToEmailModal clickSendByEmail() {
        sendEmailBtn.click();
        return new SendEstimateToEmailModal();
    }

    @Step("Нажать 'Распечатать'")
    public PrintEstimatePage clickPrintButton() throws Exception {
        Set<String> oldHandles = getDriver().getWindowHandles();
        try {
            printBtn.click(timeout);
        } catch (ElementClickInterceptedException err) {
            Log.warn(err.getMessage());
            printBtn.click(timeout);
        }

        Set<String> newHandles = driver.getWindowHandles();
        newHandles.removeAll(oldHandles);
        String handlePrintEstimate = newHandles.toArray()[0].toString();

        oldHandles = getDriver().getWindowHandles();
        switchToNewWindow(oldHandles);
        return new PrintEstimatePage(handlePrintEstimate);
    }

    @Step("Преобразовать в корзину")
    public CartPage clickTransformToCart() {
        transformToCartBtn.click();
        return new CartPage();
    }

    @Step("Нажать 'Добавить доставку'")
    public AddDeliveryModal clickAddDelivery() {
        addDeliveryBtn.click();
        return new AddDeliveryModal();
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

    @Step("Убедиться, что смета имеет статус 'Преобразован', нет активных кнопок")
    public EstimatePage shouldEstimateHasTransformedStatus() {
        softAssert.areElementsNotVisible(createBtn, addDeliveryBtn, trashBtn, transformToCartBtn, createCustomerBtn,
                customerActionBtn, clearCustomerOptionBtn, editCustomerOptionBtn, searchCustomerOptionBtn);
        softAssert.isEquals(getDocumentStatus(), SalesDocumentsConst.States.TRANSFORMED.getUiVal().toUpperCase(),
                "Неверный статус сметы");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что на странице сметы содержатся ожидаемые данные")
    public EstimatePage shouldEstimateHasData(SalesDocWebData expectedEstimateData) {
        shouldDocumentHasData(expectedEstimateData);
        return this;
    }
}
