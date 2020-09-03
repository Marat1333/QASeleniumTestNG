package com.leroy.magportal.api.constants;

import lombok.Data;
import lombok.experimental.Accessors;

public class OnlineOrderTypeConst {

    @Data
    @Accessors(chain = true)
    public static class OnlineOrderTypeData {

        public String priceDelivery;
        public String paymentType;
        public String deliveryType;
        public Integer rise;
        public Integer lift;
        public String deliveryPrice;
        public String liftPrice;
        public String deliveryServiceType;
        public String shopId;
        public String lmCode;
        public int sameDay;
        public Boolean pickupShop = false;
        public Boolean pvzData = false;
    }

    public static final OnlineOrderTypeData PICKUP_POSTPAYMENT = new OnlineOrderTypeData()
            .setPriceDelivery("0.00")
            .setPaymentType(PaymentTypeEnum.CASH.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.PICKUP.getType())
            .setPickupShop(true)
            .setRise(0)
            .setLift(0)
            .setDeliveryPrice("0.00")
            .setLiftPrice("0.00")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.PICKUP.getService());

    public static final OnlineOrderTypeData PICKUP_PREPAYMENT = new OnlineOrderTypeData()
            .setPriceDelivery("0.00")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.PICKUP.getType())
            .setPickupShop(true)
            .setRise(0)
            .setLift(0)
            .setDeliveryPrice("0.00")
            .setLiftPrice("0.00")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.PICKUP.getService());

    public static final OnlineOrderTypeData DELIVERY_TO_ENTRANCE = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_ENTRANCE.getType())
            .setRise(0)
            .setLift(0)
            .setDeliveryPrice("2000.45")
            .setLiftPrice("0.00")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.DELIVERY_ENTRANCE.getService());

    public static final OnlineOrderTypeData DELIVERY_TO_DOOR = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getType())
            .setRise(1)
            .setLift(1)
            .setDeliveryPrice("3000.89")
            .setLiftPrice("1000.44")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getService());

    public static final OnlineOrderTypeData DELIVERY_EXPRESS = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getType())
            .setRise(1)
            .setLift(1)
            .setDeliveryPrice("3000.89")
            .setLiftPrice("1000.44")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getService())
            .setSameDay(1);

    public static final OnlineOrderTypeData DELIVERY_CIZ = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.BILL.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getType())
            .setRise(1)
            .setLift(1)
            .setDeliveryPrice("3000.89")
            .setLiftPrice("1000.44")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getService())
            .setShopId(ShopTypeEnum.CIZ.getValue());

    public static final OnlineOrderTypeData DELIVERY_KK = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.COURIER.getType())
            .setRise(1)
            .setLift(1)
            .setDeliveryPrice("3000.89")
            .setLiftPrice("1000.44")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.COURIER.getService())
            .setShopId(ShopTypeEnum.KK.getValue())
            .setLmCode(LmCodeTypeEnum.KK.getValue());

    public static final OnlineOrderTypeData DELIVERY_TK = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getType())
            .setRise(1)
            .setLift(1)
            .setDeliveryPrice("3000.89")
            .setLiftPrice("1000.44")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.DELIVERY_DOOR.getService())
            .setShopId(ShopTypeEnum.TK.getValue());

    public static final OnlineOrderTypeData DELIVERY_PVZ = new OnlineOrderTypeData()
            .setPriceDelivery("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_PVZ.getType())
            .setRise(0)
            .setLift(0)
            .setDeliveryPrice("2000.45")
            .setLiftPrice("0.00")
            .setDeliveryServiceType(DeliveryServiceTypeEnum.DELIVERY_PVZ.getService())
            .setPvzData(true)
            .setLmCode(LmCodeTypeEnum.PVZ.getValue());
}
