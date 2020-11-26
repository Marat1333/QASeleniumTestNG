package com.leroy.magportal.api.data.picking;

import java.util.List;
import lombok.Data;

@Data
public class StorageLocationData {

    private String shopId;
    private List<ZoneLocation> zones;

    @Data
    public static class ZoneLocation {

        private List<String> zoneCells;
        private String zoneId;
        private String zoneName;
    }
}
