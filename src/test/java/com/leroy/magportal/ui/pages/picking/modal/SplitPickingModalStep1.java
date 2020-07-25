package com.leroy.magportal.ui.pages.picking.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import lombok.Data;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class SplitPickingModalStep1 extends SplitPickingModal {

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__zones')]//button[1]",
            metaName = "Опция 'Торговый зал'")
    PuzCheckBox shoppingRoomRadioBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__zones')]//button[2]",
            metaName = "Опция 'Склад'")
    PuzCheckBox stockRadioBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__zones')]//button[3]",
            metaName = "Опция 'СС'")
    PuzCheckBox ssRadioBtn;

    // Собрать из торгового зала (LS)
    // Нужно вручную создать Заказ в RMS под заказ Клиента
    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__WHZones')]//button",
            metaName = "Чек-бокс для подтверждения разделения сборки")
    PuzCheckBox confirmCheckBox;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__footer')]//button[contains(@class, 'iconButton')]",
            metaName = "Кнопка редактирования (карандаш)")
    Button editBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__footer')]//button[descendant::span[text()='Продолжить']]",
            metaName = "Кнопка Продолжить")
    Button continueBtn;

    // Таблица с товарами:

    @WebFindBy(xpath = MODAL_DIV_XPATH + "", clazz = PickingSplitModalProductWidget.class)
    CardWebWidgetList<PickingSplitModalProductWidget, SplitProductCardData> productWidgets;

    // Actions

    @Step("Выбераем зону откуда будет совершаться сборка")
    public SplitPickingModalStep1 selectAssemblyType(PickingConst.AssemblyType type) throws Exception {
        switch (type) {
            case SHOPPING_ROOM:
                shoppingRoomRadioBtn.setValue(true);
                break;
            case STOCK:
                stockRadioBtn.setValue(true);
                break;
            case SS:
                ssRadioBtn.setValue(true);
                break;
        }
        return this;
    }

    @Step("Выбрать опцию подтверждения разделения сборки")
    public SplitPickingModalStep1 selectConfirmCheckBox(boolean value) throws Exception {
        confirmCheckBox.setValue(value);
        return this;
    }

    @Step("Нажать кнопку 'Продолжить'")
    public SplitPickingModalStep2 clickContinueButton() {
        continueBtn.click();
        return new SplitPickingModalStep2();
    }

    // Verifications
    @Step("Проверить, что модальное окно 'Разделить сборку (Шаг 1)' отображается корректно")
    public SplitPickingModalStep1 verifyRequiredElements() {
        softAssert.areElementsVisible(shoppingRoomRadioBtn, stockRadioBtn, ssRadioBtn, header, editBtn, continueBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить информацию о товарах, которая отображается в окне")
    public SplitPickingModalStep1 shouldContainsProducts(List<PickingProductCardData> expectedProducts) throws Exception {
        List<SplitProductCardData> splitProductCardDataList = productWidgets.getDataList();
        anAssert.isEquals(splitProductCardDataList.size(), expectedProducts.size(), "Обнаружено другое кол-во товаров");
        boolean somethingSelected = shoppingRoomRadioBtn.isChecked() || ssRadioBtn.isChecked() || stockRadioBtn.isChecked();
        for (int i = 0; i < expectedProducts.size(); i++) {
            softAssert.isEquals(splitProductCardDataList.get(i).getTitle(), expectedProducts.get(i).getTitle(),
                    "Товар #" + (i + 1) + " - неверное название");
            softAssert.isEquals(splitProductCardDataList.get(i).getLmCode(), expectedProducts.get(i).getLmCode(),
                    "Товар #" + (i + 1) + " - неверный ЛМ код");
            softAssert.isEquals(splitProductCardDataList.get(i).getOriginalAssemblyQuantity(),
                    expectedProducts.get(i).getOrderedQuantity(),
                    "Товар #" + (i + 1) + " - неверное кол-во исходной сборки");
            if (somethingSelected) {
                softAssert.isEquals(splitProductCardDataList.get(i).getWantToMoveQuantity(),
                        expectedProducts.get(i).getOrderedQuantity(), // Что это за значение?
                        "Товар #" + (i + 1) + " - неверное кол-во 'хочу перенести'");
                softAssert.isEquals(splitProductCardDataList.get(i).getMoveToNewQuantity(),
                        expectedProducts.get(i).getOrderedQuantity(), // Что это за значение?
                        "Товар #" + (i + 1) + " - неверное кол-во 'перейдет в новую'");
                softAssert.isEquals(splitProductCardDataList.get(i).getRemainInOriginalQuantity(),
                        0, // Что это за значение?
                        "Товар #" + (i + 1) + " - неверное кол-во 'останется в исходной'");
            } else {
                softAssert.isTrue(splitProductCardDataList.get(i).getWantToMoveQuantity() == null,
                        "'хочу перенести' - отображается");
                softAssert.isTrue(splitProductCardDataList.get(i).getMoveToNewQuantity() == null,
                        "'перейдет в новую' - отображается");
                softAssert.isTrue(splitProductCardDataList.get(i).getRemainInOriginalQuantity() == null,
                        "'останется в исходной' - отображается");
            }

        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что кнопка Продолжить активна")
    public SplitPickingModalStep1 shouldContinueButtonIsEnabled() {
        anAssert.isTrue(continueBtn.isEnabled(), "Кнопка 'Продолжить' не активна");
        return this;
    }

    @Step("Проверить, что кнопка Продолжить неактивна")
    public SplitPickingModalStep1 shouldContinueButtonIsDisabled() {
        anAssert.isFalse(continueBtn.isEnabled(), "Кнопка 'Продолжить' активна");
        return this;
    }

    // ------------ Widgets ----------------

    @Data
    private static class SplitProductCardData {
        private String lmCode;
        private String title;
        private Integer originalAssemblyQuantity;
        private Integer wantToMoveQuantity;
        private Integer moveToNewQuantity;
        private Integer remainInOriginalQuantity;
    }

    private static class PickingSplitModalProductWidget extends CardWebWidget<SplitProductCardData> {

        public PickingSplitModalProductWidget(WebDriver driver, CustomLocator locator) {
            super(driver, locator);
        }

        @WebFindBy(xpath = ".//div[span[contains(@class, 'LmCode__accent')]]/span[2]")
        Element lmCode;

        @WebFindBy(xpath = ".//div[contains(@class, 'SplitModal__product__container')]//p")
        Element title;

        @WebFindBy(xpath = ".//div[contains(@class, 'icking-SplitModal__product')]/div[2]//span")
        Element originalAssemblyQuantity;

        @WebFindBy(xpath = ".//div[contains(@class, 'icking-SplitModal__product')]/div[3]//span")
        Element wantToMoveQuantity;

        @WebFindBy(xpath = ".//div[contains(@class, 'icking-SplitModal__product')]/div[3]/div[3]/div[1]")
        Element moveToNewQuantity;

        @WebFindBy(xpath = ".//div[contains(@class, 'icking-SplitModal__product')]/div[3]/div[3]/div[2]")
        Element remainInOriginalQuantity;

        @Override
        public SplitProductCardData collectDataFromPage() throws Exception {
            SplitProductCardData splitProductCardData = new SplitProductCardData();
            splitProductCardData.setLmCode(lmCode.getText());
            splitProductCardData.setTitle(title.getText());
            splitProductCardData.setOriginalAssemblyQuantity(ParserUtil.strToInt(originalAssemblyQuantity.getText()));
            splitProductCardData.setWantToMoveQuantity(ParserUtil.strToInt(wantToMoveQuantity.getTextIfPresent()));
            splitProductCardData.setMoveToNewQuantity(ParserUtil.strToInt(moveToNewQuantity.getTextIfPresent()));
            splitProductCardData.setRemainInOriginalQuantity(ParserUtil.strToInt(remainInOriginalQuantity.getTextIfPresent()));
            return splitProductCardData;
        }
    }

}
