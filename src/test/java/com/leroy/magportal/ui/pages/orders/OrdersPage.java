package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.pages.common.LeftDocumentListPage;
import com.leroy.magportal.ui.pages.orders.widget.ShortOrderDocumentCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.MagPortalComboBox;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OrdersPage extends LeftDocumentListPage<ShortOrderDocumentCardWidget, ShortOrderDocWebData> {

    public OrdersPage(Context context) {
        super(context);
    }

    // Left menu with document list
    @WebFindBy(xpath = "//div[contains(@class, 'Order-ListUpdater')]//button",
            metaName = "Кнопка Обновить список документов")
    private Button refreshDocumentListBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Order-OrderListItem') and contains(@class, 'container')]",
            clazz = ShortOrderDocumentCardWidget.class)
    private CardWebWidgetList<ShortOrderDocumentCardWidget, ShortOrderDocWebData> documentCardList;

    @Override
    protected Button refreshDocumentListBtn() {
        return refreshDocumentListBtn;
    }

    @Override
    protected CardWebWidgetList<ShortOrderDocumentCardWidget, ShortOrderDocWebData> documentCardList() {
        return documentCardList;
    }

    // Header with Filters

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container')][descendant::label[text()='Статус заказа']]",
            metaName = "Фильтр статус заказа")
    MagPortalComboBox statusFilter;

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container')][descendant::label[text()='Способ получения']]",
            metaName = "Фильтр способ доставки")
    MagPortalComboBox deliveryTypeFilter;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderFilterClearBtn')]//button", metaName = "Кнопка метла (Очистить)")
    Button clearFiltersBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderFilterSearchBtn')]//button", metaName = "Кнопка 'Показать заказы'")
    Button applyFiltersBtn;

    // Actions

    @Step("Выбрать в соотетствующем фильтре статусы: {values}")
    public OrdersPage selectStatusFilters(String... values) throws Exception {
        statusFilter.selectOptions(values);
        return this;
    }

    @Step("Отключить в соотетствующем фильтре статусы: {values}")
    public OrdersPage deselectStatusFilters(String... values) throws Exception {
        statusFilter.deselectOptions(values);
        return this;
    }

    @Step("Выбрать в соотетствующем фильтре способ доставки: {values}")
    public OrdersPage selectDeliveryTypeFilters(String... values) throws Exception {
        deliveryTypeFilter.selectOptions(values);
        return this;
    }

    @Step("Отключить в соотетствующем фильтре способ доставки: {values}")
    public OrdersPage deselectDeliveryTypeFilters(String... values) throws Exception {
        deliveryTypeFilter.deselectOptions(values);
        return this;
    }

    @Step("Нажать кнопку 'Показать заказы'")
    public OrdersPage clickApplyFilters() {
        applyFiltersBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Очистить фильтры")
    public OrdersPage clearFilters() {
        clearFiltersBtn.click();
        anAssert.isTrue(statusFilter.getSelectedOptionText().isEmpty(),
                "Фильтр 'Статус заказа' не был очищен");
        // todo все фильтры тут должны быть
        return this;
    }

    @Step("Очистить фильтры и подтвердить")
    public OrdersPage clearFiltersAndSubmit() {
        return clearFilters()
                .clickApplyFilters();
    }

    // Verifications

    @Step("Проверить, что в списке документов слева присутствуют только имеющие тип доставки: {types}")
    public void shouldDocumentListContainsOnlyWithDeliveryTypes(String... types) {
        Set<String> actualTypes = new HashSet<>();
        for (ShortOrderDocWebData docData : documentCardList().getDataList()) {
            actualTypes.add(docData.getDeliveryType());
        }
        actualTypes.removeAll(Arrays.asList(types));
        anAssert.isTrue(actualTypes.isEmpty(),
                "В списке слева обнаружены документы, которых быть не должно, с типом доставки:" +
                        actualTypes.toString());
    }


}
