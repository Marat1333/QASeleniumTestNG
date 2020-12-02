package com.leroy.magmobile.api.data.sales.orders;

import lombok.Data;

import java.util.List;

@Data
public class ResOrderProductCheckQuantityData {
    private String lineId;
    private String lmCode;
    private Double quantity;
    private Double availableStock;
    private String title;
    private String barCode;
    private String departmentId;
    //logistic":{"weight":"1.595","depth":"3.4","width":"47","height":"118.9"},
    List<String> images;
    private Boolean isAvs;
    private Boolean isAVS;
    private Boolean topEM;
    private Integer price;
}
