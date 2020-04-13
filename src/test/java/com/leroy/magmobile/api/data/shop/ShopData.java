package com.leroy.magmobile.api.data.shop;

import lombok.Data;

@Data
public class ShopData {
    private Integer id;
    private String name;
    private Integer departmentId;
    private String departmentName;
    private String cityName;
}
