package com.leroy.tests.app;

import com.leroy.constants.EnvConstants;
import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.common.FilterPage;
import com.leroy.pages.app.common.SearchProductPage;
import com.leroy.pages.app.sales.SalesPage;
import com.leroy.pages.app.common.NomenclatureSearchPage;
import com.leroy.pages.app.common.SuppliersSearchPage;
import com.leroy.pages.app.sales.ProductCardPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class SearchTest extends BaseState {

    private static final String ALL_DEPARTMENTS_TEXT = "Все отделы";

    @Test(description = "C3200996 Поиск товара по критериям")
    public void testC3200996() throws Exception {

        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String barCode = "5902120110575";
        String shortLmCode = "100";
        String shortBarCode = "590212011";
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS);

        // Pre-conditions
        LoginPage loginPage = new LoginPage(context);
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);

        // Step 1
        log.step("Нажмите на поле Поиск товаров и услуг");
        SalesPage salesPage = new SalesPage(context);
        SearchProductPage searchProductPage = salesPage.clickSearchBar()
                .verifyRequiredElements();
        // Step 2
        log.step("Перейдите в окно выбора единицы номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow()
                .verifyRequiredElements();
        // Step 3
        log.step("Вернитесь на окно выбора отдела");
        nomenclatureSearchPage.returnToDepartmentChoseWindow()
                .shouldTitleWithNomenclatureIs("")
                .verifyNomenclatureBackBtnVisibility(false);
        // Step 4
        log.step("Нажмите 'Показать все товары'");
        nomenclatureSearchPage.clickShowAllProductsBtn()
                .verifyRequiredElements()
                .shouldCountOfProductsOnPageMoreThan(1)
                .shouldSelectedNomenclatureIs(ALL_DEPARTMENTS_TEXT);

        // Step 5
        log.step("Введите полное значение для поиска по ЛМ коду| 10008698");
        searchProductPage.enterTextInSearchField(lmCode);
        ProductCardPage productCardPage = new ProductCardPage(context)
                .verifyRequiredElements()
                .shouldProductLMCodeIs(lmCode);
        productCardPage.returnBack();

        // Step 6
        log.step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchField(searchContext);
        searchProductPage.shouldProductCardsContainText(searchContext);

        // Step 7
        log.step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchField(barCode);
        productCardPage = new ProductCardPage(context)
                .shouldProductBarCodeIs(barCode);
        searchProductPage = productCardPage.returnBack();

        // Step 8
        log.step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchField(shortLmCode);
        searchProductPage.shouldProductCardsContainText(shortLmCode);

        // Step 9
        log.step("Ввести в поисковую строку положительное число длинной >8 символов ("+shortBarCode+") и инициировать поиск");
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
        SearchProductPage searchProductPage = salesPage.clickSearchBar();
        NomenclatureSearchPage nomenclatureSearch = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearch.choseDepartmentId(15,1595, null, 90);
        nomenclatureSearch.viewAllProducts();
        FilterPage filterPage = searchProductPage.goToFilterPage();
        filterPage.choseCheckBoxFilter(filterPage.BEST_PRICE);
        filterPage.choseCheckBoxFilter(filterPage.LIMITED_OFFER);
        filterPage.choseProductType(filterPage.ORDERED_PRODUCT_TYPE);
        filterPage.choseTopFilter();
        filterPage.choseGammaFilter();
        SuppliersSearchPage suppliersSearchPage = filterPage.goToSuppliersSearchPage();
        suppliersSearchPage.searchSupplier("123");
        suppliersSearchPage.confirmChosenSupplier();
        filterPage.applyChosenFilters();
    }
}
