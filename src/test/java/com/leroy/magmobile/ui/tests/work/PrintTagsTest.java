package com.leroy.magmobile.ui.tests.work;

import com.leroy.constants.DefectConst;
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
import com.leroy.magmobile.ui.pages.search.NomenclatureSearchPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.print_tags.*;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.enums.Format;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.*;
import io.qameta.allure.Issue;
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
        return loginAndCreateSession(catalogSearchClient.getRandomProduct().getLmCode());
    }

    private TagsListPage loginAndCreateSession(String lmCode) throws Exception {
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        workPage.goToSessionsListPage();
        return createSession(lmCode);
    }

    private TagsListPage createSession(String lmCode) {
        SessionsListPage sessionsListPage = new SessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        EditTagModalPage editTagModalPage = new EditTagModalPage();
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
        editTagModalPage.shouldProductDataIsCorrect(randomProduct);
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
        multiFunctionModal.printTag();
        EditTagModalPage editTagModalPage = new EditTagModalPage();
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
        multiFunctionModal.printTag();
        editTagModalPage = new EditTagModalPage();
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

        if (DefectConst.PRINT_TAG_NAVIGATION_ISSUE) {
            editTagModalPage = new EditTagModalPage();
            editTagModalPage.shouldSizeValuesAreCorrect(productTagData);
            editTagModalPage.shouldDeleteBtnHasCorrectCondition(true);
            editTagModalPage.addProductToPrintSession();
        }

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
        if (DefectConst.LFRONT_3640) {
            tagsListPage.callEditModal(lmCodesList.get(0));
            editTagModalPage.closeModal();
        }

        tagsListPage.switchToGroupEditorMode();
        editTagModalPage = tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(0));
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
        DeleteSessionByDeletingProductModalPage deleteSessionByDeletingProductModalPage = new DeleteSessionByDeletingProductModalPage();
        deleteSessionByDeletingProductModalPage.confirmDelete();
        if (DefectConst.PRINT_TAG_NAVIGATION_ISSUE){
            sessionsListPage = new SessionsListPage();
            sessionsListPage.shouldViewTypeIsCorrect(true);
        }else {
            productCardPage = new ProductCardPage();
            productCardPage.verifyRequiredElements(true);
        }
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
        editTagModalPage.selectCheckBox(Format.MIDDLE);
        editTagModalPage.confirm();
        tagsListPage = new TagsListPage();
        editTagModalPage = tagsListPage.callEditModalToProductByIndex(2);
        editTagModalPage.selectCheckBox(Format.BIG);
        editTagModalPage.confirm();
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
        ProductTagData tagData = new ProductTagData();

        //Step 1
        step("отредактировать дефолтные значения при добавлении в сессию");
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        tagData.setSizes(4, 3, 2);
        editTagModalPage.addProductToPrintSession(tagData);
        TagsListPage tagsListPage = new TagsListPage();
        sessionCreationTime = tagsListPage.getSessionCreationTimeStamp();
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(tagData);

        //Step 2
        step("редактирование на странице со списком ценников");
        editTagModalPage = tagsListPage.callEditModal(lmCode);
        editTagModalPage.shouldSizeValuesAreCorrect(tagData);
        tagData.setSizes(3, 2, 1);
        editTagModalPage.addProductToPrintSession(tagData);
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(tagData);

        //Step 3
        step("ввести кол-во >40 ценников");
        editTagModalPage = tagsListPage.callEditModal(lmCode);
        editTagModalPage.addProductToPrintSession(41, 41, 45);
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
        editTagModalPage.addProductToPrintSession(0, 0, 0);
        editTagModalPage.verifyRequiredElements();

        //Step 7
        step("перевести чек-боксы в enabled");
        editTagModalPage.selectCheckBoxes(Format.SMALL, Format.MIDDLE, Format.BIG);
        editTagModalPage.shouldSizeValuesAreCorrect(3, 2, 2);

        //Step 8
        step("редактирование через карточку уже добавленного товара");
        tagData.setSizes(3, 2, 2);
        editTagModalPage.addProductToPrintSession(tagData);
        tagsListPage = new TagsListPage();
        tagsListPage.addProductToSession();
        scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.shouldSizeValuesAreCorrect(tagData);
        tagData.setSizes(5, 5, 5);
        editTagModalPage.addProductToPrintSession(tagData);

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
        tagData.setSizes(6, 6, 6);
        editTagModalPage.addProductToPrintSession(tagData);
        tagsListPage = new TagsListPage();
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(tagData);
        sessionCreationTimeCheck = tagsListPage.getSessionCreationTimeStamp();
        anAssert().isEquals(sessionCreationTime, sessionCreationTimeCheck, "creation time");
    }

    @Test(description = "C23389200 массовое редактирование формата ценников")
    public void testTagsGroupEdition() throws Exception {
        int productsCount = 5;
        List<ProductItemData> randomProducts = catalogSearchClient.getRandomUniqueProductsWithTitles(productsCount);
        List<String> lmCodesList = randomProducts.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());

        ProductTagData firstProductData = new ProductTagData(lmCodesList.get(0), 3, 0, 0);
        ProductTagData secondProductData = new ProductTagData(lmCodesList.get(1), 3, 0, 0);
        ProductTagData thirdProductData = new ProductTagData(lmCodesList.get(2), 1, 0, 0);
        ProductTagData fourthProductData = new ProductTagData(lmCodesList.get(3), 0, 2, 0);
        ProductTagData fifthProductData = new ProductTagData(lmCodesList.get(4), 3, 2, 2);

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        EditTagModalPage editTagModalPage = new EditTagModalPage();

        editTagModalPage.addProductToPrintSession();
        addUniqueProductsToSessionByManualSearch(lmCodesList.subList(1, lmCodesList.size()));

        TagsListPage tagsListPage = new TagsListPage();
        editTagModalPage = tagsListPage.callEditModal(lmCodesList.get(2));
        editTagModalPage.addProductToPrintSession(thirdProductData);
        tagsListPage = new TagsListPage();
        tagsListPage.callEditModal(lmCodesList.get(3));
        editTagModalPage.addProductToPrintSession(fourthProductData);
        tagsListPage = new TagsListPage();
        tagsListPage.callEditModal(lmCodesList.get(4));
        editTagModalPage.addProductToPrintSession(fifthProductData);

        //Step 1
        step("проверить вид модалки группового редактирования при выборе 1 товара");
        tagsListPage.switchToGroupEditorMode();
        tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(0));
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(true);
        editTagModalPage.closeModal();

        //Step 2
        step("проверить вид модалки группового редактирования при выборе >1 товара");
        tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(1));
        /*minor bug https://jira.lmru.tech/browse/LFRONT-3640
        editTagModalPage.shouldDeleteBtnHasCorrectCondition(false);*/
        editTagModalPage.closeModal();

        //Step 3
        step("выбрать 2 товара с разными форматами и открыть модалку гр. редактирования");
        editTagModalPage = tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(1), lmCodesList.get(3));
        editTagModalPage.shouldCheckBoxesHasCorrectCondition(false, false, false);
        editTagModalPage.shouldSizeValuesAreCorrect(0, 0, 0);
        editTagModalPage.closeModal();

        //Step 4
        step("выбрать 2 товара с разным кол-вом, но одинаковыми форматами и открыть модалку гр. редактирования");
        editTagModalPage = tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(2), lmCodesList.get(3));
        editTagModalPage.shouldCheckBoxesHasCorrectCondition(false, false, false);
        editTagModalPage.shouldSizeValuesAreCorrect(0, 0, 0);
        editTagModalPage.closeModal();

        //Step 5
        step("выбрать 2 товара с одинаковым кол-вом и форматами, открыть модалку гр. редактирования\n" +
                "добавить им 1 формат с кол-вом, соответствующим одному из форматов последнего товара");
        editTagModalPage = tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(1), lmCodesList.get(2));
        editTagModalPage.shouldCheckBoxesHasCorrectCondition(true, false, false);
        editTagModalPage.shouldSizeValuesAreCorrect(firstProductData);
        firstProductData.setSizes(3, 2, 0);
        secondProductData.setSizes(3, 2, 0);
        editTagModalPage.setSizesAndQuantity(firstProductData);
        editTagModalPage.confirm();

        tagsListPage = new TagsListPage();
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(firstProductData, secondProductData, thirdProductData, fourthProductData, fifthProductData);
        tagsListPage.switchToGroupEditorMode();

        //Step 6
        step("отредактировать 2 товара:\n" +
                "1. товар со всеми форматами\n" +
                "2. товар с 1 форматом и тем же кол-вом конкретного формата, что и у первого товара");
        editTagModalPage = tagsListPage.choseProductsAndOpenGroupEditModal(lmCodesList.get(0), lmCodesList.get(1), lmCodesList.get(4));
        editTagModalPage.shouldCheckBoxesHasCorrectCondition(true, true, false);
        editTagModalPage.shouldSizeValuesAreCorrect(firstProductData);
        editTagModalPage.closeModal();

        //Step 7
        step("выбрать все товары в групповом редактировании и отредактировать их");
        firstProductData.setSizes(5, 5, 5);
        secondProductData.setSizes(5, 5, 5);
        thirdProductData.setSizes(5, 5, 5);
        fourthProductData.setSizes(5, 5, 5);
        fifthProductData.setSizes(5, 5, 5);

        editTagModalPage = tagsListPage.choseAllProductsAndCallEditModal();
        editTagModalPage.setSizesAndQuantity(firstProductData);
        editTagModalPage.confirm();
        tagsListPage = new TagsListPage();
        tagsListPage.shouldProductTagsHasCorrectSizesAndQuantity(firstProductData, secondProductData, thirdProductData, fourthProductData, fifthProductData);
    }

    @Test(description = "C23389198 удаление сессии")
    public void testDeleteSession() throws Exception {
        String lmCode = catalogSearchClient.getRandomProduct().getLmCode();
        TagsListPage tagsListPage = loginAndCreateSession(lmCode);

        //Step 1
        step("удалить все товары по одному");
        EditTagModalPage editTagModalPage = tagsListPage.callEditModal(lmCode);
        editTagModalPage.deleteProductFromSession();
        DeleteSessionByDeletingProductModalPage deleteSessionByDeletingProductModalPage = new DeleteSessionByDeletingProductModalPage();
        deleteSessionByDeletingProductModalPage.confirmDelete();
        SessionsListPage sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(true);

        //Step 2
        step("удалить все товары через массовое редактирование");
        tagsListPage = createSession(lmCode);
        tagsListPage.switchToGroupEditorMode();
        editTagModalPage = tagsListPage.choseProductsAndOpenGroupEditModal(lmCode);
        editTagModalPage.deleteProductFromSession();
        DeleteSessionByBtnModalPage deleteSessionByBtnModalPage = new DeleteSessionByBtnModalPage();
        deleteSessionByBtnModalPage.confirmDelete();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(true);

        //Step 3
        step("удалить товары через добавление существующего товара (поиск)");
        tagsListPage = createSession(lmCode);
        PrintTagsScannerPage printTagsScannerPage = tagsListPage.addProductToSession();
        SearchProductPage searchProductPage = printTagsScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.deleteProductFromSession();
        deleteSessionByDeletingProductModalPage = new DeleteSessionByDeletingProductModalPage();
        deleteSessionByDeletingProductModalPage.confirmDelete();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(true);

        //Step 4
        step("удалить товары через добавление существующего товара (карточка)");
        tagsListPage = createSession(lmCode);
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
        ActionWithProductModalPage multiFunctionModal = new ActionWithProductModalPage();
        multiFunctionModal.printTag();
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.deleteProductFromSession();
        deleteSessionByDeletingProductModalPage = new DeleteSessionByDeletingProductModalPage();
        deleteSessionByDeletingProductModalPage.confirmDelete();
        if (DefectConst.PRINT_TAG_NAVIGATION_ISSUE){
            sessionsListPage = new SessionsListPage();
            sessionsListPage.shouldViewTypeIsCorrect(true);
        }else {
            productCardPage = new ProductCardPage();
        productCardPage.verifyRequiredElements(true);
        searchProductPage = productCardPage.returnBack();
        searchProductPage.returnBack();
        bottomMenuPage = new BottomMenuPage();
        WorkPage workPage = bottomMenuPage.goToWork();
        workPage.goToSessionsListPage();
        }

        //Step 5
        step("удаление сессии через специальную кнопку в списке товаров сессии");
        tagsListPage = createSession(lmCode);
        tagsListPage.deleteSession();
        deleteSessionByBtnModalPage = new DeleteSessionByBtnModalPage();
        deleteSessionByBtnModalPage.confirmDelete();
        sessionsListPage = new SessionsListPage();
        sessionsListPage.shouldViewTypeIsCorrect(true);
    }

    @Test(description = "C23399939 проверить невозможность добавления товаров в сессию")
    public void testAddProductError() throws Exception {
        String productWithNoName = "12869061";
        String productWithNoBarCode = "10025797";

        //Step 1
        step("Попробовать добавить товар без необходимых данных в сессию через поиск из сканера");
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(productWithNoName);
        AddProductToSessionErrorModalPage addProductToSessionErrorModalPage = new AddProductToSessionErrorModalPage();
        addProductToSessionErrorModalPage.verifyRequiredElements();

        //Step 2
        step("Попробовать добавить товар без необходимых данных в сессию через карточку товара");
        addProductToSessionErrorModalPage.confirm();
        searchProductPage.returnBack();
        scannerPage.closeScanner();
        sessionsListPage.goBack();
        BottomMenuPage bottomMenuPage = new BottomMenuPage();
        MainProductAndServicesPage mainProductAndServicesPage = bottomMenuPage.goToSales();
        searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(productWithNoBarCode);
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        ActionWithProductModalPage actionWithProductModalPage = new ActionWithProductModalPage();
        actionWithProductModalPage.printTag();
        addProductToSessionErrorModalPage = new AddProductToSessionErrorModalPage();
        addProductToSessionErrorModalPage.verifyRequiredElements();
    }

    @Test(description = "C23409311 отсутствие запроса на поиск услуг")
    public void testNoServicesInSearchResult() throws Exception {
        //Step 1
        step("Через страницу сканер перейти в ручной поиск и выполнить поиск по всем отделам");
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        searchProductPage = nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldNotCardsBeOnPage(SearchProductPage.CardType.SERVICE);
    }

    @Test(description = "C23411538 Проверить подсчет кол-ва страниц")
    public void testPagesAmount() throws Exception {
        int productsCount = 2;
        List<ProductItemData> randomProducts = catalogSearchClient.getRandomUniqueProductsWithTitles(productsCount);
        List<String> lmCodesList = randomProducts.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());

        //Pre-conditions
        TagsListPage tagsListPage = loginAndCreateSession(lmCodesList.get(0));
        addUniqueProductsToSessionByManualSearch(lmCodesList.subList(1, lmCodesList.size()));
        EditTagModalPage editTagModalPage = tagsListPage.callEditModal(lmCodesList.get(0));
        editTagModalPage.addProductToPrintSession(26, 11, 4);
        editTagModalPage = tagsListPage.callEditModal(lmCodesList.get(1));
        editTagModalPage.addProductToPrintSession(23, 29, 11);

        //Step 1
        step("нажать распечатать все формарты");
        tagsListPage.printTags();
        SessionFormatsModalPage sessionFormatsModalPage = new SessionFormatsModalPage();
        sessionFormatsModalPage.printFormat(Format.ALL);
        PagesQuantityModalPage pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.SMALL, 3);

        //Step 2
        step("нажать продолжить печать");
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.MIDDLE, 4);

        //Step 3
        step("нажать продолжить печать");
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.BIG, 5);
    }

    @Test(description = "C23409752 Порядок отправки форматов ценников на печать")
    public void testFormatPrintingOrder() throws Exception {
        int productsCount = 3;
        List<ProductItemData> randomProducts = catalogSearchClient.getRandomUniqueProductsWithTitles(productsCount);
        List<String> lmCodesList = randomProducts.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionsListPage sessionsListPage = workPage.goToSessionsListPage();
        sessionsListPage.createNewSession();
        PrintTagsScannerPage scannerPage = new PrintTagsScannerPage();
        SearchProductPage searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.selectCheckBoxes(Format.MIDDLE);
        editTagModalPage.confirm();
        TagsListPage tagsListPage = new TagsListPage();

        tagsListPage.addProductToSession();
        scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(1));
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.selectCheckBoxes(Format.BIG);
        editTagModalPage.confirm();
        tagsListPage = new TagsListPage();

        tagsListPage.addProductToSession();
        scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(2));
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.confirm();
        tagsListPage = new TagsListPage();

        //Step 1
        step("распечатать все форматы");
        tagsListPage.printTags();
        SessionFormatsModalPage sessionFormatsModalPage = new SessionFormatsModalPage();
        sessionFormatsModalPage.printFormat(Format.ALL);
        PagesQuantityModalPage pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.SMALL, 1);

        //Step 2
        step("нажать продолжить печать");
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.MIDDLE, 1);

        //Step 3
        step("нажать продолжить печать");
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.BIG, 1);
        pagesQuantityModalPage.continuePrinting();
        SuccessPrintingPage successPrintingPage = new SuccessPrintingPage();
        successPrintingPage.closePage();

        //Step 4
        step("Создать сессию с двумя товарами разных форматов и отправить их на печать");
        sessionsListPage = new SessionsListPage();
        sessionsListPage.createNewSession();
        scannerPage = new PrintTagsScannerPage();
        searchProductPage = scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(0));
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.selectCheckBoxes(Format.BIG, Format.MIDDLE);
        editTagModalPage.confirm();
        tagsListPage = new TagsListPage();
        tagsListPage.addProductToSession();
        scannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCodesList.get(1));
        editTagModalPage = new EditTagModalPage();
        editTagModalPage.confirm();
        tagsListPage = new TagsListPage();
        tagsListPage.printTags();
        sessionFormatsModalPage = new SessionFormatsModalPage();
        sessionFormatsModalPage.printFormat(Format.ALL);
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.SMALL, 1);

        //Step 5
        step("нажать продолжить печать");
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.MIDDLE, 1);

        //Step 6
        step("нажать продолжить печать");
        pagesQuantityModalPage.continuePrinting();
        pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.shouldPagesQuantityAndFormatAreCorrect(Format.BIG, 1);
    }

    @Issue("LFRONT-3485")
    @Test(description = "C23411003 навигация")
    public void testNavigation() throws Exception {
        String lmCode = catalogSearchClient.getRandomProduct().getLmCode();

        //Step 1
        step("Создать сессию печати через страницу карточки товара и удалить ее");
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.clickActionWithProductButton();
        ActionWithProductModalPage actionWithProductModalPage = new ActionWithProductModalPage();
        actionWithProductModalPage.printTag();
        EditTagModalPage editTagModalPage = new EditTagModalPage();
        editTagModalPage.addProductToPrintSession();
        TagsListPage tagsListPage = new TagsListPage();
        tagsListPage.deleteSession();
        DeleteSessionByBtnModalPage deleteSessionByBtnModalPage = new DeleteSessionByBtnModalPage();
        deleteSessionByBtnModalPage.confirmDelete();
        //bug
        productCardPage = new ProductCardPage();
        productCardPage.verifyRequiredElements(true);

        //Step 2
        step("перейти с карточки товара назад");
        searchProductPage = productCardPage.returnBack();
        searchProductPage.verifyRequiredElements();

        //Step 3
        step("нажимаем назад");
        searchProductPage.returnBack();
        mainProductAndServicesPage = new MainProductAndServicesPage();
        mainProductAndServicesPage.verifyRequiredElements();

        //Step 4
        step("создать сессию через раздел работа, отправить ценник на печать и закрыть страницу-уведомление об успешной отправке");
        BottomMenuPage bottomMenuPage = new BottomMenuPage();
        WorkPage workPage = bottomMenuPage.goToWork();
        workPage.goToSessionsListPage();
        tagsListPage = createSession(lmCode);
        tagsListPage.printTags();
        PagesQuantityModalPage pagesQuantityModalPage = new PagesQuantityModalPage();
        pagesQuantityModalPage.continuePrinting();
        SuccessPrintingPage successPrintingPage = new SuccessPrintingPage();
        successPrintingPage.closePage();
        SessionsListPage sessionsListPage = new SessionsListPage();
        sessionsListPage.verifyRequiredElements();

        //Step 5
        step("нажимаем назад");
        sessionsListPage.goBack();
        workPage = new WorkPage();
        workPage.verifyRequiredElements();
    }

}
