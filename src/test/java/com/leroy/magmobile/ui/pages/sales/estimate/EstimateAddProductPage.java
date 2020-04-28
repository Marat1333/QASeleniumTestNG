package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import io.qameta.allure.Step;

public class EstimateAddProductPage extends AddProduct35Page {

    public EstimateAddProductPage(Context context) {
        super(context);
    }

    @Step("Проверить, что страница 'Добавление товара' отображается корректно")
    public EstimateAddProductPage verifyRequiredElements() {
        verifyRequiredElements(SubmitBtnCaptions.ADD_TO_ESTIMATE);
        return this;
    }

}
