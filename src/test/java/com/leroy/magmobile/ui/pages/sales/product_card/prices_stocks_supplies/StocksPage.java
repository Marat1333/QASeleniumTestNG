package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.product.details.ExtStocks;
import com.leroy.common_mashups.catalogs.data.product.details.StockAreas;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.ui.models.product_card.ShopCardData;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.ShopStockInfoWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import java.util.List;

public class StocksPage extends ProductPricesQuantitySupplyPage {
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

    private AndroidScrollView<ShopCardData> shopCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text,'км')]/..", ShopStockInfoWidget.class);

    @AppFindBy(containsText = "Все (1")
    Button shopListNavBtn;

    @Step("Перейти на страницу со списком магазинов")
    public ShopsStocksPage goToShopListPage() throws Exception {
        mainScrollView.scrollUpToElement(shopListNavBtn);
        shopListNavBtn.click();
        return new ShopsStocksPage();
    }

    @Override
    public void waitForPageIsLoaded() {
        availableStockLbl.waitForVisibility();
        unavailableStockLbl.waitForVisibility();
    }

    @Step("Проверить что данные по остаткам товара отображены корректно")
    public StocksPage shouldDataIsCorrect(ProductData data) throws Exception {
        StockAreas stockAreas = data.getStockAreas();
        ExtStocks extStocks = data.getExtStocks();
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(availableStockLbl.getText()), ParserUtil.prettyDoubleFmt(data.getAvailableStock()), "available 4 sale");
        Double salesHole = data.getAvailableStock() - stockAreas.getRm() - stockAreas.getEm() - stockAreas.getRd();
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(this.salesHall.getText()), ParserUtil.prettyDoubleFmt(salesHole), "Sales Hall");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(rmWarehouse.getText()), String.valueOf(stockAreas.getRm()), "RM");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(emWarehouse.getText()), String.valueOf(stockAreas.getEm()), "EM");
        //data has not contains street warehouse quantity
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(remoteRdWarehouse.getText()), String.valueOf(stockAreas.getRd()), "RD");
        Integer unavailable4Sale = (extStocks.getWhb() == null ? 0 : extStocks.getWhb()) +
                (extStocks.getBufferEM() == null ? 0 : extStocks.getBufferEM()) +
                (extStocks.getClientsReserve() == null ? 0 : extStocks.getClientsReserve()) +
                (extStocks.getTransferReserve() == null ? 0 : extStocks.getTransferReserve()) +
                (extStocks.getReturnReserve() == null ? 0 : extStocks.getReturnReserve()) +
                (extStocks.getDefectEM() == null ? 0 : extStocks.getDefectEM()) +
                (extStocks.getCorrectionStockInWait() == null ? 0 : extStocks.getCorrectionStockInWait()) +
                (extStocks.getExpo() == null ? 0 : extStocks.getExpo());
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(unavailableStockLbl.getText()), String.valueOf(unavailable4Sale), "unavailable 4 sale");
        unavailableStockLbl.click();
        mainScrollView.scrollDownToElement(expo);
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(buffer.getText()),
                String.valueOf(extStocks.getWhb() == null ? 0 : extStocks.getWhb()), "WHB");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(bufferEm.getText()),
                String.valueOf(extStocks.getBufferEM() == null ? 0 : extStocks.getBufferEM()), "buffer EM");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(reserve4Clients.getText()),
                String.valueOf(extStocks.getClientsReserve() == null ? 0 : extStocks.getClientsReserve()), "clients reserve");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(reserve4Transfer.getText()),
                String.valueOf(extStocks.getTransferReserve() == null ? 0 : extStocks.getTransferReserve()), "transfer reserve");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(reserve4Return.getText()),
                String.valueOf(extStocks.getReturnReserve() == null ? 0 : extStocks.getReturnReserve()), "return reserve");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(defectEm.getText()),
                String.valueOf(extStocks.getDefectEM() == null ? 0 : extStocks.getDefectEM()), "defect EM");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(correctionStockInWait.getText()),
                String.valueOf(extStocks.getCorrectionStockInWait() == null ? 0 : extStocks.getCorrectionStockInWait()),
                "correction stock in wait");
        softAssert.isEquals(ParserUtil.strWithOnlyDigits(expo.getText()),
                String.valueOf(extStocks.getExpo() == null ? 0 : extStocks.getExpo()), "expo");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить что данные по остаткам товара в ближайших магазинах отображены корректно")
    public StocksPage shouldShopStocksAreCorrect(List<ShopData> data) throws Exception {
        mainScrollView.scrollDownToElement(shopListNavBtn);
        List<ShopCardData> shopData = shopCardsScrollView.getFullDataList();
        for (int i = 0; i < shopData.size(); i++) {
            ShopCardData uiData = shopData.get(i);
            ShopData apiData = data.get(i);
            softAssert.isEquals(uiData.getId(), apiData.getId() + " " + apiData.getName(), "id and name");
            softAssert.isEquals(uiData.getStock(), apiData.getPriceAndStock().getStock(), "stock");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить наличие элементов")
    public void verifyRequiredElements() {
        softAssert.areElementsVisible(availableStockLbl, unavailableStockLbl, salesHall);
        softAssert.verifyAll();
    }

}
