package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.product.SalesHistoryData;
import com.leroy.magmobile.ui.pages.more.SearchShopPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SalesHistoryUnitsModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.SalesHistoryWidget;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

import java.util.List;

public class SalesHistoryPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/*[1]//android.widget.TextView")
    Element shopBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/*[2]//android.widget.TextView")
    Element quantityAmountModalCallBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/*/android.view.ViewGroup[12]/..")
    SalesHistoryWidget salesHistoryWidget;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text,'указан')]")
    Element unitsLbl;

    @Override
    public void waitForPageIsLoaded() {

        shopBtn.waitForVisibility();
        quantityAmountModalCallBtn.waitForVisibility();
    }

    public SalesHistoryUnitsModalPage openSalesHistoryUnitsModal(){
        quantityAmountModalCallBtn.click();
        return new SalesHistoryUnitsModalPage();
    }

    public SearchShopPage openSearchShopPage(){
        shopBtn.click();
        return new SearchShopPage();
    }

    @Step("Проверить, что история продаж отображена корректно")
    public SalesHistoryPage shouldSalesHistoryIsCorrect(List<SalesHistoryData> data, boolean byPrice) throws Exception{
        List<Double> salesResult = salesHistoryWidget.grabDataFromWidget();
        int dataCounter=0;
        if (byPrice){
            anAssert.isElementTextContains(unitsLbl, "в рублях", getPageSource());
            for (int i=0; i<salesResult.size();i++){
                while(salesResult.get(i)==0.0){
                    i++;
                }
                anAssert.isEquals(data.get(dataCounter).getAmount(), salesResult.get(i), "price mismatch");
                dataCounter++;
            }
        }else {
            anAssert.isElementTextContains(unitsLbl, "в штуках", getPageSource());
            for (int i=0; i<salesResult.size();i++){
                while(salesResult.get(i)==0.0){
                    i++;
                }
                anAssert.isEquals(data.get(dataCounter).getQuantity(), salesResult.get(i), "quantity mismatch");
                dataCounter++;
            }
        }
        return this;
    }

}
