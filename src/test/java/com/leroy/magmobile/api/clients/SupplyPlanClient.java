package com.leroy.magmobile.api.clients;

import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.supply_plan.Card.SupplyCardData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentData;
import com.leroy.magmobile.api.data.supply_plan.Details.ShipmentDataList;
import com.leroy.magmobile.api.data.supply_plan.Total.TotalPalletDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanCard;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanDetails;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanSuppliers;
import com.leroy.magmobile.api.requests.supply_plan.GetSupplyPlanTotal;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SupplyDailyShipmentInfo;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SupplyDetailsCardInfo;
import io.qameta.allure.Step;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SupplyPlanClient extends BaseMagMobileClient {

    @Step("Get shipments")
    public Response<ShipmentDataList> getShipments(GetSupplyPlanDetails params) {
        return execute(params, ShipmentDataList.class);
    }

    @Step("Get shipments")
    public Response<ShipmentDataList> getShipments(LocalDate date, int i) {
        return getShipments((new GetSupplyPlanDetails().setDate(date)
                .setShopId(getUserSessionData().getUserShopId())
                .setDepartmentId(i)));
    }

    @Step("Get Total Pallets")
    public Response<TotalPalletDataList> getTotalPallets(GetSupplyPlanTotal params) {
        return execute(params, TotalPalletDataList.class);
    }

    @Step("Get supply card")
    public Response<SupplyCardData> getSupplyCard(GetSupplyPlanCard params) {
        return execute(params, SupplyCardData.class);
    }

    @Step("Get supply card")
    public Response<SupplyCardData> getSupplyCard(ShipmentData data) {
        return getSupplyCard(new GetSupplyPlanCard().setDocumentNo(data.getDocumentNo().asText())
                .setDocumentType(data.getDocumentType().asText())
                .setSendingLocation(data.getSendingLocation())
                .setSendingLocationType(data.getSendingLocationType()));
    }

    @Step("Get suppliers")
    public Response<SupplierDataList> getSuppliers(GetSupplyPlanSuppliers params) {
        return execute(params, SupplierDataList.class);
    }

    @Step("Get random shipment per week")
    public ShipmentData getRandomShipment(String departmentId) {
        List<ShipmentData> weekShipments = getWeekShipments(departmentId);
        return weekShipments.get((int) (Math.random() * weekShipments.size()));
    }

    @Step("Get first founded today reserve")
    public SupplyDailyShipmentInfo getTodayReserve() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            List<ShipmentData> data = this.getShipments(date, i).asJson().getItems();
            data = data.stream().filter(y -> y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
            if (data.size() > 0) {
                return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
            }
        }
        return null;
    }

    @Step("Get first founded today reserve")
    public SupplyDailyShipmentInfo getNotTodayReserve() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            for (int z = 1; z < 7; z++) {
                List<ShipmentData> data = this.getShipments(date.plusDays(z), i).asJson().getItems();
                data = data.stream().filter(y -> y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
                if (data.size() > 0) {
                    return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
                }
            }
        }
        return null;
    }

    @Step("Get first founded today shipment info")
    public SupplyDailyShipmentInfo getTodayShipment() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            List<ShipmentData> data = this.getShipments(date, i).asJson().getItems();
            data = data.stream().filter(y -> !y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
            if (data.size() > 0) {
                return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
            }
        }
        return null;
    }

    @Step("Get first founded not today shipment info")
    public SupplyDailyShipmentInfo getNotTodayShipment() {
        LocalDate date = LocalDate.now();
        for (int i = 1; i < 16; i++) {
            for (int z = 1; z < 7; z++) {
                List<ShipmentData> data = this.getShipments(date.plusDays(z), i).asJson().getItems();
                data = data.stream().filter(y -> !y.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
                if (data.size() > 0) {
                    return new SupplyDailyShipmentInfo(data.get((int) (Math.random()) * data.size()), String.valueOf(i));
                }
            }
        }
        return null;
    }

    @Step("Get first founded multi shipment info")
    public SupplyDetailsCardInfo getMultiShipmentSupply() {
        List<ShipmentData> weekShipments;
        for (int i = 1; i < 16; i++) {
            weekShipments = getWeekShipments(String.valueOf(i));
            for (ShipmentData eachSupply : weekShipments) {
                SupplyCardData supplyCardData = this.getSupplyCard(eachSupply).asJson();
                try {
                    if (supplyCardData.getShipments().size() > 1) {
                        return new SupplyDetailsCardInfo(eachSupply, supplyCardData, String.valueOf(i));
                    }
                } catch (NullPointerException e) {
                    Log.warn("Mash-up load error");
                }
            }
        }
        return null;
    }

    @Step("Get first founded supply with extra products")
    public SupplyDetailsCardInfo getSupplyWithExtraProducts() {
        List<ShipmentData> weekShipments;
        for (int i = 1; i < 16; i++) {
            weekShipments = getWeekShipments(String.valueOf(i));
            for (ShipmentData eachSupply : weekShipments) {
                SupplyCardData supplyCardData = this.getSupplyCard(eachSupply).asJson();
                try {
                    //условие на случай, ошибки бэка, когда все товары будут в otherProducts
                    if (supplyCardData.getOtherProducts().size() > 0 && supplyCardData.getShipments().get(0).getProducts().get(0).getLmCode() != null) {
                        return new SupplyDetailsCardInfo(eachSupply, supplyCardData, String.valueOf(i));
                    }
                } catch (NullPointerException e) {
                    Log.warn("Mash-up load error");
                }
            }
        }
        return null;
    }

    @Step("Get first founded week shipment dates list")
    public List<ShipmentData> getWeekShipments(String departmentId) {
        LocalDate date = LocalDate.now();
        List<ShipmentDataList> responses = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            responses.add(this.getShipments(date.plusDays(i), Integer.parseInt(departmentId)).asJson());
        }
        List<ShipmentDataList> nonEmptyResponses = responses.stream().filter((i) -> i.getItems().size() > 0).collect(Collectors.toList());
        List<ShipmentData> weekSupplyList = new ArrayList<>();
        for (ShipmentDataList each : nonEmptyResponses) {
            weekSupplyList.addAll(each.getItems());
        }
        weekSupplyList = weekSupplyList.stream().filter(i -> !i.getRowType().equals("FIX_RESERVE")).collect(Collectors.toList());
        return weekSupplyList;
    }
}
