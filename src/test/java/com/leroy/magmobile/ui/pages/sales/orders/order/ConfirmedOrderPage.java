package com.leroy.magmobile.ui.pages.sales.orders.order;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.annotations.Form;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.OrderDetailsData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.order.forms.OrderParamsForm;
import com.leroy.magmobile.ui.pages.sales.orders.order.forms.ProductOrderForm;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

/**
 * Экран подтвержденного заказа
 */
public class ConfirmedOrderPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView",
            metaName = "Загаловок заказа")
    Element documentTitle;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[@index='3']",
            metaName = "Дата создания заказа")
    Element documentDate;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[@index='4']",
            metaName = "Автор созданного заказа")
    Element documentCreator;

    @AppFindBy(xpath = "//*[@content-desc='DefaultScreenHeader']//android.widget.TextView[@index='5']",
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

    // Verifications

    @Step("Проверить, что данные о товарах в заказе верны (expectedDocumentData)")
    public ConfirmedOrderPage shouldSalesDocumentDataIs(SalesDocumentData expectedDocumentData) {
        for (ProductOrderCardAppData productCardData : expectedDocumentData.getOrderAppDataList().get(0).getProductCardDataList()) {
            productCardData.setAvailableTodayQuantity(null);
        } // TODO Из-за рассинхрона данных на тесте идет отличие в доступном количестве
        SalesDocumentData salesDocumentData = productOrderForm.getSalesDocumentData();
        String ps = getPageSource();
        salesDocumentData.setTitle(documentTitle.getText(ps));
        salesDocumentData.setNumber(ParserUtil.strWithOnlyDigits(documentNumber.getText(ps)));
        salesDocumentData.setDate(DateTimeUtil.strToLocalDateTime(documentDate.getText(ps), "dd MMM, HH:mm"));
        salesDocumentData.setStatus(documentStatus.getText(ps));
        salesDocumentData.assertEqualsNotNullExpectedFields(expectedDocumentData);
        return this;
    }

    @Step("Проверить, что поля формы заполнены соответствующим образом")
    public ConfirmedOrderPage shouldFormFieldsAre(OrderDetailsData data) {
        orderParamsForm.shouldFormFieldsAre(data);
        return this;
    }
}
