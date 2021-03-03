package com.leroy.magportal.api.constants;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.leroymerlin.qa.core.clients.tunnel.data.enums.TypeOfLift;

public class OnlineOrderTypeConst {

    @Data
    @Accessors(chain = true)
    public static class OnlineOrderTypeData {

        public String deliveryPrice;
        public String paymentType;
        public DeliveryServiceTypeEnum deliveryType;
        public Integer rise;
        public Integer lift;
        public String deliveryFullPrice;
        public String liftPrice;
        public String shopId;
        public String lmCode;
        public int sameDay;
        public Boolean pickupShop = false;
        public Boolean pvzData = false;
        public TypeOfLift liftType = TypeOfLift.STAIRS;
    }

    public static final OnlineOrderTypeData PICKUP_POSTPAYMENT = new OnlineOrderTypeData()
            .setDeliveryPrice("0.00")
            .setPaymentType(PaymentTypeEnum.CASH.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.PICKUP)
            .setPickupShop(true)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("0.00")
            .setLiftPrice("0.00");

    public static final OnlineOrderTypeData PICKUP_PREPAYMENT = new OnlineOrderTypeData()
            .setDeliveryPrice("0.00")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.PICKUP)
            .setPickupShop(true)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("0.00")
            .setLiftPrice("0.00");

    public static final OnlineOrderTypeData DELIVERY_TO_ENTRANCE = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_ENTRANCE)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("2000.45")
            .setLiftPrice("0.00");

    public static final OnlineOrderTypeData DELIVERY_TO_DOOR = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR)
            .setRise(1)
            .setLift(1)
            .setDeliveryFullPrice("3000.89")
            .setLiftPrice("1000.44");

    public static final OnlineOrderTypeData DELIVERY_EXPRESS = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR)
            .setRise(1)
            .setLift(1)
            .setDeliveryFullPrice("3000.89")
            .setLiftPrice("1000.44")
            .setSameDay(1);

    public static final OnlineOrderTypeData DELIVERY_CIZ = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.BILL.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR)
            .setRise(1)
            .setLift(1)
            .setDeliveryFullPrice("3000.89")
            .setLiftPrice("1000.44")
            .setShopId(ShopTypeEnum.CIZ.getValue());

    public static final OnlineOrderTypeData DELIVERY_CDS = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR)
            .setRise(1)
            .setLift(1)
            .setDeliveryFullPrice("3000.89")
            .setLiftPrice("1000.44")
            .setShopId(ShopTypeEnum.CDS.getValue());

    public static final OnlineOrderTypeData DELIVERY_KK = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.COURIER)
            .setRise(1)
            .setLift(1)
            .setDeliveryFullPrice("3000.89")
            .setLiftPrice("1000.44")
            .setShopId(ShopTypeEnum.KK.getValue())
            .setLmCode(LmCodeTypeEnum.KK.getValue());

    public static final OnlineOrderTypeData DELIVERY_TK = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_DOOR)
            .setRise(1)
            .setLift(1)
            .setDeliveryFullPrice("3000.89")
            .setLiftPrice("1000.44")
            .setShopId(ShopTypeEnum.TK.getValue());

    public static final OnlineOrderTypeData DELIVERY_PVZ = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_PVZ)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("2000.45")
            .setLiftPrice("0.00")
            .setPvzData(true)
            .setLmCode(LmCodeTypeEnum.PVZ.getValue())
            .setShopId(ShopTypeEnum.PVZ.getValue());

    public static final OnlineOrderTypeData COURIER = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.COURIER)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("2000.45")
            .setLiftPrice("0.00");

    public static final OnlineOrderTypeData DELIVERY_TO_LIFT = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_TO_LIFT)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("2000.45")
            .setLiftPrice("0.00");

    public static final OnlineOrderTypeData DELIVERY_NO_LANDING = new OnlineOrderTypeData()
            .setDeliveryPrice("2000.45")
            .setPaymentType(PaymentTypeEnum.SBERBANK.getName())
            .setDeliveryType(DeliveryServiceTypeEnum.DELIVERY_TO_LIFT)
            .setRise(0)
            .setLift(0)
            .setDeliveryFullPrice("2000.45")
            .setLiftPrice("0.00");
}
