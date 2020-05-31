package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class DiscountReasonModal extends CommonMagMobilePage {

    // Причины скидки:
    public static final String PRODUCT_AFTER_REPAIRING_REASON = "Товар после ремонта в сервисном центре";
    public static final String PRODUCT_SAMPLE_REASON = "Образец товара";
    public static final String BALANCE_MEASURED_PRODUCT_REASON = "Остаток мерного товара";
    public static final String NOT_COMPLETE_SET_REASON = "Не полный комплект";
    public static final String PRODUCT_WITH_DEFECT_REASON = "Товар с браком";
    public static final String CUSTOMER_EQUIVALENT_REASON = "Аналог для клиента ИМ";

    @AppFindBy(text = "Причина скидки", metaName = "Загаловок")
    Element headerLbl;

    @AppFindBy(text = PRODUCT_AFTER_REPAIRING_REASON)
    Element productAfterRepairingMenuItem;

    @AppFindBy(text = PRODUCT_SAMPLE_REASON)
    Element productSampleMenuItem;

    @AppFindBy(text = BALANCE_MEASURED_PRODUCT_REASON)
    Element balanceMeasuredProductMenuItem;

    @AppFindBy(text = NOT_COMPLETE_SET_REASON)
    Element notCompleteSetMenuItem;

    @AppFindBy(text = PRODUCT_WITH_DEFECT_REASON)
    Element productWithDefectMenuItem;

    @AppFindBy(text = CUSTOMER_EQUIVALENT_REASON)
    Element customerEquivalentMenuItem;

    // Actions

    @Step("Выбрать причину скидки '{value}'")
    public CreatingDiscountPage selectDiscountReason(String value) {
        E(value).click();
        return new CreatingDiscountPage();
    }

    // Verifications

    @Step("Проверить, что модалка 'Причина скидки' отображается корректно")
    public DiscountReasonModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, productAfterRepairingMenuItem,
                productSampleMenuItem, balanceMeasuredProductMenuItem, notCompleteSetMenuItem,
                productWithDefectMenuItem, customerEquivalentMenuItem);
        softAssert.verifyAll();
        return this;
    }

}
