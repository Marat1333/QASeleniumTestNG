package com.leroy.magportal.api.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NearestShopsList extends ArrayList<NearestShopsData> {
}
