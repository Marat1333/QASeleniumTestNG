package com.leroy.magmobile.api.data.address;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StandData {
    private Integer id;
    private String code;

    private Integer size;
    private Integer side;
    private Integer position;

    private Integer cellsCount;
    private Integer equipmentId;
    private Integer productsCount;

    public StandData(int size, int side, int position) {
        this.size = size;
        this.side = side;
        this.position = position;
    }
}
