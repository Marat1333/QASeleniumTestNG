package com.leroy.magportal.api.helpers;

import static com.leroy.magportal.api.constants.PaymentMethodEnum.API;
import static com.leroy.magportal.api.constants.PaymentMethodEnum.CARD;
import static com.leroy.magportal.api.constants.PaymentMethodEnum.TPNET;
import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

import com.google.inject.Inject;
import com.leroy.common_mashups.customer_accounts.clients.CustomerClient;
import com.leroy.common_mashups.customer_accounts.data.CustomerListData;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters.CustomerType;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters.DiscriminantType;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.constants.DeliveryServiceTypeEnum;
import com.leroy.magportal.api.constants.LmCodeTypeEnum;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.constants.PaymentMethodEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.CommunicationPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.NextStepResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.PutPaymentPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.PutPaymentResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartPayload.Product;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.Address;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.DeliveryResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PostCalculateResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutHomeDeliveryPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutHomeDeliveryPayload.Appointment;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutPickupPayload;
import ru.leroymerlin.qa.core.clients.tunnel.enums.PaymentInstruments;
import ru.leroymerlin.qa.core.clients.tunnel.enums.PaymentTypes;

public class AemHelper extends BaseHelper {

    @Inject
    private TunnelClient tunnelClient;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private ShopsClient shopsClient;
    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private CustomerClient customerClient;

    private final LocalDateTime dateTime = LocalDateTime.now();

    @Step("Creates Online order with Dimensional LmCode")
    public PutPaymentResponse createDimensionalOnlineOrder(OnlineOrderTypeData orderData) {
        orderData.setLmCode(LmCodeTypeEnum.DIMENSIONAL.getValue());
        return this.createOnlineOrders(1, orderData, 1, API).stream().findFirst().get();
    }

    @Step("Creates Online order with 3 LmCodes")
    public PutPaymentResponse createOnlineOrder(OnlineOrderTypeData orderData) {
        return this.createOnlineOrders(1, orderData, 3, API).stream().findFirst().get();
    }

    @Step("Creates Online order with 3 LmCodes with TpNet payment method")
    public PutPaymentResponse createOnlineOrderTpNetPayment(OnlineOrderTypeData orderData) {
        return this.createOnlineOrders(1, orderData, 3, TPNET).stream().findFirst().get();
    }

    @Step("Creates Online order with 3 LmCodes with Card payment method")
    public PutPaymentResponse createOnlineOrderCardPayment(OnlineOrderTypeData orderData) {
        return this.createOnlineOrders(1, orderData, 3, CARD).stream().findFirst().get();
    }

    @Step("Creates Online orders of different types")
    public ArrayList<PutPaymentResponse> createOnlineOrders(Integer ordersCount,
            OnlineOrderTypeData orderData, Integer productCount, PaymentMethodEnum paymentMethod) {
        SimpleCustomerData customerData = SIMPLE_CUSTOMER_DATA_1;
        customerData.setId(getCustomerId(customerData));
        ShopData shopData = getShopData(orderData);
        ArrayList<PutPaymentResponse> result = new ArrayList<>();

        List<ThreadApiClient<PutPaymentResponse, TunnelClient>> threads = new ArrayList<>();

        for (int i = 0; i < ordersCount; i++) {
            ThreadApiClient<PutPaymentResponse, TunnelClient> myThread = new ThreadApiClient<>(
                    tunnelClient);

            try {
                Response<StepStartResponse> startResp = tunnelClient.createCheckoutProcess(
                        makeStartPayload(orderData, productCount, shopData.getId()), "AEM");
                String transactionId = startResp.asJson().getTransactionId();

                if (orderData.getDeliveryType().getType()
                        .equals(DeliveryServiceTypeEnum.PICKUP.getType())) {
                    Response<?> deliveryGetResp = tunnelClient.getPickup(transactionId, "");
                    Response<?> deliveryPutResp = tunnelClient.updatePickup(transactionId, "",
                            makePutPickupPayload(shopData.getId()));
                } else {
                    Response<DeliveryResponse> deliveryGetResp = tunnelClient
                            .getHomeDelivery(transactionId);
                    Response<PostCalculateResponse> calculateResp = tunnelClient
                            .postCalculate(transactionId,
                                    makeCalculatePayload(orderData, shopData));
                    Response<NextStepResponse> deliveryPutResp = tunnelClient
                            .updateHomeDelivery(transactionId,
                                    makePutDeliveryPayload(calculateResp.asJson(), orderData,
                                            shopData));
                }
                Response<?> commResp = tunnelClient
                        .updateCommunication(makePutCommPayload(customerData), transactionId);

                myThread.sendRequest(
                        client -> client
                                .putPaymentType(transactionId, makePaymentPayload(orderData)));
                threads.add(myThread);

            } catch (Exception e) {
                Log.error(e.getMessage());
            }


        }

        threads.forEach(t -> {
            try {
                PutPaymentResponse response = t.getData();
                if (response.getMdOrder() != null) {
                    result.add(response);
                    if (orderData.getPaymentType().equals(PaymentTypeEnum.SBERBANK.getName())) {
                        orderClient.waitUntilOrderGetStatus(response.getMdOrder(),
                                States.WAITING_FOR_PAYMENT,
                                PaymentStatusEnum.CONFIRMED);
                        paymentHelper.makePayment(response.getMdOrder(), paymentMethod);
                    }
                    orderClient.waitUntilOrderGetStatus(response.getMdOrder(),
                            States.ALLOWED_FOR_PICKING, null);
                }
            } catch (Exception e) {
                Log.error(e.getMessage());
            }
        });

        return result;
    }

