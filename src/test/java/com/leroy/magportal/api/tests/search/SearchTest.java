package com.leroy.magportal.api.tests.search;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.Units;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import com.leroy.magportal.ui.constants.search.CatalogSearchParams;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import com.leroy.utils.file_manager.FileManager;
import com.leroy.utils.file_manager.excel.ExcelRow;
import com.leroy.utils.file_manager.excel.ExcelSheet;
import com.leroy.utils.file_manager.excel.ExcelWorkBook;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

public class SearchTest extends BaseMagPortalApiTest {

    @Inject
    private CatalogSearchClient client;

    private String buildUri(String resource, Map<String, String> queryParamsMap) {
        String result = EnvConstants.URL_MAG_PORTAL_OLD + "/api" + resource + "?";
        StringBuilder queryParamBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParamsMap.entrySet()) {
            queryParamBuilder.append(entry.getKey()).append(entry.getValue()).append("&");
        }
        String queryParams = queryParamBuilder.toString();
        return result + queryParams.substring(0, queryParams.length() - 1);
    }

    private void shouldExcelOutputIsCorrect(List<ProductItemData> dataList,
            ExcelWorkBook excelWorkBook) {
        String yes = "да";
        String no = "нет";
        ExcelSheet excelSheet = excelWorkBook.getExcelSheetByIndex(0);
        List<ExcelRow> rowList = excelSheet.getRowList();
        //названия столбцов
        rowList.remove(0);
        for (int i = 0; i < dataList.size(); i++) {
            ProductItemData apiData = dataList.get(i);
            ExcelRow row = rowList.get(i);
            softAssert().isEquals(apiData.getLmCode(),
                    ParserUtil.strWithOnlyDigits(row.getCellStringValueByIndex(0)), "lmCode");
            softAssert().isEquals(apiData.getBarCode(),
                    ParserUtil.strWithOnlyDigits(row.getCellStringValueByIndex(1)), "barCode");
            if (apiData.getTitle() != null) {
                softAssert()
                        .isEquals(apiData.getTitle(), row.getCellStringValueByIndex(2), "title");
            }
            String ctm = apiData.getCtm() ? yes : no;
            softAssert().isEquals(ctm, row.getCellStringValueByIndex(3), "ctm");
            softAssert().isEquals(apiData.getGamma(), row.getCellStringValueByIndex(4), "gamma");
            String avsDate;
            if (apiData.getAvsDate() != null) {
                avsDate = DateTimeUtil.localDateToStr(apiData.getAvsDateAsZonedDateTime().toLocalDate(),
                        DateTimeUtil.DD_MM_YYYY);
            } else {
                avsDate = no;
            }
            softAssert().isEquals(avsDate, row.getCellStringValueByIndex(5), "avsDate");
            softAssert().isEquals(apiData.getTop(), row.getCellStringValueByIndex(6), "top");
            String topEM = apiData.getTopEM() ? yes : no;
            softAssert().isEquals(topEM, row.getCellStringValueByIndex(7), "topEM");
            softAssert().isEquals(apiData.getSupCode(),
                    ParserUtil.prettyDoubleFmt(row.getCellDoubleValueByIndex(8)), "supplier");
            softAssert().isEquals(apiData.getSupName(), row.getCellStringValueByIndex(9),
                    "supplierName");
            softAssert().isEquals(ParserUtil.prettyDoubleFmt(apiData.getAvailableStock()),
                    ParserUtil.prettyDoubleFmt(row.getCellDoubleValueByIndex(10)),
                    "available stock");
            softAssert().isEquals(ParserUtil.prettyDoubleFmt(apiData.getPrice()),
                    ParserUtil.prettyDoubleFmt(row.getCellDoubleValueByIndex(11)), "price");
            softAssert().isEquals(apiData.getPriceCurrency(), row.getCellStringValueByIndex(12),
                    "currency");
            String priceUnit =
                    apiData.getPriceUnit().equals(Units.EA.getEnName()) ? Units.EA.getRuName() : no;
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
                altPriceUnit = apiData.getAltPriceUnit().equals(Units.EA.getEnName()) ? Units.EA
                        .getRuName() : no;
            } else {
                altPriceUnit = no;
            }
            softAssert().isEquals(altPriceUnit, row.getCellStringValueByIndex(15), "alt unit");
        }
        softAssert().verifyAll();
    }

    @Test(description = "C23416271 Excel output")
    public void testExcelDownload() throws Exception {
        FileManager fileManager = new FileManager();
        String resource = "/v4/catalog/search";
        UserSessionData userSessionData = getUserSessionData();
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(CatalogSearchParams.shopId, userSessionData.getUserShopId());
        queryParams.put(CatalogSearchParams.pageSize, "90");
        queryParams.put(CatalogSearchParams.ldap, userSessionData.getUserLdap());
        queryParams.put(CatalogSearchParams.outputFormat, "xls");

        String uri = buildUri(resource, queryParams);
        List<ProductItemData> dataList = client.getProductsList();
        ExcelWorkBook excelWorkBook = new ExcelWorkBook(
                fileManager.downloadFileFromNetworkToDefaultDownloadDirectory(
                        uri, "1.xlsx"));
        shouldExcelOutputIsCorrect(dataList, excelWorkBook);
    }
}
