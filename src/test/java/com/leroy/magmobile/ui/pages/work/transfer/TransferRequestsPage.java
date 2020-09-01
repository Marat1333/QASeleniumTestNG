package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.ShortTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferRequestWidget;
import io.qameta.allure.Step;

import java.awt.*;

/**
 * Отзыв товаров со склада
 */
public class TransferRequestsPage extends CommonMagMobilePage {

    AndroidScrollView<ShortTransferTaskData> requestsScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.widget.TextView[@index='4']]",
            TransferRequestWidget.class);

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Заголовок экрана")
    Element screenTitle;

    @AppFindBy(text = "ПОПОЛНИТЬ ТОРГОВЫЙ ЗАЛ")
    MagMobButton fillShoppingRoomBtn;

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isTrue(screenTitle.isVisible(timeout),
                "Страница 'Отзыв товаров со склада' не загрузилась");
        waitUntilProgressBarIsInvisible();
    }

    // Actions

    @Step("Нажать кнопку 'Пополнить торговый зал'")
    public TransferOrderStep1Page clickFillShoppingRoomButton() {
        fillShoppingRoomBtn.click();
        return new TransferOrderStep1Page();
    }

    @Step("Найти заявку по наименованию товара в ней, и открыть эту заявку")
    public void searchForRequestAndOpenIt(String productTitle, String status) {
        CardWidget<ShortTransferTaskData> widget = requestsScrollView.searchForWidgetByText(productTitle, status);
        if (widget.getLocation().getY() / Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 100 > 95)
            requestsScrollView.scrollDown();
        widget.click();
    }

    // Verifications

    @Step("Проверить, что страница 'Отзыв товаров со склада' отображается корректно")
    public TransferRequestsPage verifyRequiredElements() {
        softAssert.areElementsVisible(screenTitle, fillShoppingRoomBtn);
        softAssert.verifyAll();
        return this;
    }

}
