package com.leroy.magmobile.ui.pages.work.ruptures;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.elements.RuptureTaskContainer;
import io.qameta.allure.Step;

import java.util.List;

public class RuptureCard extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

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

    @AppFindBy(text = "Подробнее о товаре")
    Button productCardNavigationBtn;

    @AppFindBy(text = "ПОДТВЕРДИТЬ")
    Button acceptBtn;

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
        lmCodeLbl.waitForVisibility();
        priceLbl.waitForVisibility();
        acceptBtn.waitForVisibility();
    }

    public List<String> getTasksList() {
        return ruptureTaskContainer.getTaskList();
    }

    public boolean appointTaskBtnVisibility(){
        return ruptureTaskContainer.appointTaskBtnVisibility();
    }

    @Step("Вызвать модальное окно со списком задач перебоя")
    public ActionsModalPage callActionModalPage() throws Exception{
        return ruptureTaskContainer.callActionsModalPage();
    }

    @Step("Выбрать кол-во товара на полке")
    public RuptureCard choseProductQuantityOption(QuantityOption option) {
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

    @Step("Проверить что список задач изменился")
    public RuptureCard shouldTasksHasChanged(List<String> tasksBefore, boolean appointTaskVisibilityBefore) {
        mainScrollView.scrollToBeginning();
        List<String> taskAfter;
        if (tasksBefore.size() == 0 && appointTaskVisibilityBefore) {
            return this;
        }else if (tasksBefore.size()>0){
            taskAfter = ruptureTaskContainer.getTaskList();
            anAssert.isFalse(tasksBefore.equals(taskAfter), "nothing has changed");
        }
        return this;
    }

    public RuptureCard verifyRequiredElements() {
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
}
