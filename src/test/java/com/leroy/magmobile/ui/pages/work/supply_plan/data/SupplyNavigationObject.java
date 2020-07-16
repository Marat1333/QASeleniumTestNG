package com.leroy.magmobile.ui.pages.work.supply_plan.data;

import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.PeriodSelectorPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.SuppliesListPage;
import com.leroy.utils.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupplyNavigationObject {
    private SupplyDailyShipmentInfo info;
    private ShipmentData data;
    private String supplierId;
    private String supplierName;
    private LocalDateTime shipmentDate;
    private int plannedQuantity;

    public SupplyNavigationObject(SupplyDailyShipmentInfo info) {
        this.info = info;
        data = this.info.getData();
        supplierId = data.getSendingLocation();
        supplierName = data.getSendingLocationName();
        shipmentDate = DateTimeUtil.strToLocalDateTime(data.getDate().toString() + " " + data.getTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        plannedQuantity = data.getPalletPlan();
    }

    public int getPlannedQuantity() {
        return plannedQuantity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public LocalDateTime getShipmentDate() {
        return shipmentDate;
    }

    public void choseNavigationPeriod() throws Exception{
        long dateDiff = DateTimeUtil.getDateDifferenceInDays(LocalDate.now(), data.getDate());
        if (dateDiff > 0) {
            SuppliesListPage suppliesListPage = new SuppliesListPage();
            PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
            periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
            suppliesListPage.choseDayOfWeek(dateDiff);
        }
    }

    public void choseDepartment() throws Exception{
        SuppliesListPage suppliesListPage = new SuppliesListPage();
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById(info.getDepartmentId());
    }

}
