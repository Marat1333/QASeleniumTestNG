package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.AppointmentCardData;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.ShipmentCardData;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SupplyNavigationObject;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.ReserveModalPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.ReserveWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.ShipmentWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class SupplierWeekSuppliesPage extends CommonMagMobilePage {
    @AppFindBy(xpath = "//*[@content-desc='SuppliesPerWeek']/*[@content-desc='SuppliesPerWeek']//android.widget.TextView[1]")
    Element supplierName;

    @AppFindBy(xpath = "//*[@content-desc='ScreenContent']/*[2]//android.widget.TextView")
    Button deptBtn;

    @AppFindBy(xpath = "//*[contains(@text,'НАЙДЕН')]")
    Element suppliesCount;

    @AppFindBy(xpath = "//*[contains(@text,'Поставок не найдено')]")
    Element notFoundMsg;

    @AppFindBy(accessibilityId = "Button")
    Button backBtn;

    @AppFindBy(xpath = "//*[@content-desc='SuppliesPerWeek']/*[@content-desc='SuppliesPerWeek']//android.view.ViewGroup[3]")
    Button clearTitleBtn;

    AndroidScrollView<String> mainScrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR);

    AndroidScrollView<AppointmentCardData> singleDateReserveWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//android.view.ViewGroup/android.widget.TextView[2][not" +
            "(following-sibling::android.widget.TextView[contains(@text,'палет')]) and not(contains(@text,'палет')) and not(contains(@text,'не найдено'))]/..",
            ReserveWidget.class);

    //немного кривой xpath, без локаторов не придумал как по другому сделать
    AndroidScrollView<ShipmentCardData> singleDateShipmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//*[contains(@text,'получено') or (contains(@text,'ожидается'))]" +
            "/..[not(android.widget.TextView[@text='Сегодня' or @text='Вчера' or @text='Завтра' or contains(@text,'пн') or contains(@text,'вт') or " +
            "contains(@text,'ср') or contains(@text,'чт') or contains(@text,'пт') or contains(@text,'сб') or contains(@text,'вс')])]",
            ShipmentWidget.class);

    @Step("Выбрать нужную поставку")
    public SupplyCardPage goToSupplyCard(SupplyNavigationObject object) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM, H:mm", new Locale("ru"));
        String formattedDateTime = object.getShipmentDate().format(formatter);

        Element supplierShipment = singleDateShipmentWidgetList.findChildElement(
                String.format(".//*[contains(@text,'%s')]/following-sibling::*[contains(@text,'%s')]/following-sibling::android.view.ViewGroup[*[contains(@text,'%s')]]",
                        object.getSupplierName(), formattedDateTime, object.getPlannedQuantity()));
        if (!supplierShipment.isVisible()) {
            mainScrollView.scrollDownToElement(supplierShipment);
        }
        supplierShipment.click();
        return new SupplyCardPage();
    }

    @Step("Открыть модальное окно резерва на поставку")
    public ReserveModalPage openReserveModal(SupplyNavigationObject object) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM, H:mm", new Locale("ru"));
        String formattedDateTime = object.getShipmentDate().format(formatter);

        Element supplierReserve = singleDateReserveWidgetList.findChildElement(
                String.format(".//*[contains(@text,'%s')]/following-sibling::*[contains(@text,'%s')]",
                        object.getSupplierName(), formattedDateTime));
        if (!supplierReserve.isVisible()) {
            mainScrollView.scrollDownToElement(supplierReserve);
        }
        supplierReserve.click();
        return new ReserveModalPage();
    }

    @Step("Открыть модальное окно выбора отдела")
    public DepartmentListPage openDepartmentSelectorPage() {
        deptBtn.click();
        return new DepartmentListPage();
    }

    @Step("Перейти назад")
    public SuppliesListPage goBack(){
        backBtn.click();
        return new SuppliesListPage();
    }

    @Step("Очистить тайтл")
    public SearchSupplierPage clearTitle(){
        clearTitleBtn.click();
        return new SearchSupplierPage();
    }

    @Override
    protected void waitForPageIsLoaded() {
        supplierName.waitForVisibility();
        deptBtn.waitForVisibility();
    }

    @Step("Проверить, что данные корректно отображены")
    public SupplierWeekSuppliesPage shouldDataIsCorrect(List<ShipmentData> dataList) {
        softAssert.isElementTextContains(suppliesCount, String.valueOf(dataList.size()));
        List<AppointmentCardData> appointmentUiData = singleDateReserveWidgetList.getFullDataList();
        mainScrollView.scrollToBeginning();
        List<ShipmentCardData> shipmentsUiData = singleDateShipmentWidgetList.getFullDataList();
        for (ShipmentData eachData : dataList) {
            String rowType = eachData.getRowType();
            if (rowType.equals("FR_APPOINTMENT")) {
                ShipmentCardData eachShipment = shipmentsUiData.get(0);
                softAssert.isEquals(eachData.getSendingLocationName(), eachShipment.getName(), "name");
                softAssert.isEquals(DateTimeUtil.strToLocalDateTime(eachData.getDate().toString() + " " + eachData.getTime(), "yyyy-MM-dd HH:mm:ss"),
                        eachShipment.getDateAndTime(), "date");
                int fact = eachData.getPalletFact();
                int plan = eachData.getPalletPlan();
                boolean isFullReceived;
                if (fact != 0 && plan != 0) {
                    isFullReceived = fact >= plan;
                } else {
                    //некорректные данные на бэке
                    isFullReceived = false;
                }
                softAssert.isEquals(isFullReceived, eachShipment.getIsFullReceived(), "isFullReceived");
                softAssert.isEquals(eachData.getPalletPlan(), eachShipment.getExpectedQuantity(), "pallet plan");
                softAssert.isEquals(eachData.getPalletFact(), eachShipment.getReceivedQuantity(), "pallet fact");
                shipmentsUiData.remove(0);
            } else if (rowType.equals("FIX_RESERVE")) {
                AppointmentCardData eachAppointment = appointmentUiData.get(0);
                softAssert.isEquals(eachData.getSendingLocationName(), eachAppointment.getName(), "name");
                softAssert.isEquals(DateTimeUtil.strToLocalDateTime(eachData.getDate().toString() + " " + eachData.getTime(), "yyyy-MM-dd HH:mm:ss"),
                        eachAppointment.getDateAndTime(), "date");
                appointmentUiData.remove(0);
            } else {
                throw new IllegalArgumentException("Wrong supply row type");
            }
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что отображается сообщение: \"Ничего не найдено\"")
    public SupplierWeekSuppliesPage shouldNotFoundMsgIsDisplayed(){
        anAssert.isElementVisible(notFoundMsg);
        return this;
    }

    @Step("Проверить, что имя поставщика отображено")
    public SupplierWeekSuppliesPage shouldSupplierNameIsCorrect(String supplierName){
        anAssert.isElementTextEqual(this.supplierName, supplierName.replaceAll("\"",""));
        return this;
    }

    public SupplierWeekSuppliesPage verifyRequiredElements(){
        softAssert.areElementsVisible(supplierName, clearTitleBtn);
        softAssert.verifyAll();
        return this;
    }
}
