package com.leroy.magmobile.ui.tests.work;

import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.BottomMenuPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.ruptures.ActionsModalPage;
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
        ProductItemData randomProduct = searchClient.getRandomProduct();

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
        searchProductPage.enterTextInSearchFieldAndSubmit(randomProduct.getLmCode());
        RuptureCard ruptureCard = new RuptureCard();
        ruptureCard.verifyRequiredElements();

        //Step 3
        step("Сменить количество на 3+");
        List<String> taskBeforeChange = ruptureCard.getTasksList();
        boolean appointBtnVisibility = ruptureCard.appointTaskBtnVisibility();
        ruptureCard.choseProductQuantityOption(RuptureCard.QuantityOption.THREE_OR_MORE);
        ruptureCard.shouldTasksHasChanged(taskBeforeChange, appointBtnVisibility);

        //Step 4
        step("Открыть модалку добавления экшенов (карандашик в блоке экшенов или кнопка \"назначить задачи\")");
        ActionsModalPage actionsModalPage = ruptureCard.callActionModalPage();
    }

}
