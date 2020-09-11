package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CalendarWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Экран "Фильтр списка заявок"
 */
public class FilterTransferTaskPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackCloseModal", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок страницы 'Фильтр списка заявок'")
    Element header;

    @AppFindBy(text = "Статус заявки")
    Element taskStatusFld;
    @AppFindBy(followingTextAfter = "Статус заявки", metaName = "Кол-во выбранных фильтров для Статус заявки")
    Element selectedTaskStatusCount;

    @AppFindBy(text = "Дата создания заявки")
    Element creationTaskDateFld;
    @AppFindBy(followingTextAfter = "Дата создания заявки", metaName = "Выбранное значение фильтра 'Дата создания заявки'")
    Element selectedCreationTaskDate;

    @AppFindBy(xpath = "//*[android.widget.TextView[@text='Показать только мои заявки']]//*[@content-desc='Button']",
            metaName = "Чек бокс - Показать только мои заявки")
    MagMobCheckBox showOnlyMyTasksChkBox;

    @AppFindBy(containsText = "ПОКАЗАТЬ ЗАЯВКИ", metaName = "Кнопка 'Показать заявки'")
    MagMobGreenSubmitButton applyFiltersBtn;

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isElementVisible(showOnlyMyTasksChkBox, timeout);
    }

    // Actions

    @Step("Выбрать статус заявки")
    public FilterTransferTaskPage selectTaskStatus(TaskStatusModal.Options options) throws Exception {
        taskStatusFld.click();
        TaskStatusModal taskStatusModal = new TaskStatusModal();
        taskStatusModal.selectOptions(options);
        taskStatusModal.clickConfirmBtn();
        waitUntilProgressBarAppearsAndDisappear();
        return new FilterTransferTaskPage();
    }

    @Step("Выберите дату создания заявки")
    public FilterTransferTaskPage selectCreationTaskDate(LocalDate date) throws Exception {
        creationTaskDateFld.click();
        CalendarWidget calendarWidget = new CalendarWidget(driver);
        calendarWidget.selectDate(date);
        return new FilterTransferTaskPage();
    }

    @Step("Применить фильтры и показать заявки")
    public TransferRequestsPage applyFilters() {
        applyFiltersBtn.click();
        return new TransferRequestsPage();
    }

    // Verifications

    @Step("Проверить, что страница 'Фильтр списка заявок' отображается корректно")
    public FilterTransferTaskPage verifyRequiredElements() {
        softAssert.areElementsVisible(header, taskStatusFld, creationTaskDateFld, showOnlyMyTasksChkBox);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в фильтре для 'Статус заявки' выбран {count}")
    public FilterTransferTaskPage shouldTaskStatusFilterCountIs(int count) {
        anAssert.isElementVisible(selectedTaskStatusCount);
        softAssert.isEquals(selectedTaskStatusCount.getText(), "Выбрано " + count,
                "Неверное количество выбранных фильтров для 'Статус заявки'");
        softAssert.isElementVisible(applyFiltersBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в фильтре 'Дата создания заявки' выбрана дата {expectedDate}")
    public FilterTransferTaskPage shouldCreationTaskDateFilterIs(LocalDate expectedDate) {
        anAssert.isElementVisible(selectedCreationTaskDate);
        softAssert.isEquals(DateTimeUtil.strToLocalDate(selectedCreationTaskDate.getText()
                        .replaceAll("До ", ""), "d MMMM yyyy"), expectedDate,
                "Неверное количество выбранных фильтров для 'Статус заявки'");
        softAssert.isElementVisible(applyFiltersBtn, timeout);
        softAssert.verifyAll();
        return this;
    }


    /// ------- Modal window ----------- //

    public static class TaskStatusModal extends CommonMagMobilePage {

        private String CHK_BOX_XPATH = "//android.view.ViewGroup[*[@text='%s']]/android.view.ViewGroup";

        private MagMobCheckBox draftChkBox = E(By.xpath(String.format(CHK_BOX_XPATH, "Черновик")), MagMobCheckBox.class);
        private MagMobCheckBox sendChkBox = E(By.xpath(String.format(CHK_BOX_XPATH, "Отправлен")), MagMobCheckBox.class);
        private MagMobCheckBox inSelectionChkBox = E(By.xpath(String.format(CHK_BOX_XPATH, "В подборе")), MagMobCheckBox.class);
        private MagMobCheckBox partiallySelectionChkBox = E(By.xpath(String.format(CHK_BOX_XPATH, "Подобр. частично")), MagMobCheckBox.class);
        private MagMobCheckBox finishedChkBox = E(By.xpath(String.format(CHK_BOX_XPATH, "Завершен")), MagMobCheckBox.class);
        private MagMobCheckBox cancelledChkBox = E(By.xpath(String.format(CHK_BOX_XPATH, "Отменен")), MagMobCheckBox.class);

        @AppFindBy(accessibilityId = "Button", metaName = "Кнопка подтверждения")
        Element confirmBtn;

        public void selectOptions(Options... options) throws Exception {
            if (Arrays.asList(options).contains(Options.DRAFT))
                draftChkBox.setValue(true);
            if (Arrays.asList(options).contains(Options.SEND))
                sendChkBox.setValue(true);
            if (Arrays.asList(options).contains(Options.IN_SELECTION))
                inSelectionChkBox.setValue(true);
            if (Arrays.asList(options).contains(Options.PARTIALLY_SELECTION))
                partiallySelectionChkBox.setValue(true);
            if (Arrays.asList(options).contains(Options.FINISHED))
                finishedChkBox.setValue(true);
            if (Arrays.asList(options).contains(Options.CANCELLED))
                cancelledChkBox.setValue(true);
            wait(1); // explicit wait!
        }

        public void clickConfirmBtn() {
            confirmBtn.click();
        }

        public enum Options {
            DRAFT, SEND, IN_SELECTION, PARTIALLY_SELECTION, FINISHED, CANCELLED;
        }

    }

}
