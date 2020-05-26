package com.leroy.magmobile.ui.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class SupplierCardData extends CardWidgetData {
    private String supplierCode;
    private String supplierName;
}
