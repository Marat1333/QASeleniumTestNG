package com.leroy.magportal.api.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.api.clients.ShopsClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.constants.PaymentTypeEnum;
import com.leroy.magportal.api.data.shops.ShopData;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.umbrella_extension.data.BitrixSolutionPayload;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

public class BitrixHelper extends BaseHelper {

    @Inject
    private TunnelClient tunnelClient;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private ShopsClient shopsClient;

    private BitrixSolutionPayload createBitrixPayload(OnlineOrderTypeData orderData,
                                                      Integer productCount) throws Exception {

        ShopData shop = getShopData(orderData);
        String date = LocalDateTime.now().toLocalDate().toString();
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SimpleCustomerData customerData = SIMPLE_CUSTOMER_DATA_1;

        BitrixSolutionPayload payload = new BitrixSolutionPayload();
        payload.setLId("mn");
        payload.setIdRegion(Integer.parseInt(shop.getRegionId()));
        payload.setIdShop(convertShopId(Integer.parseInt(shop.getId())));
        payload.setPersonTypeId("1");
        payload.setPayed("N");
        payload.setCanceled("N");
        payload.setStatusId("N");
        payload.setDateStatus(dateTime);
        payload.setPriceDelivery(orderData.priceDelivery);
        payload.setAllowDelivery("Y");
        payload.setPrice("156.00");
        payload.setCurrency("RUB");
        payload.setDiscountValue("0.00");
        payload.setUserId("1865235");
        payload.setPaySystemId("3");
        payload.setPaymentHandler(orderData.getPaymentType());
        payload.setDateUpdate(dateTime);
        payload.setTaxValue("0.00");
        payload.setSumPaid("0.00");
        payload.setRecountFlag("Y");
        payload.setDeducted("N");
        payload.setMarked("N");
        payload.setReserved("Y");
        payload.setAccountNumber("1256834");
        payload.setExternalOrder("N");
        payload.setDateStatusFormat(dateTime);
        payload.setDateInsertFormat(date);
        payload.setDateUpdateFormat(dateTime);

        BitrixSolutionPayload.Total total = new BitrixSolutionPayload.Total();
        total.setWeigth(0.26);
        total.setCntProducts(1);
        total.setCost(156);
        total.setCostDelivery(0);
        total.setCostLift(0);
        total.setVolume(0);
        total.setLengthy(0);
        total.setVolumeProduct(0);
        total.setExtrabig(0);
        payload.setTotal(total);

        BitrixSolutionPayload.UserData userData = new BitrixSolutionPayload.UserData();
        userData.setName(customerData.getName().split(" ")[0]);
        userData.setSurname(customerData.getName().split(" ")[1]);
        userData.setEmail(customerData.getEmail());
        userData.setPhone(convertPhone(customerData.getPhoneNumber()));
        userData.setExpressRegistration(false);
        userData.setMediasMoveCustomerNumber(customerData.getId());
        payload.setUserData(userData);

        BitrixSolutionPayload.DeliveryData deliveryData = new BitrixSolutionPayload.DeliveryData();
        deliveryData.setDate(date);
        deliveryData.setTime("В течение дня");//TODO: recheck
        deliveryData.setType(orderData.getDeliveryType());
        deliveryData.setSameDay(orderData.sameDay);
        //TODO: Add Address

        BitrixSolutionPayload.Address address = new BitrixSolutionPayload.Address();
        deliveryData.setAddress(address);
        deliveryData.setAddressNotFound(false);
        deliveryData.setCoordinates(convertCoordinates(shop));
        deliveryData.setRise(orderData.rise);
        deliveryData.setLift(orderData.lift);
        deliveryData.setExtraBig(0);
        //deliveryData.setCOMMENT("");//TODO: ADD & PARAMS
        deliveryData.setDeliveryPrice(orderData.deliveryPrice);
        deliveryData.setLiftPrice(orderData.liftPrice);
        deliveryData.setExtraBig2(0);
        deliveryData.setDeliveryServices(orderData.deliveryServiceType);
        deliveryData.setLongTail(0);//TODO: ADD & PARAMS
        deliveryData.setPvzData(orderData.pvzData);
        String pickupShopJsonString = "\"PICKUP_SHOP\":{\n" +
                "         \"ID\":\"3895480\",\n" +
                "         \"IBLOCK_ID\":\"4\",\n" +
                "         \"XML_ID\":\"035\",\n" +
                "         \"NAME\":\"Леруа Мерлен Зеленоград\",\n" +
                "         \"PROPERTY_ADDRESS_VALUE\":\"г. Москва, п. Московский, Киевское шоссе, 24-й км\",\n"
                +
                "         \"PROPERTY_ADDRESS_VALUE_ID\":\"3177131060\",\n" +
                "         \"PROPERTY_NAME_VALUE\":\"Леруа Мерлен Киевское Шоссе\",\n" +
                "         \"PROPERTY_NAME_VALUE_ID\":\"3177131063\",\n" +
                "         \"PROPERTY_WORK_TIME_VALUE\":\"08:00 - 23:00\",\n" +
                "         \"PROPERTY_WORK_TIME_VALUE_ID\":\"3177131062\",\n" +
                "         \"PROPERTY_PHONE_VALUE\":\"8 (800) 700-00-99\",\n" +
                "         \"PROPERTY_PHONE_VALUE_ID\":\"3177131061\",\n" +
                "         \"PROPERTY_PICKUP_OPERATORS_VALUE\":\"rrlog01.mag051@leroymerlin.ru\",\n" +
                "         \"PROPERTY_PICKUP_OPERATORS_VALUE_ID\":\"3187492118\",\n" +
                "         \"PROPERTY_GPS_COORDS_VALUE\":\"55.621158, 37.389973\",\n" +
                "         \"PROPERTY_GPS_COORDS_VALUE_ID\":\"3177137913\",\n" +
                "         \"DISPLAY_NAME\":\"Леруа Мерлен Зеленоград\"\n" +
                "      },";
        JsonNode pickupShop = new ObjectMapper().readTree(pickupShopJsonString);
        deliveryData.setPickupShop(pickupShop);
        payload.setDeliveryData(deliveryData);

        payload.setIdDevice(0);
        payload.setDeliveryTax(18);
        payload.setCustomerCoordinates("0,0");
        payload.setDateInsert(dateTime);
        payload.setIdOrder("1256834");
        ArrayList<BitrixSolutionPayload.Basket> y1 = makeBasket(productCount, shop.getId());

        payload.setBasket(y1);

        return payload;
    }

