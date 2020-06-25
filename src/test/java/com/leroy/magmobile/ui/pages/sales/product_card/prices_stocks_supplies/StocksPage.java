package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.ExtStocks;
import com.leroy.magmobile.api.data.catalog.product.StockAreas;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class StocksPage extends ProductPricesQuantitySupplyPage{
    @AppFindBy(xpath = "//*[@text='Доступно для продажи']/following-sibling::*[1]")
    Element availableStockLbl;

    @AppFindBy(xpath = "//*[@text='Торговый зал']/following-sibling::*[1]")
    Element salesHall;

    @AppFindBy(xpath = "//*[@text='Склад RM']/following-sibling::*[1]")
    Element rmWarehouse;

    @AppFindBy(xpath = "//*[@text='Склад EM']/following-sibling::*[1]")
    Element emWarehouse;

    @AppFindBy(xpath = "//*[@text='Склад на улице']/following-sibling::*[1]")
    Element streetWarehouse;

    @AppFindBy(xpath = "//*[@text='Удаленный склад RD']/following-sibling::*[1]")
    Element remoteRdWarehouse;

    @AppFindBy(xpath = "//*[@text='Недоступно для продажи']/following-sibling::*[1]")
    Element unavailableStockLbl;

    @AppFindBy(xpath = "//*[@text='Буфер']/following-sibling::*[1]")
    Element buffer;

    @AppFindBy(xpath = "//*[@text='Буфер EM']/following-sibling::*[1]")
    Element bufferEm;

    @AppFindBy(xpath = "//*[@text='Резерв для клиентов']/following-sibling::*[1]")
    Element reserve4Clients;

    @AppFindBy(xpath = "//*[@text='Резерв для трансфера']/following-sibling::*[1]")
    Element reserve4Transfer;

    @AppFindBy(xpath = "//*[@text='Резерв для возврата']/following-sibling::*[1]")
    Element reserve4Return;

    @AppFindBy(xpath = "//*[@text='Дефект EM']/following-sibling::*[1]")
    Element defectEm;

    @AppFindBy(xpath = "//*[@text='Коррек-ка запаса в ожидании']/following-sibling::*[1]")
    Element correctionStockInWait;

    @AppFindBy(xpath = "//*[@text='Экспо']/following-sibling::*[1]")
    Element expo;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @Override
    public void waitForPageIsLoaded() {
        availableStockLbl.waitForVisibility();
        unavailableStockLbl.waitForVisibility();
    }

    public StocksPage shouldDataIsCorrect(CatalogProductData data){
        StockAreas stockAreas = data.getStockAreas();
        ExtStocks extStocks = data.getExtStocks();
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(availableStockLbl.getText()), ParserUtil.prettyDoubleFmt(data.getAvailableStock()), "available 4 sale");
        Double salesHole = data.getAvailableStock() - stockAreas.getRm() - stockAreas.getEm() - stockAreas.getRd();
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(this.salesHall.getText()), ParserUtil.prettyDoubleFmt(salesHole), "Sales Hall");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(rmWarehouse.getText()), String.valueOf(stockAreas.getRm()), "RM");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(emWarehouse.getText()), String.valueOf(stockAreas.getEm()), "EM");
        //data has not contains street warehouse quantity
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(remoteRdWarehouse.getText()), String.valueOf(stockAreas.getRd()), "RD");
        Integer unavailable4Sale = extStocks.getWhb() + extStocks.getBufferEM() + extStocks.getClientsReserve()+
                extStocks.getTransferReserve()+extStocks.getReturnReserve()+extStocks.getDefectEM()+
                extStocks.getCorrectionStockInWait() + extStocks.getExpo();
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(unavailableStockLbl.getText()), String.valueOf(unavailable4Sale), "unavailable 4 sale");
        unavailableStockLbl.click();
        mainScrollView.scrollDownToElement(expo);
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(buffer.getText()), String.valueOf(extStocks.getWhb()), "WHB");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(bufferEm.getText()), String.valueOf(extStocks.getBufferEM()), "buffer EM");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(reserve4Clients.getText()), String.valueOf(extStocks.getClientsReserve()), "clients reserve");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(reserve4Transfer.getText()), String.valueOf(extStocks.getTransferReserve()), "transfer reserve");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(reserve4Return.getText()), String.valueOf(extStocks.getReturnReserve()), "return reserve");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(defectEm.getText()), String.valueOf(extStocks.getDefectEM()), "defect EM");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(correctionStockInWait.getText()), String.valueOf(extStocks.getCorrectionStockInWait()), "correction stock in wait");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(expo.getText()), String.valueOf(extStocks.getExpo()), "expo");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить наличие элементов")
    public void verifyRequiredElements(){
        softAssert.areElementsVisible(availableStockLbl, unavailableStockLbl, salesHall);
        softAssert.verifyAll();
    }

}
