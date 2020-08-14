package com.leroy.magmobile.ui.tests.work;

import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.ruptures.ActionsModalPage;
import com.leroy.magmobile.ui.pages.work.ruptures.SessionProductsListPage;
import com.leroy.magmobile.ui.pages.work.ruptures.RuptureCard;
import com.leroy.magmobile.ui.pages.work.ruptures.RupturesScannerPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;

@Guice(modules = {Module.class})
public class RupturesTest extends AppBaseSteps {
    RupturesClient client;
    CatalogSearchClient searchClient;

    @BeforeClass
    private void initClients() {
        client = apiClientProvider.getRupturesClient();
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
        ResRuptureSessionDataList activeSessionsData = client.getSessions("active", 1).asJson();

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
        String secondProductLmCode = randomProducts.get(1).getLmCode();;

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
        RuptureCard ruptureCard = new RuptureCard();
        ruptureCard.verifyRequiredElements();

        //Step 3
        step("Сменить количество на 3+");
        List<String> taskBeforeChange = ruptureCard.getTasksList();
        ruptureCard.choseProductQuantityOption(RuptureCard.QuantityOption.THREE_OR_MORE);
        ruptureCard.shouldTasksHasChanged(taskBeforeChange);


        //Step 4
        step("Открыть модалку добавления экшенов (карандашик в блоке экшенов или кнопка \"назначить задачи\")");
        List<String> taskAfterChange = ruptureCard.getTasksList();
        String[] taskAfterChangeArray = new String[taskAfterChange.size()];
        ActionsModalPage actionsModalPage = ruptureCard.callActionModalPage();
        actionsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(taskAfterChange.toArray(taskAfterChangeArray));

        //Step 5
        step("Добавить пару экшенов (+)\n" +
                "Убрать один из ранее рассчитанных экшенов (-)");
        List<String> possibleTasks = actionsModalPage.getPossibleTasks();
        int randomTaskIndex = (int)(Math.random()* possibleTasks.size());
        String firstRandomTask = possibleTasks.get(randomTaskIndex);
        possibleTasks.remove(randomTaskIndex);
        randomTaskIndex = (int)(Math.random()* possibleTasks.size());
        String secondRandomTask = possibleTasks.get(randomTaskIndex);

        actionsModalPage.choseTasks(firstRandomTask, secondRandomTask);
        actionsModalPage.shouldToDoListContainsTaskAndPossibleListNotContainsTask(firstRandomTask, secondRandomTask);

        List<String> toDoTasks = actionsModalPage.getToDoTasks();
        randomTaskIndex = (int)(Math.random()* toDoTasks.size());
        firstRandomTask = toDoTasks.get(randomTaskIndex);
        actionsModalPage.choseTasks(firstRandomTask);
        actionsModalPage.shouldToDoListNotContainsTaskAndPossibleListContainsTask(firstRandomTask);

        //Step 6
        step("Закрыть модалку редактирования экшенов");
        toDoTasks = actionsModalPage.getToDoTasks();
        String[] toDoTasksArray = new String[toDoTasks.size()];
        toDoTasksArray = toDoTasks.toArray(toDoTasksArray);
        ruptureCard = actionsModalPage.closeModal();
        ruptureCard.shouldTasksListContainsTasks(toDoTasksArray);

        //Step 7
        step("Чекнуть один из экшенов");
        randomTaskIndex = (int)(Math.random()* toDoTasks.size());
        firstRandomTask = toDoTasks.get(randomTaskIndex);
        ruptureCard.setTasksCheckBoxes(firstRandomTask);
        ruptureCard.shouldCheckBoxConditionIsCorrect(true, firstRandomTask);

        //Step 8
        step("Добавить любой текст в поле комментария");
        String comment = "asd123";
        ruptureCard.setComment(comment);
        ruptureCard.shouldSubmitCommentBtnIsActive();

        ruptureCard.submitComment();
        ruptureCard.shouldCommentFieldHasText(comment);

        //Step 9
        step("Перейти в основную карточку товара (пункт \"подробнее о товаре\")");
        ProductCardPage productCardPage = ruptureCard.navigateToProductCard();
        productCardPage.verifyRequiredElements(false);

        //Step 10
        step("Вернуться назад на карточку перебоя");
        ruptureCard = productCardPage.returnBack(RuptureCard.class);
        ruptureCard.shouldTasksListContainsTasks(toDoTasksArray)
                .shouldCheckBoxConditionIsCorrect(true, firstRandomTask)
                .shouldRadioBtnHasCorrectCondition(RuptureCard.QuantityOption.THREE_OR_MORE)
                .shouldCommentFieldHasText(comment);

        //Step 11
        step("Подтвердить добавление перебоя в сессию");
        rupturesScannerPage = ruptureCard.acceptAdd();
        rupturesScannerPage.shouldCounterIsCorrect(1);

        //Step 12
        step("Добавить в сессию еще один перебой");
        searchProductPage = rupturesScannerPage.navigateToSearchProductPage();
        searchProductPage.enterTextInSearchFieldAndSubmit(secondProductLmCode);
        ruptureCard = new RuptureCard();
        rupturesScannerPage = ruptureCard.acceptAdd();
        rupturesScannerPage.shouldCounterIsCorrect(2);

        //Step 13
        step("Тапнуть на кнопку \"список перебоев\"");
        SessionProductsListPage sessionProductsListPage = rupturesScannerPage.navigateToRuptureProductList();

    }

}
