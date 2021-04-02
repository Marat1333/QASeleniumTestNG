package com.leroy.magportal.api.helpers;

import static com.leroy.magportal.api.constants.PaymentMethodEnum.API;
import static com.leroy.magportal.api.constants.PaymentMethodEnum.CARD;
import static com.leroy.magportal.api.constants.PaymentMethodEnum.TPNET;
import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_2;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.DeliveryServiceTypeEnum;
import com.leroy.magportal.api.constants.LmCodeTypeEnum;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.constants.PaymentMethodEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.onlineOrders.AemPaymentResponseData;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.CommunicationPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.NextStepResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.PutPaymentPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.PutPaymentResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartPayload.Product;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.TunnelPayer;
import ru.leroymerlin.qa.core.clients.tunnel.data.TunnelRecepient;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.Address;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.DeliveryResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.Period;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.Point;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PointAddress;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PostCalculateResponse;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutHomeDeliveryPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutHomeDeliveryPayload.Appointment;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutPickupPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.PutPickupPointPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.deliverystep.Store;
import ru.leroymerlin.qa.core.clients.tunnel.data.tunnel.stephomedelivery.PostCalculatePayload;
import ru.leroymerlin.qa.core.clients.tunnel.enums.PaymentInstruments;
import ru.leroymerlin.qa.core.clients.tunnel.enums.PaymentTypes;

public class AemHelper extends BaseHelper {

    @Inject
    private TunnelClient tunnelClient;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private ShopsHelper shopsHelper;
    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private OrderClient orderClient;

    private static final int floor = 5;

    @Step("Creates Online order with Dimensional LmCode")
    public AemPaymentResponseData createDimensionalOnlineOrder(OnlineOrderTypeData orderData) {
        orderData.setLmCode(LmCodeTypeEnum.DIMENSIONAL.getValue());
        return this.createOnlineOrders(1, orderData, 1, CARD).stream().findFirst().orElse(null);
    }

    @Step("Creates Online order with 3 LmCodes")
    public AemPaymentResponseData createOnlineOrder(OnlineOrderTypeData orderData) {
        return this.createOnlineOrders(1, orderData, 3, API).stream().findFirst().orElse(null);
    }

    @Step("Creates Online order with 3 LmCodes with TpNet payment method")
    public AemPaymentResponseData createOnlineOrderTpNetPayment(OnlineOrderTypeData orderData) {
        return this.createOnlineOrders(1, orderData, 3, TPNET).stream().findFirst().orElse(null);
    }

    @Step("Creates Online order with 3 LmCodes with Card payment method")
    public AemPaymentResponseData createOnlineOrderCardPayment(OnlineOrderTypeData orderData) {
        return this.createOnlineOrders(1, orderData, 3, CARD).stream().findFirst().orElse(null);
    }

