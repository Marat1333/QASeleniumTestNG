package com.leroy.magportal.api.helpers;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters.CustomerType;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters.DiscriminantType;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.constants.DeliveryServiceTypeEnum;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

public class BitrixHelper extends BaseHelper {

    @Inject
    private TunnelClient tunnelClient;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private ShopsClient shopsClient;
    @Inject
    private CatalogSearchClient catalogSearchClient;
    @Inject
    private OrderClient orderClient;

    private final LocalDateTime dateTime = LocalDateTime.now();

    @Step("Creates Online order with specified LmCode")
    public BitrixSolutionResponse createOnlineOrder(OnlineOrderTypeData orderData, String lmCode) {
        orderData.setLmCode(lmCode);
        return this.createOnlineOrders(1, orderData, 1).stream().findFirst().get();
    }

    @Step("Creates Online orders of different types")
    public ArrayList<BitrixSolutionResponse> createOnlineOrders(Integer ordersCount,
            OnlineOrderTypeData orderData, Integer productCount) {
        SimpleCustomerData customerData = SIMPLE_CUSTOMER_DATA_1;
        customerData.setId(getCustomerId(customerData));

        ArrayList<BitrixSolutionResponse> result = new ArrayList<>();
        BitrixSolutionPayload bitrixPayload = createBitrixPayload(orderData, productCount,
                customerData);

        // Пример параллельного создания заказов. Возможно, слишком громоздко, но работает.
        List<ThreadApiClient<BitrixSolutionResponse, TunnelClient>> threads = new ArrayList<>();

        for (int i = 0; i < ordersCount; i++) {
            ThreadApiClient<BitrixSolutionResponse, TunnelClient> myThread = new ThreadApiClient<>(
                    tunnelClient);
            myThread.sendRequest(client -> client.createSolutionFromBitrix(bitrixPayload));
            threads.add(myThread);
        }

        threads.forEach(t -> {
            try {
                BitrixSolutionResponse response = t.getData();
                if (response.getSolutionId() != null) {
                    result.add(response);
                    if (orderData.getPaymentType().equals(PaymentTypeEnum.SBERBANK.getName())) {
                        orderClient.waitUntilOrderGetStatus(response.getSolutionId(),
                                States.WAITING_FOR_PAYMENT,
                                PaymentStatusEnum.CONFIRMED);
                        paymentHelper
//                                .makePaymentCard(response.getSolutionId());
                                .makeHoldCost(response.getSolutionId());
                    }
                    orderClient.waitUntilOrderGetStatus(response.getSolutionId(),
                            States.ALLOWED_FOR_PICKING, null);
                }
            } catch (InterruptedException e) {
                Log.error(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return result;
    }

    private BitrixSolutionPayload createBitrixPayload(OnlineOrderTypeData orderData,
            Integer productCount, SimpleCustomerData customerData) {
        if (orderData.getShopId() != null) {
            orderData.setShopId(userSessionData().getUserShopId());
        }

        ShopData shop = getShopData(orderData);
        BitrixSolutionPayload payload = makeGeneralPayload(orderData, shop);
        payload.setTotal(makeTotalPayload());
        payload.setUserData(makeUserDataPayload(customerData));
        payload.setDeliveryData(makeDeliveryDataPayload(orderData, shop));
        payload.setBasket(makeBasket(productCount, shop.getId(), orderData));

        return payload;
    }

    private ArrayList<BitrixSolutionPayload.Basket> makeBasket(Integer productsCount, String shopId,
            OnlineOrderTypeData orderData) {
        ArrayList<BitrixSolutionPayload.Basket> result = new ArrayList<>();

        if (orderData.getLmCode() != null) {
            ProductItemData product = catalogSearchClient.getProductByLmCode(orderData.getLmCode());
            result.add(productItemDataToPayload(product));
        } else {
            List<ProductItemData> products = catalogSearchClient
                    .getProductsForShop(productsCount, shopId);
            for (ProductItemData productData : products) {
                result.add(productItemDataToPayload(productData));
            }
        }
        return result;
    }

    private BitrixSolutionPayload.Basket productItemDataToPayload(ProductItemData product) {
        BitrixSolutionPayload.Basket basket = new BitrixSolutionPayload.Basket();
        String price = "99.99";
        if (product.getPrice() != null) {
            price = product.getPrice().toString();
        }

        basket.setId("128510514");
        basket.setSku(product.getLmCode());
        basket.setName(product.getTitle());
        basket.setPrice(price);
        basket.setTax(18);
        basket.setQuantity("10");//TODO: it's make sense to parametrise
        //TODO: ADD LT Products
        return basket;
    }

    private BitrixSolutionPayload makeGeneralPayload(OnlineOrderTypeData orderData, ShopData shop) {
        BitrixSolutionPayload payload = new BitrixSolutionPayload();
        payload.setLId("mn");
        payload.setIdRegion(Integer.parseInt(shop.getRegionId()));
        payload.setIdShop(convertShopId(Integer.parseInt(shop.getId())));
        payload.setPersonTypeId("1");
        payload.setPayed("N");
        payload.setCanceled("N");
        payload.setStatusId("N");
        payload.setDateStatus(dateTime.toString());
        payload.setPriceDelivery(orderData.priceDelivery);
        payload.setAllowDelivery("Y");
        payload.setPrice("10156.00");
        payload.setCurrency("RUB");
        payload.setDiscountValue("10.00");
        payload.setUserId("1865235");
        payload.setPaySystemId("3");
        payload.setPaymentHandler(orderData.getPaymentType());
        payload.setDeliveryId("");////
        payload.setDateUpdate(dateTime.toString());
        payload.setTaxValue("0.00");
        payload.setSumPaid("0.00");
        payload.setRecountFlag("Y");
        payload.setDeducted("N");
        payload.setMarked("N");
        payload.setReserved("Y");
        payload.setAccountNumber("1256834");
        payload.setExternalOrder("N");
        payload
                .setDateStatusFormat(
                        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        payload.setDateInsertFormat(
                dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        payload
                .setDateUpdateFormat(
                        dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        payload.setIdDevice(0);
        payload.setDeliveryTax(18);
        payload.setCustomerCoordinates("0,0");
        payload.setDateInsert(ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
        payload.setIdOrder("1256834");

        return payload;
    }

    private BitrixSolutionPayload.Total makeTotalPayload() {
        BitrixSolutionPayload.Total payload = new BitrixSolutionPayload.Total();
        payload.setWeigth(0.26);
        payload.setCntProducts(1);
        payload.setCost(156);
        payload.setCostDelivery(0);
        payload.setCostLift(0);
        payload.setVolume(0);
        payload.setLengthy(0);
        payload.setVolumeProduct(0);
        payload.setExtrabig(0);

        return payload;
    }

    private BitrixSolutionPayload.UserData makeUserDataPayload(SimpleCustomerData customerData) {
        BitrixSolutionPayload.UserData payload = new BitrixSolutionPayload.UserData();
        payload.setName(customerData.getName().split(" ")[0]);
        payload.setSurname(customerData.getName().split(" ")[1]);
        payload.setEmail(customerData.getEmail());
        payload.setPhone(convertPhone(customerData.getPhoneNumber()));
        payload.setExpressRegistration(false);
        payload.setMediasMoveCustomerNumber(customerData.getId());

        return payload;
    }

    private BitrixSolutionPayload.DeliveryData makeDeliveryDataPayload(
            OnlineOrderTypeData orderData,
            ShopData shop) {
        String comment =
                "BITRIX: " + orderData.getDeliveryType() + ", " + orderData.getPaymentType();

        BitrixSolutionPayload.DeliveryData payload = new BitrixSolutionPayload.DeliveryData();
        payload.setDate(
                dateTime.plusDays(1).toLocalDate()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        payload.setTime("08:00 - 13:00");
        payload.setType(orderData.getDeliveryType());
        payload.setSameDay(orderData.sameDay);
        payload.setAddressNotFound(false);
        payload.setCoordinates(convertCoordinates(shop));
        payload.setRise(orderData.rise);
        payload.setLift(orderData.lift);
        payload.setExtraBig(0);
        payload.setComment(comment);
        payload.setDeliveryPrice(orderData.deliveryPrice);
        payload.setLiftPrice(orderData.liftPrice);
        payload.setExtraBig2(0);
        payload.setDeliveryServices(orderData.deliveryServiceType);
        payload.setLongTail(0);
        payload.setCarryPrice("");
        payload.setCarryLength("");
//        payload.setPvzData(orderData.pvzData);

        payload.setAddress(makeAddressPayload());
        if (orderData.getDeliveryType().equals(DeliveryServiceTypeEnum.PICKUP.getType())) {
            payload.setPickupShop(makePickupShopPayload(shop));
        }
        if (orderData.getDeliveryType().equals(DeliveryServiceTypeEnum.DELIVERY_PVZ.getType())) {
            payload.setPvzData(makePvzPayload());
        }

        return payload;
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

    private String convertPhone(String phone) {
        return phone.substring(0, 2) + " (" + phone.substring(2, 5) + ") " + phone.substring(5, 8) +
                "-" + phone.substring(8, 10) + "-" + phone.substring(10, 12);
    }

    private String convertCoordinates(ShopData shop) {
        double lat = shop.getLat() + 0.5;
        double longitude = shop.getLongitude() - 0.5;

        return (lat + "," + longitude);
    }

    private String getCustomerId(SimpleCustomerData customerData) {
        CustomerClient customerClient = getCustomerClient();
        CustomerSearchFilters filter = new CustomerSearchFilters();
        filter.setCustomerType(CustomerType.NATURAL);
        filter.setDiscriminantType(DiscriminantType.PHONENUMBER);
        filter.setDiscriminantValue(customerData.getPhoneNumber());
        Response<CustomerListData> response = customerClient.searchForCustomers(filter);
        assertThat("GET Customer failed", response, successful());

        try {
            return response.asJson().getItems().get(0).getCustomerNumber();
        } catch (Exception ignored) {
        }
        return null;
    }
}
