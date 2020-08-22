package com.leroy.magmobile.ui.pages.work.transfer.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferRequestData;
import com.leroy.utils.DateTimeUtil;
import org.openqa.selenium.WebDriver;

public class TransferRequestWidget extends CardWidget<TransferRequestData> {

    public TransferRequestWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[@index='1']", metaName = "Загаловок заявки")
    Element title;

    @AppFindBy(xpath = ".//android.widget.TextView[2]", metaName = "Название товара")
    Element productTitle;

    @AppFindBy(xpath = ".//android.widget.TextView[3]", metaName = "Дата создания заявки")
    Element creationDate;

    @AppFindBy(xpath = ".//android.view.ViewGroup//android.widget.TextView", metaName = "Статус заявки")
    Element status;

    @Override
    public TransferRequestData collectDataFromPage(String pageSource) {
        TransferRequestData transferRequestData = new TransferRequestData();
        transferRequestData.setTitle(title.getText(pageSource));
        transferRequestData.setProductTitle(productTitle.getText(pageSource));
        transferRequestData.setCreationDate(DateTimeUtil.strToLocalDateTime(
                creationDate.getText(pageSource), DateTimeUtil.DD_MMM_HH_MM));
        transferRequestData.setStatus(status.getText(pageSource));
        return transferRequestData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return title.isVisible(pageSource) && status.isVisible(pageSource);
    }
}
