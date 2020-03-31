package com.leroy.magmobile.api.tests.supply_plan;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.clients.SupplyPlanClient;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletData;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SupplyPlanTest extends BaseProjectApiTest {

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @Inject
    Provider<SupplyPlanClient> supplyPlanClient;

    private List<LocalDate> getCalendarDates(int daysQuantity, LocalDate beginDate){
        List<LocalDate> dates = new ArrayList<>();
        dates.add(beginDate);
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, beginDate.getYear());
        calendar.set(Calendar.MONTH, beginDate.getMonthValue());
        calendar.set(Calendar.DAY_OF_MONTH, beginDate.getDayOfMonth()-1);
        if (daysQuantity>0) {
            for (int i = 0; i < daysQuantity; i++) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dates.add(LocalDate.of(beginDate.getYear(), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
            }
            return dates;
        }else if (daysQuantity<0){
            for (int i = 0; i > daysQuantity; i--) {
                calendar.add(Calendar.DAY_OF_MONTH, i);
                dates.add(LocalDate.of(beginDate.getYear(), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
            }
            return dates;
        }else {
            return dates;
        }
    }

    @Test(description = "C23184440 one date")
    public void testGetTotalPalletPerDay() {
        LocalDate date = LocalDate.of(2020, 1, 10);

        GetSupplyPlanTotal params = new GetSupplyPlanTotal()
                .setDate(date)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID);

        Response<TotalPalletDataList> response = supplyPlanClient.get().getTotalPallet(params);
        List<TotalPalletData> dataList = response.asJson().getItems();
        for (TotalPalletData data : dataList) {
            assertThat("date - " + date.toString() + " not matches with " + data.getDate(), date, equalTo(data.getDate()));
        }
    }

    @Test(description = "C23184439 few dates")
    public void testGetTotalPalletPerFewDays() {
        LocalDate testDate = LocalDate.of(2020, 1, 10);
        LocalDate testDate1 = LocalDate.of(2020, 1, 20);
        List<LocalDate>dateList = new ArrayList<>();
        dateList.add(testDate);
        dateList.add(testDate1);

        GetSupplyPlanTotal params = new GetSupplyPlanTotal()
                .setDate(testDate, testDate1)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID);

        Response<TotalPalletDataList> response = supplyPlanClient.get().getTotalPallet(params);
        List<TotalPalletData> dataList = response.asJson().getItems();
        int condition=0;
        for (TotalPalletData data : dataList) {
            for (LocalDate date: dateList) {
                if (date.equals(data.getDate())) {
                    condition++;
                }
            }
            assertThat("test data date - not matches with " + data.getDate(), condition, greaterThan(0));
        }
    }

    @Test(description = "C23184830 search by name")
    public void testGetSupplierByName() {
        String supplierName="сен-гобен";

        GetSupplyPlanSuppliers params = new GetSupplyPlanSuppliers()
                .setQuery(supplierName)
                .setDepartmentId(1);

        Response<SupplierDataList> response = supplyPlanClient.get().getSupplier(params);
        List<SupplierData> dataList = response.asJson().getItems();
        for (SupplierData data : dataList) {
            assertThat("supplier name - " + data.getName() + " does not contains " + supplierName, data.getName().toLowerCase(), containsString(supplierName.toLowerCase()));
        }
    }

    @Test(description = "C23184832 search by code")
    public void testGetSupplierByCode() {
        String supplierCode="10003";

        GetSupplyPlanSuppliers params = new GetSupplyPlanSuppliers()
                .setQuery(supplierCode)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);

        Response<SupplierDataList> response = supplyPlanClient.get().getSupplier(params);
        List<SupplierData> dataList = response.asJson().getItems();
        for (SupplierData data : dataList) {
            assertThat("supplier code - " + data.getSupplierId() + " does not contains " + supplierCode, data.getSupplierId().toLowerCase(), containsString(supplierCode.toLowerCase()));
        }
    }

    @Test(description = "C23184833 get shipments by date")
    public void testGetShipmentsByDate() {
        LocalDate testDate = LocalDate.of(2020, 1, 10);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(testDate)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID);

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        List<ShipmentData> dataList = response.asJson().getItems();
        for (ShipmentData data : dataList) {
            assertThat("shipment date - " + data.getDate() + " does not matches " + testDate.toString(), data.getDate(), equalTo(testDate));
        }
    }

    @Test(description = "C23184834 get shipments by supplier (one date)")
    public void testGetShipmentsBySupplier() {
        LocalDate testDate = LocalDate.of(2020, 3, 31);
        String supplier = "1001802015";

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(testDate)
                .setSendingLocations(supplier)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId("35");

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        List<ShipmentData> dataList = response.asJson().getItems();
        for (ShipmentData data : dataList) {
            assertThat("shipment date - " + data.getDate() + " does not matches " + testDate.toString(), data.getDate(), equalTo(testDate));
            assertThat("supplier - "+data.getSendingLocation()+" does not matches "+supplier,data.getSendingLocation(),equalTo(supplier));
        }
    }

    @Test(description = "C23184835 get shipments by supplier (week)")
    public void testGetShipmentsBySupplierFewDays() {
        LocalDate testDate = LocalDate.of(2020, 3, 31);
        String supplier = "1001802015";
        List<LocalDate> daysList = getCalendarDates(6, testDate);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(daysList)
                .setSendingLocations(supplier)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId("35");

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        List<ShipmentData> dataList = response.asJson().getItems();
        for (ShipmentData data : dataList) {
            assertThat("shipment date - " + data.getDate() + " not in range of " + daysList.toString(), data.getDate(), in(daysList));
            assertThat("supplier - "+data.getSendingLocation()+" does not matches "+supplier,data.getSendingLocation(),equalTo(supplier));
        }
    }
}
