package com.leroy.magportal.api.data.onlineOrders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PvzData {

    @JsonProperty("PVZ_CODE")
    private String code;
    @JsonProperty("PVZ_ADDRESS")
    private String address;
    @JsonProperty("PVZ_PHONE")
    private String phone;
    @JsonProperty("PVZ_WORKTIME")
    private String workTime;
}
