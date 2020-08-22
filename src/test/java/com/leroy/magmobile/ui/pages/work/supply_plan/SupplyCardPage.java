package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.supply_plan.Card.*;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.ShipmentProductData;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.ShipmentOtherProductWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.ShipmentProductWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SupplyCardPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "BackCloseModal")
    Button backBtn;

    @AppFindBy(xpath = "//*[contains(@text,'Заказ №') or contains(@text,'Трансфер №')]")
    Element title;

    @AppFindBy(xpath = "//*[contains(@text,'СКЛАД') or contains(@text,'ПОСТАВЩИК') or contains(@text,'МАГАЗИН')]")
    Element supplierType;

    @AppFindBy(containsText = "Код: ")
    Element supplierCode;

    @AppFindBy(xpath = "//*[contains(@text,'Код: ')]/preceding-sibling::*")
    Element supplierName;

    @AppFindBy(xpath = "//*[contains(@text,'Код: ')]/../following-sibling::*[1]/*[1]")
    Element supplierPhoneNumber;

    @AppFindBy(xpath = "//*[contains(@text,'Код: ')]/../following-sibling::*[1]/*[2]")
    Element supplierContactPersonName;

    @AppFindBy(xpath = "//*[contains(@text,'Код: ')]/../following-sibling::*[2]/*[1]")
    Element emailAddress;

    @AppFindBy(xpath = "//*[@content-desc=\"Button-text\"]")
    ElementList<Element> shipmentsTabs;

    @AppFindBy(xpath = "//*[contains(@text,' артикул')]")
    Element shipmentNumber;

    @AppFindBy(xpath = "//*[contains(@text,' артикул')]/following-sibling::android.view.ViewGroup[not(android.widget.TextView)]")
    Button callHintModalBtn;

    @AppFindBy(xpath = "//*[contains(@text,' артикул')]/following-sibling::android.widget.TextView[contains(@text,':')]")
    Element shipmentDate;

    @AppFindBy(containsText = " палет")
    Element shipmentReceivedCondition;

    @AppFindBy(xpath = "//*[contains(@text,' артикул')]/following-sibling::android.view.ViewGroup[android.widget.TextView]//*")
    Element receivedPlannedQuantity;

    @AppFindBy(text = "ПОВТОРИТЬ")
    Element sendReqOneMoreTime;

    AndroidScrollView<ShipmentProductData> shipmentProducts = new AndroidScrollView(driver, AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.widget.ScrollView/*/*/*", ShipmentProductWidget.class);

    AndroidScrollView<ShipmentProductData> shipmentOtherProducts = new AndroidScrollView(driver, AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.widget.ScrollView/*/*/*", ShipmentOtherProductWidget.class);

    AndroidScrollView<String> mainScrollView = new AndroidScrollView<String>(driver, AndroidScrollView.TYPICAL_LOCATOR);

    public enum Tab {
        FIRST_SHIPMENT,
        SECOND_SHIPMENT,
        OTHER_PRODUCTS
    }

    @Override
    protected void waitForPageIsLoaded() {
        if (sendReqOneMoreTime.isVisible()) {
            Log.warn("hasn`t get data, trying again");
            sendReqOneMoreTime.click();
            waitUntilProgressBarIsInvisible();

        }
        title.waitForVisibility();
        supplierType.waitForVisibility();
    }

    @Step("вернуться назад")
    public void goBack() {
        backBtn.click();
    }

    @Step("Переключить на указанную вкладку")
    public SupplyCardPage switchTab(Tab tab) throws Exception {
        List<String> tabsText = new ArrayList<>();
        for (Element each : shipmentsTabs) {
            tabsText.add(each.getText());
        }
        switch (tab) {
            case FIRST_SHIPMENT:
                if (tabsText.contains("1 ОТГРУЗКА")) {
                    shipmentsTabs.get(0).click();
                    shipmentDate.waitForVisibility();
                } else {
                    Log.warn("There is no needed tab");
                }
                break;
            case SECOND_SHIPMENT:
                if (tabsText.contains("2 ОТГРУЗКА")) {
                    shipmentsTabs.get(1).click();
                    shipmentDate.waitForVisibility();
                } else {
                    Log.warn("There is no needed tab");
                }
                break;
            case OTHER_PRODUCTS:
                if (tabsText.contains("ОСТАЛЬНОЕ")) {
                    shipmentsTabs.get(shipmentsTabs.getCount() - 1).click();
                    shipmentReceivedCondition.waitForInvisibility();
                } else {
                    Log.warn("There is no needed tab");
                }
                break;
        }
        return this;
    }

    @Step("Открыть модальное окно-справку")
    public void openHintModal() {
        callHintModalBtn.click();
    }

    @Step("Проверить, что выполнено переключение на таб")
    public SupplyCardPage shouldSwitchToNeededTabIsComplete(Tab tab) {
        String pageSource = getPageSource();
        if (tab.equals(Tab.FIRST_SHIPMENT) || tab.equals(Tab.SECOND_SHIPMENT)) {
            softAssert.areElementsVisible(pageSource, shipmentDate, shipmentReceivedCondition, receivedPlannedQuantity);
            softAssert.isTrue(shipmentNumber.getText().contains("№"), "number is invisible");
        } else {
            softAssert.areElementsNotVisible(pageSource, shipmentDate, shipmentReceivedCondition, receivedPlannedQuantity);
            softAssert.isTrue(!shipmentNumber.getText().contains("№"), "number is visible");
        }
        softAssert.verifyAll();

        return this;
    }

    @Step("Проверить, корректность отображенных данных")
    public SupplyCardPage shouldDataIsCorrect(ShipmentData shipmentData, SupplyCardData supplyCardData) throws Exception {
        SupplyCardSendingLocationData sendingLocation = supplyCardData.getSendingLocation();
        List<SupplyCardShipmentsData> shipments = supplyCardData.getShipments();
        List<SupplyCardOtherProductsData> otherProducts = supplyCardData.getOtherProducts();

        softAssert.isElementTextContains(title, shipmentData.getDocumentNo().asText());
        switch (shipmentData.getSendingLocationType()) {
            case "ST":
                softAssert.isElementTextEqual(supplierType, "МАГАЗИН");
                softAssert.isElementTextContains(title, "Трансфер");
                break;
            case "SUPP":
                softAssert.isElementTextEqual(supplierType, "ПОСТАВЩИК");
                softAssert.isElementTextContains(title, "Заказ");
                break;
            case "WH":
                softAssert.isElementTextEqual(supplierType, "СКЛАД");
                softAssert.isElementTextContains(title, "Трансфер");
                break;
        }
        softAssert.isElementTextEqual(supplierName, shipmentData.getSendingLocationName());
        softAssert.isElementTextContains(supplierCode, shipmentData.getSendingLocation());
        softAssert.isElementTextEqual(supplierPhoneNumber, sendingLocation.getContactPhone());
        softAssert.isElementTextEqual(supplierContactPersonName, sendingLocation.getContactName());
        softAssert.isElementTextEqual(emailAddress, sendingLocation.getEmail());
        if (shipments.size() == 1 && otherProducts.size() == 0) {
            softAssert.isTrue(shipmentsTabs.getCount() == 0, "Wrong tabs count");
        }

        for (int i = 0; i < shipments.size(); i++) {
            SupplyCardShipmentsData shipment = shipments.get(i);
            if (shipments.size() > 1) {
                shipmentsTabs.get(i).click();
            }
            if (shipmentsTabs.getCount() > 1) {
                softAssert.isElementTextContains(shipmentNumber, shipment.getShipmentId());
            }
            softAssert.isElementTextContains(shipmentNumber, shipment.getProducts().size() + " артикул");

            //разрабатывали с костылём
            String dateUiFormat = "d MMM, H:mm";
            String dateApiFormat = "yyyy-MM-dd HH:mm:ss";
            LocalDateTime secRecDate = shipment.getSecRecDate();
            LocalDateTime secRecDateFromDetails = DateTimeUtil.strToLocalDateTime(shipmentData.getDate().toString() +
                    " " + shipmentData.getTime(), dateApiFormat);
            if (secRecDate == null) {
                softAssert.isEquals(DateTimeUtil.strToLocalDateTime(shipmentDate.getText(), dateUiFormat), secRecDateFromDetails.minusSeconds(secRecDateFromDetails.getSecond()), "sec rec date");
            } else {
                softAssert.isEquals(DateTimeUtil.strToLocalDateTime(shipmentDate.getText(), dateUiFormat), secRecDate.plusHours(3).minusSeconds(secRecDate.getSecond()), "sec rec date");
            }
            //

            Integer fact = shipment.getPalletFactQuantity();
            Integer plan = shipment.getPalletPlanQuantity();
            if (fact == null || fact == 0) {
                softAssert.isElementTextEqual(shipmentReceivedCondition, "ожидается палет ");
                softAssert.isElementTextEqual(receivedPlannedQuantity, String.valueOf(plan));
            } else if (fact > 0) {
                softAssert.isElementTextEqual(shipmentReceivedCondition, "получено палет ");
                softAssert.isElementTextEqual(receivedPlannedQuantity, fact + "/" + plan);
            }
            List<SupplyCardProductsData> apiProductsDataList = shipment.getProducts();
            //Фронт сортирует по возрастающему отделу
            apiProductsDataList = apiProductsDataList.stream().sorted(Comparator.comparing(SupplyCardProductsData::getDepartmentId)).collect(Collectors.toList());
            List<ShipmentProductData> productsData = shipmentProducts.getFullDataList(apiProductsDataList.size());
            for (int y = 0; y < apiProductsDataList.size(); y++) {
                SupplyCardProductsData eachApiEntity = apiProductsDataList.get(y);
                ShipmentProductData eachUiEntity = productsData.get(y);
                softAssert.isEquals(eachUiEntity.getLmCode(), eachApiEntity.getLmCode(), "lmCode");
                softAssert.isEquals(eachUiEntity.getPlannedQuantity(), eachApiEntity.getExpectedQuantity(), "planned quantity");
                Integer received = eachApiEntity.getReceivedQuantity();
                if (received != 0) {
                    softAssert.isEquals(eachUiEntity.getReceivedQuantity(), eachApiEntity.getReceivedQuantity(), "received quantity");
                }
            }
            mainScrollView.scrollToBeginning();
        }
        if (otherProducts.size() > 0) {
            Element otherProductsTab = shipmentsTabs.get(shipmentsTabs.getCount() - 1);
            otherProductsTab.waitForVisibility();
            otherProductsTab.click();
            softAssert.isElementTextContains(shipmentNumber, String.valueOf(otherProducts.size()));
            List<ShipmentProductData> productsData = shipmentOtherProducts.getFullDataList(otherProducts.size());
            for (int i = 0; i < otherProducts.size(); i++) {
                ShipmentProductData eachUiEntity = productsData.get(i);
                SupplyCardOtherProductsData eachApiEntity = otherProducts.get(i);
                softAssert.isEquals(eachUiEntity.getLmCode(), eachApiEntity.getLmCode(), "lmCode");
                softAssert.isEquals(eachUiEntity.getPlannedQuantity(), eachApiEntity.getOrderedQuantity(), "planned quantity");
            }
        }
        softAssert.verifyAll();
        return this;
    }

    public SupplyCardPage verifyRequiredElements() {
        String pageSource = getPageSource();
        softAssert.areElementsVisible(pageSource, title, supplierType, supplierName, supplierCode, shipmentNumber);
        softAssert.verifyAll();
        return this;
    }
}