    private StepStartPayload makeStartPayload(OnlineOrderTypeData orderData,
            Integer productCount, String shopId) {

        StepStartPayload payload = new StepStartPayload();

        payload.setReferral("");
        payload.setRegionId(shopsClient.getRegionIdByShopId(shopId));
        payload.setContextStoreId(shopsClient.getRefStoreIdByShopId(shopId));
        payload.setProducts(makeProducts(productCount, shopId, orderData));

        return payload;
    }

    private PutHomeDeliveryPayload makeCalculatePayload(OnlineOrderTypeData orderData,
            ShopData shop) {

        PutHomeDeliveryPayload payload = new PutHomeDeliveryPayload();
        Address address = new Address();
        address.setLatitude(String.valueOf(shop.getLat() + 0.1));
        address.setLongitude(String.valueOf(shop.getLongitude() - 0.1));
        address.setFloor("5");
        payload.setAddress(address);

        payload.setTypeOfLift(orderData.getLiftType().toString());
        payload.setDeliveryTo(orderData.getDeliveryType().getAemCode());
        payload.setDistance(1.00);

        return payload;
    }

    private PutPickupPayload makePutPickupPayload(String shopId) {

        PutPickupPayload payload = new PutPickupPayload();
        payload.setStoreId(Integer.parseInt(shopId));

        return payload;
    }

    private PutHomeDeliveryPayload makePutDeliveryPayload(PostCalculateResponse calculateResponse, OnlineOrderTypeData orderData, ShopData shopData) {

        PutHomeDeliveryPayload payload = makeCalculatePayload(orderData, shopData);
        Address address = payload.getAddress();
        address.setId(10051983);
        address.setName("Автотестная ул, 99");
        address.setStreet("Автотестная ул");
        address.setHouse("99");
        address.setEntrance("5");
        address.setFlat("5");
        address.setIntercom("k12345");

        payload.setAddress(address);
        Appointment appointment = makeAppointmentPayload(calculateResponse);
        payload.setAppointment(appointment);

        return payload;
    }

    private Appointment makeAppointmentPayload(PostCalculateResponse calculateResponse) {

        Appointment payload = new Appointment();
        List<PostCalculateResponse.Appointment> appointments = calculateResponse.getAppointments();

        return payload;
    }

    private CommunicationPayload makePutCommPayload(SimpleCustomerData customerData) {

        CommunicationPayload payload = new CommunicationPayload();
        payload.setComment("АвтоТест Аем");

        return payload;
    }

    private PutPaymentPayload makePaymentPayload(OnlineOrderTypeData orderData) {
        PaymentInstruments paymentInstruments;
        PaymentTypes paymentTypes;
        if (orderData.getPaymentType().equals(PaymentTypeEnum.CASH.getName())) {
            paymentInstruments = PaymentInstruments.CASH;
            paymentTypes = PaymentTypes.CASH;
        } else {
            paymentInstruments = PaymentInstruments.BANK_CARD;
            paymentTypes = PaymentTypes.SBERBANK;
        }

        PutPaymentPayload payload = new PutPaymentPayload();
        payload.setPaymentType(paymentTypes);
        payload.setPaymentInstrument(paymentInstruments);

        return payload;
    }

