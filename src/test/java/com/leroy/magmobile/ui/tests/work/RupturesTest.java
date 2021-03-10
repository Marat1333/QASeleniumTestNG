package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.DefectConst;
import com.leroy.constants.EnvConstants;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.core.pages.ChromeCertificateErrorPage;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductData;
import com.leroy.magmobile.api.helpers.RuptureHelper;
import com.leroy.magmobile.api.helpers.TransferHelper;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.ruptures.*;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.TaskData;
import com.leroy.magmobile.ui.pages.work.ruptures.enums.Action;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.*;
import com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages.StockCorrectionAddProductWebPage;
import com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages.StockCorrectionCardWebPage;
import com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages.StockCorrectionLoginWebPage;
import com.leroy.magmobile.ui.pages.work.ruptures.stockCorrectionPages.StockCorrectionSuccessWebPage;
import com.leroy.magmobile.ui.pages.work.transfer.RuptureTransferToShopRoomSuccessPage;
import com.leroy.magmobile.ui.pages.work.transfer.TransferOrderStep1Page;
import com.leroy.magmobile.ui.pages.work.transfer.TransferShopRoomStep2Page;
import com.leroy.utils.ParserUtil;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Issue;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class RupturesTest extends AppBaseSteps {

    private static final ThreadLocal<Integer> sessionsNumbers = new ThreadLocal<>();

    @Inject
    RuptureHelper rupturesHelper;
    @Inject
    SearchProductHelper searchProductHelper;
    @Inject
    TransferHelper transferHelper;

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("32");
        sessionData.setUserDepartmentId("15");
        sessionData.setAccessToken(getAccessToken());
        return sessionData;
    }

    @BeforeClass
    private void clearSessionsInUsersDepartment() {
        rupturesHelper.deleteAllSessionInCurrentDepartment();
    }

    @AfterMethod
    private void deleteSession() {
        if (sessionsNumbers.get() != null)
            rupturesHelper.deleteSessions(sessionsNumbers.get());
    }

    private int createSessionWithProductWithoutActions() {
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(null);

        int sessionId = rupturesHelper.createStandardSession(Collections.singletonList(productData));
        sessionsNumbers.set(sessionId);
        return sessionId;
    }

    private int createSessionWithProductWithAllActions() {
        Random random = new Random();
        List<ActionData> actions = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ActionData data = new ActionData();
            if (i == 5) {
                data.setState(false);
            } else {
                data.setState(random.nextBoolean());
            }
            data.setAction(i);
            //
            data.setUserPosition(0);
            actions.add(data);
        }
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(actions);

        int sessionId = rupturesHelper.createStandardSession(Collections.singletonList(productData));
        sessionsNumbers.set(sessionId);
        return sessionId;
    }

    private int createSessionWithProductsWithSpecificIncompleteAction(Action action, String... lmCodes) {
        List<ActionData> actions = new ArrayList<>();
        ActionData data = new ActionData();
        data.setState(false);
        data.setAction(action.getActionNumber());
        data.setUserPosition(0);
        actions.add(data);

        List<RuptureProductData> productDataList = new ArrayList<>();

        for (String lmCode : lmCodes) {
            RuptureProductData productData = new RuptureProductData();
            productData.generateRandomData();
            productData.setLmCode(lmCode);
            productData.setActions(actions);
            productDataList.add(productData);
        }

        int sessionId = rupturesHelper.createStandardSession(productDataList);
        sessionsNumbers.set(sessionId);
        return sessionId;
    }

    private int createSessionWithNeededProductAmountWithSpecificActions(int productAmount, ActionData... actions) {
        List<RuptureProductData> ruptureProductDataList = new ArrayList<>();

        List<ActionData> actionsList = Arrays.asList(actions);
        Set<String> generatedLmCodes = new HashSet<>();

        for (int i = 0; i < productAmount; i++) {
            RuptureProductData productData = new RuptureProductData();
            productData.generateRandomData();
            while (generatedLmCodes.contains(productData.getLmCode())) {
                productData.generateRandomData();
            }
            generatedLmCodes.add(productData.getLmCode());
            productData.setActions(actionsList);
            ruptureProductDataList.add(productData);
        }

        int sessionId = rupturesHelper.createStandardSession(ruptureProductDataList);
        sessionsNumbers.set(sessionId);
        return sessionId;
    }

    // Test case C23440885
    private void makeStockCorrection(String ruptureLmCode, AcceptStockCorrectionModalPage acceptStockCorrectionModalPage) {

        //Step 1
        AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>) ContextProvider.getDriver();
        androidDriver.context("WEBVIEW_chrome");
        androidDriver.close();
        androidDriver.context("NATIVE_APP");
        acceptStockCorrectionModalPage.clickContinueButton();
        new ChromeCertificateErrorPage().waitForButtonsAreVisible()
                .skipSiteSecureError();
        androidDriver.context("WEBVIEW_chrome");
        StockCorrectionLoginWebPage stockCorrectionWebPage = new StockCorrectionLoginWebPage();

        //Step 2
        StockCorrectionAddProductWebPage stockCorrectionAddProductWebPage = stockCorrectionWebPage.clickLogIdBtn();
        stockCorrectionAddProductWebPage.checkLmCode(ruptureLmCode);

        //Step 3
        stockCorrectionAddProductWebPage.enterNewCount();

        //Step 4
        StockCorrectionCardWebPage stockCorrectionCardWebPage = stockCorrectionAddProductWebPage.clickInCardBtn();
        stockCorrectionCardWebPage.checkShopAndDepartment();
        stockCorrectionCardWebPage.checkReason();
        stockCorrectionCardWebPage.checkLmCode(ruptureLmCode);

        //Step 5
        stockCorrectionCardWebPage.clickSendBtn();

        //Step 6
        StockCorrectionSuccessWebPage stockCorrectionSuccessWebPage = stockCorrectionCardWebPage.clickConfirmSendBtn();

        //Step 7
        stockCorrectionSuccessWebPage.clickCloseBtn();
    }

    @Test(description = "C3272519 Перебои на экране работы")
    public void testRupturesOnWorkScreen() throws Exception {
        String shopWithNoRuptures = "62";

        // Step 1
        step("Перейти на экран 'работа'");
        WorkPage workPage = loginAndGoTo(WorkPage.class)
                .shouldRupturesNavigationBtnHasCorrectCondition(true);
        ResRuptureSessionDataList activeSessionsData = rupturesHelper.getActiveSessions();
        workPage.shouldRupturesSessionCounterIsCorrect(activeSessionsData.getTotalCount());

        // Step 2
        step("Сменить магазин на 62 и вернуться на экран 'работа'");
        setShopAndDepartmentForUser(workPage, shopWithNoRuptures, getUserSessionData().getUserDepartmentId())
                .goToWork()
                .shouldRupturesNavigationBtnHasCorrectCondition(false);
    }

    @Test(description = "C3272520 Создание сессии с экрана списка сессий")
    public void testCreateSessionFromSessionsListPage() throws Exception {
        List<ProductData> randomProducts = searchProductHelper.getProducts(2);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        String secondProductLmCode = randomProducts.get(1).getLmCode();

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Нажать на кнопку 'по одному'");
        RupturesScannerPage rupturesScannerPage = sessionListPage.clickScanRupturesByOneButton();
        rupturesScannerPage.shouldCounterIsCorrect(0);
        rupturesScannerPage.shouldRupturesByOneLblIsVisible();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        // Step 2
        step("Перейти в ручной поиск и найти товар по лм-коду");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(firstProductLmCode);
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        // Step 3
        step("Сменить количество на 3+");
        List<String> taskBeforeChange = ruptureCardPage.getTasksList();
        ruptureCardPage.selectProductQuantityOption(RuptureCardPage.QuantityOption.THREE_OR_MORE);
        ruptureCardPage.shouldTasksHasChanged(taskBeforeChange);

        // Step 4
        step("Открыть модалку добавления экшенов (карандашик в блоке экшенов или кнопка 'назначить задачи')");
        List<String> taskAfterChange = ruptureCardPage.getTasksList();
        TasksListsModalPage tasksListsModalPage = ruptureCardPage.callActionModalPage();
        tasksListsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(taskAfterChange);

        // Step 5
        step("Добавить пару экшенов (+), а потом Убрать один из ранее рассчитанных экшенов (-)");
        List<String> possibleTasks = tasksListsModalPage.getPossibleTasks();
        String firstTask = possibleTasks.get(3);
        String secondTask = possibleTasks.get(5);

        tasksListsModalPage.selectTasks(firstTask, secondTask);
        tasksListsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(Arrays.asList(firstTask, secondTask));

        List<String> toDoTasks = tasksListsModalPage.getToDoTasks();
        firstTask = toDoTasks.get(1);
        tasksListsModalPage.selectTasks(firstTask);
        tasksListsModalPage.shouldToDoListNotContainsTaskAndPossibleListContainsTask(firstTask);

        // Step 6
        step("Закрыть модалку редактирования экшенов");
        toDoTasks = tasksListsModalPage.getToDoTasks();
        ruptureCardPage = tasksListsModalPage.closeModal();
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasks);

        // Step 7
        step("Чекнуть один из экшенов");
        firstTask = toDoTasks.get(0);
        ruptureCardPage.setTasksCheckBoxes(firstTask);
        ruptureCardPage.shouldCheckBoxConditionIsCorrect(true, firstTask);

        // Step 8
        step("Добавить любой текст в поле комментария");
        String comment = "asd123";
        ruptureCardPage.setComment(comment);
        ruptureCardPage.shouldSubmitCommentBtnIsActive();

        ruptureCardPage.submitComment();
        ruptureCardPage.shouldCommentFieldHasText(comment);

        // Step 9
        step("Перейти в основную карточку товара (пункт 'подробнее о товаре')");
        ProductCardPage productCardPage = ruptureCardPage.navigateToProductCard();
        productCardPage.verifyRequiredElements(false);

        // Step 10
        step("Вернуться назад на карточку перебоя");
        productCardPage.navigateBack();
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, firstTask)
                .shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.THREE_OR_MORE)
                .shouldCommentFieldHasText(comment);

        // Step 11
        step("Подтвердить добавление перебоя в сессию");
        RuptureData firstAddedRupture = ruptureCardPage.getRuptureData();
        rupturesScannerPage = ruptureCardPage.clickSubmitButton();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true);
        rupturesScannerPage.shouldCounterIsCorrect(1);

        // Step 12
        step("Добавить в сессию еще один перебой");
        searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(secondProductLmCode);
        ruptureCardPage = new RuptureCardPage();
        RuptureData secondAddedRupture = ruptureCardPage.getRuptureData();
        rupturesScannerPage = ruptureCardPage.clickSubmitButton();
        rupturesScannerPage.shouldCounterIsCorrect(2);

        // Step 13
        step("Тапнуть на кнопку 'список перебоев'");
        ActiveSessionPage activeSessionPage = rupturesScannerPage.navigateToRuptureProductList();
        activeSessionPage.shouldRupturesDataIsCorrect(secondAddedRupture, firstAddedRupture)
                .verifyRequiredElements();
        SessionData sessionData = activeSessionPage.getSessionData();

        // Step 14
        step("Нажать железную кнопку назад");
        activeSessionPage.navigateBack();
        ExitActiveSessionModalPage exitActiveSessionModalPage = new ExitActiveSessionModalPage();
        exitActiveSessionModalPage.verifyRequiredElements();

        // Step 15
        step("Нажать отмена");
        activeSessionPage = exitActiveSessionModalPage.declineExit();
        activeSessionPage.verifyRequiredElements();

        // Step 16
        step("Выйти из сессии нажав стрелку назад");
        exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.verifyRequiredElements();

        // Step 17
        step("Подтвердить выход из сессии");
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements()
                .shouldActiveSessionsContainSession(sessionData);
    }

    @Test(description = "C3272525 Удаление перебоя из сессии")
    public void testDeleteRuptureFromSession() throws Exception {
        int sessionId = createSessionWithProductWithAllActions();
        List<RuptureProductData> sessionProducts = rupturesHelper.getProducts(sessionId).getItems();
        String someLmCode = sessionProducts.get(0).getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 1
        step("Тапнуть на перебой");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(someLmCode);
        ruptureCardPage.verifyRequiredElements();

        //Step 2
        step("Нажать на корзину");
        DeleteRuptureModalPage deleteRuptureModalPage = ruptureCardPage.deleteRupture();
        deleteRuptureModalPage.verifyRequiredElements();

        //Step 3
        step("Тапнуть на кнопку 'нет, оставить'");
        deleteRuptureModalPage.cancelDelete();
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements();

        //Step 4
        step("Нажать на корзину еще раз");
        deleteRuptureModalPage = ruptureCardPage.deleteRupture();
        deleteRuptureModalPage.verifyRequiredElements();

        //Step 5
        step("Подтвердить удаление");
        deleteRuptureModalPage.confirmDelete();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRuptureIsNotInList(someLmCode);
    }

    @Test(description = "C3272526 Удаление сессии")
    public void testDeleteSession() throws Exception {
        int sessionId = createSessionWithProductWithoutActions();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Перейти в активную сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        SessionData data = activeSessionPage.getSessionData();

        //Step 2
        step("Нажать на корзину (справа сверху)");
        DeleteSessionModalPage deleteSessionModalPage = activeSessionPage.deleteSession();
        deleteSessionModalPage.verifyRequiredElements();

        //Step 3
        step("Закрыть модалку железной кнопкой 'назад'");
        deleteSessionModalPage.navigateBack();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();

        //Step 4
        step("Нажать на корзину еще раз");
        deleteSessionModalPage = activeSessionPage.deleteSession();
        deleteSessionModalPage.verifyRequiredElements();

        //Step 5
        step("Подтвердить удаление");
        deleteSessionModalPage.confirmDelete();
        sessionListPage = new SessionListPage();
        sessionListPage.shouldActiveSessionsHaveNotContainSession(data);
    }

    @Test(description = "C3272522 Добавление перебоя в стандартную сессию")
    public void testAddRuptureToSession() throws Exception {
        ProductData product = searchProductHelper.getProducts(1).get(0);
        int sessionId = createSessionWithProductWithoutActions();

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Перейти в активную сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        List<RuptureData> ruptureDataList = activeSessionPage.getRupturesList();

        // Step 2
        step("Нажать кнопку '+ перебой'");
        RupturesScannerPage rupturesScannerPage = activeSessionPage.clickAddRuptureButton();
        rupturesScannerPage.shouldCounterIsCorrect(1)
                .shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();

        // Step 3
        step("Перейти к ручному поиску товара и найти любой товар");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(product.getLmCode());
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        RuptureData newRuptureData = ruptureCardPage.getRuptureData();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        // Step 4
        step("Нажать кнопку подтверждения");
        rupturesScannerPage = ruptureCardPage.clickSubmitButton();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .shouldRupturesByOneLblIsVisible()
                .shouldCounterIsCorrect(2)
                .verifyRequiredElements();

        // Step 5
        step("Закрыть сканер по железной кнопке");
        rupturesScannerPage.navigateBack();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRupturesDataIsCorrect(newRuptureData, ruptureDataList.get(0))
                .verifyRequiredElements();
        activeSessionPage.shouldRuptureCounterIsCorrect(2);
    }

    @Test(description = "C3272523 Добавление дубля в сессию при создании сессии по одному")
    public void testAddRuptureDuplicateToSession() throws Exception {
        ProductData randomProduct = searchProductHelper.getProducts(1).get(0);
        String lmCode = randomProduct.getLmCode();

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Нажать на кнопку 'по одному'");
        RupturesScannerPage rupturesScannerPage = sessionListPage.clickScanRupturesByOneButton();
        rupturesScannerPage.shouldCounterIsCorrect(0);
        rupturesScannerPage.shouldRupturesByOneLblIsVisible();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        // Step 2
        step("Перейти к ручному поиску и найти любой товар");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        // Step 3
        step("Подтвердить добавление перебоя");
        rupturesScannerPage = ruptureCardPage.clickSubmitButton();
        rupturesScannerPage.shouldCounterIsCorrect(1);
        rupturesScannerPage.shouldRupturesByOneLblIsVisible();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();

        // Step 4
        step("Перейти к ручному поиску и найти тот же товар");
        searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddDuplicateModalPage addDuplicateModalPage = new AddDuplicateModalPage();
        addDuplicateModalPage.verifyRequiredElements();

        // Step 5
        step("Нажать на кнопку 'понятно'");
        ruptureCardPage = addDuplicateModalPage.confirm();
        ruptureCardPage.verifyRequiredElements();

        // Step 6
        step("Выставить для перебоя количество 'на полке' равное 3+");
        List<String> tasksBefore = ruptureCardPage.getTasksList();
        ruptureCardPage.selectProductQuantityOption(RuptureCardPage.QuantityOption.THREE_OR_MORE);
        ruptureCardPage.shouldTasksHasChanged(tasksBefore);

        // Step 7
        step("Закрыть карточку перебоя (кнопка 'х' в левом углу)");
        RuptureData data = ruptureCardPage.getRuptureData();
        ruptureCardPage.closeRuptureCardPage();
        searchProductPage.verifyRequiredElements();

        // Step 8
        step("Закрыть страницу ручного поиска");
        searchProductPage.returnBack();
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldCounterIsCorrect(1);
        rupturesScannerPage.shouldRupturesByOneLblIsVisible();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();

        // Step 9
        step("Нажать на кнопку Закрыть сканер по кнопке 'х'");
        ActiveSessionPage activeSessionPage = rupturesScannerPage.navigateToRuptureProductList();
        activeSessionPage.shouldRuptureQuantityIsCorrect(1)
                .verifyRequiredElements();

        // Step 10
        step("Тапнуть на перебой");
        ruptureCardPage = activeSessionPage.goToRuptureCard(lmCode);
        ruptureCardPage.shouldRuptureDataIsCorrect(data)
                .shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.THREE_OR_MORE);
    }

    @Issue("RUP-118")
    @Test(description = "C23418142 Добавление дубля в сессию при работе с существующей сессией")
    public void testAddRuptureDuplicateToExistedSession() throws Exception {
        ProductData someProduct = searchProductHelper.getProducts(1).get(0);
        String someLmCode = someProduct.getLmCode();
        RuptureProductData ruptureData = new RuptureProductData();
        ruptureData.generateRandomData();
        ruptureData.setLmCode(someLmCode);
        ruptureData.setBarCode(someProduct.getBarCode());
        int sessionId = rupturesHelper.createStandardSession(Collections.singletonList(ruptureData));
        sessionsNumbers.set(sessionId);
        String comment = "asd123";

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        // Step 1
        step("Нажать на кнопку + перебой");
        RupturesScannerPage rupturesScannerPage = activeSessionPage.clickAddRuptureButton();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();
        int counterValue = rupturesScannerPage.getCounterValue();

        // Step 2
        step("Перейти к ручному поиску и найти товар, который уже есть в сессии");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(someLmCode);
        AddDuplicateModalPage addDuplicateModalPage = new AddDuplicateModalPage();
        addDuplicateModalPage.verifyRequiredElements();

        // Step 3
        step("Нажать на кнопку 'понятно'");
        addDuplicateModalPage.confirm();
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements();

        // Step 4
        step("Изменить количество на полке, Добавить 2 любых экшена," +
                " Чекнуть один из экшенов," +
                " Добавить комментарий");
        ruptureCardPage.selectProductQuantityOption(RuptureCardPage.QuantityOption.ONE);
        TasksListsModalPage tasksListsModalPage = ruptureCardPage.callActionModalPage();
        List<String> possibleTasksList = tasksListsModalPage.getPossibleTasks();
        tasksListsModalPage.selectTasks(possibleTasksList.get(0), possibleTasksList.get(1));
        List<String> toDoTasks = tasksListsModalPage.getToDoTasks();
        ruptureCardPage = tasksListsModalPage.closeModal();
        String checkedTask = toDoTasks.get(0);
        ruptureCardPage.setTasksCheckBoxes(checkedTask);
        ruptureCardPage.setComment(comment);
        ruptureCardPage.submitComment();
        RuptureData data = ruptureCardPage.getRuptureData();

        ruptureCardPage.shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.ONE)
                .shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, checkedTask)
                .shouldCommentFieldHasText(comment);

        // Step 5
        step("Выйти с карточки перебоя по железной кнопке");
        ruptureCardPage.navigateBack();
        searchProductPage = new SearchProductPage();
        searchProductPage.verifyRequiredElements();

        // Step 6
        step("Закрыть экран поиска по железной кнопке");
        searchProductPage.navigateBack();
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldCounterIsCorrect(counterValue)
                .shouldRupturesListNavBtnIsVisible(true);

        // Step 7
        step("Закрыть сканер по железной кнопке");
        rupturesScannerPage.navigateBack();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRupturesDataIsCorrect(data)
                .verifyRequiredElements();

        // Step 8
        step("Открыть карточку перебоя");
        activeSessionPage.goToRuptureCard(someLmCode);
        addDuplicateModalPage = new AddDuplicateModalPage(); // TODO убрать этот костыль когда поправят баг
        addDuplicateModalPage.verifyRequiredElements().confirm();

        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.ONE)
                .shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, checkedTask)
                .shouldCommentFieldHasText(comment);
    }

    @Test(description = "C3272524 Изменение перебоя в активной сессии")
    public void testChangeRuptureInActiveSession() throws Exception {
        String comment = "123asd";
        int sessionId = createSessionWithProductWithoutActions();
        List<RuptureProductData> sessionProducts = rupturesHelper.getProducts(sessionId).getItems();
        String firstRuptureLmCode = sessionProducts.get(0).getLmCode();

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        // Step 1
        step("Тапнуть на товар");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(firstRuptureLmCode);
        ruptureCardPage.verifyRequiredElements();

        // Step 2
        step("Изменить количество 'вижу на полке', Добавить экшенов до 3 штук, Чекнуть один из экшенов, " +
                "Добавить комментарий");
        ruptureCardPage.selectProductQuantityOption(RuptureCardPage.QuantityOption.THREE_OR_MORE);
        TasksListsModalPage tasksListsModalPage = ruptureCardPage.callActionModalPage();
        List<String> possibleTasksList = tasksListsModalPage.getPossibleTasks();
        tasksListsModalPage.selectTasks(possibleTasksList.get(0), possibleTasksList.get(1), possibleTasksList.get(2));
        List<String> toDoTasks = tasksListsModalPage.getToDoTasks();
        ruptureCardPage = tasksListsModalPage.closeModal();
        String firstCheckedTask = toDoTasks.get(0);
        ruptureCardPage.setTasksCheckBoxes(firstCheckedTask);
        ruptureCardPage.setComment(comment);
        ruptureCardPage.submitComment();

        // Step 3
        step("Выйти из карточки товара по железной кнопке");
        ruptureCardPage.navigateBack();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();

        // Step 4
        step("Чекнуть еще 1 экшен");
        String secondCheckTask = toDoTasks.get(1);
        activeSessionPage.checkRuptureActionCheckBox(firstRuptureLmCode, secondCheckTask);
        List<RuptureData> ruptureDataList = activeSessionPage.getRupturesList();

        // Step 5
        step("Выйти из сессии");
        ExitActiveSessionModalPage exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements();

        // Step 6
        step("Вернуться в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRupturesDataIsCorrect(ruptureDataList);

        // Step 7
        step("Перейти в карточку товара");
        ruptureCardPage = activeSessionPage.goToRuptureCard(firstRuptureLmCode);
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, firstCheckedTask, secondCheckTask)
                .shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.THREE_OR_MORE)
                .shouldCommentFieldHasText(comment);
    }

    @Test(description = "C3272527 Завершение сессии")
    public void testFinishSession() throws Exception {
        int sessionId = createSessionWithProductWithAllActions();

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        // Step 1
        step("Нажать кнопку 'завершить'");
        FinishSessionAcceptModalPage finishSessionAcceptModalPage = activeSessionPage.finishSession();
        finishSessionAcceptModalPage.verifyRequiredElements();

        // Step 2
        step("Отменить завершение");
        finishSessionAcceptModalPage.cancel();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();

        // Step 3
        step("Нажать кнопку завершить и подтвердить завершение сессии");
        finishSessionAcceptModalPage = activeSessionPage.finishSession();
        finishSessionAcceptModalPage.finish();
        FinishedSessionPage finishedSessionPage = new FinishedSessionPage();
        finishedSessionPage.shouldStatusIsFinished()
                .shouldTasksCountIsCorrect(8)
                .verifyRequiredElements();
        rupturesHelper.checkSessionIsFinished(sessionId);

        // Step 4
        step("Нажать назад");
        sessionListPage = finishedSessionPage.exitFinishedSession();
        sessionListPage.verifyRequiredElements();
    }

    @Test(description = "C3272528 Изменение перебоев в завершенной сессии")
    public void testChangeRuptureInFinishedSession() throws Exception {
        TaskData firstTaskData = new TaskData(Action.GIVE_APOLOGISE.getActionName(), 0, 1);
        TaskData secondTaskData = new TaskData(Action.REMOVE_PRICE_TAG.getActionName(), 1, 1);
        TaskData allTasks = new TaskData("Все задачи", 1, 2);

        String ruptureLmCode = searchProductHelper.getProducts(1).get(0).getLmCode();
        String comment = "123asd";
        int sessionId = createSessionWithProductsWithSpecificIncompleteAction(Action.FIND_PRODUCT_AND_LAY_IT_OUT, ruptureLmCode);
        rupturesHelper.finishSession(sessionId);

        List<RuptureProductData> sessionProducts = rupturesHelper.getProducts(sessionId).getItems();
        RuptureProductData ruptureData = sessionProducts.get(0);

        RuptureData doneRuptureData = new RuptureData();
        doneRuptureData.setLmCode(ruptureLmCode);
        doneRuptureData.setBarCode(ruptureData.getBarCode());
        doneRuptureData.setTitle(ruptureData.getTitle());
        doneRuptureData.setActions(Collections.singletonMap(Action.REMOVE_PRICE_TAG.getActionName(), true));

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        FinishedSessionPage finishedSessionPage = new FinishedSessionPage();

        // Step 1
        step("Перейти в раздел 'Найти товар и выложить'");
        FinishedSessionRupturesActionsPage finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.FIND_PRODUCT_AND_LAY_IT_OUT);
        finishedSessionRupturesActionsPage.shouldHeaderContainsActionName(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName())
                .verifyRequiredElements();

        // Step 2
        step("Перейти в карточку перебоя");
        RuptureCardPage ruptureCardPage = finishedSessionRupturesActionsPage.goToRuptureCard(ruptureLmCode)
                .verifyRequiredElementsInFinishedSession();

        // Step 3
        step("Перейти к редактированию списка рекомендаций (карандашик в блоке рекомендаций)");
        TasksListsModalPage tasksListsModalPage = ruptureCardPage.callActionModalPage()
                .verifyRequiredElements();

        // Step 4
        step("Убрать экшен 'найти товар и выложить'," +
                " Добавить экшены 'Поставить извиняшку' и 'Убрать ценник'," +
                " Закрыть модалку реактирования экшенов");
        tasksListsModalPage.selectTasks(
                    Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName(),
                    Action.GIVE_APOLOGISE.getActionName(),
                    Action.REMOVE_PRICE_TAG.getActionName())
                .shouldToDoListContainsTaskAndPossibleListNotContainsTask(Arrays.asList(
                        Action.GIVE_APOLOGISE.getActionName(), Action.REMOVE_PRICE_TAG.getActionName()))
                .closeModal();

        // Step 5
        step("Чекнуть экшен 'убрать ценник'");
        ruptureCardPage.setTasksCheckBoxes(Action.REMOVE_PRICE_TAG.getActionName());

        // Step 6
        step("Добавить любой комментарий");
        ruptureCardPage.setComment(comment)
                .submitComment();

        // Step 7
        step("Закрыть карточку перебоя");
        ruptureCardPage.closeRuptureCardPage();
        finishedSessionRupturesActionsPage = new FinishedSessionRupturesActionsPage();
        finishedSessionRupturesActionsPage.shouldHeaderContainsActionName(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName())
                .shouldAllRuptureTaskHaveDone(0, 0);

        // Step 8
        step("Вернуться назад на экран списка экшенов в завершенной сессии");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionPage = new FinishedSessionPage();
        finishedSessionPage.shouldTasksAreCorrect(firstTaskData, secondTaskData)
                .shouldAllTasksCounterIsCorrect(allTasks);

        // Step 9
        step("Перейти в раздел 'Убрать ценник'");
        finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.REMOVE_PRICE_TAG);
        finishedSessionRupturesActionsPage.shouldAllRuptureTaskHaveDone(1, 1)
                .shouldDoneTasksCounterIsCorrect(1);

        // Step 10
        step("Тапнуть на 'Выполненные задачи'");
        finishedSessionRupturesActionsPage = finishedSessionRupturesActionsPage.goToDoneTasks();
        finishedSessionRupturesActionsPage.shouldDoneTasksViewIsPresented()
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode,Action.GIVE_APOLOGISE)
                .shouldTasksRatioCounterIsCorrect(1)
                .verifyRequiredElements()
                .shouldRuptureDataIsCorrect(doneRuptureData);

        // Step 11
        step("Тапнуть на перебой");
        ruptureCardPage = finishedSessionRupturesActionsPage.goToRuptureCard(ruptureLmCode);
        ruptureCardPage.verifyRequiredElementsInFinishedSession()
                .shouldCheckBoxConditionIsCorrect(false, Action.GIVE_APOLOGISE.getActionName())
                .shouldCheckBoxConditionIsCorrect(true, Action.REMOVE_PRICE_TAG.getActionName())
                .shouldCommentFieldHasText(comment);
    }

    @Test(description = "C3272529 Два списка сессий (1 отдел)")
    public void testTwoSessionLists() throws Exception {
        int departmentId = 1;
        getUserSessionData().setUserDepartmentId(String.valueOf(departmentId));

        // Pre-conditions
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> sessionsIdList = rupturesHelper.createFewSessions(20);
        Collections.reverse(sessionsIdList);
        List<Integer> activeSessionsIdList = sessionsIdList.subList(0, 10);
        List<Integer> finishedSessionsIdList = sessionsIdList.subList(10, sessionsIdList.size());
        rupturesHelper.finishFewSessions(finishedSessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldTheseActiveSessionsArePresent(activeSessionsIdList)
                .shouldTheseFinishedSessionsArePresent(finishedSessionsIdList)
                .verifyRequiredElements();
    }

    @Test(description = "C23423650 Пустой список сессий (2 отдел)")
    public void testEmptySessionList() throws Exception {
        int departmentId = 2;
        getUserSessionData().setUserDepartmentId(String.valueOf(departmentId));

        // Pre-conditions
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldNoSessionMsgLblIsVisible();
    }

    @Test(description = "C23423651 Пагинация списка активных сессий, пустой список завершенных сессий (3 отдел)")
    public void testActiveSessionPagination() throws Exception {
        int departmentId = 3;
        getUserSessionData().setUserDepartmentId(String.valueOf(departmentId));

        // Pre-conditions
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> sessionsIdList = rupturesHelper.createFewSessions(11);
        Collections.reverse(sessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldTheseActiveSessionsArePresent(sessionsIdList)
                .shouldFinishedSessionCardsAreNotVisible()
                .verifyRequiredElements();
    }

    @Test(description = "C23423652 Пагинация списка завершенных сессий, пустой список активных сессий (4 отдел)")
    public void testFinishedSessionPagination() throws Exception {
        int departmentId = 4;
        getUserSessionData().setUserDepartmentId(String.valueOf(departmentId));

        // Pre-conditions
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> sessionsIdList = rupturesHelper.createFewSessions(11);
        rupturesHelper.finishFewSessions(sessionsIdList);
        Collections.reverse(sessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.verifyRequiredElements()
                .shouldTheseFinishedSessionsArePresent(sessionsIdList)
                .shouldActiveSessionCardsAreNotVisible();
    }

    @Test(description = "C23423653 Пагинация обоих списков сессий (5 отдел)")
    public void testBothSessionTypesPagination() throws Exception {
        int departmentId = 5;
        getUserSessionData().setUserDepartmentId(String.valueOf(departmentId));

        // Pre-conditions
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> sessionsIdList = rupturesHelper.createFewSessions(22);
        Collections.reverse(sessionsIdList);
        List<Integer> activeSessionsIdList = sessionsIdList.subList(0, 11);
        List<Integer> finishedSessionsIdList = sessionsIdList.subList(11, sessionsIdList.size());
        rupturesHelper.finishFewSessions(finishedSessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldTheseActiveSessionsArePresent(activeSessionsIdList)
                .shouldTheseFinishedSessionsArePresent(finishedSessionsIdList)
                .verifyRequiredElements();
    }

    @Test(description = "C23423655 Pull-to-refresh (6 отдел)")
    public void testPullToRefreshSessionLists() throws Exception {
        int departmentId = 6;
        getUserSessionData().setUserDepartmentId(String.valueOf(departmentId));

        // Pre-conditions
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> sessionsIdList = rupturesHelper.createFewSessions(10);
        Collections.reverse(sessionsIdList);
        List<Integer> activeSessionsIdList = sessionsIdList.stream().limit(5).collect(Collectors.toList());
        List<Integer> finishedSessionsIdList = sessionsIdList.stream().skip(5).limit(5).collect(Collectors.toList());
        rupturesHelper.finishFewSessions(finishedSessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldTheseActiveSessionsArePresent(activeSessionsIdList)
                .shouldTheseFinishedSessionsArePresent(finishedSessionsIdList);

        // Step 2
        step("Удалить 1 активную и 1 завершенную сессию, " +
                "Потянуть список сессий вниз (pull to refresh)");
        int lastActiveSessionIdIndex = activeSessionsIdList.size() - 1;
        int lastFinishedSessionIdIndex = finishedSessionsIdList.size() - 1;
        int lastActiveSessionId = activeSessionsIdList.get(lastActiveSessionIdIndex);
        int lastFinishedSessionId = finishedSessionsIdList.get(lastFinishedSessionIdIndex);
        finishedSessionsIdList.remove(lastFinishedSessionIdIndex);
        activeSessionsIdList.remove(lastActiveSessionIdIndex);
        rupturesHelper.deleteSessions(lastActiveSessionId, lastFinishedSessionId);
        sessionListPage = sessionListPage.pullToRefresh();
        sessionListPage.shouldTheseActiveSessionsArePresent(activeSessionsIdList)
                .shouldTheseFinishedSessionsArePresent(finishedSessionsIdList);
    }

    @Test(description = "C23423654 Смена отдела (7 отдел)")
    public void testChangeDepartment() throws Exception {
        // Pre-conditions
        int seventhDepartmentId = 7;
        getUserSessionData().setUserDepartmentId(String.valueOf(seventhDepartmentId));
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> seventhSessionsIdList = rupturesHelper.createFewSessions(4);
        Collections.reverse(seventhSessionsIdList);
        List<Integer> seventhActiveSessionsIdList = seventhSessionsIdList.stream().limit(2).collect(Collectors.toList());
        List<Integer> seventhFinishedSessionsIdList = seventhSessionsIdList.stream().skip(2).collect(Collectors.toList());
        rupturesHelper.finishFewSessions(seventhFinishedSessionsIdList);

        int eightDepartmentId = 8;
        getUserSessionData().setUserDepartmentId(String.valueOf(eightDepartmentId));
        rupturesHelper.deleteAllSessionInCurrentDepartment();
        List<Integer> eightSessionsIdList = rupturesHelper.createFewSessions(2);
        Collections.reverse(eightSessionsIdList);
        List<Integer> eightActiveSessionsIdList = eightSessionsIdList.subList(0, 1);
        List<Integer> eightFinishedSessionsIdList = eightSessionsIdList.subList(1, eightSessionsIdList.size());
        rupturesHelper.finishFewSessions(eightFinishedSessionsIdList);

        WorkPage workPage = loginAndGoTo(WorkPage.class);

        // Step 1
        step("Перейти в список сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(seventhDepartmentId);
        sessionListPage.shouldTheseActiveSessionsArePresent(seventhActiveSessionsIdList)
                .shouldTheseFinishedSessionsArePresent(seventhFinishedSessionsIdList);

        // Step 2
        step("Сменить отдел");
        sessionListPage = sessionListPage.changeDepartment(eightDepartmentId);
        sessionListPage.shouldTheseActiveSessionsArePresent(eightActiveSessionsIdList)
                .shouldTheseFinishedSessionsArePresent(eightFinishedSessionsIdList);
    }

    @Test(description = "C3272530 Список продуктов (пагинация)")
    public void testProductListPagination() throws Exception {
        int rupturesCount = 21;

        ActionData giveApologize = new ActionData();
        giveApologize.setState(false);
        giveApologize.setUserPosition(0);
        giveApologize.setAction(Action.GIVE_APOLOGISE.getActionNumber());

        ActionData removePriceTag = new ActionData();
        removePriceTag.setState(true);
        removePriceTag.setUserPosition(0);
        removePriceTag.setAction(Action.REMOVE_PRICE_TAG.getActionNumber());

        ActionData findProductAndLayItOut = new ActionData();
        findProductAndLayItOut.setState(false);
        findProductAndLayItOut.setUserPosition(0);
        findProductAndLayItOut.setAction(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionNumber());

        int sessionId = createSessionWithNeededProductAmountWithSpecificActions(rupturesCount, giveApologize, removePriceTag, findProductAndLayItOut);
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1 - 3
        step("Перейти в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRuptureQuantityIsCorrect(rupturesCount)
                .verifyRequiredElements();

        // Step 4
        step("Завершить сессию");
        FinishSessionAcceptModalPage finishSessionAcceptModalPage = activeSessionPage.finishSession();
        finishSessionAcceptModalPage.finish();
        FinishedSessionPage finishedSessionPage = new FinishedSessionPage();
        finishedSessionPage.verifyRequiredElements();

        // Step 5
        step("Перейти во все задачи, дважды проскроллить до конца экрана");
        FinishedSessionRupturesActionsPage finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.ALL_ACTIONS);
        finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(rupturesCount);

        // Step 6
        step("Перейти в выполненные задачи, дважды проскроллить до конца экрана");
        finishedSessionRupturesActionsPage = finishedSessionRupturesActionsPage.goToDoneTasks();
        finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(rupturesCount);

        // Step 7
        step("Вернуться на экран завершенной сессии, Перейти в 'поставить извиняшку'," +
                " дважды проскроллить до конца экрана");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionRupturesActionsPage = new FinishedSessionRupturesActionsPage();
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionPage = new FinishedSessionPage();
        finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.GIVE_APOLOGISE);
        finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(rupturesCount);

        // Step 8
        step("Вернуться на экран завершенной сессии, Перейти в 'Убрать ценник'," +
                " Перейти в выполненные задачи," +
                " дважды проскроллить до конца экрана");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionPage = new FinishedSessionPage();
        finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.REMOVE_PRICE_TAG);
        finishedSessionRupturesActionsPage = finishedSessionRupturesActionsPage.goToDoneTasks();
        finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(rupturesCount);
    }

    @Test(description = "C3272531 Страница завершенной сессии (каунтеры, экшены)")
    public void testFinishedSessionPage() throws Exception {
        int rupturesCount = 3;

        ActionData giveApologize = new ActionData();
        giveApologize.setState(false);
        giveApologize.setUserPosition(0);
        giveApologize.setAction(Action.GIVE_APOLOGISE.getActionNumber());

        ActionData stickRedSticker = new ActionData();
        stickRedSticker.setState(false);
        stickRedSticker.setUserPosition(0);
        stickRedSticker.setAction(Action.STICK_RED_STICKER.getActionNumber());

        ActionData findProductAndLayItOut = new ActionData();
        findProductAndLayItOut.setState(false);
        findProductAndLayItOut.setUserPosition(0);
        findProductAndLayItOut.setAction(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionNumber());

        TaskData giveApologizeTaskData = new TaskData(Action.GIVE_APOLOGISE.getActionName(), 0, 3);
        TaskData stickRedStickerTaskData = new TaskData(Action.STICK_RED_STICKER.getActionName(), 0, 3);
        TaskData findProductAndLayItOutTaskData = new TaskData(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName(), 0, 3);
        TaskData allTaskData = new TaskData(Action.ALL_ACTIONS.getActionName(), 0, 9);

        int sessionId = createSessionWithNeededProductAmountWithSpecificActions(rupturesCount, giveApologize, stickRedSticker, findProductAndLayItOut);
        List<RuptureProductData> ruptures = rupturesHelper.getProducts(sessionId).getItems();
        List<String> lmCodes = ruptures.stream().map(RuptureProductData::getLmCode).collect(Collectors.toList());
        rupturesHelper.finishSession(sessionId);
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Перейти на экране завершенной сессии");
        sessionListPage.goToSession(String.valueOf(sessionId));
        FinishedSessionPage finishedSessionPage = new FinishedSessionPage();
        finishedSessionPage.shouldAllTasksCounterIsCorrect(allTaskData)
                .shouldTasksAreCorrect(giveApologizeTaskData, stickRedStickerTaskData, findProductAndLayItOutTaskData);

        // Step 2
        step("Перейти во 'Все задачи'");
        int doneTasksCounter = 0;
        int allTasksAmount = 9;
        FinishedSessionRupturesActionsPage finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.ALL_ACTIONS);
        finishedSessionRupturesActionsPage.shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount)
                .shouldRuptureCountIsCorrect(rupturesCount);

        // Step 3
        step("Чекнуть у всех товаров экшен 'Поставить извиняшку'," +
                " После каждого чека нужно дожидаться изменения списка");
        finishedSessionRupturesActionsPage.scrollToBeginning();
        String ruptureLmCode = lmCodes.get(2);
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.GIVE_APOLOGISE, ruptureLmCode)
                .shouldDoneTasksCounterIsCorrect(++doneTasksCounter)
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode, Action.GIVE_APOLOGISE)
                .shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount);

        ruptureLmCode = lmCodes.get(1);
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.GIVE_APOLOGISE, ruptureLmCode)
                .shouldDoneTasksCounterIsCorrect(++doneTasksCounter)
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode, Action.GIVE_APOLOGISE)
                .shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount);

        ruptureLmCode = lmCodes.get(0);
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.GIVE_APOLOGISE, ruptureLmCode)
                .shouldDoneTasksCounterIsCorrect(++doneTasksCounter)
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode, Action.GIVE_APOLOGISE)
                .shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount)
                .scrollToBeginning()
                .shouldRuptureCountIsCorrect(rupturesCount);

        // Step 4
        step("Чекнуть для одного из товаров все экшены, " +
                " После каждого чека нужно дожидаться изменения списка");
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.FIND_PRODUCT_AND_LAY_IT_OUT, ruptureLmCode)
                .shouldDoneTasksCounterIsCorrect(++doneTasksCounter)
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode, Action.FIND_PRODUCT_AND_LAY_IT_OUT)
                .shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount)
                .selectTaskCheckBoxForProduct(Action.STICK_RED_STICKER, ruptureLmCode)
                .shouldDoneTasksCounterIsCorrect(++doneTasksCounter)
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode, Action.STICK_RED_STICKER)
                .shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount)
                .scrollToBeginning()
                .shouldRuptureCountIsCorrect(2);

        //Step 5
        step("Перейти в карточку одного из оставшихся товаров, Чекнуть для него все экшены, " +
                "Вернуться назад");
        ruptureLmCode = lmCodes.get(1);
        RuptureCardPage ruptureCardPage = finishedSessionRupturesActionsPage.goToRuptureCard(ruptureLmCode);
        ruptureCardPage.setTasksCheckBoxes(Action.STICK_RED_STICKER.getActionName(),
                Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName());
        doneTasksCounter += 2;
        ruptureCardPage.closeRuptureCardPage();
        //BUG "undefined is not an object"
        finishedSessionRupturesActionsPage = new FinishedSessionRupturesActionsPage();
        //BUG there are 2 ruptures
        finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(1)
                .shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount)
                .shouldDoneTasksCounterIsCorrect(doneTasksCounter);

        //Step 6
        step("Перейти в выполненные задачи");
        finishedSessionRupturesActionsPage = finishedSessionRupturesActionsPage.goToDoneTasks();
        finishedSessionRupturesActionsPage.shouldTasksRatioCounterIsCorrect(doneTasksCounter)
                .shouldRuptureCountIsCorrect(rupturesCount);

        //Step 7
        step("Снять с одного из товаров экшен \"Наклеить красный стикер\"");
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.STICK_RED_STICKER, ruptureLmCode)
                .shouldRuptureCardHasNotContainsTask(ruptureLmCode, Action.STICK_RED_STICKER)
                .shouldTasksRatioCounterIsCorrect(--doneTasksCounter);

        //Step 8
        step("Вернуться назад по железной кнопке");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionRupturesActionsPage = new FinishedSessionRupturesActionsPage();
        if (DefectConst.RUPTURES_DONE_TASKS_LIST_ISSUE)
            finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(1);
        else
            finishedSessionRupturesActionsPage.shouldRuptureCountIsCorrect(2);
        finishedSessionRupturesActionsPage.shouldTasksRatioCounterIsCorrect(doneTasksCounter, allTasksAmount)
                .shouldDoneTasksCounterIsCorrect(doneTasksCounter);

        //Step 9
        step("Вернуться по железной кнопке на экран завершенной сессии");
        allTaskData.setDoneTasksCount(doneTasksCounter);
        giveApologizeTaskData.setDoneTasksCount(3);
        findProductAndLayItOutTaskData.setDoneTasksCount(2);
        stickRedStickerTaskData.setDoneTasksCount(1);

        finishedSessionRupturesActionsPage.goBack();
        finishedSessionPage = new FinishedSessionPage();
        finishedSessionPage.shouldTasksAreCorrect(
                giveApologizeTaskData, findProductAndLayItOutTaskData, stickRedStickerTaskData)
                .shouldAllTasksCounterIsCorrect(allTaskData);

        //Step 10
        step("Перейти в раздел 'Наклеить красный стикер'");
        RuptureProductData firstRuptureProductData = ruptures.get(1);
        ruptureLmCode = firstRuptureProductData.getLmCode();
        RuptureData firstRupture = new RuptureData();
        firstRupture.setLmCode(firstRuptureProductData.getLmCode());
        firstRupture.setBarCode(firstRuptureProductData.getBarCode());
        firstRupture.setTitle(firstRuptureProductData.getTitle());
        Map<String, Boolean> action = Collections.singletonMap(Action.STICK_RED_STICKER.getActionName(), true);
        firstRupture.setActions(action);

        RuptureProductData secondRuptureProductData = ruptures.get(2);
        RuptureData secondRupture = new RuptureData();
        secondRupture.setLmCode(secondRuptureProductData.getLmCode());
        secondRupture.setBarCode(secondRuptureProductData.getBarCode());
        secondRupture.setTitle(secondRuptureProductData.getTitle());
        secondRupture.setActions(action);

        finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.STICK_RED_STICKER);
        finishedSessionRupturesActionsPage.shouldRuptureDataIsCorrect(firstRupture, secondRupture)
                .shouldDoneTasksCounterIsCorrect(1)
                .shouldTasksRatioCounterIsCorrect(1, 3);

        //Step 11
        step("Чекнуть экшены у обоих товаров");
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.STICK_RED_STICKER, ruptureLmCode);
        ruptureLmCode = secondRuptureProductData.getLmCode();
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.STICK_RED_STICKER, ruptureLmCode)
                .shouldNoActiveRuptureTasksAreAvailable()
                .shouldTasksRatioCounterIsCorrect(3, 3)
                .shouldDoneTasksCounterIsCorrect(3);

        //Step 12
        step("Вернуться назад нажав стрелочку назад");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionPage = new FinishedSessionPage();
        allTaskData.setDoneTasksCount(8);
        stickRedStickerTaskData.setDoneTasksCount(3);
        finishedSessionPage.shouldAllTasksCounterIsCorrect(allTaskData)
                .shouldTasksAreCorrect(stickRedStickerTaskData, giveApologizeTaskData, findProductAndLayItOutTaskData);

        //Step 13
        step("Перейти в 'Наклеить красный стикер'");
        finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.STICK_RED_STICKER);
        finishedSessionRupturesActionsPage.shouldNoActiveRuptureTasksAreAvailable()
                .shouldTasksRatioCounterIsCorrect(3, 3)
                .shouldDoneTasksCounterIsCorrect(3)
                .shouldRuptureCountIsCorrect(0);

        //Step 14
        step("Перейти в выполненные задачи");
        finishedSessionRupturesActionsPage = finishedSessionRupturesActionsPage.goToDoneTasks();
