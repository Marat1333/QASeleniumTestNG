package com.leroy.magportal.api.data.printer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class PrinterData {

    private Departments departments;

    @Data
    public static class Departments {

        @JsonProperty("1")
        private List<Printer> dep1;
        @JsonProperty("2")
        private List<Printer> dep2;
        @JsonProperty("3")
        private List<Printer> dep3;
        @JsonProperty("4")
        private List<Printer> dep4;
        @JsonProperty("5")
        private List<Printer> dep5;
        @JsonProperty("6")
        private List<Printer> dep6;
        @JsonProperty("7")
        private List<Printer> dep7;
        @JsonProperty("8")
        private List<Printer> dep8;
        @JsonProperty("9")
        private List<Printer> dep9;
        @JsonProperty("10")
        private List<Printer> dep10;
        @JsonProperty("11")
        private List<Printer> dep11;
        @JsonProperty("12")
        private List<Printer> dep12;
        @JsonProperty("13")
        private List<Printer> dep13;
        @JsonProperty("14")
        private List<Printer> dep14;
        @JsonProperty("15")
        private List<Printer> dep15;
        //    @JsonProperty("16")
//    private List<Printer> dep16;
        private List<Printer> others;
    }

    @Data
    public static class Printer {

        private String name;
        private String comment;
    }
}