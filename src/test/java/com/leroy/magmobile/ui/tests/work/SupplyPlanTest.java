package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.SupplyPlanClient;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletData;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.PeriodSelectorPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.SearchSupplierPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.SupplierWeekSuppliesPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.SuppliesListPage;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Guice(modules = {Module.class})
public class SupplyPlanTest extends AppBaseSteps {

    @Inject
    SupplyPlanClient client;

    private SuppliesListPage precondition() throws Exception {
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        return workPage.goToShipmentListPage();
    }

    private LocalDate[] getCurrentCalendarWeek() {
        LocalDate[] week = new LocalDate[7];
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            week[i] = date.plusDays(i);
        }
        return week;
    }

    private ShipmentData getRandomShipment(String departmentId) {
        LocalDate date = LocalDate.now();
        List<ShipmentDataList> responses = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            responses.add(client.getShipments(new GetSupplyPlanDetails().setDate(date.plusDays(i))
                    .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                    .setDepartmentId(departmentId)).asJson());
        }
        List<ShipmentDataList> nonEmptyResponses = responses.stream().filter((i) -> i.getItems().size() > 0).collect(Collectors.toList());
        int randomDay = (int) (Math.random() * nonEmptyResponses.size());
        List<ShipmentData> randomSupplyList = nonEmptyResponses.get(randomDay).getItems();
        return randomSupplyList.get((int) (Math.random() * randomSupplyList.size()));
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
        SuppliesListPage suppliesListPage = precondition();
        suppliesListPage.shouldDataIsCorrect(response1);

        //Step 2
        step("Проверить данные по поставкам и бронированиям на поставку после смены отдела");
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById(nonUserDept);
        suppliesListPage = new SuppliesListPage();
        suppliesListPage.shouldDataIsCorrect(response2);
    }

    @Test(description = "C3293182 Изменение даты по фильтру")
    public void testChangeDateByFilter() throws Exception {
        LocalDate date = LocalDate.now();

        GetSupplyPlanTotal yesterdayTotalPalletsParams = new GetSupplyPlanTotal()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setDate(date.minusDays(1));

        GetSupplyPlanTotal todayTotalPalletsParams = new GetSupplyPlanTotal()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setDate(date);

        GetSupplyPlanTotal weekTotalPalletsParam = new GetSupplyPlanTotal()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setDate(getCurrentCalendarWeek());

        GetSupplyPlanDetails yesterdayParams = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.minusDays(1));

        GetSupplyPlanDetails todayParams = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date);

        GetSupplyPlanDetails tomorrowParams = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.plusDays(1));

        GetSupplyPlanDetails todayPlus2Params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.plusDays(2));

        GetSupplyPlanDetails todayPlus3Params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.plusDays(3));

        GetSupplyPlanDetails todayPlus4Params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.plusDays(4));

        GetSupplyPlanDetails todayPlus5Params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.plusDays(5));

        GetSupplyPlanDetails todayPlus6Params = new GetSupplyPlanDetails()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDate(date.plusDays(6));

        List<TotalPalletData> yesterdayTotalPalletResponse = client.getTotalPallets(yesterdayTotalPalletsParams).asJson().getItems();
        List<TotalPalletData> todayTotalPalletResponse = client.getTotalPallets(todayTotalPalletsParams).asJson().getItems();
        List<TotalPalletData> weekTotalPalletResponse = client.getTotalPallets(weekTotalPalletsParam).asJson().getItems();

        ShipmentDataList yesterdayResponse = client.getShipments(yesterdayParams).asJson();
        ShipmentDataList todayResponse = client.getShipments(todayParams).asJson();
        ShipmentDataList tomorrowResponse = client.getShipments(tomorrowParams).asJson();
        ShipmentDataList todayPlus2Response = client.getShipments(todayPlus2Params).asJson();
        ShipmentDataList todayPlus3Response = client.getShipments(todayPlus3Params).asJson();
        ShipmentDataList todayPlus4Response = client.getShipments(todayPlus4Params).asJson();
        ShipmentDataList todayPlus5Response = client.getShipments(todayPlus5Params).asJson();
        ShipmentDataList todayPlus6Response = client.getShipments(todayPlus6Params).asJson();

        //Step 1
        step("Проверить данные по поставкам и бронированиям на поставку за сегодня");
        SuppliesListPage suppliesListPage = precondition();
        suppliesListPage.shouldTotalPalletDataIsCorrect(todayTotalPalletResponse);
        suppliesListPage.shouldDataIsCorrect(todayResponse);

        //Step 2
        step("Проверить данные по поставкам и бронированиям на поставку за вчера");
        PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
        periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.YESTERDAY);
        suppliesListPage.shouldTotalPalletDataIsCorrect(yesterdayTotalPalletResponse);
        suppliesListPage.shouldDataIsCorrect(yesterdayResponse);

        //Step 3
        step("Проверить данные по поставкам и бронированиям на поставку за неделю");
        suppliesListPage.openPeriodSelectorPage();
        periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
        suppliesListPage.shouldTotalPalletDataIsCorrect(weekTotalPalletResponse);
        suppliesListPage.shouldWeekDataIsCorrect(todayResponse, tomorrowResponse, todayPlus2Response, todayPlus3Response,
                todayPlus4Response, todayPlus5Response, todayPlus6Response);
    }

    @Test(description = "C23410073 поиск поставщиков")
    public void testSearchForSupply() throws Exception {
        String supplierName = "кнауф гипс";
        String supplierCode = "1";
        String department = "1";

        GetSupplyPlanSuppliers nameParam = new GetSupplyPlanSuppliers().setQuery(supplierName).setDepartmentId(department);
        GetSupplyPlanSuppliers idParam = new GetSupplyPlanSuppliers().setQuery(supplierCode).setDepartmentId(department);

        List<SupplierData> byNameResponse = client.getSuppliers(nameParam).asJson().getItems();
        List<SupplierData> byIdResponse = client.getSuppliers(idParam).asJson().getItems();

        //Step 1
        step("Проверить отображения результатов поиска по имени");
        SuppliesListPage suppliesListPage = precondition();
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById("01");
        SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage = searchSupplierPage.searchForSupplier(supplierName);
        searchSupplierPage.shouldDataIsCorrect(byNameResponse);

        //Step 2
        step("Проверить отображения результатов поиска по коду");
        searchSupplierPage = searchSupplierPage.searchForSupplier(supplierCode);
        searchSupplierPage.shouldDataIsCorrect(byIdResponse);

        //Step 3
        step("Проверить состояние: \"Ничего не найдено\"");
        searchSupplierPage.searchForSupplier("asd123");
        searchSupplierPage.shouldNotFoundMsgIsDisplayed();
    }

    @Test(description = "C3293184 Поиск поставок по поставщику")
    public void testSearchForSuppliesBySupplier() throws Exception {
        //TODO посмотреть что там с модалкой выбора отдела
        String department = "1";
        ShipmentData supplierDataSource = getRandomShipment(department);
        String firstSupplierCode = supplierDataSource.getSendingLocation();
        String firstSupplierName = supplierDataSource.getSendingLocationName();
        String secondSupplierCode = "12301";
        String secondSupplierName = "ЗАО САЗИ";

        GetSupplyPlanDetails param = new GetSupplyPlanDetails()
                .setPagination(true)
                .setDepartmentId(department)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSendingLocations(firstSupplierCode)
                .setDate(getCurrentCalendarWeek());
        List<ShipmentData> byIdResponse = client.getShipments(param).asJson().getItems();

        //Step 1
        step("Проверить отображения результатов поиска по имени");
        SuppliesListPage suppliesListPage = precondition();
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById("01");
        SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage = searchSupplierPage.searchForSupplier(firstSupplierCode);
        SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(firstSupplierCode);
        supplierWeekSuppliesPage.shouldSupplierNameIsCorrect(firstSupplierName);
        supplierWeekSuppliesPage.shouldDataIsCorrect(byIdResponse);

        //Step 2
        step("Проверить сообщение об отсутствии поставок у поставщика в другом отделе");
        supplierWeekSuppliesPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById("02");
        supplierWeekSuppliesPage.shouldNotFoundMsgIsDisplayed();

        //Step 3
        step("Проверить сообщение об отсутствии поставок у поставщика при переходе на страницу со списком его поставок");
        suppliesListPage = supplierWeekSuppliesPage.goBack();
        suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById("01");
        suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage.searchForSupplier(secondSupplierCode);
        searchSupplierPage.goToSupplierWeekSuppliesPage(secondSupplierCode);
        supplierWeekSuppliesPage.shouldSupplierNameIsCorrect(secondSupplierName);
        supplierWeekSuppliesPage.shouldNotFoundMsgIsDisplayed();
    }

    @Test(description = "C23409944 история поиска поставщиков")
    public void testSearchHistory() throws Exception {
        GetSupplyPlanSuppliers suppliersParam = new GetSupplyPlanSuppliers().setQuery("14").setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        List<SupplierData> suppliersResponse = client.getSuppliers(suppliersParam).asJson().getItems();
        List<String> supplierIdList = suppliersResponse.stream().map(SupplierData::getSupplierId).limit(11).collect(Collectors.toList());

        //Step 1
        step("Создать исотрию поиска из 11 элементов");
        SuppliesListPage suppliesListPage = precondition();
        SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage.shouldFirstSearchMsgBeVisible();
        searchSupplierPage.createSearchHistory(supplierIdList);
        supplierIdList.remove(0);
        searchSupplierPage.shouldSearchHistoryIsCorrect(supplierIdList);
    }

    @Test(description = "C23409945 навигация по поиску поставщиков")
    public void testNavigation() throws Exception {
        GetSupplyPlanSuppliers suppliersParam = new GetSupplyPlanSuppliers().setQuery("1").setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        List<SupplierData> suppliersResponse = client.getSuppliers(suppliersParam).asJson().getItems();
        String supplierId = suppliersResponse.stream().map(SupplierData::getSupplierId).limit(1).collect(Collectors.toList()).get(0);

        //Step 1
        step("Перейти на страницу поставок поставщика и нажать на крест");
        SuppliesListPage suppliesListPage = precondition();
        SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage.searchForSupplier(supplierId);
        SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);
        supplierWeekSuppliesPage.clearTitle();
        searchSupplierPage.verifyRequiredElements();

        //Step 2
        step("Перейти на страницу поставок поставщика и нажать на кнопку назад");
        searchSupplierPage.searchForSupplier(supplierId);
        searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);
        supplierWeekSuppliesPage.goBack();
        suppliesListPage.verifyRequiredElements(false);

        //Step 3
        step("Выбрать недельный вид отображения поставок и перейти на страницу поставок поставщика и нажать на кнопку назад");
        PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
        periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
        suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage.searchForSupplier(supplierId);
        searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);
        supplierWeekSuppliesPage.goBack();
        suppliesListPage.verifyRequiredElements(true);

        //Step 4
        step("Перейти на страницу поставок поставщика через элемент истории поиска");
        suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage.goToSupplierWeekPageBySearchHistory(supplierId);
        supplierWeekSuppliesPage.verifyRequiredElements();
    }

}
