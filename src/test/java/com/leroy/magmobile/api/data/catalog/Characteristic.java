package com.leroy.magmobile.api.data.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Characteristic {
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private String value;
}
