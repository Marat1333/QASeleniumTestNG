package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.SupplyPlanClient;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.OneDateSuppliesPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.PeriodSelectorPage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.time.LocalDate;

@Guice(modules = {Module.class})
public class SupplyPlanTest extends AppBaseSteps {

    @Inject
    SupplyPlanClient client;

    private OneDateSuppliesPage precondition() throws Exception{
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        return workPage.goToShipmentListPage();
    }

    @Test(description = "C3293181 Смена отдела по фильтру")
    public void testChangeDepartmentByFilter() throws Exception {
        String nonUserDept = "02";
        LocalDate date = LocalDate.now();
        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date);

        GetSupplyPlanDetails params1 = new GetSupplyPlanDetails()
                .setDepartmentId("2")
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date);

        ShipmentDataList response1 = client.getShipments(params).asJson();
        ShipmentDataList response2 = client.getShipments(params1).asJson();

        //Step 1
        step("Проверить данные по поставкам и бронированиям на поставку");
        OneDateSuppliesPage oneDateSuppliesPage = precondition();
        oneDateSuppliesPage.shouldDataIsCorrect(response1);

        //Step 2
        step("Проверить данные по поставкам и бронированиям на поставку после смены отдела");
        DepartmentListPage departmentListPage = oneDateSuppliesPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById(nonUserDept);
        oneDateSuppliesPage = new OneDateSuppliesPage();
        oneDateSuppliesPage.shouldDataIsCorrect(response2);
    }

    @Test(description = "C3293182 Изменение даты по фильтру")
    public void testChangeDateByFilter() throws Exception {
        LocalDate date = LocalDate.now();
        LocalDate yesterday = date.minusDays(1);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date);

        GetSupplyPlanDetails params1 = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(yesterday);

        ShipmentDataList todayResponse = client.getShipments(params).asJson();
        ShipmentDataList yesterdayResponse = client.getShipments(params1).asJson();

        //Step 1
        step("Проверить данные по поставкам и бронированиям на поставку за сегодня");
        OneDateSuppliesPage oneDateSuppliesPage = precondition();
        oneDateSuppliesPage.shouldDataIsCorrect(todayResponse);

        //Step 2
        step("Проверить данные по поставкам и бронированиям на поставку за вчера");
        PeriodSelectorPage periodSelectorPage = oneDateSuppliesPage.openPeriodSelectorPage();
        periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.YESTERDAY);
        oneDateSuppliesPage.shouldDataIsCorrect(yesterdayResponse);

        //Step 3
        step("Проверить данные по поставкам и бронированиям на поставку за неделю");


    }

}
