package com.leroy.magmobile.api.data.catalog.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesHistory extends ArrayList<SalesHistoryData> {
}
