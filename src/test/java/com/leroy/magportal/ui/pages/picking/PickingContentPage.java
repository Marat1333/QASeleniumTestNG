package com.leroy.magportal.ui.pages.picking;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.pages.picking.widget.BuildProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;

public class PickingContentPage extends PickingPage {

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard']")
    CardWebWidgetList<BuildProductCardWidget, PickingProductCardData> productCards;

}
