package com.leroy.magmobile.api.data.sales.orders;

import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PostOrderProductData extends BaseProductOrderData {

    private Integer futureStock;

}
