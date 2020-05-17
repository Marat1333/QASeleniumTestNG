package com.leroy.magmobile.ui.models.search;

import com.leroy.magmobile.ui.models.CardWidgetData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ServiceCardData extends CardWidgetData {
    private String lmCode;
    private String name;
}
