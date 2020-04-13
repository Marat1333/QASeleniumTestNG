package com.leroy.magmobile.api.data.catalog.product;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class NomenclatureData {
    private Integer groupId;
    private String groupName;
    private JsonNode departments;
}
