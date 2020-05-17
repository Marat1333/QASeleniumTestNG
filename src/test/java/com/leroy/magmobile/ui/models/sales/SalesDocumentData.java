package com.leroy.magmobile.ui.models.sales;

import com.leroy.magmobile.ui.models.CardWidgetData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class SalesDocumentData extends CardWidgetData {

    private String title;
    private String price;
    private String number;
    private String pin;
    private String date;
    private String documentState;

}
