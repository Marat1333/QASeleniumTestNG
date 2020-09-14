package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.ShortTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.widget.TransferRequestWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

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

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.HorizontalScrollView]/following-sibling::android.view.ViewGroup",
            metaName = "Кнопка Фильтр")
    Element filterBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.HorizontalScrollView]/following-sibling::android.view.ViewGroup//android.widget.TextView",
            metaName = "Счетчик выбранных фильтров")
    Element filterCount;

    @AppFindBy(text = "ПОПОЛНИТЬ ТОРГОВЫЙ ЗАЛ")
    MagMobButton fillShoppingRoomBtn;

    @Override
    protected void waitForPageIsLoaded() {
        screenTitle.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    // Grab data

    @Step("Получить информацию о последних заявках")
    public List<ShortTransferTaskData> getTransferTaskDataList(int maxCount) throws Exception {
        return requestsScrollView.getFullDataList(maxCount);
    }

    // Actions

    @Step("Обновить данные, потянув экран сверху вниз")
    public TransferRequestsPage makePullToRefresh() {
        requestsScrollView.scrollToBeginning();
        requestsScrollView.scrollToBeginning();
        return this;
    }

    @Step("Нажать кнопку 'Пополнить торговый зал'")
    public TransferOrderStep1Page clickFillShoppingRoomButton() {
        fillShoppingRoomBtn.click();
        return new TransferOrderStep1Page();
    }

    @Step("Найти заявку по наименованию товара в ней, и открыть эту заявку")
    public void searchForRequestAndOpenIt(String productTitle, String status) throws Exception {
        CardWidget<ShortTransferTaskData> widget = requestsScrollView.searchForWidgetByText(productTitle, status);
        if (widget.getLocation().getY() / Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 100 > 95)
            requestsScrollView.scrollDown();
        widget.click();
    }

    @Step("Перейти на страницу выбора фильтров")
    public FilterTransferTaskPage clickFilterBtn() {
        filterBtn.click();
        return new FilterTransferTaskPage();
    }

    // Verifications

    @Step("Проверить, что страница 'Отзыв товаров со склада' отображается корректно")
    public TransferRequestsPage verifyRequiredElements() {
        softAssert.areElementsVisible(screenTitle, fillShoppingRoomBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что количество выбранных фильтров = {value}")
    public TransferRequestsPage shouldFilterCountIs(int value) {
        anAssert.isEquals(ParserUtil.strToInt(filterCount.getText()), value,
                "Неверное количество выбранных фильтрров");
        return this;
    }

    @Step("Проверить, что в списке отображаются только заявки согласно фильтрам")
    public TransferRequestsPage verifyTransferTaskFilters(LocalDate filterDate, String status) throws Exception {
        List<ShortTransferTaskData> transferTasks = requestsScrollView.getFullDataList();
        for (int i = 0; i < transferTasks.size(); i++) {
            softAssert.isTrue(transferTasks.get(i).getCreationDate().toLocalDate().isBefore(filterDate),
                    "У " + (i + 1) + " заявки дата создания больше, чем было задано в фильтре");
            softAssert.isEquals(transferTasks.get(i).getStatus().toLowerCase(), status.toLowerCase(),
                    "У " + (i + 1) + " неверный статус, нежели был задан в фильтре");
        }
        softAssert.verifyAll();
        return this;
    }

}