    private ArrayList<Product> makeProducts(Integer productsCount, String shopId,
            OnlineOrderTypeData orderData) {
        ArrayList<Product> result = new ArrayList<>();

        if (orderData.getLmCode() != null) {
            ProductItemData product = searchProductHelper.getProductByLmCode(orderData.getLmCode());
            result.add(productItemDataToPayload(product));
        } else {
            List<ProductItemData> products = searchProductHelper
                    .getProductsForShop(productsCount, shopId);
            for (ProductItemData productData : products) {
                result.add(productItemDataToPayload(productData));
            }
        }
        return result;
    }

    private Product productItemDataToPayload(ProductItemData product) {
        Product tunnelProduct = new Product();

        double quantity = 10.00;
        if (product.getLmCode().equals(LmCodeTypeEnum.DIMENSIONAL.getValue())) {
            quantity = 9.99;
        }

        tunnelProduct.setProductId(product.getLmCode());
        tunnelProduct.setQuantity((int) quantity);
        return tunnelProduct;
    }

    private BitrixSolutionPayload.Address makeAddressPayload() {
        BitrixSolutionPayload.Address payload = new BitrixSolutionPayload.Address();
        payload.setHouse(13);
        payload.setCity("Moscow");
        payload.setStreet("Dubki");
        payload.setPorch(7);
        payload.setFloor(10);
        payload.setApartment(52);
        payload.setIntercom(12345);

        return payload;
    }

    private BitrixSolutionPayload.PickupShop makePickupShopPayload(ShopData shop) {
        BitrixSolutionPayload.PickupShop payload = new BitrixSolutionPayload.PickupShop();
        payload.setId("3895480");
        payload.setIblockId("4");
        payload.setXmlId(convertShopId(Integer.parseInt(shop.getId())));
        payload.setName(shop.getName());
        payload.setPropertyAddressValue(shop.getAddress());
        payload.setPropertyAddressValuerId("3177131060");
        payload.setPropertyNameValue(shop.getName());
        payload.setPropertyNameValueId("3177131063");
        payload.setPropertyWorkTimeValue("08:00 - 23:00");
        payload.setPropertyWorkTimeValueId("3177131062");
        payload.setPropertyPhoneValue(shop.getPhone());
        payload.setPropertyPhoneValueId("3177131061");
        payload.setPropertyPickupOperatorsValue("rrlog01.mag051@leroymerlin.ru");
        payload.setPropertyPickupOperatorsValueId("3187492118");
        payload.setPropertyGpsCoordsValue(shop.getLat() + ", " + shop.getLongitude());
        payload.setPropertyGpsCoordsValueId("3177137913");
        payload.setDisplayName(shop.getName());

        return payload;
    }

    private BitrixSolutionPayload.PvzData makePvzPayload() {
        BitrixSolutionPayload.PvzData payload = new BitrixSolutionPayload.PvzData();
        payload.setPvzCode("Sdek");
        payload.setPvzAddress("Россия, Москва, Митино, пер. Ангелов, 8");
        payload.setPvzPhone("+79264777115, +74997555271");
        payload.setPvzWorkTime("Вс 10:00-16:00, Сб 10:00-16:00, Пн-Пт 10:00-20:00");

        return payload;
    }

    private ShopData getShopData(OnlineOrderTypeData orderData) {
        String shopId;
        if (orderData.getShopId() != null) {
            shopId = orderData.getShopId();
        } else {
            shopId = userSessionData().getUserShopId();
        }

        return shopsClient.getShopById(shopId);
    }

    private String convertShopId(Integer shopId) {
        return String.format("%03d", shopId);
    }

    private String convertCoordinates(ShopData shop) {
        double lat = shop.getLat() + 0.5;
        double longitude = shop.getLongitude() - 0.5;

        return (lat + "," + longitude);
    }

    private String getCustomerId(SimpleCustomerData customerData) {
        try {
            CustomerSearchFilters filter = new CustomerSearchFilters();
            filter.setCustomerType(CustomerType.NATURAL);
            filter.setDiscriminantType(DiscriminantType.PHONENUMBER);
            filter.setDiscriminantValue(customerData.getPhoneNumber());
            Response<CustomerListData> response = customerClient.searchForCustomers(filter);

            return response.asJson().getItems().get(0).getCustomerNumber();
        } catch (Exception ignored) {
        }
        return null;
    }
}
