package com.leroy.magmobile.ui.pages.work.transfer.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TransferSearchProductCardWidget extends CardWidget<TransferProductData> {

    public TransferSearchProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='lmCode']/android.widget.TextView")
    private Element lmCode;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='barCode']/android.widget.TextView")
    private Element barCode;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView")
    private Element title;

    // Количество
    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='presenceValue'][last()]")
    private Element availableStock;

    // Рядом с количеством величина, например "шт."
    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='priceUnit']")
    private Element unit;

    // Состав отзыва
    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='—'] and descendant::android.widget.EditText]/android.widget.TextView[1]")
    Element pieceQuantity;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='—'] and descendant::android.widget.EditText]/android.widget.TextView[2]")
    Element monoPalletQuantity;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='—'] and descendant::android.widget.EditText]/android.widget.TextView[3]")
    Element mixPalletQuantity;

    @AppFindBy(xpath = ".//android.widget.EditText", metaName = "Поле для редактирования количества")
    EditBox editQuantityFld;

    @AppFindBy(containsText = "Ты не попал в кратность палет")
    Element wrongQuantityErrorTooltip;


    public void editProductQuantity(int value) {
        editQuantityFld.clearFillAndSubmit(String.valueOf(value));
    }

    public boolean isWrongQuantityErrorTooltipVisible() {
        return wrongQuantityErrorTooltip.isVisible();
    }

    @Override
    public TransferProductData collectDataFromPage(String pageSource) {
        TransferProductData transferProductData = new TransferProductData();
        transferProductData.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText(pageSource)));
        String barCodeValue = barCode.getTextIfPresent(pageSource);
        Assert.assertNotNull(barCodeValue, "У товара отсутствует бар код");
        transferProductData.setBarCode(ParserUtil.strWithOnlyDigits(barCodeValue));
        transferProductData.setTitle(title.getText(pageSource));
        transferProductData.setTotalStock(ParserUtil.strToInt(availableStock.getText(pageSource)));
        String singleQuantity = pieceQuantity.getText();
        String monoQuantity = monoPalletQuantity.getText();
        String mixQuantity = mixPalletQuantity.getText();
        transferProductData.setSelectedPieceQuantity(singleQuantity.equals("—") ? 0 : ParserUtil.strToInt(singleQuantity));
        transferProductData.setSelectedMonoPalletQuantity(monoQuantity.equals("—") ? 0 : ParserUtil.strToInt(monoQuantity));
        transferProductData.setSelectedMixPalletQuantity(mixQuantity.equals("—") ? 0 : ParserUtil.strToInt(mixQuantity));
        transferProductData.setOrderedQuantity(ParserUtil.strToInt(editQuantityFld.getText(pageSource)));
        return transferProductData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible(pageSource) && editQuantityFld.isVisible(pageSource);
    }


}