    @Step("Creates Online orders of different types")
    public ArrayList<AemPaymentResponseData> createOnlineOrders(Integer ordersCount,
            OnlineOrderTypeData orderData, Integer productCount, PaymentMethodEnum paymentMethod) {
        ShopData shopData = getShopData(orderData);
        ArrayList<AemPaymentResponseData> result = new ArrayList<>();
//        String token = userSessionData().getAccessToken();

        List<ThreadApiClient<PutPaymentResponse, TunnelClient>> threads = new ArrayList<>();

        for (int i = 0; i < ordersCount; i++) {
            ThreadApiClient<PutPaymentResponse, TunnelClient> myThread = new ThreadApiClient<>(
                    tunnelClient);

            try {
                Response<StepStartResponse> startResp = tunnelClient.createCheckoutProcess(
                        makeStartPayload(orderData, productCount, shopData.getId()), "AEM");
                Assert.assertTrue("Start Order creation process has FAILED.",
                        startResp.isSuccessful());
                String transactionId = startResp.asJson().getTransactionId();
                Response<NextStepResponse> response;

                if (orderData.getDeliveryType().getAemCode() == null) {
                    if (orderData.getDeliveryType().getType()
                            .equals(DeliveryServiceTypeEnum.PICKUP.getType())) {
                        Response<DeliveryResponse> getPickup = tunnelClient.getPickup(transactionId);
                        response = tunnelClient.updatePickup(transactionId,
                                makePutPickupPayload(getShopIdFromGetPickup(getPickup)));
                    } else {
                        Response<DeliveryResponse> deliveryGetResp = tunnelClient
                                .getPickupPoint(transactionId);
                        response = tunnelClient.updatePickupPoint(transactionId,
                                makePutPickupPointPayload(deliveryGetResp));
                    }
                    Assert.assertTrue("Delivery data was NOT set.", response.isSuccessful());
                } else {
                    tunnelClient.getHomeDelivery(transactionId);
                    Response<PostCalculateResponse> calculateResp = tunnelClient
                            .postCalculate(transactionId,
                                    makeCalculatePayload(orderData, shopData));
                    Response<?> deliveryResponse = tunnelClient
                            .updateHomeDelivery(transactionId,
                                    makePutDeliveryPayload(calculateResp, orderData,
                                            shopData));
                    Assert.assertTrue("Delivery data was NOT set.", deliveryResponse.isSuccessful());
                }

                Response<NextStepResponse> commResp = tunnelClient
                        .updateCommunication(makePutCommPayload(orderData.getDeliveryType()),
                                transactionId);
                Assert.assertTrue("Communication data was NOT set.", commResp.isSuccessful());

                myThread.sendRequest(
                        client -> client
                                .putPaymentType(transactionId, makePaymentPayload(orderData)));
                myThread.setName(startResp.asJson().getSolutionId());
                threads.add(myThread);

            } catch (Exception e) {
                Log.error(e.getMessage());
            }
        }
        List<String> errors = new ArrayList<>();

        threads.forEach(t -> {
            try {
                PutPaymentResponse response = t.getData();
                if (response.getLink() != null) {
                    AemPaymentResponseData responseData = new AemPaymentResponseData();//TODO: Until Aem issue is not fixed
                    responseData.setSolutionId(t.getName());
                    responseData.setLink(response.getLink());
                    responseData.setTransactionId(response.getTransactionId());
                    result.add(responseData);
                    if (orderData.getPaymentType().equals(PaymentTypeEnum.SBERBANK.getName())) {
                        orderClient.waitUntilOrderGetStatus(responseData.getSolutionId(),
                                States.WAITING_FOR_PAYMENT,
                                PaymentStatusEnum.CONFIRMED);
                        paymentHelper.makePayment(responseData.getSolutionId(), paymentMethod);
                    }
                    orderClient.waitUntilOrderGetStatus(responseData.getSolutionId(),
                            States.ALLOWED_FOR_PICKING, null);
                }
            } catch (Exception e) {
                Log.error(e.getMessage());
                try {
                    errors.add(t.getData().toString());
                } catch (Exception ignore) {
                }
            }
        });

        Assert.assertTrue("No orders were created due to: " + Arrays.toString(errors.toArray()),
                result.size() > 0);
        return result;
    }

    private StepStartPayload makeStartPayload(OnlineOrderTypeData orderData,
            Integer productCount, Integer shopId) {

        StepStartPayload payload = new StepStartPayload();
        payload.setReferral("");
        payload.setRegionId(shopsHelper.getRegionIdByShopId(shopId));
        payload.setContextStoreId(shopsHelper.getRefStoreIdByShopId(shopId));
        payload.setProducts(makeProducts(productCount, shopId, orderData));

        return payload;
    }

    private PostCalculatePayload makeCalculatePayload(OnlineOrderTypeData orderData,
            ShopData shop) {

        PostCalculatePayload payload = new PostCalculatePayload();
        PostCalculatePayload.Address address = new PostCalculatePayload.Address();
        address.setLatitude(String.valueOf(shop.getLat() + 0.1));
        address.setLongitude(String.valueOf(shop.getLongitude() - 0.1));
        address.setFloor(floor);
        payload.setAddress(address);
        payload.setTypeOfLift(orderData.getLiftType());
        payload.setDeliveryTo(orderData.getDeliveryType().getAemCode());
        payload.setDistance(1.0f);

        return payload;
    }

    private PutPickupPayload makePutPickupPayload(String shopId) {

        PutPickupPayload payload = new PutPickupPayload();
        payload.setStoreId(Integer.parseInt(shopId));

        return payload;
    }

    private PutPickupPointPayload makePutPickupPointPayload(Response<DeliveryResponse> response) {
        Assert.assertTrue("GET PickupPoint request has Failed", response.isSuccessful());
        DeliveryResponse deliveryResponse = response.asJson();

        Point point = deliveryResponse.getPoints().stream().filter(x -> Strings
                .isNotNullAndNotEmpty(x.getAvailableDate())).findAny().orElse(new Point());
        PointAddress address = point.getPointAddress();
        PutPickupPointPayload payload = new PutPickupPointPayload();
        payload.setPointId(address.getPointId());
        payload.setAdditionalProperty("partner", address.getPartner());

        return payload;
    }

