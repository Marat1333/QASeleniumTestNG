package com.leroy.umbrella_extension.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//import ru.leroymerlin.qa.core.commons.PayloadBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BitrixSolutionPayload {

    @JsonProperty("LID")
    private String lId;
    @JsonProperty("ID_REGION")
    private Integer idRegion;
    @JsonProperty("ID_SHOP")
    private String idShop;
    @JsonProperty("PERSON_TYPE_ID")
    private String personTypeId;
    @JsonProperty("PAYED")
    private String payed;
    @JsonProperty("CANCELED")
    private String canceled;
    @JsonProperty("STATUS_ID")
    private String statusId;
    @JsonProperty("DATE_STATUS")
    private String dateStatus;
    @JsonProperty("PRICE_DELIVERY")
    private String priceDelivery;
    @JsonProperty("ALLOW_DELIVERY")
    private String allowDelivery;
    @JsonProperty("PRICE")
    private String price;
    @JsonProperty("CURRENCY")
    private String currency;
    @JsonProperty("DISCOUNT_VALUE")
    private String discountValue;
    @JsonProperty("USER_ID")
    private String userId;
    @JsonProperty("PAY_SYSTEM_ID")
    private String paySystemId;
    @JsonProperty("PAYMENT_HANDLER")
    private String paymentHandler;
    @JsonProperty("PAYMENT_PROVIDER")
    private Object paymentProvider;
    @JsonProperty("DELIVERY_ID")
    private String deliveryId;
    @JsonProperty("DATE_UPDATE")
    private String dateUpdate;
    @JsonProperty("USER_DESCRIPTION")
    private String userDescription;
    @JsonProperty("TAX_VALUE")
    private String taxValue;
    @JsonProperty("SUM_PAID")
    private String sumPaid;
    @JsonProperty("RECOUNT_FLAG")
    private String recountFlag;
    @JsonProperty("DEDUCTED")
    private String deducted;
    @JsonProperty("MARKED")
    private String marked;
    @JsonProperty("RESERVED")
    private String reserved;
    @JsonProperty("ACCOUNT_NUMBER")
    private String accountNumber;
    @JsonProperty("EXTERNAL_ORDER")
    private String externalOrder;
    @JsonProperty("DATE_STATUS_FORMAT")
    private String dateStatusFormat;
    @JsonProperty("DATE_INSERT_FORMAT")
    private String dateInsertFormat;
    @JsonProperty("DATE_UPDATE_FORMAT")
    private String dateUpdateFormat;
    @JsonProperty("DATE_LOCK_FORMAT")
    private Object dateLockFormat;
    @JsonProperty("TOTAL")
    private BitrixSolutionPayload.Total total;
    @JsonProperty("USER_DATA")
    private BitrixSolutionPayload.UserData userData;
    @JsonProperty("DELIVERY_DATA")
    private BitrixSolutionPayload.DeliveryData deliveryData;
    @JsonProperty("ID_DEVICE")
    private Integer idDevice;
    @JsonProperty("ADMITAD_UID")
    private String admitadUid;
    @JsonProperty("DELIVERY_TAX")
    private Integer deliveryTax;
    @JsonProperty("SHORT_PAY_URL")
    private Object shortPayUrl;
    @JsonProperty("LONG_TAIL")
    private Object longTail;
    @JsonProperty("CUSTOMER_COORDINATES")
    private String customerCoordinates;
    @JsonProperty("SALES_CHANNEL")
    private Object salesChannel;
    @JsonProperty("BASKET")
    private List<Basket> basket;
    @JsonProperty("DATE_INSERT")
    private String dateInsert;
    @JsonProperty("ID_ORDER")
    private String idOrder;

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DeliveryData {
        @JsonProperty("DATE")
        private String date;
        @JsonProperty("TIME")
        private String time;
        @JsonProperty("TYPE")
        private String type;
        @JsonProperty("PICKUP_SHOP")
        private BitrixSolutionPayload.PickupShop pickupShop;
        @JsonProperty("SAME_DAY")
        private Integer sameDay;
        @JsonProperty("ADDRESS")
        private BitrixSolutionPayload.Address address;
        @JsonProperty("ADDRESS_NOT_FOUND")
        private Boolean addressNotFound;
        @JsonProperty("COORDINATES")
        private String coordinates;
        @JsonProperty("RISE")
        private Integer rise;
        @JsonProperty("LIFT")
        private Integer lift;
        @JsonProperty("EXTRA_BIG")
        private Integer extraBig;
        @JsonProperty("COMMENT")
        private String comment;
        @JsonProperty("COMMENT_ASSORTIMENT")
        private Object commentAssortment;
        @JsonProperty("DELIVERY_PRICE")
        private String deliveryPrice;
        @JsonProperty("LIFT_PRICE")
        private String liftPrice;
        @JsonProperty("EXTRA_BIG2")
        private Integer extraBig2;
        @JsonProperty("DELIVERY_SERVICES")
        private String deliveryServices;
        @JsonProperty("LONG_TAIL")
        private Integer longTail;
        @JsonProperty("CARRY_PRICE")
        private String carryPrice;
        @JsonProperty("CARRY_LENGTH")
        private String carryLength;
        @JsonProperty("PVZ_DATA")
        private PvzData pvzData;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Basket {

        @JsonProperty("ID")
        private String id;
        @JsonProperty("SKU")
        private String sku;
        @JsonProperty("PRICE")
        private String price;
        @JsonProperty("QUANTITY")
        private String quantity;
        @JsonProperty("LINK")
        private String link;
        @JsonProperty("IMG")
        private String img;
        @JsonProperty("NAME")
        private String name;
        @JsonProperty("WEIGHT")
        private Double weight;
        @JsonProperty("VOLUME")
        private Integer volume;
        @JsonProperty("BARCODE")
        private Object barcode;
        @JsonProperty("DEPARTMENT")
        private String department;
        @JsonProperty("SUBDIVISION")
        private String subdivision;
        @JsonProperty("TYPE")
        private String type;
        @JsonProperty("SUBTYPE")
        private String subtype;
        @JsonProperty("UOM")
        private String uom;
        @JsonProperty("ROOT_SECTION")
        private String rootSection;
        @JsonProperty("WIDTH")
        private Double width;
        @JsonProperty("HEIGHT")
        private Double height;
        @JsonProperty("LENGTH")
        private Integer length;
        @JsonProperty("TAX")
        private Integer tax;
        @JsonProperty("RMS_STOCK")
        private Integer rmsStock;
        @JsonProperty("STOCK")
        private Integer stock;
        @JsonProperty("PYXIS_STOCK")
        private Integer pyxisStock;
        @JsonProperty("LONG_TAIL")
        private Integer longTail;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Total {

        @JsonProperty("WEIGHT")
        private Double weigth;
        @JsonProperty("CNT_PRODUCTS")
        private Integer cntProducts;
        @JsonProperty("COST")
        private Integer cost;
        @JsonProperty("COST_DELIVERY")
        private Integer costDelivery;
        @JsonProperty("COST_LIFT")
        private Integer costLift;
        @JsonProperty("VOLUME")
        private Integer volume;
        @JsonProperty("LENGTHY")
        private Integer lengthy;
        @JsonProperty("VOLUME_PRODUCT")
        private Integer volumeProduct;
        @JsonProperty("EXTRABIG")
        private Integer extrabig;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class UserData {

        @JsonProperty("NAME")
        private String name;
        @JsonProperty("SURNAME")
        private String surname;
        @JsonProperty("EMAIL")
        private String email;
        @JsonProperty("PHONE")
        private String phone;
        @JsonProperty("RECEIVE_NAME")
        private String recieveName;
        @JsonProperty("RECEIVE_SURNAME")
        private String receiveSurname;
        @JsonProperty("RECEIVE_PHONE")
        private String receivePhone;
        @JsonProperty("EXPRESS_REGISTRATION")
        private Boolean expressRegistration;
        @JsonProperty("MEDIA5_MOVE_CUSTOMER_NUMBER")
        private String mediasMoveCustomerNumber;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Address {

        @JsonProperty("HOUSE")
        private Object house;
        @JsonProperty("CITY")
        private Object city;
        @JsonProperty("STREET")
        private String street;
        @JsonProperty("PORCH")
        private Object porch;
        @JsonProperty("FLOOR")
        private Object floor;
        @JsonProperty("APARTMENT")
        private Object apartment;
        @JsonProperty("INTERCOM")
        private Object intercom;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class PickupShop {

        @JsonProperty("ID")
        private String id;
        @JsonProperty("IBLOCK_ID")
        private String iblockId;
        @JsonProperty("XML_ID")
        private String xmlId;
        @JsonProperty("NAME")
        private String name;
        @JsonProperty("PROPERTY_ADDRESS_VALUE")
        private String propertyAddressValue;
        @JsonProperty("PROPERTY_ADDRESS_VALUE_ID")
        private String propertyAddressValuerId;
        @JsonProperty("PROPERTY_NAME_VALUE")
        private String propertyNameValue;
        @JsonProperty("PROPERTY_NAME_VALUE_ID")
        private String propertyNameValueId;
        @JsonProperty("PROPERTY_WORK_TIME_VALUE")
        private String propertyWorkTimeValue;
        @JsonProperty("PROPERTY_WORK_TIME_VALUE_ID")
        private String propertyWorkTimeValueId;
        @JsonProperty("PROPERTY_PHONE_VALUE")
        private String propertyPhoneValue;
        @JsonProperty("PROPERTY_PHONE_VALUE_ID")
        private String propertyPhoneValueId;
        @JsonProperty("PROPERTY_PICKUP_OPERATORS_VALUE")
        private String propertyPickupOperatorsValue;
        @JsonProperty("PROPERTY_PICKUP_OPERATORS_VALUE_ID")
        private String propertyPickupOperatorsValueId;
        @JsonProperty("PROPERTY_GPS_COORDS_VALUE")
        private String propertyGpsCoordsValue;
        @JsonProperty("PROPERTY_GPS_COORDS_VALUE_ID")
        private String propertyGpsCoordsValueId;
        @JsonProperty("DISPLAY_NAME")
        private String displayName;
    }

    @Data
    public static class PvzData {
        @JsonProperty("PVZ_CODE")
        private String pvzCode;
        @JsonProperty("PVZ_ADDRESS")
        private String pvzAddress;
        @JsonProperty("PVZ_PHONE")
        private String pvzPhone;
        @JsonProperty("PVZ_WORKTIME")
        private String pvzWorkTime;
    }
}

