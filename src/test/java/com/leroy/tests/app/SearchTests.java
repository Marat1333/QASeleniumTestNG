package com.leroy.tests.app;

import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.common.NomenclatureSearch;
import com.leroy.pages.app.common.SearchProductPage;
import com.leroy.pages.app.sales.ProductCardPage;
import com.leroy.pages.app.sales.SalesPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class SearchTests extends BaseState {
    @Test(description = "Поиск товара по критериям")
    public void c3200996() throws Exception{

        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String barCode = "5902120110575";
        String shortLmCode = "100";
        String shortBarCode = "590212011";

        log.step("Зайдите в раздел Продажа, Товары и услуги");
        LoginPage loginPage = new LoginPage(context);
        SalesPage salesPage = new SalesPage(context);
        UserData seller = new UserData("60069807","Passwd123");
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);

        log.step("Нажмите на поле Поиск товаров и услуг");
        SearchProductPage searchProductPage = salesPage.selectSearchString();
        NomenclatureSearch nomenclatureSearch = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearch.returnToDepartmentChoseWindow();
        nomenclatureSearch.viewAllProducts();

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
    @Test(description = "Сквозной сценарий поиска")
    public void smokeTest() throws Exception{
        LoginPage loginPage = new LoginPage(context);
        SalesPage salesPage = new SalesPage(context);
        UserData seller = new UserData("60069807","Passwd123");
        String shortLmCode = "10";

        log.step("Логинимся на главную");

        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);
        SearchProductPage searchProductPage = salesPage.selectSearchString();
        NomenclatureSearch nomenclatureSearch = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearch.choseDepartmentId(15,105, null, 10);
        nomenclatureSearch.viewAllProducts();
    }
}
