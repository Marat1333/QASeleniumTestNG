package com.leroy.magportal.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Тестовый класс надо будет переименовать и дать соответствующее название
public class SomeTest extends BasePaymentTest {

    @Test(description = "C1 Название теста")
    public void test() throws Exception {
        String id = createOnlineOrder();
        makePayment(id);
    }

    @Inject
    private TunnelClient tunnelClient;

    /**
     * Создает ONLINE ордер
     *
     * @return task id
     */
    private String createOnlineOrder() throws Exception {
        BitrixSolutionPayload payload = new BitrixSolutionPayload();
        payload.setLID("mn");
        payload.setIDREGION(3428);
        payload.setIDSHOP("010");
        payload.setPERSONTYPEID("1");
        payload.setPAYED("N");
        payload.setCANCELED("N");
        payload.setSTATUSID("N");
        payload.setDATESTATUS("2018-11-08 08:30:38");
        payload.setPRICEDELIVERY("0.00");
        payload.setALLOWDELIVERY("N");
        payload.setPRICE("156.00");
        payload.setCURRENCY("RUB");
        payload.setDISCOUNTVALUE("0.00");
        payload.setUSERID("1865235");
        payload.setPAYSYSTEMID("3");
        payload.setPAYMENTHANDLER("Sberbank");
        //payload.setDELIVERYID("");
        payload.setDATEUPDATE("2018-11-08 08:30:38");
        //payload.setUSERDESCRIPTION("");
        payload.setTAXVALUE("0.00");
        payload.setSUMPAID("0.00");
        payload.setRECOUNTFLAG("Y");
        payload.setDEDUCTED("N");
        payload.setMARKED("N");
        payload.setRESERVED("Y");
        payload.setACCOUNTNUMBER("1256834");
        payload.setEXTERNALORDER("N");
        payload.setDATESTATUSFORMAT("08.11.2018 08:30:38");
        payload.setDATEINSERTFORMAT("08.11.2018");
        payload.setDATEUPDATEFORMAT("08.11.2018 08:30:38");

        BitrixSolutionPayload.Total total = new BitrixSolutionPayload.Total();
        total.setWEIGHT(0.26);
        total.setCNTPRODUCTS(1);
        total.setCOST(156);
        total.setCOSTDELIVERY(0);
        total.setCOSTLIFT(0);
        total.setVOLUME(0);
        total.setLENGTHY(0);
        total.setVOLUMEPRODUCT(0);
        total.setEXTRABIG(0);
        payload.setTOTAL(total);

        BitrixSolutionPayload.UserData userData = new BitrixSolutionPayload.UserData();
        userData.setNAME("Sergey");
        userData.setSURNAME("Moskal");
        userData.setEMAIL("n.test@mail.ru");
        userData.setPHONE("+7 (985) 625-34-44");
        //userData.setRECEIVENAME("");
        //userData.setSURNAME("");
        //userData.setPHONE("");
        userData.setEXPRESSREGISTRATION(true);
        userData.setMEDIA5MOVECUSTOMERNUMBER("6107624");
        payload.setUSERDATA(userData);

        BitrixSolutionPayload.DeliveryData deliveryData = new BitrixSolutionPayload.DeliveryData();
        deliveryData.setDATE("28.11.2018");
        deliveryData.setTIME("В течение дня");
        deliveryData.setTYPE("Самовывоз");

        String pickupShopJsonString = "\"PICKUP_SHOP\":{\n" +
                "         \"ID\":\"3895480\",\n" +
                "         \"IBLOCK_ID\":\"4\",\n" +
                "         \"XML_ID\":\"035\",\n" +
                "         \"NAME\":\"Леруа Мерлен Зеленоград\",\n" +
                "         \"PROPERTY_ADDRESS_VALUE\":\"г. Москва, п. Московский, Киевское шоссе, 24-й км\",\n" +
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
        deliveryData.setSAMEDAY(0);
        BitrixSolutionPayload.Address address = new BitrixSolutionPayload.Address();
        deliveryData.setADDRESS(address);
        deliveryData.setADDRESSNOTFOUND(false);
        deliveryData.setCOORDINATES("55.621158,37.389973");
        deliveryData.setRISE(0);
        deliveryData.setLIFT(0);
        deliveryData.setEXTRABIG(0);
        //deliveryData.setCOMMENT("");
        deliveryData.setDELIVERYPRICE("0.00 РУБ.");
        deliveryData.setLIFTPRICE("0.00 РУБ.");
        deliveryData.setEXTRABIG2(0);
        deliveryData.setDELIVERYSERVICES("PICKUP");
        deliveryData.setLONGTAIL(0);
        deliveryData.setPVZDATA(false);
        payload.setDELIVERYDATA(deliveryData);

        payload.setIDDEVICE(0);
        payload.setDELIVERYTAX(18);
        payload.setCUSTOMERCOORDINATES("0,0");
        payload.setDATEINSERT("2018-11-08 08:30:39");
        payload.setIDORDER("1256834");

        BitrixSolutionPayload.Basket basket1 = new BitrixSolutionPayload.Basket();
        basket1.setID("128510514");
        basket1.setSKU("18171182");
        basket1.setPRICE("54.00");
        basket1.setQUANTITY("3.00");
        basket1.setLINK("https:\\/\\/leroymerlin.ru\\/catalogue\\/hranenie\\/mebelnye-fasady-i-dveri\\/komplektuyushchie-dlya-dverey-kupe\\/82013012\\/");
        basket1.setIMG("https:\\/\\/s.leroymerlin.ru\\/upload\\/catalog\\/img\\/f\\/9\\/82013012\\/100x100\\/82013012.jpg?v=1144");
        basket1.setNAME("Хомуток нерж. W2 16-25 мм (2 шт)");
        basket1.setWEIGHT(0.26);
        basket1.setVOLUME(0);
        basket1.setDEPARTMENT("14");
        basket1.setSUBDIVISION("1410");
        basket1.setTYPE("20");
        basket1.setSUBTYPE("60");
        basket1.setUOM("шт.");
        basket1.setROOTSECTION("32");
        basket1.setWIDTH(0.11);
        basket1.setHEIGHT(0.15);
        basket1.setLENGTH(0);
        basket1.setTAX(18);
        basket1.setRMSSTOCK(57);
        basket1.setSTOCK(57);
        basket1.setPYXISSTOCK(57);
        basket1.setLONGTAIL(0);
        basket1.setTAX(18);
        payload.setBASKET(Arrays.asList(basket1));

        // Пример параллельного создания заказов. Возможно, слишком громоздко, но работает.
        List<ThreadApiClient<BitrixSolutionResponse, TunnelClient>> threads = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ThreadApiClient<BitrixSolutionResponse, TunnelClient> myThread = new ThreadApiClient<>(
                    tunnelClient);
            myThread.sendRequest(client -> client.createSolutionFromBitrix(payload));
            threads.add(myThread);
        }

        threads.forEach(t -> {
            try {
                t.getData();
            } catch (InterruptedException e) {
                Log.error(e.getMessage());
            }
        });

        Response<BitrixSolutionResponse> r = tunnelClient.createSolutionFromBitrix(payload);
        return r.asJson().getSolutionId();
    }


}
