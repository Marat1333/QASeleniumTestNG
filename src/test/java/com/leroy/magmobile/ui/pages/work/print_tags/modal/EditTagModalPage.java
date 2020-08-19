package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.ui.elements.MagMobGreenCheckBox;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.enums.Format;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class EditTagModalPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

    @AppFindBy(text = "Форматы и кол-во ценников")
    Element header;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc=\"lmCode\"]")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc=\"barCode\"]")
    Element barCode;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::*[1]")
    Element title;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::*[2]")
    Element price;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::android.widget.TextView[3]")
    Element dateOfPriceChangeLbl;

    @AppFindBy(xpath = "//*[@text='4 × 6 см']/../following-sibling::android.widget.EditText")
    EditBox smallSizeEditBox;

    @AppFindBy(xpath = "//*[@text='6 × 10 см']/../following-sibling::android.widget.EditText")
    EditBox middleSizeEditBox;

    @AppFindBy(xpath = "//*[@text='10 × 18 см']/../following-sibling::android.widget.EditText")
    EditBox bigSizeEditBox;

    @AppFindBy(xpath = "//*[@text='4 × 6 см']/preceding-sibling::*[1]")
    MagMobGreenCheckBox smallSizeCheckBox;

    @AppFindBy(xpath = "//*[@text='6 × 10 см']/preceding-sibling::*[1]")
    MagMobGreenCheckBox middleSizeCheckBox;

    @AppFindBy(xpath = "//*[@text='10 × 18 см']/preceding-sibling::*[1]")
    MagMobGreenCheckBox bigSizeCheckBox;

    @AppFindBy(xpath = "//*[contains(@text,'6 см')]/../../following-sibling::*[1]//*[contains(@text,'Ценников может быть от 1 до 40.')]")
    Element smallSizeControlLbl;

    @AppFindBy(xpath = "//*[contains(@text,'10 см')]/../../following-sibling::*[1]//*[contains(@text,'Ценников может быть от 1 до 40.')]")
    Element middleSizeControlLbl;

    @AppFindBy(xpath = "//*[contains(@text,'18 см')]/../../following-sibling::*[1]//*[contains(@text,'Ценников может быть от 1 до 40.')]")
    Element bigSizeControlLbl;

    @AppFindBy(accessibilityId = "deleteProduct")
    Button deleteProductBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button-container\"][last()]")
    Button addProductBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        addProductBtn.waitForVisibility();
    }

    @Step("Установить размеры и кол-во")
    public EditTagModalPage setSizesAndQuantity(int smallSizeCount, int middleSizeCount, int bigSizeCount) {
        if (Integer.parseInt(smallSizeEditBox.getText()) != smallSizeCount) {
            if (smallSizeCount > 0) {
                smallSizeEditBox.clearAndFill(String.valueOf(smallSizeCount));
            } else {
                smallSizeEditBox.clearAndFill("0");
            }
            hideKeyboard();
        }

        if (Integer.parseInt(middleSizeEditBox.getText()) != middleSizeCount) {
            if (middleSizeCount > 0) {
                middleSizeEditBox.clearAndFill(String.valueOf(middleSizeCount));
            } else {
                middleSizeEditBox.clearAndFill("0");
            }
            hideKeyboard();
        }

        if (Integer.parseInt(bigSizeEditBox.getText()) != bigSizeCount) {
            if (bigSizeCount > 0) {
                bigSizeEditBox.clearAndFill(String.valueOf(bigSizeCount));
            } else {
                bigSizeEditBox.clearAndFill("0");
            }
            hideKeyboard();
        }
        return this;
    }

    @Step("Установить размеры и кол-во")
    public EditTagModalPage setSizesAndQuantity(ProductTagData productTagData) {
        return setSizesAndQuantity(productTagData.getSmallSizeCount(), productTagData.getMiddleSizeCount(), productTagData.getBigSizeCount());
    }

    @Step("Нажать на кнопку подтвердить")
    public void confirm() {
        addProductBtn.click();
    }

    @Step("Добавить товар в сессию печати ценников")
    public ProductTagData addProductToPrintSession() {
        return addProductToPrintSession(3, 0, 0);
    }

    @Step("Добавить товар в сессию печати ценников")
    public ProductTagData addProductToPrintSession(int smallCount, int midCount, int bigCount) {
        ProductTagData data = new ProductTagData(ParserUtil.strWithOnlyDigits(lmCode.getText()), smallCount, midCount, bigCount);
        setSizesAndQuantity(smallCount, midCount, bigCount);
        confirm();
        return data;
    }

    @Step("Добавить товар в сессию печати ценников")
    public ProductTagData addProductToPrintSession(ProductTagData data) {
        data.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText()));
        setSizesAndQuantity(data.getSmallSizeCount(), data.getMiddleSizeCount(), data.getBigSizeCount());
        confirm();
        return data;
    }

    @Step("Удалить товар из сессии")
    public void deleteProductFromSession() {
        deleteProductBtn.click();
        deleteProductBtn.waitForInvisibility();
    }

    @Step("Закрыть модалку")
    public void closeModal() {
        closeModalBtn.click();
        closeModalBtn.waitForInvisibility();
    }

    @Step("нажать на чек-боксы")
    public EditTagModalPage selectCheckBoxes(Format... formats) {
        for (Format each : formats) {
            selectCheckBox(each);
        }
        return this;
    }

    @Step("Нажать на чек-бокс с размером")
    public EditTagModalPage selectCheckBox(Format format) {
        String pageSource = getPageSource();
        switch (format) {
            case SMALL:
                smallSizeCheckBox.click();
                break;
            case MIDDLE:
                middleSizeCheckBox.click();
                break;
            case BIG:
                bigSizeCheckBox.click();
                break;
            case ALL:
                throw new IllegalArgumentException();
        }
        waitUntilContentIsChanged(pageSource);
        return this;
    }

    @Step("Проверить данные товара")
    public EditTagModalPage shouldProductDataIsCorrect(ProductItemData data){
        softAssert.isElementTextEqual(lmCode, "ЛМ "+data.getLmCode());
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(barCode.getText()), data.getBarCode(), "barCode");
        softAssert.isElementTextEqual(title, data.getTitle());
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(price.getText()),
                ParserUtil.doubleToStr(data.getPrice(), 2, false), "price");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что для товары корректно предвыбраны значения")
    public EditTagModalPage shouldSizeValuesAreCorrect(int smallSize, int middleSize, int bigSize) {
        softAssert.isEquals(Integer.parseInt(smallSizeEditBox.getText()), smallSize, "small size");
        softAssert.isEquals(Integer.parseInt(middleSizeEditBox.getText()), middleSize, "middle size");
        softAssert.isEquals(Integer.parseInt(bigSizeEditBox.getText()), bigSize, "big size");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что для товары корректно предвыбраны значения")
    public EditTagModalPage shouldSizeValuesAreCorrect(ProductTagData data) {
        softAssert.isEquals(Integer.parseInt(smallSizeEditBox.getText()), data.getSmallSizeCount(), "small size");
        softAssert.isEquals(Integer.parseInt(middleSizeEditBox.getText()), data.getMiddleSizeCount(), "middle size");
        softAssert.isEquals(Integer.parseInt(bigSizeEditBox.getText()), data.getBigSizeCount(), "big size");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что кнопка удаления товара присутствует")
    public EditTagModalPage shouldDeleteBtnHasCorrectCondition(boolean isVisible) {
        if (isVisible) {
            anAssert.isElementVisible(deleteProductBtn);
        } else {
            anAssert.isElementNotVisible(deleteProductBtn);
        }
        return this;
    }

    @Step("Проверить, что контроль на кол-во ценников сработал")
    public EditTagModalPage shouldWrongCountControlIsVisible(boolean smallSizeControlVisibility,
                                                             boolean middleSizeControlVisibility,
                                                             boolean bigSizeControlVisibility) {
        if (smallSizeControlVisibility)
            anAssert.isElementVisible(smallSizeControlLbl);
        if (middleSizeControlVisibility)
            anAssert.isElementVisible(middleSizeControlLbl);
        if (bigSizeControlVisibility)
            anAssert.isElementVisible(bigSizeControlLbl);
        return this;
    }

    @Step("Проверить состояния чек-боксов размеров")
    public EditTagModalPage shouldCheckBoxesHasCorrectCondition(boolean smallSizeEnabled,
                                                                boolean middleSizeEnabled,
                                                                boolean bigSizeEnabled) throws Exception {

        if (smallSizeEnabled)
            anAssert.isTrue(smallSizeCheckBox.isChecked(), "small size checkbox unchecked");
        if (middleSizeEnabled)
            anAssert.isTrue(middleSizeCheckBox.isChecked(), "middle size checkbox unchecked");
        if (bigSizeEnabled)
            anAssert.isTrue(bigSizeCheckBox.isChecked(), "big size checkbox unchecked");
        return this;
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(lmCode, barCode, header, addProductBtn, deleteProductBtn);
        softAssert.verifyAll();
    }
}
