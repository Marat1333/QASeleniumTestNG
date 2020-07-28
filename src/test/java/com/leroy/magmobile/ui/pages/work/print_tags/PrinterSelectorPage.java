package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class PrinterSelectorPage extends CommonMagMobilePage {
    @AppFindBy(text = "Выбор принтера")
    Element header;

    @AppFindBy(accessibilityId = "Button-icon")
    Button acceptBtn;

    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(xpath = "//*[@text='ВЫБРАННЫЙ ПРИНТЕР']/following-sibling::android.widget.ScrollView[1]//android.widget.TextView[1]")
    Element chosenPrinterLbl;

    AndroidScrollView<String> printerNamesView = new AndroidScrollView<>(driver, By.xpath(AndroidScrollView.TYPICAL_XPATH +
            "//android.widget.ScrollView/*/*//android.widget.TextView[1]"), true);

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        backBtn.waitForVisibility();
    }

    @Step("Выбрать принтер")
    public TagsListPage chosePrinter(String printerName) {
        Element printerElement = E(printerName);
        printerNamesView.scrollDownToElement(printerElement);
        printerElement.click();
        acceptBtn.waitForVisibility();
        acceptBtn.click();
        return new TagsListPage();
    }

    @Step("Проверить отображение всех принтеров")
    public PrinterSelectorPage shouldAllFiltersIsDisplayed(List<String> dataList) {
        List<String> printerNames = printerNamesView.getFullDataList(dataList.size()+1);
        if (printerNames.size() > 0) {
            //выбранный принтер
            printerNames.remove(0);
        }
        for (int i = 0; i < dataList.size(); i++) {
            softAssert.isEquals(printerNames.get(i), dataList.get(i), "printerName");
        }
        softAssert.verifyAll();
        if (!chosenPrinterLbl.isVisible()) {
            printerNamesView.scrollToBeginning();
        }
        return this;
    }

    @Step("Проверить, что выбранный принтер соответствует критерию")
    public PrinterSelectorPage shouldChosenPrinterIsCorrect(String printerName, boolean strictEquals) {
        if (strictEquals) {
            anAssert.isElementTextEqual(chosenPrinterLbl, printerName);
        } else {
            anAssert.isElementTextContains(chosenPrinterLbl, printerName);
        }
        return this;
    }

}
