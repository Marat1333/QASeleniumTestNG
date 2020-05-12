package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.pages.common.LeftDocumentListPage;
import com.leroy.magportal.ui.pages.orders.widget.ShortOrderDocumentCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import com.leroy.magportal.ui.webelements.commonelements.PuzMultiSelectComboBox;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OrderHeaderPage extends LeftDocumentListPage<ShortOrderDocumentCardWidget, ShortOrderDocWebData> {

    public OrderHeaderPage(Context context) {
        super(context);
    }

    public static class SearchTypes {
        public static final String ORDER_NUMBER = "Номер заказа";
        public static final String PHONE_NUMBER = "Номер телефона";
        public static final String CUSTOMER_FIRST_NAME = "Имя покупателя";
        public static final String CUSTOMER_LAST_NAME = "Фамилия покупателя";
        public static final String CUSTOMER_EMAIL = "Email покупателя";
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

    @WebFindBy(xpath = "//div[contains(@class, 'Order-OrderQuickFilter__select')]",
            metaName = "Раскрывающийся список типа поиска")
    PuzComboBox searchTypeComboBox;

    @WebFindBy(xpath = "//input[@name='orderId']", metaName = "Поле поиска по номеру заказа")
    EditBox searchByOrderNumberFld;

    @WebFindBy(xpath = "//input[@name='customerPhone']", metaName = "Поле поиска по номеру телефона")
    EditBox searchByPhoneFld;

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container')][descendant::label[text()='Статус заказа']]",
            metaName = "Фильтр статус заказа")
    PuzMultiSelectComboBox statusFilter;

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container')][descendant::label[text()='Способ получения']]",
            metaName = "Фильтр способ доставки")
    PuzMultiSelectComboBox deliveryTypeFilter;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderFilterClearBtn')]//button", metaName = "Кнопка метла (Очистить)")
    Button clearFiltersBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderFilterSearchBtn')]//button", metaName = "Кнопка 'Показать заказы'")
    Button applyFiltersBtn;

    // Actions

    @Step("Выбрать тип поиска - {value}")
    public OrderHeaderPage selectSearchType(String value) throws Exception {
        searchTypeComboBox.selectOption(value);
        return this;
    }

    @Step("Ввести в поле поиска '{value}' и нажать 'Показать товары'")
    public void enterSearchTextAndSubmit(String value) {
        if (value.startsWith("+7"))
            value = value.substring(2);
        if (searchByOrderNumberFld.isPresent())
            searchByOrderNumberFld.clearAndFill(value);
        else if (searchByPhoneFld.isPresent()) {
            searchByPhoneFld.click();
            searchByPhoneFld.clear(true);
            searchByPhoneFld.fill(value);
        }
        applyFiltersBtn.click();
        waitForSpinnerAppearAndDisappear();
    }

    @Step("Выбрать в соотетствующем фильтре статусы: {values}")
    public OrderHeaderPage selectStatusFilters(String... values) throws Exception {
        statusFilter.selectOptions(values);
        return this;
    }

    @Step("Отключить в соотетствующем фильтре статусы: {values}")
    public OrderHeaderPage deselectStatusFilters(String... values) throws Exception {
        statusFilter.deselectOptions(values);
        return this;
    }

    @Step("Выбрать в соотетствующем фильтре способ доставки: {values}")
    public OrderHeaderPage selectDeliveryTypeFilters(String... values) throws Exception {
        deliveryTypeFilter.selectOptions(values);
        return this;
    }

    @Step("Отключить в соотетствующем фильтре способ доставки: {values}")
    public OrderHeaderPage deselectDeliveryTypeFilters(String... values) throws Exception {
        deliveryTypeFilter.deselectOptions(values);
        return this;
    }

    @Step("Нажать кнопку 'Показать заказы'")
    public OrderHeaderPage clickApplyFilters() {
        applyFiltersBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Очистить фильтры")
    public OrderHeaderPage clearFilters() {
        clearFiltersBtn.click();
        anAssert.isTrue(statusFilter.getSelectedOptionText().isEmpty(),
                "Фильтр 'Статус заказа' не был очищен");
        // todo все фильтры тут должны быть
        return this;
    }

    @Step("Очистить фильтры и подтвердить")
    public OrderHeaderPage clearFiltersAndSubmit() {
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
