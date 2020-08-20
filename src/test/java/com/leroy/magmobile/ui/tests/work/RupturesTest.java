package com.leroy.magmobile.ui.tests.work;

import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.ruptures.*;
import com.leroy.magmobile.ui.pages.work.ruptures.data.ActiveSessionData;
import com.leroy.magmobile.ui.pages.work.ruptures.data.RuptureData;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.DeleteRuptureModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.DeleteSessionModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.modal.ExitActiveSessionModalPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;

@Guice(modules = {Module.class})
public class RupturesTest extends AppBaseSteps {
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
        String[] taskAfterChangeArray = new String[taskAfterChange.size()];
        ActionsModalPage actionsModalPage = ruptureCardPage.callActionModalPage();
        actionsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(taskAfterChange.toArray(taskAfterChangeArray));

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
        String[] toDoTasksArray = new String[toDoTasks.size()];
        toDoTasksArray = toDoTasks.toArray(toDoTasksArray);
        ruptureCardPage = actionsModalPage.closeModal();
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasksArray);

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
        ruptureCardPage.shouldTasksListContainsTasks(toDoTasksArray)
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
        RupturesListPage rupturesListPage = rupturesScannerPage.navigateToRuptureProductList();
        rupturesListPage.verifyRequiredElements()
                .shouldRupturesDataIsCorrect(secondAddedRupture, firstAddedRupture);

        //Step 14
        step("Выйти из сессии по железной кнопке");
        ExitActiveSessionModalPage exitActiveSessionModalPage = rupturesListPage.exitActiveSession();
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
        RupturesListPage rupturesListPage = new RupturesListPage();
        rupturesListPage.verifyRequiredElements();
        ActiveSessionData activeSessionData = rupturesListPage.getActiveSessionData();

        //Step 5
        step("Выйти из сессии нажав стрелку назад");
        ExitActiveSessionModalPage exitActiveSessionModalPage = rupturesListPage.exitActiveSession();
        exitActiveSessionModalPage.verifyRequiredElements();

        //Step 6
        step("Подтвердить выход из сессии");
        exitActiveSessionModalPage.confirmExit();
        sessionListPage = new SessionListPage();
        sessionListPage.verifyRequiredElements()
                .shouldActiveSessionContainsSession(activeSessionData);
    }

    @Test(description = "C3272525 Удаление перебоя из сессии")
    public void testDeleteRuptureFromSession() throws Exception {
        int sessionId = ruptureClient.getActiveSessionWithProducts();
        List<RuptureProductData> sessionProducts = ruptureClient.getProducts(sessionId).asJson().getItems();
        String randomLmCode = sessionProducts.get((int)(Math.random()*sessionProducts.size())).getLmCode();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();
        RupturesListPage rupturesListPage = sessionListPage.goToSession(String.valueOf(sessionId));

        //Step 1
        step("Тапнуть на перебой");
        RuptureCardPage ruptureCardPage = rupturesListPage.goToRuptureCard(randomLmCode);
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
        rupturesListPage = new RupturesListPage();
        rupturesListPage.shouldRuptureIsNotInList(randomLmCode);
    }

    @Test(description = "C3272526 Удаление сессии")
    public void testDeleteSession() throws Exception {
        int sessionId = ruptureClient.getActiveSessionWithProducts();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Перейти в активную сессию");
        RupturesListPage rupturesListPage = sessionListPage.goToSession(String.valueOf(sessionId));
        rupturesListPage.verifyRequiredElements();
        ActiveSessionData data = rupturesListPage.getActiveSessionData();

        //Step 2
        step("Нажать на корзину (справа сверху)");
        DeleteSessionModalPage deleteSessionModalPage = rupturesListPage.deleteSession();
        deleteSessionModalPage.verifyRequiredElements();

        //Step 3
        step("Отменить удаление сессии");
        deleteSessionModalPage.cancelDelete();
        rupturesListPage = new RupturesListPage();
        rupturesListPage.verifyRequiredElements();

        //Step 4
        step("Нажать на корзину еще раз");
        deleteSessionModalPage = rupturesListPage.deleteSession();
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
        int sessionId = ruptureClient.getActiveSessionWithProducts();

        //Pre-conditions
        WorkPage workPage = loginAndGoTo(WorkPage.class);
        SessionListPage sessionListPage = workPage.goToRuptures();

        //Step 1
        step("Перейти в активную сессию");
        RupturesListPage rupturesListPage = sessionListPage.goToSession(String.valueOf(sessionId));
        rupturesListPage.verifyRequiredElements();

        //Step 2
        step("Нажать кнопку \"+ перебой\"");
        RupturesScannerPage rupturesScannerPage = rupturesListPage.addRuptureToSession();
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
        rupturesListPage = new RupturesListPage();
        rupturesListPage.verifyRequiredElements()
                .shouldRuptureCounterIsCorrect(ruptureCounter)
                .shouldRupturesDataIsCorrect(data);
    }



}
