package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobRadioButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.elements.RuptureTaskContainer;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.DeleteRuptureModalPage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuptureCardPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='DefaultScreenHeader']/*[2]")
    Button deleteRuptureBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='lmCode']")
    Element lmCodeLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='barCode']")
    Element barCodeLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView")
    Element titleLbl;

    @AppFindBy(xpath = "//android.widget.ImageView")
    Element productPhoto;

    @AppFindBy(xpath = "//*[contains(@text,'Цена')]/../preceding-sibling::*[1]", clazz = RuptureTaskContainer.class)
    RuptureTaskContainer ruptureTaskContainer;

    @AppFindBy(xpath = "//*[contains(@text,'Цена')]/following-sibling::*[1]")
    Element priceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Торговый зал']/following-sibling::*[@content-desc='presenceValue']")
    Element salesHallProductQuantityLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Торговый зал']/following-sibling::*[@content-desc='priceUnit']")
    Element salesHallProductUnitLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Склад RM']/following-sibling::*[@content-desc='presenceValue']")
    Element rmWarehouseProductQuantityLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Склад RM']/following-sibling::*[@content-desc='priceUnit']")
    Element rmWarehouseProductUnitLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Поставка']/following-sibling::*[1]")
    Element supplyDateLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='Button-text' and @text='0 шт.']")
    Button zeroProductNeedToAddBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='Button-text' and @text='1']")
    Button oneProductNeedToAddBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='Button-text' and @text='2']")
    Button twoProductsNeedToAddBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='Button-text' and @text='3+']")
    Button threeOrMoreProductsNeedToAddBtn;

    @AppFindBy(accessibilityId = "comment")
    EditBox commentField;

    @AppFindBy(xpath = "//*[@content-desc=\"comment\"]/ancestor::android.view.ViewGroup[2]/following-sibling::android.view.ViewGroup")
    MagMobButton submitCommentBtn;

    @AppFindBy(text = "Подробнее о товаре")
    Button productCardNavigationBtn;

    @AppFindBy(text = "ПОДТВЕРДИТЬ")
    Button acceptBtn;

    @AppFindBy(text = "ДЕЙСТВИЯ С ПЕРЕБОЕМ")
    Button ruptureActionsBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@text='ПОДТВЕРДИТЬ']/ancestor::*[@content-desc='Button-container']/preceding-sibling::android.view.ViewGroup[1]")
    Button ruptureCallActionModalBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView")
    AndroidScrollView<String> mainScrollView;

    public enum QuantityOption {
        ZERO,
        ONE,
        TWO,
        THREE_OR_MORE
    }

    @Override
    protected void waitForPageIsLoaded() {
        //долгий запрос на бэк
        priceLbl.waitForVisibility(long_timeout);
        zeroProductNeedToAddBtn.waitForVisibility();
    }

    public List<String> getTasksList() {
        return ruptureTaskContainer.getTaskList();
    }

    public RuptureData getRuptureData() throws Exception {
        RuptureData data = new RuptureData();
        if (!lmCodeLbl.isVisible()) {
            mainScrollView.scrollToBeginning();
        }
        String ps = getPageSource();
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCodeLbl.getText(ps)));
        data.setBarCode(ParserUtil.strWithOnlyDigits(barCodeLbl.getText(ps)));
        data.setTitle(titleLbl.getText(ps));
        Map<String, Boolean> actionsMap = new HashMap<>();
        List<String> actions = getTasksList();
        for (String each : actions) {
            actionsMap.put(each, ruptureTaskContainer.getCheckBoxCondition(each));
        }
        data.setActions(actionsMap);
        return data;
    }

    @Step("Закрыть карточку перебоя")
    public void closeRuptureCardPage(){
        closeModalBtn.click();
        closeModalBtn.waitForInvisibility();
    }

    @Step("Подтвердить добавление перебоя")
    public RupturesScannerPage acceptAdd() {
        acceptBtn.click();
        return new RupturesScannerPage();
    }

    @Step("Перейти на страницу карточки товара")
    public ProductCardPage navigateToProductCard() {
        productCardNavigationBtn.click();
        return new ProductCardPage();
    }

    @Step("Ввести комментарий")
    public RuptureCardPage setComment(String comment) {
        mainScrollView.scrollToEnd();
        commentField.clearAndFill(comment);
        return this;
    }

    @Step("Подтвердить ввод комментария")
    public RuptureCardPage submitComment() {
        submitCommentBtn.click();
        return this;
    }

    @Step("Нажать на чек-боксы задач")
    public RuptureCardPage setTasksCheckBoxes(String... tasksNames) {
        for (String each : tasksNames) {
            ruptureTaskContainer.setCheckBoxesToTasks(each);
        }
        return this;
    }

    @Step("Вызвать модальное окно со списком задач перебоя")
    public ActionsModalPage callActionModalPage() throws Exception {
        return ruptureTaskContainer.callActionsModalPage();
    }

    @Step("Выбрать кол-во товара на полке")
    public RuptureCardPage choseProductQuantityOption(QuantityOption option) throws Exception {
        if (!supplyDateLbl.isVisible()) {
            mainScrollView.scrollDownToElement(supplyDateLbl);
        }
        String ps = getPageSource();
        switch (option) {
            case ZERO:
                zeroProductNeedToAddBtn.click();
                break;
            case ONE:
                oneProductNeedToAddBtn.click();
                break;
            case TWO:
                twoProductsNeedToAddBtn.click();
                break;
            case THREE_OR_MORE:
                threeOrMoreProductsNeedToAddBtn.click();
                break;
        }
        waitUntilContentIsChanged(ps);
        return this;
    }

    @Step("Удалить перебой")
    public DeleteRuptureModalPage deleteRupture(){
        deleteRuptureBtn.click();
        return new DeleteRuptureModalPage();
    }

    @Step("Проверить, что кнопка подтверждения ввода комментария активна")
    public RuptureCardPage shouldSubmitCommentBtnIsActive() throws Exception {
        anAssert.isTrue(submitCommentBtn.isActive(), "inactive");
        return this;
    }

    @Step("Проверить, что комментарий заполнен")
    public RuptureCardPage shouldCommentFieldHasText(String comment) {
        mainScrollView.scrollToEnd();
        anAssert.isElementTextEqual(commentField, comment);
        return this;
    }

    @Step("Проверить, что радио-баттон установлен в правильное положение")
    public RuptureCardPage shouldRadioBtnHasCorrectCondition(QuantityOption option) throws Exception {
        MagMobRadioButton magMobRadioButton = null;
        switch (option) {
            case ZERO:
                magMobRadioButton = E(zeroProductNeedToAddBtn.getXpath() + "/ancestor::*[@content-desc='Button-container']",
                        MagMobRadioButton.class);
                break;
            case ONE:
                magMobRadioButton = E(oneProductNeedToAddBtn.getXpath() + "/ancestor::*[@content-desc='Button-container']",
                        MagMobRadioButton.class);
                break;
            case TWO:
                magMobRadioButton = E(twoProductsNeedToAddBtn.getXpath() + "/ancestor::*[@content-desc='Button-container']",
                        MagMobRadioButton.class);
                break;
            case THREE_OR_MORE:
                magMobRadioButton = E(threeOrMoreProductsNeedToAddBtn.getXpath() + "/ancestor::*[@content-desc='Button-container']",
                        MagMobRadioButton.class);
                break;
        }
        anAssert.isTrue(magMobRadioButton.isChecked(), "unchecked");
        return this;
    }

    @Step("Проверить, что состояние чек-бокса корректное")
    public RuptureCardPage shouldCheckBoxConditionIsCorrect(boolean isEnabled, String taskName) throws Exception {
        boolean checkBoxCondition = ruptureTaskContainer.getCheckBoxCondition(taskName);
        if (isEnabled) {
            anAssert.isTrue(checkBoxCondition, "чекбокс в состоянии disabled");
        } else {
            anAssert.isFalse(checkBoxCondition, "чекбокс в состоянии enabled");
        }
        return this;
    }

    @Step("Проверить что список задач изменился")
    public RuptureCardPage shouldTasksHasChanged(List<String> tasksBefore) {
        mainScrollView.scrollToBeginning();
        List<String> taskAfter;
        if (tasksBefore.size() == 0) {
            return this;
        } else {
            taskAfter = ruptureTaskContainer.getTaskList();
            anAssert.isFalse(tasksBefore.equals(taskAfter), "nothing has changed");
        }
        return this;
    }

    @Step("Проверить, что список задач содержит переданные задачи")
    public RuptureCardPage shouldTasksListContainsTasks(String... tasks) {
        List<String> uiTasksList = ruptureTaskContainer.getTaskList();
        for (String task : tasks) {
            softAssert.isTrue(uiTasksList.contains(task), "список не содержит задачу" + task);
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что данные отображены корректно")
    public RuptureCardPage shouldRuptureDataIsCorrect(RuptureData data) throws Exception{
        RuptureData currentData = getRuptureData();
        anAssert.isEquals(currentData, data, "data mismatch");
        return this;
    }

    public RuptureCardPage verifyRequiredElementsWhenCreateRupture() {
        softAssert.areElementsVisible(getPageSource(), closeModalBtn, lmCodeLbl, barCodeLbl, titleLbl, productPhoto,
                ruptureTaskContainer, priceLbl);
        mainScrollView.scrollToEnd();
        softAssert.areElementsVisible(getPageSource(), productCardNavigationBtn, salesHallProductQuantityLbl, zeroProductNeedToAddBtn,
                oneProductNeedToAddBtn, twoProductsNeedToAddBtn, threeOrMoreProductsNeedToAddBtn,
                rmWarehouseProductQuantityLbl, supplyDateLbl, acceptBtn, ruptureCallActionModalBtn, commentField);
        mainScrollView.scrollToBeginning();
        softAssert.verifyAll();
        return this;
    }

    public RuptureCardPage verifyRequiredElements() {
        softAssert.areElementsVisible(getPageSource(), closeModalBtn, lmCodeLbl, barCodeLbl, titleLbl, productPhoto,
                ruptureTaskContainer, priceLbl, deleteRuptureBtn, ruptureActionsBtn);
        softAssert.verifyAll();
        return this;
    }
}
