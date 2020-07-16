package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletData;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            ReserveWidget.class);

    //немного кривой xpath, без локаторов не придумал как по другому сделать
    AndroidScrollView<ShipmentCardData> singleDateShipmentWidgetList = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//*[contains(@text,'получено') or contains(@text,'ожидается')]" +
            "/..[not(android.widget.TextView[@text='Сегодня' or @text='Вчера' or @text='Завтра' or contains(@text,', пн') or contains(@text,', вт') or " +
            "contains(@text,', ср') or contains(@text,', чт') or contains(@text,', пт') or contains(@text,', сб') or contains(@text,', вс')])]",
            ShipmentWidget.class);

    @AppFindBy(xpath = "//android.widget.ScrollView//android.widget.TextView[@text='Сегодня' or @text='Вчера' or @text='Завтра' or contains(@text,', пн') " +
            "or contains(@text,', вт') or contains(@text,', ср') or contains(@text,', чт') or contains(@text,', пт') " +
            "or contains(@text,', сб') or contains(@text,', вс')]")
    ElementList<Element> weekOptions;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.widget.TextView[@text='Сегодня' or @text='Вчера' or @text='Завтра' or contains(@text,', пн') " +
            "or contains(@text,', вт') or contains(@text,', ср') or contains(@text,', чт') or contains(@text,', пт') or contains(@text,', сб') " +
            "or contains(@text,', вс')]/following-sibling::*[1]")
    ElementList<Element> suppliesCondition;

    @Override
    public void waitForPageIsLoaded() {
        selectDepartmentBtn.waitForVisibility();
        selectPeriodBtn.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    @Step("Выбрать нужный день недели")
    public SuppliesListPage choseDayOfWeek(long dateDiff) throws Exception {
        if (dateDiff > 6 || dateDiff < 0) {
            throw new IllegalArgumentException("date difference should be less than 7 days");
        } else {
            weekOptions.get((int) dateDiff).click();
        }
        return this;
    }

    @Step("Закрыть выбранный день недели")
    public SuppliesListPage closeChosenDayOfWeek(long dateDiff) throws Exception {
        choseDayOfWeek(dateDiff);
        return new SuppliesListPage();
    }

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
        selectDepartmentBtn.click();
        return new DepartmentListPage();
    }

    @Step("Открыть модальное окно выбора периода")
    public PeriodSelectorPage openPeriodSelectorPage() {
        selectPeriodBtn.click();
        return new PeriodSelectorPage();
    }

    @Step("Перейти на страницу поиска поставщиков")
    public SearchSupplierPage goToSearchSupplierPage() {
        navigateToSearchSupplierButton.click();
        return new SearchSupplierPage();
    }

    @Step("Проверить, что данные о поставках за день отображены корректно")
    public SuppliesListPage shouldTotalPalletDataIsCorrect(List<TotalPalletData> dataList) throws Exception {
        int apiDataCounter = 0;
        for (int i = 0; i < suppliesCondition.getCount(); i++) {
            Element each = suppliesCondition.get(i);
            TotalPalletData data = dataList.get(apiDataCounter);
            String condition = each.getText();
            String receivedQuantity;
            String plannedQuantity;
            switch (condition) {
                case "получено палет":
                    receivedQuantity = each.findChildElement("./following-sibling::*[1]").getText();
                    plannedQuantity = each.findChildElement("./following-sibling::*[3]").getText();
                    softAssert.isEquals(receivedQuantity + " из " + plannedQuantity, data.getSummPalletFact() + " из " + data.getSummPalletPlan(), "wrong quantity");
                    apiDataCounter++;
                    break;
                case "ожидается палет":
                    plannedQuantity = each.findChildElement("./following-sibling::*[1]").getText();
                    softAssert.isEquals(plannedQuantity, String.valueOf(data.getSummPalletPlan()), "planned quantity");
                    apiDataCounter++;
                    break;
                case "поставок не найдено":
            }
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что данные корректно отображены")
    public SuppliesListPage shouldDataIsCorrect(ShipmentDataList data) {
        List<ShipmentData> dataList = data.getItems();
        List<AppointmentCardData> appointmentUiData = singleDateReserveWidgetList.getFullDataList();
        mainScrollView.scrollToBeginning(1);
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
        List<String> dayOfWeekOptions = new ArrayList<>();
        weekOptions.forEach(y->dayOfWeekOptions.add(y.getText()));

        for (int i = 0; i < data.length; i++) {
            List<ShipmentData> dataList = data[i].getItems();
            boolean needToVerify = false;
            for (ShipmentData tmp : dataList) {
                if (tmp.getRowType().equals("FR_APPOINTMENT")) {
                    needToVerify = true;
                }
            }
            Element dayOfWeekOption = E(dayOfWeekOptions.get(i));
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
                dayOfWeekOption.findChildElement("./following-sibling::android.view.ViewGroup[2]").waitForInvisibility();
            }
        }
        return this;
    }

    public SuppliesListPage verifyRequiredElements(boolean weekView) {
        String pageSource = getPageSource();
        if (weekView) {
            softAssert.isTrue(weekOptions.getCount() > 1, "Отображено не более одного дня");
            softAssert.isTrue(suppliesCondition.getCount() > 1, "Отображено не более одной суммы паллет");
        } else {
            softAssert.isTrue(weekOptions.getCount() == 1, "Отображено некорректное кол-во дней");
            softAssert.isTrue(suppliesCondition.getCount() == 1, "Отображено некорректное кол-во сумм паллет");
        }
        softAssert.areElementsVisible(pageSource, selectDepartmentBtn, selectPeriodBtn, navigateToSearchSupplierButton);
        softAssert.verifyAll();
        return this;
    }

}
