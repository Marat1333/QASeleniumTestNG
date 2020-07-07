package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
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

public class OneDateSuppliesPage extends CommonMagMobilePage {
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

    AndroidScrollView<AppointmentCardData> singleDateAppointmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//android.view.ViewGroup/android.widget.TextView[2][not(following-sibling::android.widget.TextView[contains(@text,'палет')]) and not(contains(@text,'палет'))]/..",
            AppointmentWidget.class);

    //немного кривой xpath, без локаторов не придумал как по другому сделать
    AndroidScrollView<ShipmentCardData> singleDateShipmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//*[contains(@text,'получено') or (contains(@text,'ожидается'))]/..[not(android.widget.TextView[@text='Сегодня' or @text='Вчера'])]",
            ShipmentWidget.class);

    AndroidScrollView<AppointmentCardData> multiDateAppointmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./descendant::android.view.ViewGroup[1]/android.view.ViewGroup" +
            "/descendant::android.view.ViewGroup[2]/android.view.ViewGroup[./android.view.ViewGroup]//android.view.ViewGroup" +
            "[not(./android.widget.TextView[3]) and ./android.widget.TextView]", AppointmentWidget.class);

    AndroidScrollView<ShipmentCardData> multiDateShipmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./descendant::android.view.ViewGroup[1]/android.view.ViewGroup" +
            "/descendant::android.view.ViewGroup[2]/android.view.ViewGroup[./android.view.ViewGroup]//android.view.ViewGroup" +
            "/android.widget.TextView[3]/ancestor::android.view.ViewGroup[1]", ShipmentWidget.class);

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

    @Step("Проверить, что данные корректно отображены")
    public OneDateSuppliesPage shouldDataIsCorrect(ShipmentDataList data) {
        List<ShipmentData> dataList = data.getItems();
        List<AppointmentCardData> appointmentUiData = singleDateAppointmentWidgetList.getFullDataList();
        mainScrollView.scrollToBeginning();
        List<ShipmentCardData> shipmentsUiData = singleDateShipmentWidgetList.getFullDataList();
        for (ShipmentData eachData : dataList){
            String rowType = eachData.getRowType();
            if (rowType.equals("FR_APPOINTMENT")){
                ShipmentCardData eachShipment = shipmentsUiData.get(0);
                softAssert.isEquals(eachData.getSendingLocationName(), eachShipment.getName(),"name");
                softAssert.isEquals(DateTimeUtil.strToLocalDateTime(eachData.getDate().toString()+" "+eachData.getTime(),"yyyy-MM-dd HH:mm:ss"),
                        eachShipment.getDateAndTime(),"date");
                softAssert.isEquals(eachData.getIsFullReceived(), eachShipment.getIsFullReceived(), "isFullReceived");
                softAssert.isEquals(eachData.getPalletPlan(), eachShipment.getExpectedQuantity(),"pallet plan");
                softAssert.isEquals(eachData.getPalletFact(), eachShipment.getReceivedQuantity(), "pallet fact");
                shipmentsUiData.remove(0);
            }else if (rowType.equals("FIX_RESERVE")){
                AppointmentCardData eachAppointment = appointmentUiData.get(0);
                softAssert.isEquals(eachData.getSendingLocationName(), eachAppointment.getName(),"name");
                softAssert.isEquals(DateTimeUtil.strToLocalDateTime(eachData.getDate().toString()+" "+eachData.getTime(),"yyyy-MM-dd HH:mm:ss"),
                        eachAppointment.getDateAndTime(),"date");
                appointmentUiData.remove(0);
            }else {
                throw new IllegalArgumentException("Wrong supply row type");
            }
        }
        softAssert.verifyAll();
        return this;
    }

}
