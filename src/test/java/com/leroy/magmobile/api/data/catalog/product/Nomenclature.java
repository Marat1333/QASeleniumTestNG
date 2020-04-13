package com.leroy.magmobile.api.data.catalog.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Nomenclature {
    private List<NomenclatureData> items;
}
