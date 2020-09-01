package com.leroy.magmobile.ui.pages.work.transfer.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TransferTaskProductWidget extends CardWidget<TransferProductData> {

    public TransferTaskProductWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='lmCode']/android.widget.TextView")
    private Element lmCode;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='barCode']/android.widget.TextView")
    private Element barCode;

    @AppFindBy(xpath = ".//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView")
    private Element title;

    // Заказано
    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='presenceValue'][last()]")
    private Element orderedQuantity;

    // Рядом с количеством величина, например "шт."
    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='priceUnit']")
    private Element unit;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='priceUnit']/following-sibling::android.widget.TextView[1]",
            metaName = "Цена за единицу товара")
    private Element price;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='priceUnit']/following-sibling::android.widget.TextView[2]",
            metaName = "Стоимость товара")
    private Element totalPrice;

    // Запасы

    @AppFindBy(xpath = "//android.view.ViewGroup[ android.widget.TextView[@text='—']]/android.widget.TextView[1]")
    Element pieceQuantity;

    @AppFindBy(xpath = "//android.view.ViewGroup[ android.widget.TextView[@text='—']]/android.widget.TextView[2]")
    Element monoPalletQuantity;

    @AppFindBy(xpath = "//android.view.ViewGroup[ android.widget.TextView[@text='—']]/android.widget.TextView[3]")
    Element mixPalletQuantity;

    @Override
    public TransferProductData collectDataFromPage(String pageSource) {
        TransferProductData transferProductData = new TransferProductData();
        transferProductData.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText(pageSource)));
        String barCodeValue = barCode.getTextIfPresent(pageSource);
        Assert.assertNotNull(barCodeValue, "У товара отсутствует бар код");
        transferProductData.setBarCode(ParserUtil.strWithOnlyDigits(barCodeValue));
        transferProductData.setTitle(title.getText(pageSource));
        transferProductData.setOrderedQuantity(ParserUtil.strToInt(orderedQuantity.getText(pageSource)));
        transferProductData.setPrice(ParserUtil.strToDouble(price.getTextIfPresent(pageSource)));
        transferProductData.setTotalPrice(ParserUtil.strToDouble(totalPrice.getTextIfPresent(pageSource)));
        transferProductData.setSelectedPieceQuantity(ParserUtil.strToInt(pieceQuantity.getText(pageSource)));
        transferProductData.setSelectedMonoPalletQuantity(ParserUtil.strToInt(monoPalletQuantity.getText(pageSource)));
        transferProductData.setSelectedMixPalletQuantity(ParserUtil.strToInt(mixPalletQuantity.getText(pageSource)));
        return transferProductData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible(pageSource) && pieceQuantity.isVisible(pageSource);
    }
}
