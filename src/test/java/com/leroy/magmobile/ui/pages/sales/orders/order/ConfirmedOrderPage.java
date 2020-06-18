package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.Form;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.CartOrderEstimatePage;
import com.leroy.magmobile.ui.pages.sales.orders.order.forms.OrderParamsForm;
import com.leroy.magmobile.ui.pages.sales.orders.order.forms.ProductOrderForm;
import com.leroy.magmobile.ui.pages.sales.orders.order.modal.ConfirmRemoveOrderModal;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

/**
 * Экран подтвержденного заказа
 */
public class ConfirmedOrderPage extends CartOrderEstimatePage {

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='DefaultScreenHeader']//android.view.ViewGroup[@content-desc='Button-icon']")
    private Element trashIconBtn;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView",
            metaName = "Загаловок заказа")
    Element documentTitle;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[contains(@text, '№')]/preceding-sibling::android.widget.TextView[2]",
            metaName = "Дата создания заказа")
    Element documentDate;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[contains(@text, '№')]/preceding-sibling::android.widget.TextView[1]",
            metaName = "Автор созданного заказа")
    Element documentCreator;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[contains(@text, '№')]",
            metaName = "Номер документа")
    Element documentNumber;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[@content-desc='Badge-Text']",
            metaName = "Статус документа")
    Element documentStatus;

    @Form
    OrderParamsForm orderParamsForm;

    @Form
    ProductOrderForm productOrderForm;

    @Override
    public void waitForPageIsLoaded() {
        orderParamsForm.waitUntilFormIsVisible();
    }

    // Grab data
    @Step("Получить информацию о документе со страницы")
    public SalesDocumentData getSalesDocumentData() {
        OrderDetailsData orderDetailsData = orderParamsForm.getOrderDetailData();
        SalesDocumentData salesDocumentData = productOrderForm.getSalesDocumentData();
        salesDocumentData.setOrderDetailsData(orderDetailsData);
        String ps = getPageSource();
        salesDocumentData.setTitle(documentTitle.getText(ps));
        salesDocumentData.setNumber(ParserUtil.strWithOnlyDigits(documentNumber.getText(ps)));
        salesDocumentData.setDate(DateTimeUtil.strToLocalDateTime(documentDate.getText(ps), "dd MMM, HH:mm"));
        salesDocumentData.setStatus(documentStatus.getText(ps));
        return salesDocumentData;
    }

    // Actions

    @Step("Нажать иконку (мусорка) для удаления заказа")
    public ConfirmRemoveOrderModal clickTrashIcon() {
        trashIconBtn.click();
        return new ConfirmRemoveOrderModal();
    }

    @Step("Нажмите на {index}-ую карточку товара/услуги")
    public OrderActionWithProductCardModel<ConfirmedOrderPage> clickCardByIndex(int index) throws Exception {
        productOrderForm.clickCardByIndex(index);
        return new OrderActionWithProductCardModel<>(ConfirmedOrderPage.class);
    }

    @Step("Нажмите кнопку для добавления товара в корзину")
    public SearchProductPage clickAddProductButton() {
        return productOrderForm.clickAddProductButton();
    }

    @Step("Нажмите кнопку Сохранить")
    public <T extends CommonMagMobilePage> T clickSaveButton(Class<T> page) throws Exception {
        productOrderForm.clickSaveButton();
        return page.getConstructor().newInstance();
    }

    // Verifications

    @Step("Проверить, что данные о товарах в заказе верны (expectedDocumentData)")
    public ConfirmedOrderPage shouldSalesDocumentDataIs(SalesDocumentData expectedDocumentData) {
        for (ProductOrderCardAppData productCardData : expectedDocumentData.getOrderAppDataList().get(0).getProductCardDataList()) {
            productCardData.setAvailableTodayQuantity(null);
        } // TODO Из-за рассинхрона данных на тесте идет отличие в доступном количестве
        SalesDocumentData salesDocumentData = getSalesDocumentData();
        salesDocumentData.assertEqualsNotNullExpectedFields(expectedDocumentData);
        return this;
    }

    @Step("Проверить, что поля формы заполнены соответствующим образом")
    public ConfirmedOrderPage shouldFormFieldsAre(OrderDetailsData data) {
        orderParamsForm.shouldFormFieldsAre(data);
        return this;
    }

    @Step("Проверить, что документ нельзя отредактировать - нет активных кнопок для добавления товара, кнопки сохранить и т.п.")
    public ConfirmedOrderPage shouldAllActiveButtonsAreDisabled() {
        productOrderForm.shouldAllActiveButtonsAreDisabled();
        return this;
    }
}
