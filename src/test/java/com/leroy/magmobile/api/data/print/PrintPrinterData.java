package com.leroy.magmobile.api.data.print;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrintPrinterData {
    @JsonProperty(required = true)
    private String name;
    @JsonProperty(required = true)
    private String comment;
}
