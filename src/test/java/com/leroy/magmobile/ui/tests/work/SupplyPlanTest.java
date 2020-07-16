package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.api.Module;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.SupplyPlanClient;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletData;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanCard;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.DepartmentListPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.*;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SupplyDailyShipmentInfo;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SupplyDetailsCardInfo;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.FewShipmentsModalPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.OtherProductsModal;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.ReserveModalPage;
import com.leroy.utils.DateTimeUtil;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    //TODO create several helpers

    private LocalDate[] getCurrentCalendarWeek() {
        LocalDate[] week = new LocalDate[7];
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            week[i] = date.plusDays(i);
        }
        return week;
    }

    private ShipmentData getRandomShipment(String departmentId) {
        List<ShipmentData> weekShipments = getWeekShipments(departmentId);
        return weekShipments.get((int) (Math.random() * weekShipments.size()));
    }

    private SupplyDetailsCardInfo getMultiShipmentSupply() {
        List<ShipmentData> weekShipments;
        for (int i = 1; i < 16; i++) {
            weekShipments = getWeekShipments(String.valueOf(i));
            for (ShipmentData eachSupply : weekShipments) {
                SupplyCardData supplyCardData = client.getSupplyCard(new GetSupplyPlanCard().setDocumentNo(eachSupply.getDocumentNo().asText())
                        .setDocumentType(eachSupply.getDocumentType().asText())
                        .setSendingLocation(eachSupply.getSendingLocation())
                        .setSendingLocationType(eachSupply.getSendingLocationType())).asJson();
                try {
                    if (supplyCardData.getShipments().size() > 1) {
                        return new SupplyDetailsCardInfo(eachSupply, supplyCardData, String.valueOf(i));
                    }
                } catch (NullPointerException e) {
                    Log.warn("Mash-up load error");
                }
            }
        }
        return null;
    }

    private SupplyDetailsCardInfo getSupplyWithExtraProducts() {
        List<ShipmentData> weekShipments;
        for (int i = 1; i < 16; i++) {
            weekShipments = getWeekShipments(String.valueOf(i));
            for (ShipmentData eachSupply : weekShipments) {
                SupplyCardData supplyCardData = client.getSupplyCard(new GetSupplyPlanCard().setDocumentNo(eachSupply.getDocumentNo().asText())
                        .setDocumentType(eachSupply.getDocumentType().asText())
                        .setSendingLocation(eachSupply.getSendingLocation())
                        .setSendingLocationType(eachSupply.getSendingLocationType())).asJson();
                try {
                    //условие на случай, ошибки бэка, когда все товары будут в otherProducts
                    if (supplyCardData.getOtherProducts().size() > 0 && supplyCardData.getShipments().get(0).getProducts().get(0).getLmCode() != null) {
                        return new SupplyDetailsCardInfo(eachSupply, supplyCardData, String.valueOf(i));
                    }
                } catch (NullPointerException e) {
                    Log.warn("Mash-up load error");
                }
            }
        }
        return null;
    }

    private SupplyDailyShipmentInfo getTodayReserve() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            List<ShipmentData> data = client.getShipments(new GetSupplyPlanDetails().setDate(date)
                    .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                    .setDepartmentId(i)).asJson().getItems();
            data = data.stream().filter(y -> y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
            if (data.size() > 0) {
                return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
            }
        }
        return null;
    }

    private SupplyDailyShipmentInfo getNotTodayReserve() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            for (int z = 1; z < 7; z++) {
                List<ShipmentData> data = client.getShipments(new GetSupplyPlanDetails().setDate(date.plusDays(z))
                        .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                        .setDepartmentId(i)).asJson().getItems();
                data = data.stream().filter(y -> y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
                if (data.size() > 0) {
                    return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
                }
            }
        }
        return null;
    }

    private List<ShipmentData> getWeekShipments(String departmentId) {
        LocalDate date = LocalDate.now();
        List<ShipmentDataList> responses = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            responses.add(client.getShipments(new GetSupplyPlanDetails().setDate(date.plusDays(i))
                    .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                    .setDepartmentId(departmentId)).asJson());
        }
        List<ShipmentDataList> nonEmptyResponses = responses.stream().filter((i) -> i.getItems().size() > 0).collect(Collectors.toList());
        List<ShipmentData> weekSupplyList = new ArrayList<>();
        for (ShipmentDataList each : nonEmptyResponses) {
            weekSupplyList.addAll(each.getItems());
        }
        weekSupplyList = weekSupplyList.stream().filter(i -> !i.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
        return weekSupplyList;
    }

    private SupplyDailyShipmentInfo getTodayShipment() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            List<ShipmentData> data = client.getShipments(new GetSupplyPlanDetails().setDate(date)
                    .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                    .setDepartmentId(i)).asJson().getItems();
            data = data.stream().filter(y -> !y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
            if (data.size() > 0) {
                return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
            }
        }
        return null;
    }

    private SupplyDailyShipmentInfo getNotTodayShipment() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            for (int z = 1; z < 7; z++) {
                List<ShipmentData> data = client.getShipments(new GetSupplyPlanDetails().setDate(date.plusDays(z))
                        .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                        .setDepartmentId(i)).asJson().getItems();
                data = data.stream().filter(y -> !y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
                if (data.size() > 0) {
                    return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
                }
            }
        }
        return null;
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
        //supplierWeekSuppliesPage.shouldDataIsCorrect(byIdResponse);

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

    @Test(description = "C3293186 проверить детали поставки")
    public void testSupplyCard() throws Exception {
        ShipmentData randomSupply = getRandomShipment(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        String supplierId = randomSupply.getSendingLocation();
        String supplierName = randomSupply.getSendingLocationName();

        LocalDate today = LocalDate.now();
        LocalDate shipmentDate = randomSupply.getDate();
        long dateDiff = DateTimeUtil.getDateDifferenceInDays(today, shipmentDate);

        GetSupplyPlanCard param = new GetSupplyPlanCard().setDocumentNo(randomSupply.getDocumentNo().asText())
                .setDocumentType(randomSupply.getDocumentType().asText())
                .setSendingLocation(supplierId)
                .setSendingLocationType(randomSupply.getSendingLocationType());
        SupplyCardData data = client.getSupplyCard(param).asJson();

        //Step 1
        step("Проверить, что все данные в карточке поставки отображены корректно");
        SuppliesListPage suppliesListPage = precondition();
        if (dateDiff > 0) {
            PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
            periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
            suppliesListPage.choseDayOfWeek(dateDiff);
        }
        String apiDateFormat = "yyyy-MM-dd HH:mm:ss";
        SupplyCardPage supplyCardPage = suppliesListPage.goToSupplyCard(supplierName,
                DateTimeUtil.strToLocalDateTime(shipmentDate.toString() + " " + randomSupply.getTime(), apiDateFormat), randomSupply.getPalletPlan());
        supplyCardPage.shouldDataIsCorrect(randomSupply, data);
    }

    @Test(description = "C3293187 Переключение между табами в карточке заказа/трансфера")
    public void testSupplyCardTabSwitch() throws Exception {
        //Pre-conditions
        String apiDateFormat = "yyyy-MM-dd HH:mm:ss";
        SupplyDetailsCardInfo multiShipment = getMultiShipmentSupply();
        SupplyDetailsCardInfo otherProductsSupply = getSupplyWithExtraProducts();

        SuppliesListPage suppliesListPage = precondition();
        SupplyCardPage supplyCardPage;

        if (otherProductsSupply != null) {
            ShipmentData randomSupply = otherProductsSupply.getDetails();
            String supplierName = randomSupply.getSendingLocationName();

            String tmpDepartment = otherProductsSupply.getDepartmentId();
            String department = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;
            DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
            departmentListPage.selectDepartmentById(department);

            LocalDate today = LocalDate.now();
            LocalDate shipmentDate = randomSupply.getDate();
            long dateDiff = DateTimeUtil.getDateDifferenceInDays(today, shipmentDate);
            if (dateDiff > 0) {
                PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
                periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
                suppliesListPage.choseDayOfWeek(dateDiff);
            }
            //Step 1
            step("Перейти в карточку поставки у которой есть дополнительные товары");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplierName,
                    DateTimeUtil.strToLocalDateTime(shipmentDate.toString() + " " + randomSupply.getTime(), apiDateFormat), randomSupply.getPalletPlan());
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.FIRST_SHIPMENT);

            //Step 2
            step("Открыть вкладку \"Остальное\"");
            supplyCardPage.switchTab(SupplyCardPage.Tab.OTHER_PRODUCTS);
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.OTHER_PRODUCTS);
            supplyCardPage.goBack();
        }
        if (multiShipment != null) {
            ShipmentData randomSupply = multiShipment.getDetails();
            String supplierName = randomSupply.getSendingLocationName();

            String tmpDepartment = multiShipment.getDepartmentId();
            String department = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;
            DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
            departmentListPage.selectDepartmentById(department);

            LocalDate today = LocalDate.now();
            LocalDate shipmentDate = randomSupply.getDate();
            long dateDiff = DateTimeUtil.getDateDifferenceInDays(today, shipmentDate);
            if (dateDiff > 0) {
                PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
                periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
                suppliesListPage.choseDayOfWeek(dateDiff);
            }
            //Step 3
            step("Перейти в карточку поставки у которой есть более одной отгрузки");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplierName,
                    DateTimeUtil.strToLocalDateTime(shipmentDate.toString() + " " + randomSupply.getTime(), apiDateFormat), randomSupply.getPalletPlan());
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.FIRST_SHIPMENT);

            //Step 4
            step("Открыть вкладку \"2 отгрузка\"");
            supplyCardPage.switchTab(SupplyCardPage.Tab.SECOND_SHIPMENT);
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.SECOND_SHIPMENT);
        }
    }

    @Test(description = "C3293191 Модальные окна справки")
    public void testHintsModal() throws Exception {
        String apiDateFormat = "yyyy-MM-dd HH:mm:ss";
        SupplyDailyShipmentInfo todayReserve = getTodayReserve();
        SupplyDailyShipmentInfo notTodayReserve = getNotTodayReserve();
        String dept;
        ShipmentData data;
        ReserveModalPage reserveModalPage;

        SuppliesListPage suppliesListPage = precondition();
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();

        if (todayReserve != null) {
            String tmpDepartment = todayReserve.getDepartmentId();
            dept = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;
            departmentListPage.selectDepartmentById(dept);
            data = todayReserve.getData();

            //Step 1
            step("Открыть модальное окно-подсказку о резерве поставки через дневной вид");
            reserveModalPage = suppliesListPage.openReserveModal(data.getSendingLocationName(),
                    DateTimeUtil.strToLocalDateTime(data.getDate().toString() + " " + data.getTime(), apiDateFormat));
            reserveModalPage.verifyRequiredElements();
            reserveModalPage.closeModal();
        }
        if (notTodayReserve != null) {
            String tmpDepartment = notTodayReserve.getDepartmentId();
            dept = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;
            suppliesListPage.openDepartmentSelectorPage();
            departmentListPage.selectDepartmentById(dept);
            PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
            periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);

            data = notTodayReserve.getData();
            String supplierId = data.getSendingLocation();
            String supplierName = data.getSendingLocationName();
            LocalDateTime reserveTime = DateTimeUtil.strToLocalDateTime(data.getDate().toString() + " " + data.getTime(), apiDateFormat);

            LocalDate today = LocalDate.now();
            LocalDate shipmentDate = data.getDate();
            long dateDiff = DateTimeUtil.getDateDifferenceInDays(today, shipmentDate);
            suppliesListPage.choseDayOfWeek(dateDiff);

            //Step 2
            step("Открыть модальное окно-подсказку о резерве поставки через недельный вид");
            reserveModalPage = suppliesListPage.openReserveModal(supplierName, reserveTime);
            reserveModalPage.verifyRequiredElements();
            reserveModalPage.closeModal();

            SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
            searchSupplierPage.searchForSupplier(supplierId);
            SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);

            //Step 3
            step("Открыть модальное окно-подсказку о резерве поставки через поиск поставок поставщика");
            supplierWeekSuppliesPage.openReserveModal(supplierName, reserveTime);
            reserveModalPage.verifyRequiredElements();
            reserveModalPage.closeModal();
            supplierWeekSuppliesPage.goBack();
        }
        SupplyDetailsCardInfo otherProductsSupply = getSupplyWithExtraProducts();
        if (otherProductsSupply != null) {
            String tmpDepartment = otherProductsSupply.getDepartmentId();
            dept = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;
            data = otherProductsSupply.getDetails();
            String supplierId = data.getSendingLocation();
            String supplierName = data.getSendingLocationName();
            Integer plannedPalletQuantity = data.getPalletPlan();
            LocalDateTime shipmentTime = DateTimeUtil.strToLocalDateTime(data.getDate().toString() + " " + data.getTime(), apiDateFormat);

            suppliesListPage.openDepartmentSelectorPage();
            departmentListPage.selectDepartmentById(dept);
            SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
            searchSupplierPage.searchForSupplier(supplierId);
            SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);
            SupplyCardPage supplyCardPage = supplierWeekSuppliesPage.goToSupplyCard(supplierName, shipmentTime, plannedPalletQuantity);

            //Step 4
            step("Перейти в карточку поставки, у которой есть дополнительные товары и открыть модалку при выбранном табе отгрузки");
            supplyCardPage.openHintModal();
            FewShipmentsModalPage fewShipmentsModalPage = new FewShipmentsModalPage();
            fewShipmentsModalPage.verifyRequiredElements();
            fewShipmentsModalPage.closeModal();
            supplyCardPage = new SupplyCardPage();

            //Step 5
            step("переключится на таб остальных товаров и открыть модалку");
            supplyCardPage.switchTab(SupplyCardPage.Tab.OTHER_PRODUCTS);
            supplyCardPage.openHintModal();
            OtherProductsModal otherProductsModal = new OtherProductsModal();
            otherProductsModal.verifyRequiredElements();
        }
    }

    @Test(description = "C3293192 Навигация в/из карточки заказа/трансфера")
    public void testNavigationToSupplyCard() throws Exception {
        String apiDateFormat = "yyyy-MM-dd HH:mm:ss";
        String dept;
        SupplyCardPage supplyCardPage;

        SupplyDailyShipmentInfo todayShipment = getTodayShipment();
        SupplyDailyShipmentInfo notTodayShipment = getNotTodayShipment();

        SuppliesListPage suppliesListPage = precondition();
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        if (todayShipment!=null){
            String tmpDepartment = todayShipment.getDepartmentId();
            dept = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;
            ShipmentData supplyData = todayShipment.getData();
            String supplierName = supplyData.getSendingLocationName();
            LocalDateTime supplyDate = DateTimeUtil.strToLocalDateTime(supplyData.getDate()+" "+supplyData.getTime(), apiDateFormat);
            Integer plannedQuantity = supplyData.getPalletPlan();

            departmentListPage.selectDepartmentById(dept);

            //Step 1
            step("Перейти в карточку товара из дневного вида списка поставок и обратно");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplierName, supplyDate, plannedQuantity);
            supplyCardPage.verifyRequiredElements();
            supplyCardPage.goBack();
            suppliesListPage = new SuppliesListPage();
            suppliesListPage.verifyRequiredElements(false);
        }
        if (notTodayShipment!=null){
            String tmpDepartment = notTodayShipment.getDepartmentId();
            dept = tmpDepartment.length() > 1 ? tmpDepartment : "0" + tmpDepartment;

            ShipmentData supplyData = notTodayShipment.getData();
            String supplierName = supplyData.getSendingLocationName();
            String supplierId = supplyData.getSendingLocation();
            LocalDateTime supplyDate = DateTimeUtil.strToLocalDateTime(supplyData.getDate()+" "+supplyData.getTime(), apiDateFormat);
            Integer plannedQuantity = supplyData.getPalletPlan();

            LocalDate today = LocalDate.now();
            LocalDate shipmentDate = supplyData.getDate();
            long dateDiff = DateTimeUtil.getDateDifferenceInDays(today, shipmentDate);

            suppliesListPage.openDepartmentSelectorPage();
            departmentListPage.selectDepartmentById(dept);
            if (dateDiff > 0) {
                PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
                periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
                suppliesListPage.choseDayOfWeek(dateDiff);
            }

            //Step 2
            step("Перейти в карточку товара из недельного вида списка поставок и обратно");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplierName, supplyDate, plannedQuantity);
            supplyCardPage.verifyRequiredElements();
            supplyCardPage.goBack();
            suppliesListPage = new SuppliesListPage();
            suppliesListPage.closeChosenDayOfWeek(dateDiff);
            suppliesListPage.verifyRequiredElements(true);

            //Step 3
            step("Перейти в карточку товара из поиска поставок и обратно");
            SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
            searchSupplierPage.searchForSupplier(supplierId);
            SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);
            supplyCardPage = supplierWeekSuppliesPage.goToSupplyCard(supplierName, supplyDate, plannedQuantity);
            supplyCardPage.verifyRequiredElements();
            supplyCardPage.goBack();
            supplierWeekSuppliesPage = new SupplierWeekSuppliesPage();
            supplierWeekSuppliesPage.verifyRequiredElements();
        }
    }
}
