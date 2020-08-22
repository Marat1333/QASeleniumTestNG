package com.leroy.magmobile.ui.pages.work.transfer.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

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
    private Element selectedQuantity;

    // Рядом с количеством величина, например "шт."
    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='priceUnit']")
    private Element unit;

    @Override
    public TransferProductData collectDataFromPage(String pageSource) {
        TransferProductData transferProductData = new TransferProductData();
        transferProductData.setLmCode(lmCode.getText(pageSource));
        transferProductData.setBarCode(barCode.getText(pageSource));
        transferProductData.setTitle(title.getText(pageSource));
        //transferProductData.setAvailableStock(ParserUtil.strToInt(availableStock.getText(pageSource)));
        transferProductData.setSelectedQuantity(ParserUtil.strToInt(selectedQuantity.getText(pageSource)));
        return transferProductData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible(pageSource) && selectedQuantity.isVisible(pageSource);
    }
}
