package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.LeftDocumentListPage;
import com.leroy.magportal.ui.webelements.MagPortalComboBox;
import io.qameta.allure.Step;

public class OrdersPage extends LeftDocumentListPage {

    public OrdersPage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container')][descendant::label[text()='Статус заказа']]",
            metaName = "Фильтр статус заказа")
    MagPortalComboBox statusFilter;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderFilterSearchBtn')]//button", metaName = "Кнопка 'Показать заказы'")
    Button applyFiltersBtn;

    // Actions

    @Step("Выбрать в соотетствующем фильтре статусы: {values}")
    public OrdersPage selectStatusFilters(String... values) throws Exception {
        statusFilter.selectOptions(values);
        return this;
    }

    @Step("Выбрать в соотетствующем фильтре статусы: {values}")
    public OrdersPage deselectStatusFilters(String... values) throws Exception {
        statusFilter.deselectOptions(values);
        return this;
    }

    @Step("Нажать кнопку 'Показать заказы'")
    public OrdersPage clickApplyFilters() throws Exception {
        applyFiltersBtn.click();
        return this;
    }


}
