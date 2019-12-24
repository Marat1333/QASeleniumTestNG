package com.leroy.tests.app;

import com.leroy.constants.EnvConstants;
import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.common.SearchProductPage;
import com.leroy.pages.app.sales.SalesPage;
import com.leroy.pages.app.common.NomenclatureSearchPage;
import com.leroy.pages.app.sales.ProductCardPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class SearchTests extends BaseState {
    @Test(description = "C3200996 Поиск товара по критериям")
    public void testC3200996() throws Exception{

        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String barCode = "5902120110575";
        String shortLmCode = "100";
        String shortBarCode = "590212011";
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME,EnvConstants.BASIC_USER_PASS);

        log.step("Зайдите в раздел Продажа, Товары и услуги");
        LoginPage loginPage = new LoginPage(context);
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);

        log.step("Нажмите на поле Поиск товаров и услуг");
        SalesPage salesPage = new SalesPage(context);
        SearchProductPage searchProductPage = salesPage.selectSearchString();
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnToDepartmentChoseWindow();
        nomenclatureSearchPage.viewAllProducts();

        log.step("Нажмите на поле ввода");
        log.step("Введите полное значение для поиска по ЛМ коду| 10008698");
        searchProductPage.enterTextInSearchField(lmCode);
        ProductCardPage productCardPage = new ProductCardPage(context);
        productCardPage.verifyRequiredContext(lmCode);
        productCardPage.returnBack();

        log.step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchField(searchContext);
        searchProductPage.shouldProductCardsContainText(searchContext);

        log.step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchField(barCode);
        productCardPage.verifyRequiredContext(barCode);
        productCardPage.returnBack();

        log.step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchField(shortLmCode);
        searchProductPage.shouldProductCardsContainText(shortLmCode);

        log.step("ввести в поисковую строку положительное число длинной >8 символов (4670009014962) и инициировать поиск");
        searchProductPage.enterTextInSearchField(shortBarCode);
        searchProductPage.shouldProductCardsContainText(shortBarCode);
    }
}
