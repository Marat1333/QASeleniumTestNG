package com.leroy.umbrella_extension.magmobile.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceItemListResponse {

    private List<ServiceItemResponse> items;
    private Integer totalCount;

}
