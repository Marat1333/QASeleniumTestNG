package com.leroy.magmobile.api.data.print;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PrintDepartments {
    @JsonProperty(value = "1", required = true)
    private List<PrintPrinterData> dept1;
    @JsonProperty(value = "2", required = true)
    private List<PrintPrinterData> dept2;
    @JsonProperty(value = "3", required = true)
    private List<PrintPrinterData> dept3;
    @JsonProperty(value = "4", required = true)
    private List<PrintPrinterData> dept4;
    @JsonProperty(value = "5", required = true)
    private List<PrintPrinterData> dept5;
    @JsonProperty(value = "6", required = true)
    private List<PrintPrinterData> dept6;
    @JsonProperty(value = "7", required = true)
    private List<PrintPrinterData> dept7;
    @JsonProperty(value = "8", required = true)
    private List<PrintPrinterData> dept8;
    @JsonProperty(value = "9", required = true)
    private List<PrintPrinterData> dept9;
    @JsonProperty(value = "10", required = true)
    private List<PrintPrinterData> dept10;
    @JsonProperty(value = "11", required = true)
    private List<PrintPrinterData> dept11;
    @JsonProperty(value = "12", required = true)
    private List<PrintPrinterData> dept12;
    @JsonProperty(value = "13", required = true)
    private List<PrintPrinterData> dept13;
    @JsonProperty(value = "14", required = true)
    private List<PrintPrinterData> dept14;
    @JsonProperty(value = "15", required = true)
    private List<PrintPrinterData> dept15;
    @JsonProperty(required = true)
    private List<PrintPrinterData> others;

}
