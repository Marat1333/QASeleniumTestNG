package com.leroy.common_mashups.catalogs.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class NomenclatureData {
    private Integer groupId;
    private String groupName;
    private JsonNode departments;
}
