package com.leroy.magmobile.ui.tests.work;

import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.PrintPriceClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.print_tags.PrintTagsScannerPage;
import com.leroy.magmobile.ui.pages.work.print_tags.SessionsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.TagsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.ConfirmSessionExitModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.EditTagModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.UnsuccessfullSessionCreationModalPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;

@Guice(modules = {Module.class})
public class PrintTagsTest extends AppBaseSteps {
    private CatalogSearchClient catalogSearchClient;
    private PrintPriceClient printPriceClient;

    @BeforeClass
    private void initClients() {
        catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        printPriceClient = apiClientProvider.getPrintPriceClient();
    }

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("32");
        return sessionData;
    }

    private ProductItemData getRandomProduct() {
        ProductItemDataList productItemDataList = catalogSearchClient.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        anAssert().isTrue(productItemData.size() > 0, "size must be more than 0");
        return productItemData.get((int) (Math.random() * productItemData.size()));
    }

    @Test(description = "C23389191 Создание сессии через раздел \"Работа\"")
    public void testCreateSessionFromWorkPage()throws Exception{
        ProductItemData randomProduct = getRandomProduct();
        String lmCode = randomProduct.getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();

        //Step 1
        step("создать новую ссессию");
        sessionsListPage.shouldViewTypeIsCorrect(true);
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        scannerPage.verifyRequiredElements();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        TagsListPage tagsListPage = editTagModalPage.addProductToPrintSession();
        tagsListPage.shouldProductsAreCorrect(lmCode);

        //Step 2
        step("Попробовать создать еще одну сессию");
        ConfirmSessionExitModalPage exitModalPage = tagsListPage.goBack();
        exitModalPage.verifyRequiredElements();
        exitModalPage.exit();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(false);
        sessionsListPage.createNewSession();
        UnsuccessfullSessionCreationModalPage errorModal = new UnsuccessfullSessionCreationModalPage();
        errorModal.verifyRequiredElements();
        errorModal.confirm();
        sessionsListPage.verifyRequiredElements();
    }
}
