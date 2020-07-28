package com.leroy.magmobile.ui.tests.work;

import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.PrintPriceClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProductModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.print_tags.PrintTagsScannerPage;
import com.leroy.magmobile.ui.pages.work.print_tags.PrinterSelectorPage;
import com.leroy.magmobile.ui.pages.work.print_tags.SessionsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.TagsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.ConfirmSessionExitModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.EditTagModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.UnsuccessfullSessionCreationModalPage;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Guice(modules = {Module.class})
public class PrintTagsTest extends AppBaseSteps {

    private CatalogSearchClient catalogSearchClient;
    private PrintPriceClient printPriceClient;

    private TagsListPage createSession() throws Exception{
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        scannerPage.verifyRequiredElements();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(catalogSearchClient.getRandomProduct().getLmCode());
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(false);
        editTagModalPage.addProductToPrintSession();
        return new TagsListPage();
    }

    @BeforeClass
    private void initClients() {
        catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        printPriceClient = apiClientProvider.getPrintPriceClient();
    }

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("32");
        sessionData.setUserDepartmentId("15");
        return sessionData;
    }

    @Step("Добавить в сессию товары")
    private List<ProductTagData> addUniqueProductsToSessionByManualSearch(List<String> lmCodes) {
        List<ProductTagData> productTagDataList = new ArrayList<>();
        for (String lmCode : lmCodes) {
            TagsListPage tagsListPage = new TagsListPage();
            PrintTagsScannerPage scannerPage = tagsListPage.addProductToSession();
            SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
            searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
            EditTagModalPage editTagModalPage = new EditTagModalPage();
            productTagDataList.add(editTagModalPage.addProductToPrintSession());
        }
        return productTagDataList;
    }

    @Test(description = "C23389191 Создание сессии через раздел \"Работа\"")
    public void testCreateSessionFromWorkPage() throws Exception {
        ProductItemData randomProduct = catalogSearchClient.getRandomProduct();
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
        editTagModalPage.addProductToPrintSession();
        TagsListPage tagsListPage = new TagsListPage();
        tagsListPage.verifyRequiredElements();
        tagsListPage.shouldProductsAreCorrect(lmCode);

        //Step 2
        step("Попробовать создать еще одну сессию");
        ConfirmSessionExitModalPage exitModalPage = tagsListPage.goBack();
        exitModalPage.exit();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.createNewSession();
        UnsuccessfullSessionCreationModalPage errorModal = new UnsuccessfullSessionCreationModalPage();
        errorModal.verifyRequiredElements();
        errorModal.confirm();
        sessionsListPage.verifyRequiredElements();
    }

    @Test(description = "C23389192 Создание сессии через карточку товара")
    public void testCreateSessionFromProductCard() throws Exception {
        ProductItemData randomProduct = catalogSearchClient.getRandomProduct();
        String lmCode = randomProduct.getLmCode();

        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);

        // Step 1
        step("Добавить товар в сессию печати через карточку товара");
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        ActionWithProductModalPage multiFunctionModal = new ActionWithProductModalPage();
        EditTagModalPage editTagModalPage = multiFunctionModal.printTag();
        editTagModalPage.addProductToPrintSession();
        TagsListPage tagsListPage = new TagsListPage();
        tagsListPage.verifyRequiredElements();
        tagsListPage.shouldProductsAreCorrect(lmCode);
    }

    @Test(description = "C23389199 возвращение в созданную ранее сессию")
    public void testReturnToCreatedSession() throws Exception {
        ProductItemData randomProduct = catalogSearchClient.getRandomProduct();
        String lmCode = randomProduct.getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();

        //Step 1
        step("Создать сессию печати через страницу \"Работа\" и выйти из нее");
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.addProductToPrintSession();
        TagsListPage tagsListPage = new TagsListPage();
        ConfirmSessionExitModalPage exitModalPage = tagsListPage.goBack();
        exitModalPage.verifyRequiredElements();
        exitModalPage.exit();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(false);

        //Step 2
        step("нажать на активную сессию");
        sessionsListPage.navigateToActiveSession();
        tagsListPage.verifyRequiredElements();
        tagsListPage.shouldProductsAreCorrect(lmCode);
    }

    @Test(description = "C23389193 добавление товара в сессию")
    public void testAddProductToCreatedSession() throws Exception {
        int productsCount = 3;
        List<ProductItemData> randomProducts = catalogSearchClient.getRandomUniqueProductsWithTitles(productsCount);
        List<String> lmCodesList = randomProducts.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
        String[] lmCodes = new String[lmCodesList.size()];
        lmCodes = lmCodesList.toArray(lmCodes);

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();

        //Step 1
        step("добавить товар через поиск");
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        scannerPage.verifyRequiredElements();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(false);
        editTagModalPage.addProductToPrintSession();

        addUniqueProductsToSessionByManualSearch(Collections.singletonList(lmCodesList.get(1)));
        TagsListPage tagsListPage = new TagsListPage();
        tagsListPage.shouldProductsAreCorrect(Arrays.copyOfRange(lmCodes, 0, 2));

        //Step 2
        step("добавить товар через карточку");
        ConfirmSessionExitModalPage confirmSessionExitModalPage = tagsListPage.goBack();
        confirmSessionExitModalPage.exit();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.goBack();
        BottomMenuPage bottomMenuPage = new BottomMenuPage();
        MainProductAndServicesPage mainProductAndServicesPage = bottomMenuPage.goToSales();
        searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(2));
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        ActionWithProductModalPage multiFunctionModal = new ActionWithProductModalPage();
        editTagModalPage = multiFunctionModal.printTag();
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(false);
        ProductTagData productTagData = editTagModalPage.addProductToPrintSession();
        tagsListPage = new TagsListPage();
        tagsListPage.verifyRequiredElements();
        tagsListPage.shouldProductsAreCorrect(lmCodes);

        //Step 3
        step("добавление уже добавленного ранее товара через поиск");
        scannerPage = tagsListPage.addProductToSession();
        scannerPage.shouldCounterIsCorrect(3);
        searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(2));
        //workaround for minor bug
        searchProductPage.searchProductAndSelect(lmCodesList.get(2));
        //
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldSizeValuesAreCorrect(productTagData);
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(true);
        editTagModalPage.addProductToPrintSession();
        tagsListPage = new TagsListPage();
        tagsListPage.shouldProductCountIsCorrect(3);

        //Step 4
        step("добавление уже добавленного ранее товара через карточку");
        confirmSessionExitModalPage = tagsListPage.goBack();
        confirmSessionExitModalPage.exit();
        productCardPage= new ProductCardPage();
        searchProductPage = productCardPage.returnBack();
        //вид поиска немного изменился
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(2));

        //как должно быть
        /*productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        multiFunctionModal = new ActionWithProductModalPage();
        editTagModalPage = multiFunctionModal.printTag();
        productTagData = editTagModalPage.addProductToPrintSession();*/

        //workaround
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldSizeValuesAreCorrect(productTagData);
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(true);
        editTagModalPage.addProductToPrintSession();
        //

        tagsListPage = new TagsListPage();
        tagsListPage.shouldProductCountIsCorrect(3);
    }

    @Test(description = "C23389194 выбор принтера")
    public void testChosePrinter() throws Exception{
        String chosenPrinterName = printPriceClient.getRandomPrinterName();
        String userDept = getUserSessionData().getUserDepartmentId();

        //Step 1
        step("создать сессию");
        TagsListPage tagsListPage = createSession();
        tagsListPage.shouldPrinterIsCorrect(userDept);

        //Step 2
        step("выбрать принтер");
        PrinterSelectorPage printerSelectorPage = tagsListPage.goToPrinterSelectorPage();
        printerSelectorPage.shouldChosenPrinterIsCorrect(userDept, false);
        printerSelectorPage.shouldAllFiltersIsDisplayed(printPriceClient.getPrinterNamesList());
        tagsListPage = printerSelectorPage.chosePrinter(chosenPrinterName);
        tagsListPage.shouldPrinterIsCorrect(chosenPrinterName);

        //Step 3
        step("Перезайти в сессию");
        ConfirmSessionExitModalPage exitModalPage = tagsListPage.goBack();
        exitModalPage.exit();
        SessionsListPage sessionsListPage = new SessionsListPage();
        tagsListPage = sessionsListPage.navigateToActiveSession();
        tagsListPage.shouldPrinterIsCorrect(chosenPrinterName);

        //Step 4
        step("Перейти на страницу выбора принтера");
        printerSelectorPage = tagsListPage.goToPrinterSelectorPage();
        printerSelectorPage.shouldChosenPrinterIsCorrect(chosenPrinterName, true);
    }
}
