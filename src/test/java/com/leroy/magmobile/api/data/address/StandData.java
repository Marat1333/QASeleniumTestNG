package com.leroy.magmobile.api.data.address;

import lombok.Data;

import java.util.List;

@Data
public class StandData {
    private List<StandItemData> items;
    private String email;
    private Integer alleyType;
    private String alleyCode;
}
