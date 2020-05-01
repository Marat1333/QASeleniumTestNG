package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.cart_estimate.widget.ProductOrderCardPuzWidget;

public class CreateCartPage extends CreateCartEstimatePage {

    public CreateCartPage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Cart-CartsView__cart__group')]/div[contains(@class, 'SalesDocProduct')]",
            clazz = ProductOrderCardPuzWidget.class)
    ElementList<ProductOrderCardPuzWidget> products;

    @Override
    public ElementList<ProductOrderCardPuzWidget> products() {
        return products;
    }
}
