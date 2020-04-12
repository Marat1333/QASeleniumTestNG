package com.leroy.magmobile.api.tests.supply_plan;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.magmobile.api.clients.SupplyPlanClient;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardData;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardProductsData;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardSendingLocationData;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardShipmentsData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletData;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanCard;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SupplyPlanTest extends BaseProjectApiTest {

    @BeforeClass
    public void setUp() {
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("15");
    }

    @Inject
    Provider<SupplyPlanClient> supplyPlanClient;

    private final String SUPPLIER = "1001802015";

    private enum LocationType {
        SUPPLIER("SUPP"),
        WAREHOUSE("WH"),
        STORE("ST");

        private String typeName;

        LocationType(String name) {
            typeName = name;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    private enum DocumentType {
        PO("PO"),
        TRANSFER("TSF");

        private String typeName;

        DocumentType(String name) {
            typeName = name;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    private List<LocalDate> getCalendarDatesFromBeginDate(int daysQuantity, LocalDate beginDate) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(beginDate);
        if (daysQuantity > 0) {
            for (int i = 1; i <= daysQuantity; i++) {
                dates.add(beginDate.plusDays(i));
            }
            return dates;
        } else if (daysQuantity < 0) {
            for (int i = 1; i >= daysQuantity; i--) {
                dates.add(beginDate.minusDays(i));
            }
            return dates;
        } else {
            return dates;
        }
    }

    private void verifyDataIsSortedByDate(List<ShipmentData> data) {
        List<Date> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String tmp;
        Date tmpDate;
        for (ShipmentData data1 : data) {
            tmp = data1.getDate().toString() + "T" + data1.getTime() + ".000Z";
            try {
                tmpDate = sdf.parse(tmp);
                dateList.add(tmpDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(dateList);
        String formattedString;
        for (int i = 0; i < dateList.size(); i++) {
            formattedString = sdf.format(dateList.get(i));
            assertThat("Shipments sorted incorrect by date", formattedString, containsString(data.get(i).getDate().toString()));
            assertThat("Shipments sorted incorrect by time", formattedString, containsString(data.get(i).getTime()));
        }
    }

    private List<String> sortDates(List<String> datesList, boolean descendingOrder) {
        List<Date> dateList = new ArrayList<>();
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date tmpDate;
        for (String data1 : datesList) {
            try {
                tmpDate = sdf.parse(data1);
                dateList.add(tmpDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!descendingOrder) {
            Collections.sort(dateList);
        } else {
            dateList.sort(Collections.reverseOrder());
        }
        String formattedString;
        for (int i = 0; i < dateList.size(); i++) {
            formattedString = sdf.format(dateList.get(i));
            result.add(formattedString);
        }
        return result;
    }

    @Test(description = "C23184440 one date")
    public void testGetTotalPalletPerDay() {
        LocalDate date = LocalDate.of(2020, 1, 10);

        GetSupplyPlanTotal params = new GetSupplyPlanTotal()
                .setDate(date)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<TotalPalletDataList> response = supplyPlanClient.get().getTotalPallets(params);
        isResponseOk(response);
        List<TotalPalletData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (TotalPalletData data : dataList) {
            assertThat("date - " + date.toString() + " not matches with " + data.getDate(), date,
                    equalTo(data.getDate()));
        }
    }

    @Test(description = "C23184439 few dates")
    public void testGetTotalPalletPerFewDays() {
        LocalDate testDate = LocalDate.of(2020, 1, 10);
        LocalDate testDate1 = LocalDate.of(2020, 1, 20);
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(testDate);
        dateList.add(testDate1);

        GetSupplyPlanTotal params = new GetSupplyPlanTotal()
                .setDate(testDate, testDate1)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<TotalPalletDataList> response = supplyPlanClient.get().getTotalPallets(params);
        isResponseOk(response);
        List<TotalPalletData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (TotalPalletData data : dataList) {
            assertThat("test data date - not matches with " + data.getDate(), data.getDate(),
                    oneOf(dateList.toArray()));
        }
    }

    @Test(description = "C23184830 search by name")
    public void testGetSupplierByName() {
        String supplierName = "сен-гобен";

        GetSupplyPlanSuppliers params = new GetSupplyPlanSuppliers()
                .setQuery(supplierName)
                .setDepartmentId(1);

        Response<SupplierDataList> response = supplyPlanClient.get().getSuppliers(params);
        isResponseOk(response);
        List<SupplierData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (SupplierData data : dataList) {
            assertThat("supplier name - " + data.getName() + " does not contains " + supplierName,
                    data.getName(), containsStringIgnoringCase(supplierName));
        }
    }

    @Test(description = "C23184832 search by code")
    public void testGetSupplierByCode() {
        String supplierCode = "10003";

        GetSupplyPlanSuppliers params = new GetSupplyPlanSuppliers()
                .setQuery(supplierCode)
                .setDepartmentId(sessionData.getUserDepartmentId());

        Response<SupplierDataList> response = supplyPlanClient.get().getSuppliers(params);
        isResponseOk(response);
        List<SupplierData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (SupplierData data : dataList) {
            assertThat("supplier code - " + data.getSupplierId() + " does not contains " + supplierCode,
                    data.getSupplierId().toLowerCase(), containsString(supplierCode.toLowerCase()));
        }
    }

    @Test(description = "C23184833 get shipments by date")
    public void testGetShipmentsByDate() {
        LocalDate testDate = LocalDate.of(2020, 1, 10);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(testDate)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        isResponseOk(response);
        List<ShipmentData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (ShipmentData data : dataList) {
            assertThat("shipment date - " + data.getDate() + " does not matches " + testDate.toString(),
                    data.getDate(), equalTo(testDate));
        }
    }

    @Test(description = "C23184834 get shipments by supplier (one date)")
    public void testGetShipmentsBySupplier() {
        LocalDate testDate = LocalDate.of(2020, 3, 31);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(testDate)
                .setSendingLocations(SUPPLIER)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        isResponseOk(response);
        List<ShipmentData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (ShipmentData data : dataList) {
            assertThat("shipment date - " + data.getDate() + " does not matches " + testDate.toString(),
                    data.getDate(), equalTo(testDate));
            assertThat("supplier - " + data.getSendingLocation() + " does not matches " + SUPPLIER,
                    data.getSendingLocation(), equalTo(SUPPLIER));
        }
    }

    @Test(description = "C23184835 get shipments by supplier (week)")
    public void testGetShipmentsBySupplierFewDays() {
        LocalDate testDate = LocalDate.of(2020, 3, 31);
        List<LocalDate> daysList = getCalendarDatesFromBeginDate(6, testDate);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(daysList)
                .setSendingLocations(SUPPLIER)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        isResponseOk(response);
        List<ShipmentData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (ShipmentData data : dataList) {
            assertThat("shipment date - " + data.getDate() + " not in range of " + daysList.toString(),
                    data.getDate(), in(daysList));
            assertThat("supplier - " + data.getSendingLocation() + " does not matches " + SUPPLIER,
                    data.getSendingLocation(), equalTo(SUPPLIER));
        }
    }

    @Test(description = "C23185243 isFullReceived flag correct")
    public void testVerifyIsFullReceivedFlag() {
        LocalDate testDate = LocalDate.of(2020, 3, 31);
        List<LocalDate> daysList = getCalendarDatesFromBeginDate(6, testDate);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(daysList)
                .setSendingLocations(SUPPLIER)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        isResponseOk(response);
        List<ShipmentData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        for (ShipmentData data : dataList) {
            if (data.getRowType().equals("FR_APPOINTMENT")) {
                assertThat("isFullReceived should be not null", data.getIsFullReceived(), notNullValue());
                if (data.getPalletPlan() - data.getPalletFact() == 0) {
                    assertThat("isFullReceived is incorrect in supply " + data.getDocumentNo(),
                            data.getIsFullReceived(), is(true));
                } else {
                    assertThat("isFullReceived is incorrect in supply " + data.getDocumentNo(),
                            data.getIsFullReceived(), is(false));
                }
            }
        }
    }

    @Test(description = "C23185314 shipments sorted by date & time")
    public void testVerifySortByDateAndTime() {
        LocalDate testDate = LocalDate.of(2020, 3, 31);
        List<LocalDate> daysList = getCalendarDatesFromBeginDate(6, testDate);

        GetSupplyPlanDetails params = new GetSupplyPlanDetails()
                .setDate(daysList)
                .setSendingLocations(SUPPLIER)
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setShopId(sessionData.getUserShopId());

        Response<ShipmentDataList> response = supplyPlanClient.get().getShipments(params);
        isResponseOk(response);
        List<ShipmentData> dataList = response.asJson().getItems();
        assertThat(dataList.size(), greaterThan(0));
        verifyDataIsSortedByDate(dataList);
    }

    @Test(description = "C23185309 Warehouse - shop card")
    public void testGetShopCard() {
        String supplier = "5";
        String documentNumber = "1114743188";
        String defaultContactPhone = "8 800 600-82-02";
        String defaultEmail = "supportlogistic@leroymerlin.ru";
        String defaultContactName = "Группа SUPPORTLOGISTIC";


        GetSupplyPlanCard params = new GetSupplyPlanCard()
                .setSendingLocation(supplier)
                .setSendingLocationType(LocationType.STORE.getTypeName())
                .setDocumentType(DocumentType.TRANSFER.getTypeName())
                .setDocumentNo(documentNumber);

        Response<SupplyCardData> response = supplyPlanClient.get().getSupplyCard(params);
        isResponseOk(response);
        List<SupplyCardSendingLocationData> dataList = response.asJson().getSendingLocation();
        assertThat(dataList.size(), greaterThan(0));
        for (SupplyCardSendingLocationData data : dataList) {
            assertThat("store/warehouse phone is " + data.getContactPhone() + " not mathes " + defaultContactPhone,
                    data.getContactPhone(), equalTo(defaultContactPhone));
            assertThat("store/warehouse email is " + data.getEmail() + " not matches " + defaultEmail,
                    data.getEmail(), equalTo(defaultEmail));
            assertThat("store/warehouse contact name is " + data.getContactName() + " not matches " + defaultContactName,
                    data.getContactName(), equalTo(defaultContactName));
        }
    }

    @Test(description = "C23185310 isFullReceived flag correct")
    public void testVerifyIsFullReceivedFlagInCard() {
        String supplier = "5";
        String documentNumber = "1114743188";

        GetSupplyPlanCard params = new GetSupplyPlanCard()
                .setSendingLocation(supplier)
                .setSendingLocationType(LocationType.STORE.getTypeName())
                .setDocumentType(DocumentType.TRANSFER.getTypeName())
                .setDocumentNo(documentNumber);

        Response<SupplyCardData> response = supplyPlanClient.get().getSupplyCard(params);
        isResponseOk(response);
        List<SupplyCardShipmentsData> dataList = response.asJson().getShipments();
        assertThat(dataList.size(), greaterThan(0));
        for (SupplyCardShipmentsData shipments : dataList) {
            if (shipments.getStatus().equals("R") || shipments.getStatus().equals("C")) {
                assertThat("isFullReceived should be not null", shipments.getIsFullReceived(), notNullValue());
                if (shipments.getPalletPlanQuantity() - shipments.getPalletFactQuantity() == 0) {
                    assertThat("isFullReceived is incorrect in shipment " + shipments.getShipmentId(),
                            shipments.getIsFullReceived(), is(true));
                } else {
                    assertThat("isFullReceived is incorrect in shipment " + shipments.getShipmentId(),
                            shipments.getIsFullReceived(), is(false));
                }
            } else {
                assertThat("isFullReceived should be null", shipments.getIsFullReceived(), nullValue());
            }
        }
        for (SupplyCardShipmentsData data : dataList) {
            List<SupplyCardProductsData> productsData = data.getProducts();
            for (SupplyCardProductsData productData : productsData) {
                if (productData.getExpectedQuantity() - productData.getReceivedQuantity() == 0) {
                    assertThat("isFullReceived is incorrect for product " + productData.getLmCode(),
                            productData.getIsFullReceived(), is(true));
                } else {
                    assertThat("isFullReceived is incorrect for product " + productData.getLmCode(),
                            productData.getIsFullReceived(), is(false));
                }
            }
        }
    }

    //bug
    @Test(description = "C23185311 shipments sort by secRecDate")
    public void testVerifySortBySecRecDate() {
        String warehouse = "922";
        String documentNumber = "1101831359";

        GetSupplyPlanCard params = new GetSupplyPlanCard()
                .setSendingLocation(warehouse)
                .setSendingLocationType(LocationType.STORE.getTypeName())
                .setDocumentType(DocumentType.TRANSFER.getTypeName())
                .setDocumentNo(documentNumber);

        Response<SupplyCardData> response = supplyPlanClient.get().getSupplyCard(params);
        isResponseOk(response);
        List<SupplyCardShipmentsData> dataList = response.asJson().getShipments();
        assertThat(dataList.size(), greaterThan(0));
        List<String> datesList = new ArrayList<>();

        for (SupplyCardShipmentsData data : dataList) {
            datesList.add(data.getSecRecDate());
        }
        List<String> sorted = sortDates(datesList, false);
        for (int i = 0; i < dataList.size(); i++) {
            assertThat("date sorting is incorrect: date should be " + sorted.get(i) + " but date is " +
                            dataList.get(i).getSecRecDate(),
                    sorted.get(i), equalTo(dataList.get(i).getSecRecDate()));
        }

    }
}
