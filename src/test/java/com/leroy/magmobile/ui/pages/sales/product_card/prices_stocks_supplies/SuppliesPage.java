package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierDataOld;
import com.leroy.magmobile.ui.pages.sales.product_card.data.SupplyHistoryData;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.SupplyHistoryWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.util.List;

public class SuppliesPage extends ProductPricesQuantitySupplyPage {

    @AppFindBy(accessibilityId = "BackButton")
    Element backBtn;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Код поставщика')]/preceding-sibling::*")
    Element supplierNameLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Код поставщика')]")
    Element supplierCodeLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Код поставщика\")]/ancestor::*[2]/*[2]//android.widget.TextView[1]")
    Element supplierPhoneNumberLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Код поставщика\")]/ancestor::*[2]/*[2]//android.widget.TextView[2]")
    Element supplierContactNameLbl;

    //Кривовато, но без айдишников по-другому никак
    @AppFindBy(xpath = "//*[contains(@text,\"@\")]")
    ElementList<Element> supplierEmailLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Плановая дата\")]/following-sibling::*")
    Element todayOrderSupplyDateLbl;

    @AppFindBy(xpath = "//*[contains(@text, \"Дата ближайшего заказа\")]/following-sibling::*")
    Element nearestSupplyDateLbl;

    @AppFindBy(xpath = "//*[@text='Статус']/following-sibling::*")
    Element statusLbl;

    @AppFindBy(xpath = "//*[@text='Тип поставки']/following-sibling::*")
    Element typeLbl;

    @AppFindBy(xpath = "//*[@text='Срок поставки СС']/following-sibling::*")
    Element supplyPeriodLbl;

    @AppFindBy(xpath = "//*[@text='Франко']/following-sibling::*")
    Element frankoLbl;

    @AppFindBy(xpath = "//*[contains(@text,'Кратность заказа')]/following-sibling::*")
    Element packSizeLbl;

    @AppFindBy(xpath = "//android.widget.ScrollView")
    AndroidScrollView<String> mainScrollView;

    private AndroidScrollView<SupplyHistoryData> supplyHistoryScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text, 'факт приёмки')]/ancestor::*[1]",
            SupplyHistoryWidget.class);

    public SuppliesPage verifyRequiredElements() {
        softAssert.areElementsVisible(supplierNameLbl, supplierCodeLbl);
        softAssert.verifyAll();
        return this;
    }

    @Override
    public void waitForPageIsLoaded() {
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
        supplierNameLbl.waitForVisibility();
    }

    @Step("Проверить, что все данные отображены корректно")
    public SuppliesPage shouldAllSupplyDataIsCorrect(CatalogSupplierDataOld data) throws Exception {
        String uiDateFormat = "d MMMM yyyy";
        String apiDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        softAssert.isElementTextEqual(supplierNameLbl, data.getName());
        softAssert.isElementTextContains(supplierCodeLbl, data.getCode());
        softAssert.isElementTextEqual(supplierPhoneNumberLbl, data.getContactPhone());
        softAssert.isElementTextEqual(supplierContactNameLbl, data.getContactPerson());
        String[] emails = data.getContactEmail().split("[\\,,;]");
        for (int i = 0; i < supplierEmailLbl.getCount(); i++) {
            softAssert.isEquals(supplierEmailLbl.get(i).getText(), emails[i], "email");
        }
        String todayOrderSupplyDate = todayOrderSupplyDateLbl.getText();
        if (!todayOrderSupplyDate.equals("Нет данных")) {
            softAssert.isEquals(DateTimeUtil.strToLocalDateTime(todayOrderSupplyDate, uiDateFormat),
                    DateTimeUtil.strToLocalDateTime(data.getPlanningDeliveryTime(), apiDateFormat).plusHours(3), "planned date");
        }
        String nearestSupplyDate = nearestSupplyDateLbl.getText();
        if (!nearestSupplyDate.equals("Нет данных")) {
            softAssert.isEquals(DateTimeUtil.strToLocalDateTime(nearestSupplyDateLbl.getText(), uiDateFormat),
                    DateTimeUtil.strToLocalDateTime(data.getNextOrderDate(), apiDateFormat).plusHours(3), "nearest date");
        }
        mainScrollView.scrollDownToText("факт приёмки");
        List<SupplyHistoryData> uiSupplyHistoryDataList = supplyHistoryScrollView.getFullDataList();
        List<com.leroy.common_mashups.catalogs.data.supply.SupplyHistoryData> apiSupplyHistoryDataList = data.getHistory();
        anAssert.isEquals(uiSupplyHistoryDataList.size(), apiSupplyHistoryDataList.size(), "supply history lists size mismatch");
        for (int i = 0; i < apiSupplyHistoryDataList.size(); i++) {
            SupplyHistoryData uiEntity = uiSupplyHistoryDataList.get(i);
            com.leroy.common_mashups.catalogs.data.supply.SupplyHistoryData apiEntity = apiSupplyHistoryDataList.get(i);
            softAssert.isEquals(uiEntity.getId(), apiEntity.getOrderNo(), "order number");
            softAssert.isEquals(uiEntity.getOrderedAmount(), apiEntity.getOrderedItemQty(), "ordered quantity");
            softAssert.isEquals(uiEntity.getReceivedAmount(), apiEntity.getReceivedItemQty(), "received quantity");
            if (apiEntity.getPlannedDeliveryDate() != null) {
                softAssert.isEquals(uiEntity.getContractDate(), apiEntity.getPlannedDeliveryDate().plusHours(3).toLocalDate(), "contract date");
            }
            if (apiEntity.getSupplierDate() != null) {
                softAssert.isEquals(uiEntity.getNoteDate(), apiEntity.getSupplierDate().plusHours(3).toLocalDate(), "planned date");
            }
            if (apiEntity.getActualDeliveryDate() != null) {
                softAssert.isEquals(uiEntity.getReceiveDate(), apiEntity.getActualDeliveryDate().plusHours(3).toLocalDate(), "receiving date");
            }
        }

        mainScrollView.scrollDownToText("Способ получения товара клиентом");
        if (!statusLbl.isVisible()) {
            mainScrollView.scrollUpToElement(statusLbl);
        }
        switch (statusLbl.getText()) {
            case "активен":
                softAssert.isEquals(data.getStatus(), "A", "status");
                break;
            default:
                throw new IllegalArgumentException("Wrong status");
        }
        softAssert.isElementTextEqual(typeLbl, data.getDeliveryType());
        //double space
        if (!frankoLbl.isVisible()) {
            mainScrollView.scrollDownToElement(frankoLbl);
        }
        softAssert.isElementTextContains(supplyPeriodLbl, data.getContractDeliveryTime());
        if (!frankoLbl.getText().equals("")) {
            softAssert.isElementTextEqual(frankoLbl, "от " + data.getFranko() + ",00  ₽");
        }
        softAssert.isElementTextEqual(packSizeLbl, data.getPackSize());
        softAssert.verifyAll();
        return this;
    }
}
