package com.leroy.magmobile.ui.tests.work;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.ruptures.*;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.SessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.TaskData;
import com.leroy.magmobile.ui.pages.work.ruptures.enums.Action;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Guice(modules = {Module.class})
public class RupturesTest extends AppBaseSteps {
    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    RupturesClient ruptureClient;
    CatalogSearchClient searchClient;

    @BeforeClass
    private void initClients() {
        ruptureClient = apiClientProvider.getRupturesClient();
        searchClient = apiClientProvider.getCatalogSearchClient();
    }

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("32");
        sessionData.setUserDepartmentId("15");
        sessionData.setAccessToken(getAccessToken());
        return sessionData;
    }

    /**
     * подразумевается что на тест создается 1 сессия (вызывается один из методов создания сессии)
     * даже если сессия не была создана, вернется 400
     */
    @AfterTest
    private void deleteSession() {
        ruptureClient.deleteSession(threadLocal.get());
    }

    private int createSessionWithProductWithoutActions() {
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(null);

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = ruptureClient.createSession(rupturePostData);
        int result = resp.asJson().get("sessionId").intValue();
        threadLocal.set(result);
        return result;
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

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = ruptureClient.createSession(rupturePostData);
        int result = resp.asJson().get("sessionId").intValue();
        threadLocal.set(result);
        return result;
    }

    private int createSessionWithProductWithSpecificIncompleteAction(Action action) {
        List<ActionData> actions = new ArrayList<>();
        ActionData data = new ActionData();
        data.setState(false);
        data.setAction(action.getActionNumber());
        //
        data.setUserPosition(0);
        actions.add(data);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(actions);

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = ruptureClient.createSession(rupturePostData);
        int result = resp.asJson().get("sessionId").intValue();
        threadLocal.set(result);
        return result;
    }

    @Test(description = "C3272519 Перебои на экране работы")
    public void testRupturesOnWorkScreen() throws Exception {
        String shopWithNoRuptures = "62";
        ResRuptureSessionDataList activeSessionsData = ruptureClient.getSessions("active", 1).asJson();

        //Step 1
        step("Перейти на экран \"работа\"");
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        workPage.shouldRupturesNavigationBtnHasCorrectCondition(true)
                .shouldRupturesSessionCounterIsCorrect(activeSessionsData.getTotalCount());
        //Step 2
        step("Перейти в \"еще\" и сменить магазин на 62\n" +
                "Перейти на экран \"работа\"");
        BottomMenuPage bottomMenuPage = new BottomMenuPage();
        setShopAndDepartmentForUser(bottomMenuPage, shopWithNoRuptures, getUserSessionData().getUserDepartmentId());
        workPage = bottomMenuPage.goToWork();
        workPage.shouldRupturesNavigationBtnHasCorrectCondition(false);
    }

    @Test(description = "C3272520 Создание сессии с экрана работы")
    public void testCreateSessionFromWorkPage() throws Exception {
        List<ProductItemData> randomProducts = searchClient.getRandomUniqueProductsWithTitles(2);
        String firstProductLmCode = randomProducts.get(0).getLmCode();
        String secondProductLmCode = randomProducts.get(1).getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        //Step 1
        step("Тапнуть на иконку \"+\"");
        RupturesScannerPage rupturesScannerPage = workPage.createRupturesSession();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 2
        step("Перейти в ручной поиск и найти товар по лм-коду");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(firstProductLmCode);
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        //Step 3
        step("Сменить количество на 3+");
        List<String> taskBeforeChange = ruptureCardPage.getTasksList();
        ruptureCardPage.choseProductQuantityOption(RuptureCardPage.QuantityOption.THREE_OR_MORE);
        ruptureCardPage.shouldTasksHasChanged(taskBeforeChange);


        //Step 4
        step("Открыть модалку добавления экшенов (карандашик в блоке экшенов или кнопка \"назначить задачи\")");
        List<String> taskAfterChange = ruptureCardPage.getTasksList();
        ActionsModalPage actionsModalPage = ruptureCardPage.callActionModalPage();
        actionsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(taskAfterChange);

        //Step 5
        step("Добавить пару экшенов (+)\n" +
                "Убрать один из ранее рассчитанных экшенов (-)");
        List<String> possibleTasks = actionsModalPage.getPossibleTasks();
        int randomTaskIndex = (int) (Math.random() * possibleTasks.size());
        String firstRandomTask = possibleTasks.get(randomTaskIndex);
        possibleTasks.remove(randomTaskIndex);
        randomTaskIndex = (int) (Math.random() * possibleTasks.size());
        String secondRandomTask = possibleTasks.get(randomTaskIndex);

        actionsModalPage.choseTasks(firstRandomTask, secondRandomTask);
        actionsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(firstRandomTask, secondRandomTask);

        List<String> toDoTasks = actionsModalPage.getToDoTasks();
        randomTaskIndex = (int) (Math.random() * toDoTasks.size());
        firstRandomTask = toDoTasks.get(randomTaskIndex);
        actionsModalPage.choseTasks(firstRandomTask);
        actionsModalPage.shouldToDoListNotContainsTaskAndPossibleListContainsTask(firstRandomTask);

        //Step 6
        step("Закрыть модалку редактирования экшенов");
        toDoTasks = actionsModalPage.getToDoTasks();
        ruptureCardPage = actionsModalPage.closeModal();
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasks);

        //Step 7
        step("Чекнуть один из экшенов");
        randomTaskIndex = (int) (Math.random() * toDoTasks.size());
        firstRandomTask = toDoTasks.get(randomTaskIndex);
        ruptureCardPage.setTasksCheckBoxes(firstRandomTask);
        ruptureCardPage.shouldCheckBoxConditionIsCorrect(true, firstRandomTask);

        //Step 8
        step("Добавить любой текст в поле комментария");
        String comment = "asd123";
        ruptureCardPage.setComment(comment);
        ruptureCardPage.shouldSubmitCommentBtnIsActive();

        ruptureCardPage.submitComment();
        ruptureCardPage.shouldCommentFieldHasText(comment);

        //Step 9
        step("Перейти в основную карточку товара (пункт \"подробнее о товаре\")");
        ProductCardPage productCardPage = ruptureCardPage.navigateToProductCard();
        productCardPage.verifyRequiredElements(false);

        //Step 10
        step("Вернуться назад на карточку перебоя");
        ruptureCardPage = productCardPage.returnBack(RuptureCardPage.class);
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, firstRandomTask)
                .shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.THREE_OR_MORE)
                .shouldCommentFieldHasText(comment);

        //Step 11
        step("Подтвердить добавление перебоя в сессию");
        RuptureData firstAddedRupture = ruptureCardPage.getRuptureData();
        rupturesScannerPage = ruptureCardPage.acceptAdd();
        rupturesScannerPage.shouldCounterIsCorrect(1);

        //Step 12
        step("Добавить в сессию еще один перебой");
        searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(secondProductLmCode);
        ruptureCardPage = new RuptureCardPage();
        RuptureData secondAddedRupture = ruptureCardPage.getRuptureData();
        rupturesScannerPage = ruptureCardPage.acceptAdd();
        rupturesScannerPage.shouldCounterIsCorrect(2);

        //Step 13
        step("Тапнуть на кнопку \"список перебоев\"");
        ActiveSessionPage activeSessionPage = rupturesScannerPage.navigateToRuptureProductList();
        activeSessionPage.shouldRupturesDataIsCorrect(secondAddedRupture, firstAddedRupture)
                .verifyRequiredElements();

        //Step 14
        step("Выйти из сессии по железной кнопке");
        ExitActiveSessionModalPage exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.verifyRequiredElements();

        //Step 15
        step("Подтвердить выход из сессии");
        exitActiveSessionModalPage.confirmExit();
        workPage = new WorkPage();
        workPage.verifyRequiredElements();
    }

    @Test(description = "C3272521 Создание сессии со списка сессий")
    public void testCreateSessionFromSessionList() throws Exception {
        ProductItemData product = searchClient.getRandomProduct();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Нажать на кнопку сканирования перебоев");
        RupturesScannerPage rupturesScannerPage = sessionListPage.callScannerPage();
        rupturesScannerPage.verifyRequiredElements();

        //Step 2
        step("Перейти в ручной поиск и найти любой товар.\n" +
                "Клинкнуть на него.");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(product.getLmCode());
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        //Step 3
        step("Подтвердить добавление перебоя в сессию");
        rupturesScannerPage = ruptureCardPage.acceptAdd();
        rupturesScannerPage.shouldCounterIsCorrect(1)
                .shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();

        //Step 4
        step("Закрыть сканер по кнопке \"х\"");
        rupturesScannerPage.closeScanner();
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();
        SessionData sessionData = activeSessionPage.getSessionData();

        //Step 5
        step("Выйти из сессии нажав стрелку назад");
        ExitActiveSessionModalPage exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.verifyRequiredElements();

        //Step 6
        step("Подтвердить выход из сессии");
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements()
                .shouldActiveSessionContainsSession(sessionData);
    }

    @Test(description = "C3272525 Удаление перебоя из сессии")
    public void testDeleteRuptureFromSession() throws Exception {
        int sessionId = ruptureClient.getActiveSessionWithProductsId();
        List<RuptureProductData> sessionProducts = ruptureClient.getProducts(sessionId).asJson().getItems();
        String randomLmCode = sessionProducts.get((int) (Math.random() * sessionProducts.size())).getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 1
        step("Тапнуть на перебой");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(randomLmCode);
        ruptureCardPage.verifyRequiredElements();

        //Step 2
        step("Нажать на корзину");
        DeleteRuptureModalPage deleteRuptureModalPage = ruptureCardPage.deleteRupture();
        deleteRuptureModalPage.verifyRequiredElements();

        //Step 3
        step("Отменить удаление");
        deleteRuptureModalPage.cancelDelete();
        ruptureCardPage.verifyRequiredElements();

        //Step 4
        step("Нажать на корзину еще раз");
        deleteRuptureModalPage = ruptureCardPage.deleteRupture();
        deleteRuptureModalPage.verifyRequiredElements();

        //Step 5
        step("Подтвердить удаление");
        deleteRuptureModalPage.confirmDelete();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRuptureIsNotInList(randomLmCode);
    }

    @Test(description = "C3272526 Удаление сессии")
    public void testDeleteSession() throws Exception {
        int sessionId = ruptureClient.getActiveSessionWithProductsId();

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
        step("Отменить удаление сессии");
        deleteSessionModalPage.cancelDelete();
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
        sessionListPage.shouldActiveSessionHasNotContainsSession(data);
    }

    @Test(description = "C3272522 Добавление перебоев в сессию")
    public void testAddRuptureToSession() throws Exception {
        ProductItemData product = searchClient.getRandomProduct();
        int sessionId = ruptureClient.getActiveSessionWithProductsId();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Перейти в активную сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();

        //Step 2
        step("Нажать кнопку \"+ перебой\"");
        RupturesScannerPage rupturesScannerPage = activeSessionPage.addRuptureToSession();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();
        int ruptureCounter = rupturesScannerPage.getCounterValue();

        //Step 3
        step("Перейти к ручному поиску товара и найти любой товар");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(product.getLmCode());
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        RuptureData data = ruptureCardPage.getRuptureData();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        //Step 4
        step("Нажать кнопку подтверждения");
        rupturesScannerPage = ruptureCardPage.acceptAdd();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .shouldCounterIsCorrect(++ruptureCounter)
                .verifyRequiredElements();

        //Step 5
        step("Закрыть сканер по железной кнопке");
        rupturesScannerPage.closeScanner();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRupturesDataIsCorrect(data)
                .verifyRequiredElements();
        activeSessionPage.shouldRuptureCounterIsCorrect(ruptureCounter);
    }

    @Test(description = "C3272523 Добавление дубля в сессию при создании сессии")
    public void testAddRuptureDuplicateToSession() throws Exception {
        ProductItemData randomProduct = searchClient.getRandomProduct();
        String lmCode = randomProduct.getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        //Step 1
        step("Перейти к добавлению сессии");
        RupturesScannerPage rupturesScannerPage = workPage.createRupturesSession();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(false)
                .verifyRequiredElements();

        //Step 2
        step("Перейти к ручному поиску и найти любой товар");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElementsWhenCreateRupture();

        //Step 3
        step("Подтвердить добавление перебоя");
        rupturesScannerPage = ruptureCardPage.acceptAdd();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();

        //Step 4
        step("Перейти к ручному поиску и найти тот же товар");
        searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddDuplicateModalPage addDuplicateModalPage = new AddDuplicateModalPage();
        addDuplicateModalPage.verifyRequiredElements();

        //Step 5
        step("Нажать на кнопку \"понятно\"");
        ruptureCardPage = addDuplicateModalPage.confirm();
        ruptureCardPage.verifyRequiredElements();

        //Step 6
        step("Выставить для перебоя количество \"на полке\" равное 3+");
        List<String> tasksBefore = ruptureCardPage.getTasksList();
        ruptureCardPage.choseProductQuantityOption(RuptureCardPage.QuantityOption.THREE_OR_MORE);
        ruptureCardPage.shouldTasksHasChanged(tasksBefore);

        //Step 7
        step("Закрыть карточку перебоя (кнопка \"х\" в левом углу)");
        RuptureData data = ruptureCardPage.getRuptureData();
        ruptureCardPage.closeRuptureCardPage();
        searchProductPage.verifyRequiredElements();

        //Step 8
        step("Закрыть страницу ручного поиска");
        searchProductPage.returnBack();
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .shouldCounterIsCorrect(1)
                .verifyRequiredElements();

        //Step 9
        step("Нажать на кнопку \"список перебоев\"");
        ActiveSessionPage activeSessionPage = rupturesScannerPage.navigateToRuptureProductList();
        activeSessionPage.shouldRuptureQuantityIsCorrect(1)
                .verifyRequiredElements();

        //Step 10
        step("Тапнуть на перебой");
        ruptureCardPage = activeSessionPage.goToRuptureCard(lmCode);
        ruptureCardPage.shouldRuptureDataIsCorrect(data)
                .shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.THREE_OR_MORE);
    }

    @Test(description = "C23418142 Добавление дубля в сессию при работе с существующей сессией")
    public void testAddRuptureDuplicateToExistedSession() throws Exception {
        String comment = "asd123";
        int sessionId = ruptureClient.getActiveSessionWithProductsId();
        List<RuptureProductData> sessionProducts = ruptureClient.getProducts(sessionId).asJson().getItems();
        String randomLmCode = sessionProducts.get(0).getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 1
        step("Нажать на кнопку + перебой");
        RupturesScannerPage rupturesScannerPage = activeSessionPage.addRuptureToSession();
        rupturesScannerPage.shouldRupturesListNavBtnIsVisible(true)
                .verifyRequiredElements();
        int counterValue = rupturesScannerPage.getCounterValue();

        //Step 2
        step("Перейти к ручному поиску и найти товар, который уже есть в сессии");
        SearchProductPage searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(randomLmCode);
        AddDuplicateModalPage addDuplicateModalPage = new AddDuplicateModalPage();
        addDuplicateModalPage.verifyRequiredElements();

        //Step 3
        step("Нажать на кнопку \"понятно\"");
        addDuplicateModalPage.confirm();
        RuptureCardPage ruptureCardPage = new RuptureCardPage();
        ruptureCardPage.verifyRequiredElements();

        //Step 4
        step("Изменить количество на полке\n" +
                "Добавить 2 любых экшена\n" +
                "Чекнуть один из экшенов\n" +
                "Добавить комментарий");
        ruptureCardPage.choseProductQuantityOption(RuptureCardPage.QuantityOption.ONE);
        ActionsModalPage actionsModalPage = ruptureCardPage.callActionModalPage();
        List<String> possibleTasksList = actionsModalPage.getPossibleTasks();
        actionsModalPage.choseTasks(possibleTasksList.get(0), possibleTasksList.get(1));
        List<String> toDoTasks = actionsModalPage.getToDoTasks();
        ruptureCardPage = actionsModalPage.closeModal();
        String checkedTask = toDoTasks.get(0);
        ruptureCardPage.setTasksCheckBoxes(checkedTask);
        ruptureCardPage.setComment(comment);
        ruptureCardPage.submitComment();
        RuptureData data = ruptureCardPage.getRuptureData();

        ruptureCardPage.shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.ONE)
                .shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, checkedTask)
                .shouldCommentFieldHasText(comment);

        //Step 5
        step("Выйти с карточки перебоя по железной кнопке");
        ruptureCardPage.closeRuptureCardPage();
        searchProductPage = new SearchProductPage();
        searchProductPage.verifyRequiredElements();

        //Step 6
        step("Закрыть экран поиска по железной кнопке");
        searchProductPage.returnBack();
        rupturesScannerPage = new RupturesScannerPage();
        rupturesScannerPage.shouldCounterIsCorrect(counterValue)
                .shouldRupturesListNavBtnIsVisible(true);

        //Step 7
        step("Закрыть сканер по железной кнопке");
        rupturesScannerPage.closeScanner();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRupturesDataIsCorrect(data)
                .verifyRequiredElements();

        //Step 8
        step("Открыть карточку перебоя");
        ruptureCardPage = activeSessionPage.goToRuptureCard(randomLmCode);
        ruptureCardPage.shouldRadioBtnHasCorrectCondition(RuptureCardPage.QuantityOption.ONE)
                .shouldTasksListContainsTasks(toDoTasks)
                .shouldCheckBoxConditionIsCorrect(true, checkedTask)
                .shouldCommentFieldHasText(comment);
    }

    @Test(description = "C3272524 Изменение перебоя в активной сессии")
    public void testChangeRuptureInActiveSession() throws Exception {
        String comment = "123asd";
        int sessionId = createSessionWithProductWithoutActions();
        List<RuptureProductData> sessionProducts = ruptureClient.getProducts(sessionId).asJson().getItems();
        String firstRuptureLmCode = sessionProducts.get(0).getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 1
        step("Тапнуть на товар");
        RuptureCardPage ruptureCardPage = activeSessionPage.goToRuptureCard(firstRuptureLmCode);
        ruptureCardPage.verifyRequiredElements();

        //Step 2
        step("Изменить количество \"вижу на полке\"\n" +
                "Добавить экшенов до 3 штук\n" +
                "Чекнуть один из экшенов\n" +
                "Добавить комментарий");
        ruptureCardPage.choseProductQuantityOption(RuptureCardPage.QuantityOption.THREE_OR_MORE);
        ActionsModalPage actionsModalPage = ruptureCardPage.callActionModalPage();
        List<String> possibleTasksList = actionsModalPage.getPossibleTasks();
        actionsModalPage.choseTasks(possibleTasksList.get(0), possibleTasksList.get(1), possibleTasksList.get(2));
        List<String> toDoTasks = actionsModalPage.getToDoTasks();
        ruptureCardPage = actionsModalPage.closeModal();
        String firstCheckedTask = toDoTasks.get(0);
        ruptureCardPage.setTasksCheckBoxes(firstCheckedTask);
        ruptureCardPage.setComment(comment);
        ruptureCardPage.submitComment();

        //Step 3
        step("Выйти из карточки товара по железной кнопке");
        ruptureCardPage.closeRuptureCardPage();
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.verifyRequiredElements();

        //Step 4
        step("Чекнуть еще 1 экшен");
        String secondCheckTask = toDoTasks.get(1);
        activeSessionPage.checkRuptureActionCheckBox(firstRuptureLmCode, secondCheckTask);
        List<RuptureData> ruptureDataList = activeSessionPage.getRupturesList();

        //Step 5
        step("Выйти из сессии");
        ExitActiveSessionModalPage exitActiveSessionModalPage = activeSessionPage.exitActiveSession();
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements();

        //Step 6
        step("Вернуться в сессию");
        sessionListPage.goToSession(String.valueOf(sessionId));
        activeSessionPage = new ActiveSessionPage();
        activeSessionPage.shouldRupturesDataIsCorrect(ruptureDataList);

        //Step 7
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

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        ActiveSessionPage activeSessionPage = new ActiveSessionPage();

        //Step 1
        step("Нажать кнопку \"завершить\"");
        FinishSessionAcceptModalPage finishSessionAcceptModalPage = activeSessionPage.finishSession();
        finishSessionAcceptModalPage.verifyRequiredElements();

        //Step 2
        step("Отменить завершение");
        activeSessionPage = finishSessionAcceptModalPage.cancel();
        activeSessionPage.verifyRequiredElements();

        //Step 3
        step("Нажать кнопку завершить и подтвердить завершение сессии");
        finishSessionAcceptModalPage = activeSessionPage.finishSession();
        FinishedSessionPage finishedSessionPage = finishSessionAcceptModalPage.finish();
        finishedSessionPage.shouldStatusIsFinished()
                .shouldTasksCountIsCorrect(8)
                .verifyRequiredElements();

        //Step 4
        step("Нажать назад");
        sessionListPage = finishedSessionPage.exitFinishedSession();
        sessionListPage.verifyRequiredElements();
    }

    @Test(description = "C3272528 Изменение перебоев в завершенной сессии")
    public void testChangeRuptureInFinishedSession() throws Exception {
        TaskData firstTaskData = new TaskData();
        firstTaskData.setAllTasksCount(1);
        firstTaskData.setDoneTasksCount(0);
        firstTaskData.setTaskName(Action.GIVE_APOLOGISE.getActionName());
        TaskData secondTaskData = new TaskData();
        secondTaskData.setAllTasksCount(1);
        secondTaskData.setDoneTasksCount(1);
        secondTaskData.setTaskName(Action.REMOVE_PRICE_TAG.getActionName());
        TaskData allTasks = new TaskData();
        allTasks.setAllTasksCount(2);
        allTasks.setDoneTasksCount(1);
        allTasks.setTaskName("Все задачи");

        String comment = "123asd";
        int sessionId = createSessionWithProductWithSpecificIncompleteAction(Action.FIND_PRODUCT_AND_LAY_IT_OUT);
        ruptureClient.finishSession(sessionId);
        List<RuptureProductData> sessionProducts = ruptureClient.getProducts(sessionId).asJson().getItems();
        RuptureProductData ruptureData = sessionProducts.get(0);
        String firstRuptureLmCode = ruptureData.getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage.goToSession(String.valueOf(sessionId));
        FinishedSessionPage finishedSessionPage = new FinishedSessionPage();

        //Step 1
        step("Перейти в раздел \"Найти товар и выложить\"");
        ActionWithSessionRupturesPage actionWithSessionRupturesPage = finishedSessionPage.goToActionPage(Action.FIND_PRODUCT_AND_LAY_IT_OUT);
        actionWithSessionRupturesPage.shouldHeaderContainsActionName(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName())
                .verifyRequiredElements();

        //Step 2
        step("Перейти в карточку перебоя");
        RuptureCardPage ruptureCardPage = actionWithSessionRupturesPage.goToRuptureCard(firstRuptureLmCode)
                .verifyRequiredElementsInFinishedSession();

        //Step 3
        step("Перейти к редактированию списка рекомендаций (карандашик в блоке рекомендаций)");
        ActionsModalPage actionsModalPage = ruptureCardPage.callActionModalPage()
                .verifyRequiredElements();

        //Step 4
        step("Убрать экшен \"найти товар и выложить\"\n" +
                "Добавить экшены \"Поставить извиняшку\" и \"Убрать ценник\"\n" +
                "Закрыть модалку реактирования экшенов");
        actionsModalPage.choseTasks(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName(),
                Action.GIVE_APOLOGISE.getActionName(), Action.REMOVE_PRICE_TAG.getActionName())
                .shouldToDoListContainsTaskAndPossibleListNotContainsTask(
                        Action.GIVE_APOLOGISE.getActionName(), Action.REMOVE_PRICE_TAG.getActionName())
                .closeModal();

        //Step 5
        step("Чекнуть экшен \"убрать ценник\"");
        ruptureCardPage.setTasksCheckBoxes(Action.REMOVE_PRICE_TAG.getActionName());

        //Step 6
        step("Добавить любой комментарий");
        ruptureCardPage.setComment(comment)
                .submitComment();

        //Step 7
        step("Закрыть карточку перебоя");
        ruptureCardPage.closeRuptureCardPage();
        actionWithSessionRupturesPage = new ActionWithSessionRupturesPage();
        actionWithSessionRupturesPage.shouldHeaderContainsActionName(Action.FIND_PRODUCT_AND_LAY_IT_OUT.getActionName())
                .shouldAllRuptureTaskHaveDone();

        //Step 8
        step("Закрыть карточку перебоя");
        finishedSessionPage = actionWithSessionRupturesPage.goBack();
        finishedSessionPage.shouldTasksAreCorrect(firstTaskData, secondTaskData)
                .shouldAllTasksCounterIsCorrect(allTasks);

        //Step 9
        step("Перейти в раздел \"Убрать ценник\"");
        actionWithSessionRupturesPage = finishedSessionPage.goToActionPage(Action.REMOVE_PRICE_TAG);
        actionWithSessionRupturesPage.shouldDoneTasksCounterIsCorrect(1)
                .shouldTasksRatioCounterIsCorrect(1, 1)
                .shouldNoActiveRuptureTasksAreAvailable();

        //Step 10
        step("Тапнуть на \"Выполненные задачи\"");
        RuptureData doneRuptureData = new RuptureData();
        doneRuptureData.setLmCode(firstRuptureLmCode);
        doneRuptureData.setBarCode(ruptureData.getBarCode());
        doneRuptureData.setTitle(ruptureData.getTitle());
        doneRuptureData.setActions(Collections.singletonMap(Action.REMOVE_PRICE_TAG.getActionName(), true));

        actionWithSessionRupturesPage = actionWithSessionRupturesPage.goToDoneTasks();
        actionWithSessionRupturesPage.shouldDoneTasksViewIsPresented()
                .shouldTasksRatioCounterIsCorrect(1)
                .verifyRequiredElements()
                .shouldRuptureDataIsCorrect(doneRuptureData);

        //Step 11
        step("Тапнуть на перебой");
        ruptureCardPage = actionWithSessionRupturesPage.goToRuptureCard(firstRuptureLmCode);
        ruptureCardPage.verifyRequiredElementsInFinishedSession()
                .shouldCheckBoxConditionIsCorrect(false, Action.GIVE_APOLOGISE.getActionName())
                .shouldCheckBoxConditionIsCorrect(true, Action.REMOVE_PRICE_TAG.getActionName())
                .shouldCommentFieldHasText(comment);
    }

    @Test(description = "C3272529 Два списка сессий (1 отдел)")
    public void testTwoSessionLists() throws Exception {
        int departmentId = 1;

        //Pre-conditions
        ruptureClient.deleteAllSessionInDepartment(departmentId);
        List<Integer> sessionsIdList = ruptureClient.createFewSessions(departmentId, 20);
        List<Integer> activeSessionsIdList = sessionsIdList.subList(0, 10);
        List<Integer> finishedSessionsIdList = sessionsIdList.subList(10, sessionsIdList.size());
        ruptureClient.finishFewSessions(finishedSessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);


        //Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldAllActiveSessionAreVisible(activeSessionsIdList)
                .shouldAllFinishedSessionAreVisible(finishedSessionsIdList)
                .verifyRequiredElements();
    }

    @Test(description = "C23423650 Пустой список сессий (2 отдел)")
    public void testEmptySessionList() throws Exception {
        int departmentId = 2;

        //Pre-conditions
        ruptureClient.deleteAllSessionInDepartment(departmentId);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        //Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldNoSessionMsgLblIsVisible();
    }

    @Test(description = "C23423651 Пагинация списка активных сессий, пустой список завершенных сессий (3 отдел)")
    public void testActiveSessionPagination() throws Exception {
        int departmentId = 3;

        //Pre-conditions
        ruptureClient.deleteAllSessionInDepartment(departmentId);
        List<Integer> sessionsIdList = ruptureClient.createFewSessions(departmentId, 11);
        WorkPage workPage = loginAndGoTo(WorkPage.class);


        //Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldAllActiveSessionAreVisible(sessionsIdList)
                .shouldFinishedSessionCardsIsNotVisible()
                .verifyRequiredElements();
    }

    @Test(description = "C23423652 Пагинация списка завершенных сессий, пустой список активных сессий (4 отдел)")
    public void testFinishedSessionPagination() throws Exception {
        int departmentId = 4;

        //Pre-conditions
        ruptureClient.deleteAllSessionInDepartment(departmentId);
        List<Integer> sessionsIdList = ruptureClient.createFewSessions(departmentId, 11);
        ruptureClient.finishFewSessions(sessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);

        //Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.verifyRequiredElements()
                .shouldAllFinishedSessionAreVisible(sessionsIdList);
    }

    @Test(description = "C23423653 Пагинация обоих списков сессий (5 отдел)")
    public void testBothSessionTypesPagination() throws Exception {
        int departmentId = 5;

        //Pre-conditions
        ruptureClient.deleteAllSessionInDepartment(departmentId);
        List<Integer> sessionsIdList = ruptureClient.createFewSessions(departmentId, 22);
        List<Integer> activeSessionsIdList = sessionsIdList.subList(0, 11);
        List<Integer> finishedSessionsIdList = sessionsIdList.subList(11, sessionsIdList.size());
        ruptureClient.finishFewSessions(finishedSessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);


        //Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldAllActiveSessionAreVisible(activeSessionsIdList)
                .shouldAllFinishedSessionAreVisible(finishedSessionsIdList)
                .verifyRequiredElements();
    }

    @Test(description = "C23423655 Pull-to-refresh (6 отдел)")
    public void testPullToRefreshSessionLists() throws Exception {
        int departmentId = 6;

        //Pre-conditions
        ruptureClient.deleteAllSessionInDepartment(departmentId);
        List<Integer> sessionsIdList = ruptureClient.createFewSessions(departmentId, 10);
        List<Integer> activeSessionsIdList = sessionsIdList.stream().limit(5).collect(Collectors.toList());
        List<Integer> finishedSessionsIdList = sessionsIdList.stream().skip(5).limit(5).collect(Collectors.toList());
        ruptureClient.finishFewSessions(finishedSessionsIdList);
        WorkPage workPage = loginAndGoTo(WorkPage.class);


        //Step 1
        step("Перейти на экран списка сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(departmentId);
        sessionListPage.shouldAllActiveSessionAreVisible(activeSessionsIdList)
                .shouldAllFinishedSessionAreVisible(finishedSessionsIdList);

        //Step 2
        step("Удалить 1 активную и 1 завершенную сессию (через АПИ ruptures/session?sessionId={id})\n" +
                "Потянуть список сессий вниз (pull to refresh)");
        int lastActiveSessionIdIndex = activeSessionsIdList.size() - 1;
        int lastFinishedSessionIdIndex = finishedSessionsIdList.size() - 1;
        int lastActiveSessionId = activeSessionsIdList.get(lastActiveSessionIdIndex);
        int lastFinishedSessionId = finishedSessionsIdList.get(lastFinishedSessionIdIndex);
        finishedSessionsIdList.remove(lastFinishedSessionIdIndex);
        activeSessionsIdList.remove(lastActiveSessionIdIndex);
        ruptureClient.deleteFewSessions(lastActiveSessionId, lastFinishedSessionId);
        sessionListPage = sessionListPage.pullToRefresh();
        sessionListPage.shouldAllActiveSessionAreVisible(activeSessionsIdList)
                .shouldAllFinishedSessionAreVisible(finishedSessionsIdList);
    }

    @Test(description = "C23423654 Смена отдела (7 отдел)")
    public void testChangeDepartment() throws Exception {
        //Pre-conditions
        int seventhDepartmentId = 7;
        ruptureClient.deleteAllSessionInDepartment(seventhDepartmentId);
        List<Integer> seventhSessionsIdList = ruptureClient.createFewSessions(seventhDepartmentId, 4);
        List<Integer> seventhActiveSessionsIdList = seventhSessionsIdList.stream().limit(2).collect(Collectors.toList());
        List<Integer> seventhFinishedSessionsIdList = seventhSessionsIdList.stream().skip(2).collect(Collectors.toList());
        ruptureClient.finishFewSessions(seventhFinishedSessionsIdList);

        int firstDepartmentId = 1;
        ruptureClient.deleteAllSessionInDepartment(firstDepartmentId);
        List<Integer> firstSessionsIdList = ruptureClient.createFewSessions(firstDepartmentId, 2);
        List<Integer> firstActiveSessionsIdList = firstSessionsIdList.subList(0, 1);
        List<Integer> firstFinishedSessionsIdList = firstSessionsIdList.subList(1, firstSessionsIdList.size());
        ruptureClient.finishFewSessions(firstFinishedSessionsIdList);

        WorkPage workPage = loginAndGoTo(WorkPage.class);


        //Step 1
        step("Перейти в список сессий");
        SessionListPage sessionListPage = workPage.goToRuptures();
        sessionListPage = sessionListPage.changeDepartment(seventhDepartmentId);
        sessionListPage.shouldAllActiveSessionAreVisible(seventhActiveSessionsIdList)
                .shouldAllFinishedSessionAreVisible(seventhFinishedSessionsIdList);

        //Step 2
        step("Выбрать 1 отдел");
        sessionListPage = sessionListPage.changeDepartment(firstDepartmentId);
        sessionListPage.shouldAllActiveSessionAreVisible(firstActiveSessionsIdList)
                .shouldAllFinishedSessionAreVisible(firstFinishedSessionsIdList);
    }

}
