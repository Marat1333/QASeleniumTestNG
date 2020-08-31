package com.leroy.magmobile.ui.pages.work.transfer.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.ShortTransferTaskData;
import com.leroy.utils.DateTimeUtil;
import org.openqa.selenium.WebDriver;

public class TransferRequestWidget extends CardWidget<ShortTransferTaskData> {

    public TransferRequestWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'торг.')]", metaName = "Загаловок заявки")
    Element title;

    @AppFindBy(xpath = ".//android.widget.TextView[2]", metaName = "Название товара #1")
    Element productTitle1;

    @AppFindBy(xpath = ".//android.widget.TextView[3][not(contains(@text, ':'))]", metaName = "Название товара #2")
    Element productTitle2;

    @AppFindBy(xpath = ".//android.widget.TextView[4][contains(@text, '+')]", metaName = "Количество товаров еще")
    Element additionalProductCount;

    @AppFindBy(xpath = ".//android.widget.TextView[last()]", metaName = "Дата создания заявки")
    Element creationDate;

    @AppFindBy(xpath = ".//android.view.ViewGroup//android.widget.TextView", metaName = "Статус заявки")
    Element status;

    @Override
    public ShortTransferTaskData collectDataFromPage(String pageSource) {
        ShortTransferTaskData shortTransferTaskData = new ShortTransferTaskData();
        shortTransferTaskData.setTitle(title.getText(pageSource));
        shortTransferTaskData.setProductTitle1(productTitle1.getText(pageSource));
        shortTransferTaskData.setProductTitle2(productTitle2.getTextIfPresent(pageSource));
        shortTransferTaskData.setAdditionalProductCount(additionalProductCount.getTextIfPresent(pageSource));
        shortTransferTaskData.setCreationDate(DateTimeUtil.strToLocalDateTime(
                creationDate.getText(pageSource), DateTimeUtil.DD_MMM_HH_MM));
        shortTransferTaskData.setStatus(status.getText(pageSource));
        return shortTransferTaskData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return title.isVisible(pageSource) && status.isVisible(pageSource);
    }
}
