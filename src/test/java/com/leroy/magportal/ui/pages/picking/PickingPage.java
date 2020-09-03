package com.leroy.magportal.ui.pages.picking;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.common.LeftDocumentListPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.picking.widget.ShortPickingTaskCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import io.qameta.allure.Step;

public class PickingPage extends
        LeftDocumentListPage<ShortPickingTaskCardWidget, ShortPickingTaskData> {

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-PickingHeader')]//*[text()='Сборки']", metaName = "Загаловок")
    Element headerLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-PickingQuickFilter__select')]", metaName = "Тип поиска")
    PuzComboBox searchTypeComboBox;

    @WebFindBy(xpath = "//input[@name='orderId']", metaName = "Поле поиска заказа")
    EditBox orderSearchFld;

    // ФИЛЬТРЫ:
    @WebFindBy(xpath = "//button[contains(@class, 'SwitchButton')][1]", metaName = "Фильтр 'Мои'")
    PuzCheckBox myOptionChkBox;

    @WebFindBy(xpath = "//button[contains(@class, 'SwitchButton')][2]", metaName = "Фильтр 'Неразмещенные'")
    PuzCheckBox unplacedOptionChkBox;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingFiltersFields__select-field') and contains(., 'Тип сборки')]",
            metaName = "Тип сборки")
    PuzComboBox buildTypeComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingFiltersFields__select-field') and contains(., 'Статус')]",
            metaName = "Статус")
    PuzComboBox statusComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingFiltersFields__select-field') and contains(., 'Отдел')]",
            metaName = "Отдел")
    PuzComboBox departmentComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingFiltersFields__select-field') and contains(., 'Тип заказа')]",
            metaName = "Тип заказа")
    PuzComboBox orderTypeComboBox;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingFiltersFields__select-field') and contains(., 'Тип клиента')]",
            metaName = "Тип клиента")
    PuzComboBox customerTypeComboBox;

    @WebFindBy(xpath = "//div[button[contains(@class, 'PickingFiltersFields__submit-btn')]]//button[1]",
            metaName = "Кнопка 'Очистить фильтры'")
    Button clearFiltersBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'PickingFiltersFields__submit-btn')]",
            metaName = "Кнопка 'Применить фильтр'")
    Button confirmFiltersBtn;

    // Левое меню с информацией о созданных сборках:

    @WebFindBy(xpath = "//div[contains(@class, 'Refresh-banner')]//button",
            metaName = "Кнопка Обновить список документов")
    private Button refreshDocumentListBtn;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('Picking-PickingListItem') +1) = 'Picking-PickingListItem' or contains(@class, 'Picking-PickingListItem__active')]",
            clazz = ShortPickingTaskCardWidget.class)
    private CardWebWidgetList<ShortPickingTaskCardWidget, ShortPickingTaskData> documentCardList;

    // Данные сборки (Информация вверху - над вкладками 'Содержание', 'Комментарии', 'Информация')

    private static final String PICKING_VIEW_HEADER = "div[contains(@class, 'PickingViewHeader__orderLink')]";

    @WebFindBy(xpath = "//div[" + PICKING_VIEW_HEADER + "]/span[1]",
            metaName = "Номер сборки")
    Element buildNumber;

    @WebFindBy(xpath = "//" + PICKING_VIEW_HEADER,
            metaName = "Ссылка на заказ (последние 4 цифры заказа)")
    Element orderLink;

    @WebFindBy(xpath = "//div[" + PICKING_VIEW_HEADER + "]/span[2]",
            metaName = "Тип сборки")
    Element assemblyType;

    @WebFindBy(xpath = "//div[" + PICKING_VIEW_HEADER + "]/span[3]",
            metaName = "Статус")
    Element status;

    @WebFindBy(xpath = "//div[" + PICKING_VIEW_HEADER + "]/span[4]",
            metaName = "Дата?")
    Element date; // TODO что это за время?

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-PickingViewHeader')]/div[2]/span[1]",
            metaName = "Дата создания")
    Element creationDate;

    // Tabs

    private final static String TABS_XPATH = "//div[contains(@class, 'Picking-PickingView__tabs')]";

    @WebFindBy(xpath = TABS_XPATH + "//div[1]",
            metaName = "Вкладка 'Содержание'")
    Element contentTab;

    @WebFindBy(xpath = TABS_XPATH + "//div[2]",
            metaName = "Вкладка 'Комментарии'")
    Element commentTab;

    @WebFindBy(xpath = TABS_XPATH + "//div[3]",
            metaName = "Вкладка 'Информация'")
    Element informationTab;

    @Override
    protected Button refreshDocumentListBtn() {
        return refreshDocumentListBtn;
    }

    @Override
    protected CardWebWidgetList<ShortPickingTaskCardWidget, ShortPickingTaskData> documentCardList() {
        return documentCardList;
    }

    protected String getNumber() {
        return buildNumber.getText() + " " + orderLink.getText();
    }

    protected PickingConst.AssemblyType getAssemblyType() {
        String assemblyTypeText = this.assemblyType.getText().toLowerCase();
        switch (assemblyTypeText) {
            case "торг.зал":
                return PickingConst.AssemblyType.SHOPPING_ROOM;
            case ">":
                return PickingConst.AssemblyType.SS;
            default:
                anAssert.isTrue(false, "Обнаружен неизвестный тип сборки - " + assemblyTypeText);
        }
        return null;
    }

    protected String getStatus() {
        return status.getText();
    }

    protected String getDate() {
        return date.getText();
    }

    protected String getCreationDate() {
        return creationDate.getText();
    }

    // Actions

    @Step("Нажать на номер заказа для перехода в этот заказ")
    public OrderCreatedContentPage clickOrderLinkAndGoToOrderPage() {
        orderLink.click();
        waitForSpinnerAppearAndDisappear();
        return new OrderCreatedContentPage();
    }

    @Step("Переключиться на вкладку 'Комментарий'")
    public PickingCommentPage switchToCommentTab() {
        commentTab.click();
        return new PickingCommentPage();
    }

    @Step("Переключиться на вкладку 'Содержание'")
    public PickingContentPage switchToContentTab() {
        contentTab.click();
        return new PickingContentPage();
    }

    @Step("Ввести {text} в поле поиска заказа")
    public PickingPage enterOrderNumberInSearchFld(String text) {
        orderSearchFld.clearFillAndSubmit(text);
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать кнопку 'Применить фильтр'")
    public PickingPage clickApplyFilter() {
        confirmFiltersBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }
}
