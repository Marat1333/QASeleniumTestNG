package com.leroy.magportal.api.tests.search;

import com.leroy.constants.Units;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import com.leroy.utils.file_manager.FileManager;
import com.leroy.utils.file_manager.excel.ExcelRow;
import com.leroy.utils.file_manager.excel.ExcelSheet;
import com.leroy.utils.file_manager.excel.ExcelWorkBook;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class SearchTest extends BaseProjectApiTest {
    CatalogSearchClient client;

    @BeforeClass
    private void setUp() {
        client = apiClientProvider.getPortalCatalogSearchClient();
    }

    private void shouldExcelOutputIsCorrect(List<ProductItemData> dataList, ExcelWorkBook excelWorkBook) {
        String yes = "да";
        String no = "нет";
        ExcelSheet excelSheet = new ExcelSheet(excelWorkBook.getExcelSheetByIndex(0));
        List<ExcelRow> rowList = excelSheet.getRowList();
        //названия столбцов
        rowList.remove(0);
        for (int i = 0; i < dataList.size(); i++) {
            ProductItemData apiData = dataList.get(i);
            ExcelRow row = rowList.get(i);
            softAssert().isEquals(apiData.getLmCode(), ParserUtil.strWithOnlyDigits(row.getCellStringValueByIndex(0)), "lmCode");
            softAssert().isEquals(apiData.getBarCode(), ParserUtil.strWithOnlyDigits(row.getCellStringValueByIndex(1)), "barCode");
            if (apiData.getTitle() != null) {
                softAssert().isEquals(apiData.getTitle(), row.getCellStringValueByIndex(2), "title");
            }
            String ctm = apiData.getCtm() ? yes : no;
            softAssert().isEquals(ctm, row.getCellStringValueByIndex(3), "ctm");
            softAssert().isEquals(apiData.getGamma(), row.getCellStringValueByIndex(4), "gamma");
            String avsDate;
            if (apiData.getAvsDate()!=null){
                avsDate = DateTimeUtil.localDateToStr(apiData.getAvsDate().toLocalDate(), DateTimeUtil.DD_MM_YYYY);
            }else {
                avsDate = no;
            }
            softAssert().isEquals(avsDate, row.getCellStringValueByIndex(5), "avsDate");
            softAssert().isEquals(apiData.getTop(), row.getCellStringValueByIndex(6), "top");
            String topEM = apiData.getTopEM() ? yes : no;
            softAssert().isEquals(topEM, row.getCellStringValueByIndex(7), "topEM");
            softAssert().isEquals(apiData.getSupCode(), ParserUtil.prettyDoubleFmt(row.getCellDoubleValueByIndex(8)), "supplier");
            softAssert().isEquals(apiData.getSupName(), row.getCellStringValueByIndex(9), "supplierName");
            softAssert().isEquals(ParserUtil.prettyDoubleFmt(apiData.getAvailableStock()),
                    ParserUtil.prettyDoubleFmt(row.getCellDoubleValueByIndex(10)), "available stock");
            softAssert().isEquals(ParserUtil.prettyDoubleFmt(apiData.getPrice()),
                    ParserUtil.prettyDoubleFmt(row.getCellDoubleValueByIndex(11)), "price");
            softAssert().isEquals(apiData.getPriceCurrency(), row.getCellStringValueByIndex(12), "currency");
            String priceUnit = apiData.getPriceUnit().equals(Units.EA.getEnName()) ? Units.EA.getRuName() : no;
            softAssert().isEquals(priceUnit, row.getCellStringValueByIndex(13), "unit");
            String altPrice;
            if (apiData.getAltPrice() != null) {
                altPrice = ParserUtil.prettyDoubleFmt(apiData.getAltPrice());
            } else {
                altPrice = no;
            }
            softAssert().isEquals(altPrice, row.getCellStringValueByIndex(14), "alt price");
            String altPriceUnit;
            if (apiData.getAltPriceUnit() != null) {
                altPriceUnit = apiData.getAltPriceUnit().equals(Units.EA.getEnName()) ? Units.EA.getRuName() : no;
            } else {
                altPriceUnit = no;
            }
            softAssert().isEquals(altPriceUnit, row.getCellStringValueByIndex(15), "alt unit");
        }
        softAssert().verifyAll();
    }

    @Test(description = "C23416271 Excel output")
    public void testExcelDownload() throws IOException {
        List<ProductItemData> dataList = client.getProductsList();
        ExcelWorkBook excelWorkBook = new ExcelWorkBook(FileManager.downloadFileFromNetworkToDefaultDownloadDirectory(
                "https://dev-aao-magfront-stage.apps.lmru.tech/api/v4/catalog/search?shopId=35&pageSize=90&ldap=60069807&outputFormat=xls", "1.xlsx"));
        shouldExcelOutputIsCorrect(dataList, excelWorkBook);
    }
}
