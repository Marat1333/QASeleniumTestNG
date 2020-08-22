package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferRequestData;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferRequestWidget;
import io.qameta.allure.Step;

import java.awt.*;

/**
 * Отзыв товаров со склада
 */
public class TransferRequestsPage extends CommonMagMobilePage {

    AndroidScrollView<TransferRequestData> requestsScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.widget.TextView[@index='4']]",
            TransferRequestWidget.class);

    // Actions

    @Step("Найти заявку по наименованию товара в ней, и открыть эту заявку")
    public void searchForRequestAndOpenIt(String productTitle, String status) {
        CardWidget<TransferRequestData> widget = requestsScrollView.searchForWidgetByText(productTitle, status);
        if (widget.getLocation().getY() / Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 100 > 95)
            requestsScrollView.scrollDown();
        widget.click();
    }

}
