package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.AppointmentCardData;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.ShipmentCardData;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.AppointmentWidget;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.ShipmentWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.util.List;

public class SuppliesListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "Поиск поставок")
    Button navigateToSearchSupplierButton;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/android.view.ViewGroup[2]" +
            "/android.view.ViewGroup[1]//android.widget.TextView")
    Element selectDepartmentBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/android.view.ViewGroup[2]" +
            "/android.view.ViewGroup[2]//android.widget.TextView")
    Element selectPeriodBtn;

    AndroidScrollView<String> mainScrollView = new AndroidScrollView<>(driver, AndroidScrollView.TYPICAL_LOCATOR);

    AndroidScrollView<AppointmentCardData> singleDateReserveWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//android.view.ViewGroup/android.widget.TextView[2][not" +
            "(following-sibling::android.widget.TextView[contains(@text,'палет')]) and not(contains(@text,'палет')) and not(contains(@text,'не найдено'))]/..",
            AppointmentWidget.class);

    //немного кривой xpath, без локаторов не придумал как по другому сделать
    AndroidScrollView<ShipmentCardData> singleDateShipmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//*[contains(@text,'получено') or (contains(@text,'ожидается'))]" +
            "/..[not(android.widget.TextView[@text='Сегодня' or @text='Вчера' or @text='Завтра' or contains(@text,'пн') or contains(@text,'вт') or " +
            "contains(@text,'ср') or contains(@text,'чт') or contains(@text,'пт') or contains(@text,'сб') or contains(@text,'вс')])]",
            ShipmentWidget.class);

    @AppFindBy(xpath = "//android.widget.TextView[@text='Сегодня' or @text='Вчера' or @text='Завтра' or contains(@text,'пн') " +
            "or contains(@text,'вт') or contains(@text,'ср') or contains(@text,'чт') or contains(@text,'пт') " +
            "or contains(@text,'сб') or contains(@text,'вс')]")
    ElementList<Element> weekOptions;

    @Override
    public void waitForPageIsLoaded() {
        waitUntilProgressBarAppearsAndDisappear();
        selectDepartmentBtn.waitForVisibility();
        selectPeriodBtn.waitForVisibility();
    }

    @Step("Открыть модальное окно выбора отдела")
    public DepartmentListPage openDepartmentSelectorPage() {
        selectDepartmentBtn.click();
        return new DepartmentListPage();
    }

    @Step("Открыть модальное окно выбора периода")
    public PeriodSelectorPage openPeriodSelectorPage() {
        selectPeriodBtn.click();
        return new PeriodSelectorPage();
    }

    @Step("Перейти на страницу поиска поставщиков")
    public SearchSupplierPage goToSearchSupplierPage(){
        navigateToSearchSupplierButton.click();
        return new SearchSupplierPage();
    }

    @Step("Проверить, что данные корректно отображены")
    public SuppliesListPage shouldDataIsCorrect(ShipmentDataList data) throws Exception {
        List<ShipmentData> dataList = data.getItems();
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

    @Step("Проверить, что данные корректно отображены")
    public SuppliesListPage shouldWeekDataIsCorrect(ShipmentDataList... data) throws Exception {
        if (data.length != 7) {
            throw new IllegalArgumentException("Need 7 days data to verify");
        }
        for (int i = 0; i < data.length; i++) {
            List<ShipmentData> dataList = data[i].getItems();
            boolean needToVerify = false;
            for (ShipmentData tmp : dataList) {
                if (tmp.getRowType().equals("FR_APPOINTMENT")) {
                    needToVerify = true;
                }
            }
            Element dayOfWeekOption = weekOptions.get(i);
            if (!needToVerify) {
                softAssert.isTrue(dayOfWeekOption.findChildElement("./following-sibling::*[contains(@text,'поставок не найдено')]").isVisible(), "поставок не должно быть");
            } else {
                dayOfWeekOption.waitForVisibility();
                dayOfWeekOption.click();
                waitUntilProgressBarAppearsAndDisappear();
                shouldDataIsCorrect(data[i]);
                mainScrollView.scrollUpToText(dayOfWeekOption.getText());
                dayOfWeekOption.waitForVisibility();
                dayOfWeekOption.click();
            }
        }
        return this;
    }

}