//        finishedSessionRupturesActionsPage.shouldTasksRatioCounterIsCorrect(3) TODO Вернуть обратно после фикса RUP-173
        finishedSessionRupturesActionsPage.shouldTasksRatioCounterIsCorrect(8)
                .shouldRuptureCountIsCorrect(3);

        //Step 15
        step("Перейти в выполненные задачи");
        finishedSessionRupturesActionsPage.selectTaskCheckBoxForProduct(Action.STICK_RED_STICKER, ruptureLmCode)
//                .shouldTasksRatioCounterIsCorrect(2) TODO Вернуть обратно после фикса RUP-173
                .shouldTasksRatioCounterIsCorrect(7)
                .shouldRuptureCountIsCorrect(2);

        //Step 16
        step("Выйти назад");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionRupturesActionsPage = new FinishedSessionRupturesActionsPage();
        finishedSessionRupturesActionsPage.shouldTasksRatioCounterIsCorrect(2, 3)
//                .shouldRuptureCountIsCorrect(1) TODO Вернуть обратно после фикса RUP-275
                .shouldRuptureCountIsCorrect(0)
                .shouldDoneTasksCounterIsCorrect(2);

        //Step 17
        step("Выйти назад на экран завершенной сессии");
        finishedSessionRupturesActionsPage.goBack();
        finishedSessionPage = new FinishedSessionPage();
        allTaskData.setDoneTasksCount(7);
        stickRedStickerTaskData.setDoneTasksCount(2);
        finishedSessionPage.shouldAllTasksCounterIsCorrect(allTaskData)
                .shouldTasksAreCorrect(stickRedStickerTaskData, giveApologizeTaskData, findProductAndLayItOutTaskData);
    }

    @Test(description = "C23389122 Создание отзыва с РМ с экрана добавляемого товара", enabled = false)
    public void testCreateRecallFromRmFromAddedProductPage() throws Exception {
        getUserSessionData().setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        List<TransferSearchProductData> products = transferHelper.searchForProductsForTransfer();
        String ruptureLmCode = products.get(0).getLmCode();

        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Нажать '+ сканировать перебой' и перейти в ручной поиск, " +
                "Найти товар из списка пригодных для отзыва с РМ (есть сток на РМ)");
        RupturesScannerPage rupturesScannerPage = sessionListPage.clickScanRupturesByOneButton();
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(ruptureLmCode);
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        //Step 2
        step("Тапнуть на круглую кнопку в нижней части экрана\n" +
                "Выбрать пункт \"Сделать отзыв с РМ\"");
        ActionModalPage actionModalPage = ruptureCardPage.callActionModalByRoundBtn();
        actionModalPage.recallFromRm();
        AcceptRecallFromRmModalPage acceptRecallFromRmModalPage = new AcceptRecallFromRmModalPage();
        acceptRecallFromRmModalPage.verifyRequiredElements();

        //Step 3
        step("Выбрать \"продолжить\"");
        AddProduct35Page<TransferOrderStep1Page> addProductPage = acceptRecallFromRmModalPage.clickContinueButton();
        addProductPage.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_TASK, false);

        //Step 4
        step("Нажать кнопку \"Добавить в заявку\"");
        TransferOrderStep1Page transferOrderStep1Page = addProductPage.clickSubmitButton();
        transferOrderStep1Page.verifyElementsWhenProductsAdded();

        //Step 5
        step("Нажать \"далее\"");
        transferOrderStep1Page.clickNextButton();
        TransferShopRoomStep2Page transferShopRoomStep2Page = new TransferShopRoomStep2Page()
                .verifyRequiredElements();

        //Step 6
        step("Выбрать ожидаемое время поставки и отправить заявку на отзыв");
        LocalDate testDate = LocalDate.now().plusDays(1);
        transferShopRoomStep2Page.editDeliveryDate(testDate);
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect, true);
        transferShopRoomStep2Page.clickSubmitBtn();
        RuptureTransferToShopRoomSuccessPage successPage = new RuptureTransferToShopRoomSuccessPage();
        successPage.verifyRequiredElements();

        //Step 7
        step("Нажать \"Вернуться в сессию перебоев\"");
        successPage.clickSubmitButton();
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements()
                .shouldRecallRequestHasBeenCreatedMsgIsVisible();

        //Step 8
        step("Нажать кнопку \"действия с перебоем\"");
        actionModalPage = ruptureCardPage.callActionModalByPressingActionsWithRupturesBtn();
        actionModalPage.recallFromRm();
        actionModalPage.verifyRequiredElements();

        // Step 9
        step("Закрыть модалку, " +
                "Перейти к редактированию экшенов перебоя (карандашик)");
        ruptureCardPage = actionModalPage.closeModal();
        TasksListsModalPage tasksListsModalPage = ruptureCardPage.callActionModalPage();
        String pressedTaskName = Action.RECALL_FROM_RM.getActionName();
        tasksListsModalPage.selectTasks(pressedTaskName)
                .shouldToDoListContainsTaskAndPossibleListNotContainsTask(Collections.singletonList(pressedTaskName));

        //Step 10
        step("Закрыть модалку \"задачи по перебою\"\n" +
                "Закрыть карточку перебоя");
        ruptureCardPage = tasksListsModalPage.closeModal();
        ruptureCardPage.closeRuptureCardPage();
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRuptureInTheList(ruptureLmCode)
                .shouldRecallRequestHasBeenCreatedMsgIsVisible()
                .verifyRequiredElements();

        //Step 11
        step("Нажать железную кнопку назад и подтвердить выход из сессии");
        String sessionNumber = activeSessionPage.getSessionNumber();
        ExitActiveSessionModalPage exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.shouldActiveSessionsContainSession(sessionNumber);
    }

    @Test(description = "C23389123 Создание отзыва с РМ из активной сессии (карточка, список перебоев)", enabled = false)
    public void testCreateRecallFromRmFromActiveSession() throws Exception {
        getUserSessionData().setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        getUserSessionData().setUserDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        List<TransferSearchProductData> products = transferHelper.searchForProductsForTransfer();
        String ruptureLmCode = products.get(1).getLmCode();

        int sessionId = createSessionWithProductsWithSpecificIncompleteAction(
                Action.RECALL_FROM_RM, ruptureLmCode);

        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 1
        step("Тапнуть на перебой");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(ruptureLmCode);
        ruptureCardPage.verifyRequiredElements();

        //Step 2
        step("Тапнуть на \"Сделать отзыв с RM\"");
        AcceptRecallFromRmModalPage acceptRecallFromRmModalPage = ruptureCardPage.recallProductFromRm();
        acceptRecallFromRmModalPage.verifyRequiredElements();

        //Step 3
        step("Выбрать \"продолжить\"");
        AddProduct35Page<TransferOrderStep1Page> addProductPage = acceptRecallFromRmModalPage.clickContinueButton();
        addProductPage.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_TASK, false);

        //Step 4
        step("Нажать кнопку \"Добавить в заявку\"");
        TransferOrderStep1Page transferOrderStep1Page = addProductPage.clickSubmitButton();
        transferOrderStep1Page.verifyElementsWhenProductsAdded();

        //Step 5
        step("Нажать \"далее\"");
        transferOrderStep1Page.clickNextButton();
        TransferShopRoomStep2Page transferShopRoomStep2Page = new TransferShopRoomStep2Page();
        transferShopRoomStep2Page.verifyRequiredElements();

        //Step 6
        step("Выбрать ожидаемое время поставки и отправить заявку на отзыв");
        LocalDate testDate = LocalDate.now().plusDays(1);
        transferShopRoomStep2Page.editDeliveryDate(testDate);
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect, true);
        transferShopRoomStep2Page.clickSubmitBtn();
        RuptureTransferToShopRoomSuccessPage successPage = new RuptureTransferToShopRoomSuccessPage();
        successPage.verifyRequiredElements();

        //Step 7
        step("Нажать \"Вернуться в сессию перебоев\"");
        successPage.clickSubmitButton();
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements()
                .shouldRecallRequestHasBeenCreatedMsgIsVisible();

        //Step 8
        step("Закрыть карточку перебоя");
        ruptureCardPage.closeRuptureCardPage();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRuptureInTheList(ruptureLmCode)
                .shouldRecallRequestHasBeenCreatedMsgIsVisible()
                .verifyRequiredElements();
    }

    @Test(description = "C23389124 Создание отзыва с РМ из завершенной сессии", enabled = false)
    public void testCreateRecallFromRmFromFinishedSession() throws Exception {
        getUserSessionData().setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        getUserSessionData().setUserDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
        List<TransferSearchProductData> products = transferHelper.searchForProductsForTransfer();
        String ruptureLmCode = products.get(2).getLmCode();

        int sessionId = createSessionWithProductsWithSpecificIncompleteAction(
                Action.RECALL_FROM_RM, ruptureLmCode);
        rupturesHelper.finishSession(sessionId);

        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        FinishedSessionPage finishedSessionPage = new FinishedSessionPage();

        //Step 1
        step("Нажать \"все задачи\"");
        FinishedSessionRupturesActionsPage finishedSessionRupturesActionsPage = finishedSessionPage.goToActionPage(Action.ALL_ACTIONS);
        finishedSessionRupturesActionsPage.verifyRequiredElements()
                .shouldRuptureCountIsCorrect(1)
                .shouldRecallFromRmTaskIsVisible();

        //Step 2
        step("Тапнуть на \"Сделать отзыв с RM\"");
        AcceptRecallFromRmModalPage acceptRecallFromRmModalPage = finishedSessionRupturesActionsPage.recallProductFromRm();
        acceptRecallFromRmModalPage.verifyRequiredElements();

        //Step 3
        step("Выбрать \"продолжить\"");
        AddProduct35Page<TransferOrderStep1Page> addProductPage = acceptRecallFromRmModalPage.clickContinueButton();
        addProductPage.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_TASK, false);

        //Step 4
        step("Нажать кнопку \"Добавить в заявку\"");
        TransferOrderStep1Page transferOrderStep1Page = addProductPage.clickSubmitButton();
        transferOrderStep1Page.verifyElementsWhenProductsAdded();

        //Step 5
        step("Нажать \"далее\"");
        transferOrderStep1Page.clickNextButton();
        TransferShopRoomStep2Page transferShopRoomStep2Page = new TransferShopRoomStep2Page();
        transferShopRoomStep2Page.verifyRequiredElements();

        //Step 6
        step("Выбрать ожидаемое время поставки и отправить заявку на отзыв");
        LocalDate testDate = LocalDate.now().plusDays(1);
        transferShopRoomStep2Page.editDeliveryDate(testDate);
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect, true);
        transferShopRoomStep2Page.clickSubmitBtn();
        RuptureTransferToShopRoomSuccessPage successPage = new RuptureTransferToShopRoomSuccessPage();
        successPage.verifyRequiredElements();

        //Step 7
        step("Нажать \"Вернуться в сессию перебоев\"");
        successPage.clickSubmitButton();
        finishedSessionRupturesActionsPage = new FinishedSessionRupturesActionsPage();
        finishedSessionRupturesActionsPage.verifyRequiredElements()
                .shouldNoActiveRuptureTasksAreAvailable();

        //Step 8
        step("Закрыть карточку перебоя");
        finishedSessionRupturesActionsPage = finishedSessionRupturesActionsPage.goToDoneTasks();
        finishedSessionRupturesActionsPage.verifyRequiredElements()
                .shouldRuptureCountIsCorrect(1)
                .shouldRecallRequestHasBeenCreatedMsgIsVisible();
    }

    @Test(description = "C23424394 Коррекция C3 с экрана добавляемого товара")
    public void testCreateStockCorrectionFromAddProductPage() throws Exception {
        getUserSessionData().setUserShopId("35");
        getUserSessionData().setUserDepartmentId("5");
        CatalogSearchFilter filter = new CatalogSearchFilter().setDepartmentId("5");
        List<ProductData> products = searchProductHelper.getProducts(1, filter);
        String ruptureLmCode = products.get(0).getLmCode();

        WorkPage workPage = loginAndGoTo("60069805", "Passwd12345", false, WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Нажать 'по одному' и перейти в ручной поиск, " +
                "Найти товар из 5 отдела");
        RupturesScannerPage rupturesScannerPage = sessionListPage.clickScanRupturesByOneButton();
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(ruptureLmCode);
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        //Step 2
        step("Тапнуть на круглую кнопку в нижней части экрана\n" +
                "Выбрать пункт \"Сделать коррекцию стока\"");
        ActionModalPage actionModalPage = ruptureCardPage.callActionModalByRoundBtn();
        actionModalPage.stockCorrection();
        AcceptStockCorrectionModalPage acceptStockCorrectionModalPage = new AcceptStockCorrectionModalPage();
        acceptStockCorrectionModalPage.verifyRequiredElements();

        //Step 3
        step("Выполнить коррекцию стока (шаги из тк C23389124)");
        makeStockCorrection(ruptureLmCode, acceptStockCorrectionModalPage);
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements()
                .shouldStockCorrectionHasBeenCreatedMsgIsVisible();

        //Step 4
        step("Нажать кнопку \"действия с перебоем\"");
        actionModalPage = ruptureCardPage.callActionModalByPressingActionsWithRupturesBtn();
        actionModalPage.stockCorrection();
        actionModalPage.verifyRequiredElements();

        // Step 5
        step("Закрыть модалку, " +
                "Перейти к редактированию экшенов перебоя (карандашик)");
        ruptureCardPage = actionModalPage.closeModal();
        TasksListsModalPage tasksListsModalPage = ruptureCardPage.callActionModalPage();
        String pressedTaskName = Action.MAKE_C3_CORRECTION.getActionName();
        tasksListsModalPage.selectTasks(pressedTaskName)
                .shouldToDoListContainsTaskAndPossibleListNotContainsTask(Collections.singletonList(pressedTaskName));

        //Step 6
        step("Закрыть модалку \"задачи по перебою\"\n" +
                "Закрыть карточку перебоя");
        ruptureCardPage = tasksListsModalPage.closeModal();
        ruptureCardPage.closeRuptureCardPage();
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRuptureInTheList(ruptureLmCode)
                .shouldStockCorrectionHasBeenCreatedMsgIsVisible()
                .verifyRequiredElements();

        //Step 7
        step("Нажать железную кнопку назад и подтвердить выход из сессии");
        String sessionNumber = activeSessionPage.getSessionNumber();
        sessionsNumbers.set(ParserUtil.strToInt(sessionNumber));
        ExitActiveSessionModalPage exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.shouldActiveSessionsContainSession(sessionNumber);
    }

    @Test(description = "C23440884 Коррекция C3 из активной сессии со списка перебоев")
    public void testCreateStockCorrectionFromActiveSessionPage() throws Exception {
        getUserSessionData().setUserShopId("35");
        getUserSessionData().setUserDepartmentId("5");

        CatalogSearchFilter filter = new CatalogSearchFilter().setDepartmentId("5");
        List<ProductData> products = searchProductHelper.getProducts(2, filter);
        String firstProductLm = products.get(0).getLmCode();
        String secondProductLm = products.get(1).getLmCode();

        int sessionId = createSessionWithProductsWithSpecificIncompleteAction(Action.MAKE_C3_CORRECTION, firstProductLm, secondProductLm);

        WorkPage workPage = loginAndGoTo("60069805", "Passwd12345", false, WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Перейти в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 2
        step("Сделать коррекцию C3");
        activeSessionPage.checkRuptureActionCheckBox(firstProductLm, "Сделать коррекцию C3");
        AcceptStockCorrectionModalPage acceptStockCorrectionModalPage = new AcceptStockCorrectionModalPage();
        acceptStockCorrectionModalPage.verifyRequiredElements();

        //Step 3
        step("Выполнить коррекцию стока (шаги из тк C23389124)");
        makeStockCorrection(firstProductLm, acceptStockCorrectionModalPage);
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        activeSessionPage.checkStockCorrectionStatus(firstProductLm, true);
        activeSessionPage.checkStockCorrectionStatus(secondProductLm, false);

        //Step 4
        step("Нажать железную кнопку назад и подтвердить выход из сессии");
        activeSessionPage.navigateBack();
        ExitActiveSessionModalPage exitActiveSessionModalPage = new ExitActiveSessionModalPage();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();

        //Step 5
        step("Перейти в обратно в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        activeSessionPage.checkStockCorrectionStatus(firstProductLm, true);
        activeSessionPage.checkStockCorrectionStatus(secondProductLm, false);
    }

    @Test(description = "C23440934 Коррекция C3 из активной сессии из карточка перебоя (через список экшенов)")
    public void testCreateStockCorrectionFromActiveSessionRuptureCardActionsList() throws Exception {
        getUserSessionData().setUserShopId("35");
        getUserSessionData().setUserDepartmentId("5");

        CatalogSearchFilter filter = new CatalogSearchFilter().setDepartmentId("5");
        List<ProductData> products = searchProductHelper.getProducts(2, filter);
        String firstProductLm = products.get(0).getLmCode();
        String secondProductLm = products.get(1).getLmCode();

        int sessionId = createSessionWithProductsWithSpecificIncompleteAction(Action.MAKE_C3_CORRECTION, firstProductLm, secondProductLm);

        WorkPage workPage = loginAndGoTo("60069805", "Passwd12345", false, WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Перейти в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 2
        step("Перейти в карточку перебоя первого товара");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(firstProductLm);
        ruptureCardPage.verifyRequiredElements();

        //Step 3
        step("Выбрать \"Сделать коррекцию С3\"");
        AcceptStockCorrectionModalPage acceptStockCorrectionModalPage = ruptureCardPage.makeStockCorrection();
        acceptStockCorrectionModalPage.verifyRequiredElements();

        //Step 4
        step("Выполнить коррекцию стока (шаги из тк C23389124)");
        makeStockCorrection(firstProductLm, acceptStockCorrectionModalPage);
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements();
        ruptureCardPage.shouldStockCorrectionHasBeenCreatedMsgIsVisible();

        //Step 5
        step("Выйти на экран активной сессии по железной кнопке");
        ruptureCardPage.navigateBack();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        activeSessionPage.checkStockCorrectionStatus(firstProductLm, true);
        activeSessionPage.checkStockCorrectionStatus(secondProductLm, false);

        //Step 6
        step("Нажать железную кнопку назад и подтвердить выход из сессии");
        activeSessionPage.navigateBack();
        ExitActiveSessionModalPage exitActiveSessionModalPage = new ExitActiveSessionModalPage();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();

        //Step 7
        step("Перейти в обратно в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        activeSessionPage.checkStockCorrectionStatus(firstProductLm, true);
        activeSessionPage.checkStockCorrectionStatus(secondProductLm, false);
    }

    @Test(description = "C23440886 Коррекция C3 из активной сессии из карточка перебоя (через действия с перебоями)")
    public void testCreateStockCorrectionFromActiveSessionRuptureCardActionModal() throws Exception {
        getUserSessionData().setUserShopId("35");
        getUserSessionData().setUserDepartmentId("5");

        CatalogSearchFilter filter = new CatalogSearchFilter().setDepartmentId("5");
        List<ProductData> products = searchProductHelper.getProducts(2, filter);
        String firstProductLm = products.get(0).getLmCode();
        String secondProductLm = products.get(1).getLmCode();

        int sessionId = createSessionWithProductsWithSpecificIncompleteAction(Action.MAKE_C3_CORRECTION, firstProductLm, secondProductLm);

        WorkPage workPage = loginAndGoTo("60069805", "Passwd12345", false, WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Перейти в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 2
        step("Перейти в карточку перебоя первого товара");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(firstProductLm);
        ruptureCardPage.verifyRequiredElements();

        //Step 3
        step("Кликнуть на кнопку \"Действия с перебоями\"");
        ActionModalPage actionModalPage = ruptureCardPage.callActionModal();
        actionModalPage.verifyRequiredElements();

        //Step 4
        step("Выбрать \"Сделать коррекцию стока\"");
        actionModalPage.stockCorrection();
        AcceptStockCorrectionModalPage acceptStockCorrectionModalPage = new AcceptStockCorrectionModalPage();
        acceptStockCorrectionModalPage.verifyRequiredElements();

        //Step 5
        step("Выполнить коррекцию стока (шаги из тк C23389124)");
        makeStockCorrection(firstProductLm, acceptStockCorrectionModalPage);
        ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements();
        ruptureCardPage.shouldStockCorrectionHasBeenCreatedMsgIsVisible();

        //Step 6
        step("Выйти на экран активной сессии по железной кнопке");
        ruptureCardPage.navigateBack();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        activeSessionPage.checkStockCorrectionStatus(firstProductLm, true);
        activeSessionPage.checkStockCorrectionStatus(secondProductLm, false);

        //Step 7
        step("Нажать железную кнопку назад и подтвердить выход из сессии");
        activeSessionPage.navigateBack();
        ExitActiveSessionModalPage exitActiveSessionModalPage = new ExitActiveSessionModalPage();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();

        //Step 8
        step("Перейти в обратно в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        activeSessionPage.checkStockCorrectionStatus(firstProductLm, true);
        activeSessionPage.checkStockCorrectionStatus(secondProductLm, false);
    }

    @Test(description = "C23717536 Создание массовой сессии")
    public void testCreateBulkSession()  throws Exception {
        List<ProductData> randomProducts = searchProductHelper.getProducts(2);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        String secondProductLmCode = randomProducts.get(1).getLmCode();

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Нажать на кнопку 'Массово'");
        RupturesScannerPage rupturesScannerPage = sessionListPage.clickScanRupturesBulkButton();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
                .shouldCounterIsCorrect(0)
                .shouldDeleteButtonIsVisible(false)
                .shouldFinishButtonIsVisible(false)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 2
        step("Добавить первый товар через ручной поиск по ЛМ");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.searchProductAndSelect(firstProductLmCode);
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
//                .checkSuccessToast()
                .shouldCounterIsCorrect(1)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 3
        step("Добавить второй товар через ручной поиск по ЛМ");
        searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.searchProductAndSelect(secondProductLmCode);
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
//                .checkSuccessToast()
                .shouldCounterIsCorrect(2)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 4
        step("Выйти из сессии по железной кнопке");
        rupturesScannerPage.navigateBack();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyLastBulkSessionData(2);
    }

    @Test(description = "C23437718 Добавление товаров в массовую сессию")
    public void testAddProductToBulkSession()  throws Exception {
        List<ProductData> randomProducts = searchProductHelper.getProducts(3);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        String secondProductLmCode = randomProducts.get(1).getLmCode();
        String thirdProductLmCode = randomProducts.get(2).getLmCode();
        int sessionId = rupturesHelper.createBulkSession(Arrays.asList(firstProductLmCode, secondProductLmCode));
        sessionsNumbers.set(sessionId);

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Тапнуть на массовую сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        RupturesScannerPage rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
                .shouldCounterIsCorrect(2)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 2
        step("Добавить третий товар через ручной поиск по ЛМ");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.searchProductAndSelect(thirdProductLmCode);
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
//                .checkSuccessToast()
                .shouldCounterIsCorrect(3)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 3
        step("Выйти из сессии нажав на крестик закрытия сканера");
        rupturesScannerPage.closeScanner();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyActiveBulkSessionDataBySessionId(3, sessionId);
    }

    @Test(description = "C23438915 Добавление дубля в массовую сессию")
    public void testAddDuplicateProductToBulkSession()  throws Exception {
        List<ProductData> randomProducts = searchProductHelper.getProducts(2);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        String secondProductLmCode = randomProducts.get(1).getLmCode();
        int sessionId = rupturesHelper.createBulkSession(Arrays.asList(firstProductLmCode, secondProductLmCode));
        sessionsNumbers.set(sessionId);

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Тапнуть на массовую сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        RupturesScannerPage rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
                .shouldCounterIsCorrect(2)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 2
        step("Добавить третий товар через ручной поиск по ЛМ");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.searchProductAndSelect(secondProductLmCode);
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
//                .shouldCounterIsCorrect(2) TODO Вернуть после исправления RUP-335, возможно, дополнить
                .shouldCounterIsCorrect(3)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 3
        step("Выйти из сессии нажав на крестик закрытия сканера");
        rupturesScannerPage.closeScanner();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyActiveBulkSessionDataBySessionId(2, sessionId);
    }

    @Test(description = "C23437719 Завершение массовой сессии")
    public void testFinishBulkSession() throws Exception {
        List<ProductData> randomProducts = searchProductHelper.getProducts(2);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        String secondProductLmCode = randomProducts.get(1).getLmCode();
        int sessionId = rupturesHelper.createBulkSession(Arrays.asList(firstProductLmCode, secondProductLmCode));
        sessionsNumbers.set(sessionId);

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Тапнуть на массовую сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        RupturesScannerPage rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
                .shouldCounterIsCorrect(2)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        // Step 2
        step("Тапнуть на кнопку завершения сессии");
        FinishSessionAcceptModalPage finishSessionAcceptModalPage = rupturesScannerPage.finishBulkSession();
        finishSessionAcceptModalPage.verifyRequiredElements();

        // Step 3
        step("Отменить завершение по кнопке на модалке");
        finishSessionAcceptModalPage.cancel();
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.verifyRequiredElements();

        // Step 4
        step("Нажать кнопку завершить и подтвердить завершение сессии");
        finishSessionAcceptModalPage = rupturesScannerPage.finishBulkSession();
        finishSessionAcceptModalPage.finish();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements();
//        sessionListPage.checkSuccessToast(); TODO рассмотреть возможность поиска тоста во время waitForPageIsLoaded
        sessionListPage.shouldActiveSessionsHaveNotContainSession(String.valueOf(sessionId));
        sessionListPage.shouldFinishedSessionsContainSession(String.valueOf(sessionId));
        rupturesHelper.checkSessionIsFinished(sessionId);

        // Step 5
        step("Тапнуть на завершенную массовую сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        sessionListPage.checkFinishedBulkSessionToast();
    }

    @Test(description = "C23437720 Удаление массовой сессии")
    public void testDeleteBulkSession() throws Exception {
        List<ProductData> randomProducts = searchProductHelper.getProducts(1);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        int sessionId = rupturesHelper.createBulkSession(Collections.singletonList(firstProductLmCode));
        sessionsNumbers.set(sessionId);

        // Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        // Step 1
        step("Тапнуть на массовую сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        RupturesScannerPage rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesBulkLblIsVisible()
                .shouldCounterIsCorrect(1)
                .shouldDeleteButtonIsVisible(true)
                .shouldFinishButtonIsVisible(true)
                .shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        // Step 2
        step("Тапнуть на кнопку удаления сессии");
        DeleteSessionModalPage deleteSessionModalPage = rupturesScannerPage.deleteBulkSession();
        deleteSessionModalPage.verifyRequiredElements();

        // Step 3
        step("Отменить удаление железной кнопкой");
        deleteSessionModalPage.navigateBack();
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.verifyRequiredElements();

        // Step 4
        step("Нажать кнопку удаления и подтвердить удаление сессии");
        deleteSessionModalPage = rupturesScannerPage.deleteBulkSession();
        deleteSessionModalPage.confirmDelete();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements();
        sessionListPage.shouldActiveSessionsHaveNotContainSession(String.valueOf(sessionId));
        sessionListPage.shouldFinishedSessionsHaveNotContainSession(String.valueOf(sessionId));
        rupturesHelper.checkSessionIsDeleted(sessionId);
    }
}
