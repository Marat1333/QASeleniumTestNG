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
import com.leroy.magmobile.ui.pages.work.print_tags.*;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.enums.Format;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.*;
import com.leroy.magmobile.ui.tests.sales.MultiFunctionalButtonTest;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Guice(modules = {Module.class})
public class PrintTagsTest extends AppBaseSteps {

    private CatalogSearchClient catalogSearchClient;
    private PrintPriceClient printPriceClient;

    private TagsListPage loginAndCreateSession() throws Exception {
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        workPage.goToSessionsListPage();
        return createSession();
    }

    private TagsListPage createSession(String lmCode) {
        SessionsListPage sessionsListPage = new SessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        scannerPage.verifyRequiredElements();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(false);
        editTagModalPage.addProductToPrintSession();
        return new TagsListPage();
    }

    private TagsListPage createSession() {
        return createSession(catalogSearchClient.getRandomProduct().getLmCode());
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
        //TODO добавить проверку данных товара в модалке
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
        productCardPage = new ProductCardPage();
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
    public void testChosePrinter() throws Exception {
        String chosenPrinterName = printPriceClient.getRandomPrinterName();
        String userDept = getUserSessionData().getUserDepartmentId();

        //Step 1
        step("создать сессию");
        TagsListPage tagsListPage = loginAndCreateSession();
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

    @Test(description = "C23411536 удаление товаров")
    public void testDeleteProducts() throws Exception {
        int productsCount = 5;
        List<ProductItemData> randomProducts = catalogSearchClient.getRandomUniqueProductsWithTitles(productsCount);
        List<String> lmCodesList = randomProducts.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        scannerPage.shouldTagsListNavBtnIsVisible(false);
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(false);
        editTagModalPage.addProductToPrintSession();
        addUniqueProductsToSessionByManualSearch(lmCodesList.subList(1, lmCodesList.size()));

        //Step 1
        step("удалить товар через редактирование существующего");
        TagsListPage tagsListPage = new TagsListPage();
        editTagModalPage = tagsListPage.callEditModal(lmCodesList.get(0));
        editTagModalPage.deleteProductFromSession();
        tagsListPage.shouldProductDeleted(lmCodesList.get(0));
        lmCodesList.remove(0);

        //Step 2
        step("удалить товар через массовое редактирование");

        //workaround for minor bug https://jira.lmru.tech/browse/LFRONT-3640
        tagsListPage.callEditModal(lmCodesList.get(0));
        editTagModalPage.closeModal();
        //

        tagsListPage.switchToGroupEditorMode();
        editTagModalPage = tagsListPage.choseProductsAndOpenEditModal(lmCodesList.get(0));
        editTagModalPage.deleteProductFromSession();
        tagsListPage.shouldProductDeleted(lmCodesList.get(0));
        lmCodesList.remove(0);


        //Step 3
        step("удалить товар через добавление в сессию существующего (поиск)");
        tagsListPage.addProductToSession();
        scannerPage.shouldCounterIsCorrect(3);
        scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        editTagModalPage.deleteProductFromSession();
        searchProductPage.returnBack();
        scannerPage = new PrintTagsScannerPage();
        scannerPage.shouldCounterIsCorrect(2);
        tagsListPage = scannerPage.navigateToTagsList();
        tagsListPage.shouldProductDeleted(lmCodesList.get(0));
        lmCodesList.remove(0);

        //Step 4
        step("удалить товар через добавление в сессию существующего (карточка)");
        tagsListPage.goBack();
        ConfirmSessionExitModalPage confirmSessionExitModalPage = new ConfirmSessionExitModalPage();
        confirmSessionExitModalPage.exit();
        sessionsListPage.goBack();
        BottomMenuPage bottomMenuPage = new BottomMenuPage();
        MainProductAndServicesPage mainProductAndServicesPage = bottomMenuPage.goToSales();
        searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        ActionWithProductModalPage multiFunctionModal = new ActionWithProductModalPage();
        multiFunctionModal.printTag();
        editTagModalPage.deleteProductFromSession();
        productCardPage.returnBack();
        searchProductPage.backToSalesPage();
        bottomMenuPage.goToWork();
        workPage.goToSessionsListPage();
        sessionsListPage.navigateToActiveSession();
        tagsListPage.shouldProductDeleted(lmCodesList.get(0));
        lmCodesList.remove(0);

        //Step 5
        step("удаление единственного в сессии товара через модалку редактирования при добавлении в сессию");
        tagsListPage.goBack();
        confirmSessionExitModalPage.exit();
        sessionsListPage.goBack();
        mainProductAndServicesPage = bottomMenuPage.goToSales();
        searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        multiFunctionModal.printTag();
        editTagModalPage.deleteProductFromSession();
        DeleteSessionModalPage deleteSessionModalPage = new DeleteSessionModalPage();
        deleteSessionModalPage.confirmDelete();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(true);
    }

    @Test(description = "C23389195 отправка на печать ценников")
    public void testSendingToPrint() throws Exception {
        int productsCount = 3;
        List<ProductItemData> randomProducts = catalogSearchClient.getRandomUniqueProductsWithTitles(productsCount);
        List<String> lmCodesList = randomProducts.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());

        //Step 1
        step("отправка единственного формата");
        TagsListPage tagsListPage = loginAndCreateSession();
        String printerName = tagsListPage.getCurrentPrinterName();
        tagsListPage.printTags();
        PagesQuantityModalPage pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.continuePrinting();
        SuccessPrintingPage successPrintingPage = new SuccessPrintingPage();
        successPrintingPage.shouldPrinterNameIsVisible(printerName);
        successPrintingPage.verifyRequiredElements();
        successPrintingPage.closePage();

        //Step 2
        step("отправка нескольких форматов одного продукта");
        tagsListPage = createSession();
        EditTagModalPage editTagModalPage = tagsListPage.callEditModalToProductByIndex(0);
        editTagModalPage.addProductToPrintSession(2, 3, 4);
        tagsListPage.printTags();
        SessionFormatsModalPage sessionFormatsModalPage = new SessionFormatsModalPage();
        sessionFormatsModalPage.shouldFormatBtnAreVisible(Format.SMALL, Format.MIDDLE, Format.BIG);
        pagesQuantityModalPage = sessionFormatsModalPage.printFormat(Format.ALL);
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.SMALL, 1);
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.MIDDLE, 1);
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.BIG, 2);
        pagesQuantityModalPage.continuePrinting();
        successPrintingPage = new SuccessPrintingPage();
        successPrintingPage.verifyRequiredElements();
        successPrintingPage.closePage();

        //Step 3
        step("отправка 1 формата 1 продукта (с 1 форматом) из нескольких");
        createSession(lmCodesList.get(0));
        addUniqueProductsToSessionByManualSearch(lmCodesList.subList(1, lmCodesList.size()));
        tagsListPage = new TagsListPage();
        editTagModalPage = tagsListPage.callEditModalToProductByIndex(1);
        editTagModalPage.addProductToPrintSession(0, 3, 0);
        tagsListPage = new TagsListPage();
        editTagModalPage = tagsListPage.callEditModalToProductByIndex(2);
        editTagModalPage.addProductToPrintSession(0, 0, 2);
        tagsListPage = new TagsListPage();
        tagsListPage.printTags();
        sessionFormatsModalPage.printFormat(Format.SMALL);
        pagesQuantityModalPage.continuePrinting();
        FormatSuccessPrintModalPage formatSuccessPrintModalPage = new FormatSuccessPrintModalPage();
        formatSuccessPrintModalPage.shouldHeaderContainsFormat(Format.SMALL);
        formatSuccessPrintModalPage.confirm();
        tagsListPage.shouldProductDeleted(lmCodesList.get(0));

        //Step 4
        step("отправка нескольких форматов двух и более продуктов");
        tagsListPage.printTags();
        sessionFormatsModalPage.printFormat(Format.MIDDLE);
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.continuePrinting();
        formatSuccessPrintModalPage = new FormatSuccessPrintModalPage();
        formatSuccessPrintModalPage.shouldHeaderContainsFormat(Format.MIDDLE);
        formatSuccessPrintModalPage.confirm();
        tagsListPage.shouldProductDeleted(lmCodesList.get(1));

        tagsListPage.printTags();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.continuePrinting();
        successPrintingPage = new SuccessPrintingPage();
        successPrintingPage.verifyRequiredElements();
        successPrintingPage.closePage();
        SessionsListPage sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(true);
    }

    @Test(description = "C23389196 редактирование форматов и кол-ва ценников")
    public void testEditingTagsSizesAndQuantity() throws Exception {
        String lmCode = catalogSearchClient.getRandomProduct().getLmCode();
        LocalDateTime sessionCreationTime;
        LocalDateTime sessionCreationTimeCheck;

        //Step 1
        step("отредактировать дефолтные значения при добавлении в сессию");
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        ProductTagData tagData = editTagModalPage.addProductToPrintSession(4, 3, 2);
        TagsListPage tagsListPage = new TagsListPage();
        sessionCreationTime = tagsListPage.getSessionCreationTimeStamp();
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(tagData);

        //Step 2
        step("редактирование на странице со списком ценников");
        editTagModalPage = tagsListPage.callEditModal(lmCode);
        editTagModalPage.shouldSizeValuesAreCorrect(tagData);
        tagData = editTagModalPage.addProductToPrintSession(3,2,1);
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(tagData);

        //Step 3
        step("ввести кол-во >40 ценников");
        editTagModalPage = tagsListPage.callEditModal(lmCode);
        editTagModalPage.addProductToPrintSession(41, 41,45);
        editTagModalPage.shouldWrongCountControlIsVisible(true, true, true);
        editTagModalPage.verifyRequiredElements();


        //Step 4
        step("ввести 0 в кол-ве");
        editTagModalPage.setSizesAndQuantity(0, 0, 0);
        editTagModalPage.shouldCheckBoxesHasCorrectCondition(false, false, false);
        editTagModalPage.verifyRequiredElements();

        //Step 5
        step("ввести не 0 в инпут каждого размера");
        editTagModalPage.setSizesAndQuantity(1, 1, 1);
        editTagModalPage.shouldCheckBoxesHasCorrectCondition(true, true, true);

        //Step 6
        step("перевести чек-боксы в disabled");
        editTagModalPage.selectCheckBoxes(Format.SMALL, Format.MIDDLE, Format.BIG);
        editTagModalPage.shouldSizeValuesAreCorrect(0, 0, 0);
        editTagModalPage.addProductToPrintSession(0,0,0);
        editTagModalPage.verifyRequiredElements();

        //Step 7
        step("перевести чек-боксы в enabled");
        editTagModalPage.selectCheckBoxes(Format.SMALL, Format.MIDDLE, Format.BIG);
        editTagModalPage.shouldSizeValuesAreCorrect(3, 2, 2);

        //Step 8
        step("редактирование через карточку уже добавленного товара");
        tagData = editTagModalPage.addProductToPrintSession(3, 2,2);
        tagsListPage = new TagsListPage();
        tagsListPage.addProductToSession();
        scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldSizeValuesAreCorrect(tagData);
        tagData = editTagModalPage.addProductToPrintSession(5, 5, 5);

        //Step 9
        step("редактировать через поиск уже добавленного товара");
        tagsListPage = new TagsListPage();
        tagsListPage.goBack();
        ConfirmSessionExitModalPage confirmSessionExitModalPage = new ConfirmSessionExitModalPage();
        confirmSessionExitModalPage.exit();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.goBack();
        BottomMenuPage bottomMenuPage = new BottomMenuPage();
        MainProductAndServicesPage mainProductAndServicesPage = bottomMenuPage.goToSales();
        searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        ActionWithProductModalPage actionWithProductModalPage = new ActionWithProductModalPage();
        actionWithProductModalPage.printTag();
        editTagModalPage.shouldSizeValuesAreCorrect(tagData);
        tagData = editTagModalPage.addProductToPrintSession(6,6,6);
        tagsListPage = new TagsListPage();
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(tagData);
        sessionCreationTimeCheck = tagsListPage.getSessionCreationTimeStamp();
        anAssert().isEquals(sessionCreationTime, sessionCreationTimeCheck,"creation time");
    }

    @Test(description = "C23389200 массовое редактирование формата ценников")
    public void testTagsGroupEdition() throws Exception {

    }

}
