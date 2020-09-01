package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.android.AndroidScrollViewV2;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magmobile.ui.pages.work.transfer.data.DetailedTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.enums.TransferTaskTypes;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferTaskProductWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

/**
 * Экран с подтвержденной (статус отправлен) заявкой на отзыв товара для клиента в торговый зал
 */
public class TransferConfirmedTaskToClientPage extends TransferOrderPage {


    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Место выдачи']]/android.widget.EditText",
            metaName = "Место выдачи")
    EditBox pickupPlaceFld;

    @AppFindBy(xpath = "(//android.view.ViewGroup[descendant::android.widget.TextView[@text='Место выдачи']]/following-sibling::android.view.ViewGroup)[1]",
            metaName = "Карточка выбранного клиента")
    TransferOrderToClientStep2Page.SelectedClientWidget selectedClientWidget;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Дата поставки товара']]/android.widget.EditText",
            metaName = "Дата поставки товара")
    EditBox deliveryDateFld;

    AndroidScrollViewV2<TransferTaskProductWidget, TransferProductData> productScrollView =
            new AndroidScrollViewV2<>(driver,
                    AndroidScrollView.TYPICAL_LOCATOR,
                    ".//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]]]",
                    TransferTaskProductWidget.class);

    // Grab data

    private TransferTaskTypes getPickupPlace(String ps) {
        String place = pickupPlaceFld.getText(ps);
        if (place.toLowerCase().contains("клиенту в торговый зал"))
            return TransferTaskTypes.CLIENT_IN_SHOP_ROOM;
        if (place.toLowerCase().contains("крупногабаритная касса"))
            return TransferTaskTypes.OVER_SIZED_CHECKOUT;
        throw new RuntimeException("Получено неизвестное место выдачи:" + place);
    }

    @Step("Получить информацию о деталях подтвержденной заявки на отзыв")
    public DetailedTransferTaskData getTransferTaskData() {
        String ps = getPageSource();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setNumber(getTaskNumber(ps));
        detailedTransferTaskData.setPickupPlace(getPickupPlace(ps));
        detailedTransferTaskData.setClient(selectedClientWidget.collectDataFromPage(ps));
        detailedTransferTaskData.setDeliveryDate(DateTimeUtil.strToLocalDate(deliveryDateFld.getText(ps), ""));
        detailedTransferTaskData.setProducts(productScrollView.getFullDataList());
        return detailedTransferTaskData;
    }

    // Verifications

    @Step("Проверить, что данные заявки на отзыв соответствуют ожидаемым значениям")
    public TransferConfirmedTaskToClientPage shouldTransferTaskDataIs(DetailedTransferTaskData transferTaskData) {
        DetailedTransferTaskData expectedTransferTaskData = transferTaskData.clone();
        if (expectedTransferTaskData.getClient() != null) {
            expectedTransferTaskData.getClient().setEmail(null);
        }
        expectedTransferTaskData.getProducts().forEach(p -> p.setTotalStock(null));
        DetailedTransferTaskData actualData = getTransferTaskData();
        actualData.assertEqualsNotNullExpectedFields(expectedTransferTaskData);
        return this;
    }


}
