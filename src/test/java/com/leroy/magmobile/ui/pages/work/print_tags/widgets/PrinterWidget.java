package com.leroy.magmobile.ui.pages.work.print_tags.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.work.print_tags.data.PrinterData;
import org.openqa.selenium.WebDriver;

public class PrinterWidget extends CardWidget<PrinterData> {
    @AppFindBy(xpath = ".//android.widget.TextView[1]")
    Element printerNameLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[2]")
    Element printerDescriptionLbl;

    public PrinterWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public PrinterData collectDataFromPage(String pageSource) {
        PrinterData data = new PrinterData();
        data.setPrinterName(printerNameLbl.getText(pageSource));
        data.setDescription(printerDescriptionLbl.getText(pageSource));
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return printerNameLbl.isVisible(pageSource) && printerDescriptionLbl.isVisible(pageSource);
    }
}