    public ArrayList<BitrixSolutionResponse> createOnlineOrders(Integer ordersCount,
                                                                OnlineOrderTypeData orderData, Integer productCount) throws Exception {
        ArrayList<BitrixSolutionResponse> result = new ArrayList<>();
        BitrixSolutionPayload bitrixPayload = createBitrixPayload(orderData, productCount);

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
                        paymentHelper.makeHoldCost(response.getSolutionId());
                    }
                }
            } catch (InterruptedException e) {
                Log.error(e.getMessage());
            }
        });

//    List<String> newList = result.stream()
//        .filter(x -> x.getSolutionId() != null)
//        .map(BitrixSolutionResponse::getSolutionId)
//        .collect(Collectors.toList());

        return result;
    }

    private ArrayList<BitrixSolutionPayload.Basket> makeBasket(Integer productsCount, String shopId) {
        CatalogSearchClient catalogSearchClient = getCatalogSearchClient();
        ArrayList<BitrixSolutionPayload.Basket> result = new ArrayList<>();
        List<ProductItemData> products = catalogSearchClient
                .getRandomUniqueProductsWithTitlesForShop(productsCount, shopId);
        for (ProductItemData productData : products) {
            result.add(productItemDataToPayload(productData));
        }

        return result;
    }

    private BitrixSolutionPayload.Basket productItemDataToPayload(ProductItemData product) {
        BitrixSolutionPayload.Basket basket = new BitrixSolutionPayload.Basket();
        basket.setId("128510514");
        basket.setSku(product.getLmCode());
        basket.setName(product.getTitle());
        basket.setPrice(product.getPrice().toString());
        basket.setTax(18);
        basket.setQuantity("10");//TODO: it's make sense to parametrise
//TODO: ADD LT Products
        return basket;
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
}
