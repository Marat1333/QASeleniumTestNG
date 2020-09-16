package com.leroy.magmobile.ui.tests.work;

import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
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
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SupplyNavigationData;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.FewShipmentsModalPage;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.OtherProductsModal;
import com.leroy.magmobile.ui.pages.work.supply_plan.modal.ReserveModalPage;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SupplyPlanTest extends AppBaseSteps {

    private SupplyPlanClient client;

    @BeforeClass
    private void setUpClient() {
        client = apiClientProvider.getSupplyPlanClient();
    }


    private SuppliesListPage precondition() throws Exception {
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        return workPage.goToShipmentListPage();
    }

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("32");
        return sessionData;
    }

    private LocalDate[] getCurrentCalendarWeek() {
        LocalDate[] week = new LocalDate[7];
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            week[i] = date.plusDays(i);
        }
        return week;
    }

    @Step("Выбрать период")
    private SuppliesListPage choseNavigationPeriod(LocalDate date) throws Exception {
        long dateDiff = DateTimeUtil.getDateDifferenceInDays(LocalDate.now(), date);
        if (dateDiff > 0) {
            SuppliesListPage suppliesListPage = new SuppliesListPage();
            PeriodSelectorPage periodSelectorPage = suppliesListPage.openPeriodSelectorPage();
            periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
            suppliesListPage.choseDayOfWeek(dateDiff);
        }
        return new SuppliesListPage();
    }

    @Step("Выбрать отдел")
    private SuppliesListPage choseDepartment(String departmentId) throws Exception {
        SuppliesListPage suppliesListPage = new SuppliesListPage();
        DepartmentListPage departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById(departmentId);
        return new SuppliesListPage();
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
        suppliesListPage = periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.YESTERDAY);
        suppliesListPage.shouldTotalPalletDataIsCorrect(yesterdayTotalPalletResponse);
        suppliesListPage.shouldDataIsCorrect(yesterdayResponse);

        //Step 3
        step("Проверить данные по поставкам и бронированиям на поставку за неделю");
        suppliesListPage.openPeriodSelectorPage();
        suppliesListPage = periodSelectorPage.selectPeriodOption(PeriodSelectorPage.PeriodOption.WEEK);
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
        String department = "1";
        ShipmentData supplierDataSource = client.getRandomShipment(department);
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
        departmentListPage = supplierWeekSuppliesPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById("02");
        supplierWeekSuppliesPage.shouldNotFoundMsgIsDisplayed();

        //Step 3
        step("Проверить сообщение об отсутствии поставок у поставщика при переходе на страницу со списком его поставок");
        suppliesListPage = supplierWeekSuppliesPage.goBack();
        departmentListPage = suppliesListPage.openDepartmentSelectorPage();
        departmentListPage.selectDepartmentById("01");
        suppliesListPage.goToSearchSupplierPage();
        searchSupplierPage.searchForSupplier(secondSupplierCode);
        supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(secondSupplierCode);
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
        ShipmentData randomSupply = client.getRandomShipment(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        String supplierId = randomSupply.getSendingLocation();

        GetSupplyPlanCard param = new GetSupplyPlanCard().setDocumentNo(randomSupply.getDocumentNo().asText())
                .setDocumentType(randomSupply.getDocumentType().asText())
                .setSendingLocation(supplierId)
                .setSendingLocationType(randomSupply.getSendingLocationType());
        SupplyCardData data = client.getSupplyCard(param).asJson();

        //Step 1
        step("Проверить, что все данные в карточке поставки отображены корректно");
        SuppliesListPage suppliesListPage = precondition();
        SupplyNavigationData supplyNavigationData = new SupplyNavigationData(new SupplyDailyShipmentInfo(randomSupply, EnvConstants.BASIC_USER_DEPARTMENT_ID));
        choseDepartment(supplyNavigationData.getDepartmentId());
        choseNavigationPeriod(supplyNavigationData.getDate());
        SupplyCardPage supplyCardPage = suppliesListPage.goToSupplyCard(supplyNavigationData);
        supplyCardPage.shouldDataIsCorrect(randomSupply, data);
    }

    @Test(description = "C3293187 Переключение между табами в карточке заказа/трансфера")
    public void testSupplyCardTabSwitch() throws Exception {
        //Pre-conditions
        SupplyDetailsCardInfo multiShipment = client.getMultiShipmentSupply();
        SupplyDetailsCardInfo otherProductsSupply = client.getSupplyWithExtraProducts();

        SuppliesListPage suppliesListPage = precondition();
        SupplyCardPage supplyCardPage;

        if (otherProductsSupply != null) {
            //Step 1
            step("Перейти в карточку поставки у которой есть дополнительные товары");
            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(new SupplyDailyShipmentInfo(otherProductsSupply.getDetails(), otherProductsSupply.getDepartmentId()));
            choseDepartment(supplyNavigationData.getDepartmentId());
            choseNavigationPeriod(supplyNavigationData.getDate());
            supplyCardPage = suppliesListPage.goToSupplyCard(supplyNavigationData);
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.FIRST_SHIPMENT);

            //Step 2
            step("Открыть вкладку \"Остальное\"");
            supplyCardPage.switchTab(SupplyCardPage.Tab.OTHER_PRODUCTS);
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.OTHER_PRODUCTS);
            supplyCardPage.goBack();
        }
        if (multiShipment != null) {
            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(new SupplyDailyShipmentInfo(multiShipment.getDetails(), multiShipment.getDepartmentId()));
            //Step 3
            step("Перейти в карточку поставки у которой есть более одной отгрузки");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplyNavigationData);
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.FIRST_SHIPMENT);

            //Step 4
            step("Открыть вкладку \"2 отгрузка\"");
            supplyCardPage.switchTab(SupplyCardPage.Tab.SECOND_SHIPMENT);
            supplyCardPage.shouldSwitchToNeededTabIsComplete(SupplyCardPage.Tab.SECOND_SHIPMENT);
        }
    }

    @Test(description = "C3293191 Модальные окна справки")
    public void testHintsModal() throws Exception {
        SupplyDailyShipmentInfo todayReserve = client.getTodayReserve();
        SupplyDailyShipmentInfo notTodayReserve = client.getNotTodayReserve();
        ReserveModalPage reserveModalPage;

        SuppliesListPage suppliesListPage = precondition();

        if (todayReserve != null) {
            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(todayReserve);
            choseDepartment(supplyNavigationData.getDepartmentId());

            //Step 1
            step("Открыть модальное окно-подсказку о резерве поставки через дневной вид");
            suppliesListPage = new SuppliesListPage();
            reserveModalPage = suppliesListPage.openReserveModal(supplyNavigationData);
            reserveModalPage.verifyRequiredElements();
            reserveModalPage.closeModal();
        }
        if (notTodayReserve != null) {
            String supplierId = notTodayReserve.getData().getSendingLocation();
            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(notTodayReserve);
            choseDepartment(supplyNavigationData.getDepartmentId());
            choseNavigationPeriod(supplyNavigationData.getDate());

            //Step 2
            step("Открыть модальное окно-подсказку о резерве поставки через недельный вид");
            reserveModalPage = suppliesListPage.openReserveModal(supplyNavigationData);
            reserveModalPage.verifyRequiredElements();
            reserveModalPage.closeModal();

            SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
            searchSupplierPage.searchForSupplier(supplierId);
            SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);

            //Step 3
            step("Открыть модальное окно-подсказку о резерве поставки через поиск поставок поставщика");
            supplierWeekSuppliesPage.openReserveModal(supplyNavigationData);
            reserveModalPage.verifyRequiredElements();
            reserveModalPage.closeModal();
            supplierWeekSuppliesPage.goBack();
        }
        SupplyDetailsCardInfo otherProductsSupply = client.getSupplyWithExtraProducts();
        if (otherProductsSupply != null) {
            String supplierId = otherProductsSupply.getDetails().getSendingLocation();
            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(new SupplyDailyShipmentInfo(otherProductsSupply.getDetails(), otherProductsSupply.getDepartmentId()));
            choseDepartment(supplyNavigationData.getDepartmentId());

            SearchSupplierPage searchSupplierPage = suppliesListPage.goToSearchSupplierPage();
            searchSupplierPage.searchForSupplier(supplierId);
            SupplierWeekSuppliesPage supplierWeekSuppliesPage = searchSupplierPage.goToSupplierWeekSuppliesPage(supplierId);
            SupplyCardPage supplyCardPage = supplierWeekSuppliesPage.goToSupplyCard(supplyNavigationData);

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
        SupplyCardPage supplyCardPage;
        SupplyDailyShipmentInfo todayShipment = client.getTodayShipment();
        SupplyDailyShipmentInfo notTodayShipment = client.getNotTodayShipment();

        SuppliesListPage suppliesListPage = precondition();
        if (todayShipment != null) {
            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(todayShipment);
            choseDepartment(supplyNavigationData.getDepartmentId());

            //Step 1
            step("Перейти в карточку товара из дневного вида списка поставок и обратно");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplyNavigationData);
            supplyCardPage.verifyRequiredElements();
            supplyCardPage.goBack();
            suppliesListPage = new SuppliesListPage();
            suppliesListPage.verifyRequiredElements(false);
        }
        if (notTodayShipment != null) {
            String supplierId = notTodayShipment.getData().getSendingLocation();
            LocalDate today = LocalDate.now();
            LocalDate shipmentDate = notTodayShipment.getData().getDate();
            long dateDiff = DateTimeUtil.getDateDifferenceInDays(today, shipmentDate);

            SupplyNavigationData supplyNavigationData = new SupplyNavigationData(notTodayShipment);
            choseDepartment(supplyNavigationData.getDepartmentId());
            choseNavigationPeriod(supplyNavigationData.getDate());

            //Step 2
            step("Перейти в карточку товара из недельного вида списка поставок и обратно");
            supplyCardPage = suppliesListPage.goToSupplyCard(supplyNavigationData);
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
            supplyCardPage = supplierWeekSuppliesPage.goToSupplyCard(supplyNavigationData);
            supplyCardPage.verifyRequiredElements();
            supplyCardPage.goBack();
            supplierWeekSuppliesPage = new SupplierWeekSuppliesPage();
            supplierWeekSuppliesPage.verifyRequiredElements();
        }
    }
}
