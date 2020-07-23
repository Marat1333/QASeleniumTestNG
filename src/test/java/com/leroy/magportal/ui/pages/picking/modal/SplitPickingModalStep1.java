package com.leroy.magportal.ui.pages.picking.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import io.qameta.allure.Step;

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

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__WHZones')]//button",
            metaName = "Опция 'Собрать из торгового зала (LS)'")
    PuzCheckBox collectFromShoppingRoomChkBox;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__footer')]//button[contains(@class, 'iconButton')]",
            metaName = "Кнопка редактирования (карандаш)")
    Button editBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__footer')]//button[descendant::span[text()='Продолжить']]",
            metaName = "Кнопка Продолжить")
    Button continueBtn;

    // Таблица с товарами:

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[span[contains(@class, 'LmCode__accent')]]/span[2]")
    ElementList<Element> lmCodes;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'SplitModal__product__container')]//p")
    ElementList<Element> titles;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'icking-SplitModal__product')]/div[2]//span")
    ElementList<Element> originalAssemblyQuantities;

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

    @Step("Выбрать опцию 'Собрать из торгового зала (LS)'")
    public SplitPickingModalStep1 selectCollectFromShoppingRoomOption(boolean value) throws Exception {
        collectFromShoppingRoomChkBox.setValue(value);
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
        anAssert.isEquals(titles.getCount(), expectedProducts.size(), "Обнаружено другое кол-во товаров (названия)");
        anAssert.isEquals(lmCodes.getCount(), expectedProducts.size(), "Обнаружено другое кол-во товаров (ЛМ Коды)");
        anAssert.isEquals(originalAssemblyQuantities.getCount(), expectedProducts.size(),
                "Обнаружено другое кол-во товаров (Цифра Исходная сборка)");
        for (int i = 0; i < expectedProducts.size(); i++) {
            softAssert.isEquals(titles.get(i).getText(), expectedProducts.get(i).getTitle(),
                    "Товар #" + (i + 1) + " - неверное название");
            softAssert.isEquals(lmCodes.get(i).getText(), expectedProducts.get(i).getLmCode(),
                    "Товар #" + (i + 1) + " - неверный ЛМ код");
            softAssert.isEquals(originalAssemblyQuantities.get(i).getText(), "1", // От чего это число зависит?
                    "Товар #" + (i + 1) + " - неверное кол-во исходной сборки");
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

}
