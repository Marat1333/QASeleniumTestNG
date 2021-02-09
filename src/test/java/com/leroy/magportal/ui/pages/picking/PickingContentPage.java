package com.leroy.magportal.ui.pages.picking;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.models.picking.PickingTaskData;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.magportal.ui.pages.picking.widget.AssemblyProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PickingContentPage extends PickingPage {

    @WebFindBy(xpath = "//button[contains(@class, 'PickingView__tab__checkbox')]", metaName = "Опция 'Выбрать все'")
    PuzCheckBox selectAllChkBox;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard']",
            clazz = AssemblyProductCardWidget.class)
    CardWebWidgetList<AssemblyProductCardWidget, PickingProductCardData> productCards;

    // Bottom area

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-InfoCard') and not(ancestor::div[contains(@class, 'hidden')])]//button", metaName = "Кнопка 'Начать сборку'")
    Button startAssemblyBtn;

    @WebFindBy(xpath = "//button[descendant::span[contains(text(), 'Завершить')] and not(ancestor::div[contains(@class, 'hidden')])]", metaName = "Кнопка 'Завершить'")
    Button finishAssemblyBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-InfoCard') and not(ancestor::div[contains(@class, 'hidden')])]//div[contains(@class, 'popover')]//button",
            metaName = "Кнопка 'Редактировать сборку'")
    Button editAssemblyBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='РАЗДЕЛИТЬ'] and not(ancestor::div[contains(@class, 'hidden')])]", metaName = "Кнопка 'Разделить' в нижней области")
    Button splitAssemblyBtn;

    @Override
    public void waitForPageIsLoaded() {
        productCards.waitUntilAtLeastOneElementIsPresent();
    }

    // Grab information

    @Step("Забрать информацию о Сборке")
    public PickingTaskData getPickingTaskData() throws Exception {
        PickingTaskData pickingTaskData = new PickingTaskData();
        pickingTaskData.setNumber(getNumber());
        pickingTaskData.setAssemblyType(getAssemblyType());
        pickingTaskData.setStatus(getStatus());
        pickingTaskData.setCreationDate(getCreationDate());
        boolean onlySelectedProducts = false;
        if (!onlySelectedProducts)
            pickingTaskData.setProducts(productCards.getDataList());
        else {
            List<PickingProductCardData> productCardDataList = new ArrayList<>();
            for (AssemblyProductCardWidget widget : productCards) {
                if (widget.isSplitChecked())
                    productCardDataList.add(widget.collectDataFromPage());
            }
            pickingTaskData.setProducts(productCardDataList);
        }
        return pickingTaskData;
    }

    // Actions

    @Step("Нажать кнопку редактирования сборки")
    public PickingContentPage clickEditAssemblyButton() {
        editAssemblyBtn.click();
        waitForSpinnerAppearAndDisappear();
        anAssert.isElementVisible(selectAllChkBox);
        anAssert.isElementVisible(splitAssemblyBtn);
        return this;
    }

    @Step("Нажать кнопку 'Начать сборку'")
    public PickingContentPage clickStartAssemblyButton() {
        startAssemblyBtn.click();
        anAssert.isTrue(finishAssemblyBtn.waitForVisibility(3), "Кнопка Завершить не отображается");
        anAssert.isFalse(finishAssemblyBtn.isEnabled(), "Кнопка Завершить активна");
        return this;
    }

    @Step("Нажать кнопку 'Завершить сборку'")
    public PickingContentPage clickFinishAssemblyButton() {
        finishAssemblyBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Указать причину отсутствия для {index}-ого товара")
    public PickingContentPage selectReasonForLackOfProduct(
            int index, ReasonForLackOfProductModal.Reasons reason) throws Exception {
        index--;
        productCards.get(index).clickReason();
        new ReasonForLackOfProductModal().selectReason(reason)
                .clickSave();
        return this;
    }

    @Step("Изменить кол-во сборка для {index}-ого товара")
    public PickingContentPage editCollectQuantity(int index, int val) throws Exception {
        index--;
        productCards.get(index).editCollectQuantity(val);
        return this;
    }

    @Step("Нажать кнопку 'Разделить' для {index}-ого товара")
    public PickingContentPage setSplitForProductCard(int index, boolean val) throws Exception {
        index--;
        productCards.get(index).setSplitOption(val);
        return this;
    }

    @Step("Нажать на 'Выбрать все'")
    public PickingContentPage setSelectAllOption(boolean val) throws Exception {
        selectAllChkBox.setValue(val);
        return this;
    }

    @Step("Нажать кнопку 'Разделить' на нижней панели")
    public SplitPickingModalStep1 clickSplitAssemblyButton() {
        splitAssemblyBtn.click();
        return new SplitPickingModalStep1();
    }

    // Verifications

    @Step("Проверить выбрана ли опция 'Выбрать все'")
    public PickingContentPage checkSelectAllOptionIsSelected(boolean shouldBeSelected) throws Exception {
        anAssert.isFalse(selectAllChkBox.isChecked() ^ shouldBeSelected,
                "Опция 'Выбрать все' " + (shouldBeSelected ? "" : "не") + " должна быть выбрана");
        return this;
    }

    @Step("Проверить, что данные сборки отображаются корректно")
    public PickingContentPage shouldPickingTaskDataIs(PickingTaskData expectedPickingTaskData) throws Exception {
        PickingTaskData actualData = getPickingTaskData();
        actualData.assertEqualsNotNullExpectedFields(expectedPickingTaskData);
        return this;
    }

    @Step("Проверить, что кол-во 'Собрано' у {index}-ого товара равно {value}")
    public PickingContentPage shouldProductCollectedQuantityIs(int index, int value) throws Exception {
        index--;
        anAssert.isEquals(productCards.get(index).getCollectedQuantity(), String.valueOf(value),
                "Неверное кол-во 'собрано' у " + (index + 1) + "-ого товара");
        return this;
    }

    @Step("Проверить, что у {index}-ого товара выбрана причина отсутствия - {reason}")
    public PickingContentPage shouldProductReasonIs(
            int index, ReasonForLackOfProductModal.Reasons reason) throws Exception {
        index--;
        String actualReason = productCards.get(index).getReason();
        anAssert.isEquals(actualReason, reason.getTitle(),
                "Неверная причина отсутствия у " + (index + 1) + "-ого товара");
        return this;
    }

    @Step("Проверить, что счетчик на кнопке Завершить равен ({one}/{all})")
    public PickingContentPage shouldFinishButtonCountIs(int one, int all) throws Exception {
        String text = finishAssemblyBtn.getText();
        String[] actualCount = StringUtils.substringBetween(text, "(", ")").split("/");
        anAssert.isEquals(actualCount.length, 2,
                "На кнопке Завершить не отображается счетчик (или отображается некорректно)");
        softAssert.isEquals(actualCount[0], String.valueOf(one), "Счетчик на кнопке Завершить - собранное кол-во неверен");
        softAssert.isEquals(actualCount[1], String.valueOf(all), "Счетчик на кнопке Завершить - общее кол-ва товара неверен");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, активна ли кнопка завершить")
    public PickingContentPage checkIfFinishButtonIsEnabled(boolean shouldBeEnabled) {
        anAssert.isFalse(finishAssemblyBtn.isEnabled() ^ shouldBeEnabled,
                "Неверное состояние кнопки Завершить. Актуальное значение: isEnabled = " + !shouldBeEnabled);
        return this;
    }

    // Modal window

    public static class ReasonForLackOfProductModal extends MagPortalBasePage {

        public enum Reasons {
            UNAVAILABILITY_OF_PRODUCT("Недоступность товара"),
            DEFECTIVE_PRODUCT("Бракованный товар"),
            EXAMPLE_PRODUCT("Товар на образце");

            private String title;

            Reasons(String val) {
                this.title = val;
            }

            public String getTitle() {
                return title;
            }
        }

        private static final String MODAL_WINDOW_XPATH = "//div[contains(@class, 'Common-ConfirmModal__modal')]";

        @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//div[contains(@class, 'ModalFooter__container')]//button[descendant::span[text()='ОТМЕНА']]",
                metaName = "Кнопка Отмена")
        Button cancelBtn;

        @WebFindBy(xpath = MODAL_WINDOW_XPATH + "//div[contains(@class, 'ModalFooter__container')]//button[descendant::span[text()='СОХРАНИТЬ']]",
                metaName = "Кнопка Сохранить")
        Button saveBtn;

        @Step("Выбрать причину {reason}")
        public ReasonForLackOfProductModal selectReason(Reasons reason) {
            E("//button[contains(@class, 'ReasonsModal__switch')][descendant::span[text()='" + reason.getTitle() + "']]")
                    .click();
            return this;
        }

        @Step("Нажать 'Сохранить'")
        public ReasonForLackOfProductModal clickSave() {
            saveBtn.click();
            E(MODAL_WINDOW_XPATH).waitForInvisibility();
            return this;
        }

    }

}
