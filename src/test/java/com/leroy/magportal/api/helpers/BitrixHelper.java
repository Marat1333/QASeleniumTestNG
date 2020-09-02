package com.leroy.magportal.api.helpers;

import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  private BitrixSolutionPayload createBitrixPayload(OnlineOrderTypeData orderData,
      Integer productCount) throws Exception {

    ShopData shop = getShopData(orderData);
    String date = LocalDateTime.now().toLocalDate().toString();
    String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    SimpleCustomerData customerData = SIMPLE_CUSTOMER_DATA_1;

    BitrixSolutionPayload payload = new BitrixSolutionPayload();
    payload.setLID("mn");
    payload.setIDREGION(Integer.parseInt(shop.getRegionId()));
    payload.setIDSHOP(convertShopId(Integer.parseInt(shop.getId())));
    payload.setPERSONTYPEID("1");
    payload.setPAYED("N");
    payload.setCANCELED("N");
    payload.setSTATUSID("N");
    payload.setDATESTATUS(dateTime);
    payload.setPRICEDELIVERY(orderData.priceDelivery);
    payload.setALLOWDELIVERY("Y");
    payload.setPRICE("156.00");
    payload.setCURRENCY("RUB");
    payload.setDISCOUNTVALUE("0.00");
    payload.setUSERID("1865235");
    payload.setPAYSYSTEMID("3");
    payload.setPAYMENTHANDLER(orderData.getPaymentType());
    payload.setDATEUPDATE(dateTime);
    payload.setTAXVALUE("0.00");
    payload.setSUMPAID("0.00");
    payload.setRECOUNTFLAG("Y");
    payload.setDEDUCTED("N");
    payload.setMARKED("N");
    payload.setRESERVED("Y");
    payload.setACCOUNTNUMBER("1256834");
    payload.setEXTERNALORDER("N");
    payload.setDATESTATUSFORMAT(dateTime);
    payload.setDATEINSERTFORMAT(date);
    payload.setDATEUPDATEFORMAT(dateTime);
//TODO: check is it necessary
//    BitrixSolutionPayload.Total total = new BitrixSolutionPayload.Total();
//    total.setWEIGHT(0.26);
//    total.setCNTPRODUCTS(1);
//    total.setCOST(156);
//    total.setCOSTDELIVERY(0);
//    total.setCOSTLIFT(0);
//    total.setVOLUME(0);
//    total.setLENGTHY(0);
//    total.setVOLUMEPRODUCT(0);
//    total.setEXTRABIG(0);
//    payload.setTOTAL(total);

    BitrixSolutionPayload.UserData userData = new BitrixSolutionPayload.UserData();
    userData.setNAME(customerData.getName().split(" ")[0]);
    userData.setSURNAME(customerData.getName().split(" ")[1]);
    userData.setEMAIL(customerData.getEmail());
    userData.setPHONE(convertPhone(customerData.getPhoneNumber()));
    userData.setEXPRESSREGISTRATION(false);
    userData.setMEDIA5MOVECUSTOMERNUMBER(customerData.getId());
    payload.setUSERDATA(userData);

    BitrixSolutionPayload.DeliveryData deliveryData = new BitrixSolutionPayload.DeliveryData();
    deliveryData.setDATE(date);
    deliveryData.setTIME("В течение дня");//TODO: recheck
    deliveryData.setTYPE(orderData.getDeliveryType());
    deliveryData.setSAMEDAY(orderData.sameDay);
    //TODO: Add Address

    BitrixSolutionPayload.Address address = new BitrixSolutionPayload.Address();
    deliveryData.setADDRESS(address);
    deliveryData.setADDRESSNOTFOUND(false);
    deliveryData.setCOORDINATES(convertCoordinates(shop));
    deliveryData.setRISE(orderData.rise);
    deliveryData.setLIFT(orderData.lift);
    deliveryData.setEXTRABIG(0);
    //deliveryData.setCOMMENT("");//TODO: ADD & PARAMS
    deliveryData.setDELIVERYPRICE(orderData.deliveryPrice);
    deliveryData.setLIFTPRICE(orderData.liftPrice);
    deliveryData.setEXTRABIG2(0);
    deliveryData.setDELIVERYSERVICES(orderData.deliveryServiceType);
    deliveryData.setLONGTAIL(0);//TODO: ADD & PARAMS
    deliveryData.setPVZDATA(orderData.pvzData);
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
    deliveryData.setPICKUPSHOP(pickupShop);
    payload.setDELIVERYDATA(deliveryData);
    payload.setDELIVERYDATA(deliveryData);

    payload.setIDDEVICE(0);
    payload.setDELIVERYTAX(18);
    payload.setCUSTOMERCOORDINATES("0,0");
    payload.setDATEINSERT(dateTime);
    payload.setIDORDER("1256834");
    ArrayList<BitrixSolutionPayload.Basket> y1 = makeBasket(productCount, shop.getId());

    payload.setBASKET(y1);

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
    basket.setID("128510514");
    basket.setSKU(product.getLmCode());
    basket.setNAME(product.getTitle());
    basket.setPRICE(product.getPrice().toString());
    basket.setTAX(18);
    basket.setQUANTITY("10");//TODO: it's make sense to parametrise
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