    private PutHomeDeliveryPayload makePutDeliveryPayload(
            Response<PostCalculateResponse> calculateResponse, OnlineOrderTypeData orderData,
            ShopData shopData) {
        Assert.assertTrue("Delivery Calculation has FAILED", calculateResponse.isSuccessful());
        PutHomeDeliveryPayload payload = new PutHomeDeliveryPayload();
        Address address = new Address();
        address.setId(10051983);
        address.setName("Автотестная ул, 99");
        address.setStreet("Автотестная ул");
        address.setHouse("99");
        address.setEntrance("5");
        address.setFlat("5");
        address.setIntercom("k12345");
        address.setLatitude(String.valueOf(shopData.getLat() + 0.1));
        address.setLongitude(String.valueOf(shopData.getLongitude() - 0.1));
        address.setFloor(floor);
        payload.setAddress(address);

        payload.setTypeOfLift(orderData.getLiftType());
        payload.setDeliveryTo(orderData.getDeliveryType().getAemCode());

        Appointment appointment = makeAppointmentPayload(
                calculateResponse.asJson().getAppointments());
        payload.setAppointment(appointment);

        return payload;
    }

    private Appointment makeAppointmentPayload(
            List<PostCalculateResponse.Appointment> appointments) {

        Appointment payload = new Appointment();
        PostCalculateResponse.Appointment appointment = appointments.stream()
                .filter(x -> Strings.isNotNullAndNotEmpty(x.getDate())).findAny()
                .orElse(new PostCalculateResponse.Appointment());
        payload.setDate(appointment.getDate());
        Period period = appointment.getPeriods().stream()
                .filter(x -> Strings.isNotNullAndNotEmpty(x.getStart())).findAny()
                .orElse(new Period());
        payload.setPeriod(new Period(true, period.getStart(), period.getEnd()));

        return payload;
    }

    private CommunicationPayload makePutCommPayload(
            DeliveryServiceTypeEnum deliveryServiceTypeEnum) {
        SimpleCustomerData payerData = SIMPLE_CUSTOMER_DATA_1;
        payerData.fillFirstLastNames();
        SimpleCustomerData recipientData = SIMPLE_CUSTOMER_DATA_2;
        recipientData.fillFirstLastNames();
        CommunicationPayload payload = new CommunicationPayload();
        TunnelPayer payer = new TunnelPayer();
        payer.setName(payerData.getFirstName());
        payer.setSurname(payerData.getLastName());
        payer.setEmail(payerData.getEmail());
        payer.setPhone(payerData.getPhoneNumber());
        TunnelRecepient recipient = new TunnelRecepient();
        recipient.setName(recipientData.getFirstName());
        recipient.setSurname(recipientData.getLastName());
        recipient.setPhone(recipientData.getPhoneNumber());

        payload.setPayer(payer);
        payload.setRecepient(recipient);
        payload.setComment("АвтоТест Аем. Тип Ордера: " + deliveryServiceTypeEnum.getType());

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

    private ArrayList<Product> makeProducts(Integer productsCount, Integer shopId,
            OnlineOrderTypeData orderData) {
        ArrayList<Product> result = new ArrayList<>();

        if (orderData.getLmCode() != null) {
            ProductData product = searchProductHelper.getProductByLmCode(orderData.getLmCode());
            result.add(productItemDataToPayload(product));
        } else {
            List<ProductData> products = searchProductHelper
                    .getProductsForShop(productsCount, shopId.toString());
            for (ProductData productData : products) {
                result.add(productItemDataToPayload(productData));
            }
        }
        return result;
    }

    private Product productItemDataToPayload(ProductData product) {
        Product tunnelProduct = new Product();

        double quantity = 10.00;
        if (product.getLmCode().equals(LmCodeTypeEnum.DIMENSIONAL.getValue())) {
            quantity = 9.99;
        }

        tunnelProduct.setProductId(product.getLmCode());
        tunnelProduct.setQuantity(quantity);
        return tunnelProduct;
    }

    private ShopData getShopData(OnlineOrderTypeData orderData) {
        Integer shopId;
        if (orderData.getShopId() != null) {
            shopId = orderData.getShopId();
        } else {
            shopId = Integer.parseInt(userSessionData().getUserShopId());
        }

        return shopsHelper.getShopById(shopId);
    }

    private String getShopIdFromGetPickup(Response<DeliveryResponse> resp) {
        assertThatResponseIsOk(resp);
        List<Store> stores = resp.asJson().getStores().stream().filter(x -> x.getUnavailableProducts().size() == 0).collect(
                Collectors.toList());
        int expectedShopId = Integer.parseInt(userSessionData().getUserShopId());
        try {
            Store result = stores.stream().filter(x -> x.getStoreId() == expectedShopId).findFirst().orElse(stores.get(0));
            return result.getStoreId().toString();
        } catch (Exception ignore) {
            return userSessionData().getUserShopId();
        }
    }
}
