package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.android.AndroidScrollViewV2;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magmobile.ui.pages.work.transfer.data.DetailedTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferTaskProductWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

/**
 * Экран с подтвержденной (статус отправлен) заявкой на отзыв товара для пополнения торгового зала
 */
public class TransferConfirmedTaskToShopRoomPage extends TransferOrderPage {

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Дата поставки товара']]/android.widget.EditText",
            metaName = "Дата поставки товара")
    EditBox deliveryDateFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Ожидаемое время поставки товара']]/android.widget.EditText",
            metaName = "Ожидаемое время поставки товара")
    EditBox deliveryTimeFld;

    AndroidScrollViewV2<TransferTaskProductWidget, TransferProductData> productScrollView =
            new AndroidScrollViewV2<>(driver,
                    AndroidScrollView.TYPICAL_LOCATOR,
                    ".//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]]]",
                    TransferTaskProductWidget.class);

    // Grab data

    @Step("Получить информацию о деталях подтвержденной заявки на отзыв")
    public DetailedTransferTaskData getTransferTaskData() {
        String ps = getPageSource();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setNumber(getTaskNumber(ps));
        detailedTransferTaskData.setDeliveryDate(DateTimeUtil.strToLocalDate(deliveryDateFld.getText(ps), ""));
        detailedTransferTaskData.setDeliveryTime(DateTimeUtil.strToLocalTime(deliveryTimeFld.getText(ps)));
        detailedTransferTaskData.setProducts(productScrollView.getFullDataList());
        return detailedTransferTaskData;
    }

    // Verifications

    @Step("Проверить, что данные заявки на отзыв соответствуют ожидаемым значениям")
    public TransferConfirmedTaskToShopRoomPage shouldTransferTaskDataIs(DetailedTransferTaskData transferTaskData) {
        DetailedTransferTaskData expectedTransferTaskData = transferTaskData.clone();
        expectedTransferTaskData.getProducts().forEach(p -> p.setTotalStock(null));
        DetailedTransferTaskData actualData = getTransferTaskData();
        actualData.assertEqualsNotNullExpectedFields(expectedTransferTaskData);
        return this;
    }

}
