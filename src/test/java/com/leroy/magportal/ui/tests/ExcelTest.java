package com.leroy.magportal.ui.tests;

import com.leroy.core.configuration.DriverFactory;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.file_manager.FileManager;
import java.io.File;
import java.time.LocalDateTime;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class ExcelTest extends WebBaseSteps {

    @AfterTest
    private void clearDownloadData() {
        FileManager.clearDownloadDirectory();
    }

    @Test(description = "C23416164 check excel output")
    public void testExcelOutput() throws Exception {
        FileManager fileManager = new FileManager();

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        step("Нажать на кнопку выгрузки excel");
        searchProductPage.downloadExcelSearchResultOutput();
        LocalDateTime downloadTime = LocalDateTime.now();
        if (!DriverFactory.isGridProfile()) {
            downloadTime = downloadTime.minusHours(3);
        }
        File file = fileManager.getFileFromDefaultDownloadDirectory(
                String.format("LEGO_Item_Extraction_%s.xlsx", DateTimeUtil
                        .localDateTimeToStr(downloadTime, DateTimeUtil.YYYY_MM_DD_HH_MM)));
        fileManager.waitUntilFileAppears();
        anAssert().isTrue(file.exists(), "file does not exist");
    }
}
